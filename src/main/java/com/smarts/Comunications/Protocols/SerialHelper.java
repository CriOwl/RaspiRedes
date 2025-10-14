package com.smarts.Comunications.Protocols;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fazecast.jSerialComm.SerialPort;

public  class SerialHelper {
    private static final String uniquenamePort = "Silicon_Labs";
    private static SerialPort serialPort;
    private static String pathSerialCom="/home/" + "EPI5" + "/.Smarts/SerialPort.txt";
    private static File configFile= new File(pathSerialCom);
    private static String[] serialUsed;
    private static String locatedPort;
    public SerialHelper() {
        
    }
    public static void eliminarUsoSerial(){
         try {
            if(!(configFile.exists())){
                configFile.createNewFile();
            }
            serialUsed=readSerialUse();
            String linea="";
            FileWriter escritor = new FileWriter(configFile,false);
            System.out.println(locatedPort);
            for (int i = 0; i < serialUsed.length; i++) {
                if(serialUsed[i]!=null&&!(serialUsed[i].contains(locatedPort))){
                    linea=linea+serialUsed[i]+";";
                    System.out.println(linea);
                }
            }
            escritor.write(linea);
            escritor.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    private static String[] readSerialUse(){
        String[] vectorSerialUsed=new String[6];
        try {
            if(configFile.exists()){
                Scanner lector = new Scanner(configFile);
                lector.useDelimiter(";");
                for (int i = 0; lector.hasNext(); i++) {
                    vectorSerialUsed[i]=lector.next();
                    System.out.println(vectorSerialUsed[i]);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            vectorSerialUsed=new String[1];
            vectorSerialUsed[0]="0;";
            return vectorSerialUsed;
        } 
        return vectorSerialUsed;
    }

    private static void writeSerialUse(String nameSerialPort){
        try {
            if(!(configFile.exists())){
                configFile.createNewFile();
            }
            FileWriter escritor = new FileWriter(configFile,true);
            escritor.write(nameSerialPort);
            escritor.close();
        } catch (Exception e) {
        }
    }
    private static boolean comprobationSerial(String locationSerial, String[] serialUsedNames){
        for (int i = 0; i < serialUsedNames.length; i++) {
            if(locationSerial.equalsIgnoreCase(serialUsedNames[i])){
                return false;
            }
        }
        return true;
    }
    public static void findPort() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        serialUsed=readSerialUse();
        if (!(serialPorts.length > 0)) {
            System.out.println("Error 1 No common Ports");
            return;
        }
        for (SerialPort nameport : serialPorts) {
            System.out.println(nameport.getPortLocation());
            if (getNameIdVendor(nameport.getSystemPortName())) {
                if (getopenPort(nameport.getSystemPortName())&& comprobationSerial(nameport.getPortLocation(),serialUsed)) {
                    serialPort = SerialPort.getCommPort(nameport.getSystemPortName());
                    locatedPort=nameport.getPortLocation();
                    writeSerialUse(nameport.getPortLocation()+";");
                    setupSerialPort(1200);
                    return;
                }
            }
        }
        for (SerialPort nameport : serialPorts) {
            if (getopenPort(nameport.getSystemPortName())&&comprobationSerial(nameport.getPortLocation(),serialUsed)) {
                serialPort = SerialPort.getCommPort(nameport.getSystemPortName());
                locatedPort=nameport.getPortLocation();
                writeSerialUse(nameport.getPortLocation()+";");
                setupSerialPort(1200);
            }
        }
        System.out.println(serialPort.getSystemPortName()+"///////////");
    }

    private static boolean getopenPort(String namePort) {
        SerialPort test = SerialPort.getCommPort(namePort);
        if (!(test.openPort())) {
            System.out.println("Error " + namePort + " can not open port");
            return false;
        }
        test.closePort();
        return true;
    }

    public  static void setupSerialPort(Integer baudrate) {
        serialPort.setBaudRate(baudrate);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(1);
    }

    private static boolean getNameIdVendor(String namePort) {
        try {
            ProcessBuilder bash = new ProcessBuilder("bash", "-c",
                    "udevadm info -q property -n " + namePort + " | grep -E 'ID_VENDOR=|ID_MODEL=' ;exit");
            Process comand = bash.start();
            BufferedReader ou = new BufferedReader(new InputStreamReader(comand.getInputStream()));
            String line;
            while (!((line = ou.readLine())==null)) {
                if (line.contains(uniquenamePort)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error" + e);

        }
        System.out.println("No Serial ports with unique name");
        return false;
    }

    public static byte [] stablishConnection(byte[] comand, int lenght){
        if(!(serialPort.openPort())){
            System.out.println("Error "+ serialPort.getSystemPortName()+" is busy");
            return null;
        }
        try {
            ExecutorService service=Executors.newSingleThreadExecutor();
            Future<byte[]> futureBytes=service.submit(sendandReceiveData(comand, lenght));
            byte[] actuallybytes=futureBytes.get();
            service.shutdown();
            return actuallybytes;
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e + "Error to moment to excute service");
            return null;
        }
    }
    private static Callable<byte[]> sendandReceiveData(byte [] comand,int lenght){
        return ()->{
            if(!(serialPort.openPort())){
                System.out.println("Error to send Data");
                return null;
            }
            try {
                try (OutputStream ou = serialPort.getOutputStream()) {
                    ou.write(comand);
                    ou.flush();
                }
                int error = 0;
                while (serialPort.bytesAvailable() < lenght && error < 30) {
                    error++;
                    Thread.sleep(50);
                }
                byte[] bytesReader=new byte[lenght];
                while (serialPort.bytesAvailable()>0) {
                    int numRead=serialPort.readBytes(bytesReader, bytesReader.length);
                    System.out.println("[INFO] Datos recibidos (" + numRead + " bytes): " + bytesToHex(bytesReader));
                }
                if(!comprobationCheckSum(bytesReader)){
                    System.out.println("Error checksum");
                    return null;
                }
                serialPort.closePort();
                return bytesReader;
            } catch (IOException | InterruptedException e) {
                System.out.println(e+"Error to send Data");
            }
            return null;
        };
    }

    private static String bytesToHex(byte[] bytes) {
    if (bytes == null) return "null";
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02X ", b));
    }
    return sb.toString().trim();
}

    public static byte[] createDataRequestPacket(byte type, int version) {
        byte[] packet;
        if(version==0){
            packet = new byte[11];
            packet[0] = 0x00;
            packet[1] = 0x00;
            packet[2] = 0x00;
            packet[3] = 0x00;
            packet[4] = type;
            packet[5] = 0x00;
            packet[6] = 0x00;
            packet[7] = 0x01;
            packet[8] = 0x00;
            byte[] nChecksum = packet;
            int checksum = calculateChecksum(nChecksum, nChecksum.length - 2);
            System.out.println("CRC Calculado: " + Integer.toHexString(checksum));
            packet[9] = (byte) (checksum & 0xFF);
            packet[10] = (byte) ((checksum >> 8) & 0xFF);
        }else{
            packet = new byte[13];
            packet[0] = 0x00;
            packet[1] = 0x00;
            packet[2] = 0x00;
            packet[3] = 0x00;
            packet[4] = type;
            packet[5] = 0x00;
            packet[6] = 0x00;
            packet[7] = 0x00;
            packet[8] = 0x00;
            packet[9] = 0x01;
            packet[10] = 0x00;
            byte[] nChecksum = packet;
            int checksum = calculateChecksum(nChecksum, nChecksum.length - 2);
            System.out.println("CRC Calculado: " + Integer.toHexString(checksum));
            packet[11] = (byte) (checksum & 0xFF);
            packet[12] = (byte) ((checksum >> 8) & 0xFF);
        }
        return packet;
    }
    public static byte[] createDataRequestPacketData(byte type,byte data, int version) {
        byte[] packet;
        if(version==0){
        packet = new byte[11];
        packet[0] = 0x00;
        packet[1] = 0x00;
        packet[2] = 0x00;
        packet[3] = 0x00;
        packet[4] = type;
        packet[5] = 0x00;
        packet[6] = 0x00;
        packet[7] = 0x01;
        packet[8] = data;
        byte[] nChecksum = packet;
        int checksum = calculateChecksum(nChecksum, nChecksum.length - 2);
        System.out.println("CRC Calculado: " + Integer.toHexString(checksum));
        packet[9] = (byte) (checksum & 0xFF);
        packet[10] = (byte) ((checksum >> 8) & 0xFF);
        }else{
            packet = new byte[13];
            packet[0] = 0x00;
            packet[1] = 0x00;
            packet[2] = 0x00;
            packet[3] = 0x00;
            packet[4] = type;
            packet[5] = 0x00;
            packet[6] = 0x00;
            packet[7] = 0x00;
            packet[8] = 0x00;
            packet[9] = 0x01;
            packet[10] = data;
            byte[] nChecksum = packet;
            int checksum = calculateChecksum(nChecksum, nChecksum.length - 2);
            System.out.println("CRC Calculado: " + Integer.toHexString(checksum));
            packet[11] = (byte) (checksum & 0xFF);
            packet[12] = (byte) ((checksum >> 8) & 0xFF);
        }
        return packet;
    }
    public static byte[] createDataRequestPacketAddress(byte type,byte starAddress,byte starAddress2,byte dataLong) {
        byte[] packet = new byte[11];
        packet[0] = 0x00;
        packet[1] = 0x00;
        packet[2] = 0x00;
        packet[3] = 0x00;
        packet[4] = type;
        packet[5] = starAddress;
        packet[6] = starAddress2;
        packet[7] = dataLong;
        packet[8] = 0x00;
        byte[] nChecksum = packet;
        int checksum = calculateChecksum(nChecksum, nChecksum.length - 2);
        System.out.println("CRC Calculado: " + Integer.toHexString(checksum));
        packet[9] = (byte) (checksum & 0xFF);
        packet[10] = (byte) ((checksum >> 8) & 0xFF);
        return packet;
    }

    private static int calculateChecksum(byte[] data, int length) {
        int chrBit = 0;
        int crcBit = 0;
        int checksumResult = 0;
        int chr = 0;
        int bitCnt = 0;
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                chr = data[i];
                for (bitCnt = 0; bitCnt < 8; bitCnt++) {
                    chrBit = (chr & 0x01);
                    crcBit = (checksumResult & 0x01);
                    checksumResult >>= 1;
                    chr >>= 1;
                    if (crcBit != chrBit) {
                        checksumResult ^= 0x8408;
                    }
                }
            }
        }
        return checksumResult;
    }

    private static boolean comprobationCheckSum(byte[] readBuffer) {
        if (readBuffer == null || readBuffer.length == 0) {
            return false;
        }
        byte[] temporal = Arrays.copyOf(readBuffer, readBuffer.length-2);
        int checksum = calculateChecksum(temporal, temporal.length);
        int checksumLow = checksum & 0xFF;
        int checksumHigh = (checksum >> 8) & 0xFF;
        if ((readBuffer[readBuffer.length - 2] & 0xFF) == checksumLow &&
                (readBuffer[readBuffer.length - 1] & 0xFF) == checksumHigh) {
            System.out.println("correct");
            return true;
        }
        return false;
    }
    
}
