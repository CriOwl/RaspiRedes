package com.smarts.ManageDataStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class DataCsv {
    private final String header = "ID;SERIAL; CORRECTED_VOLUME; UNCORRECTED_VOLUME; CORRECTED_RESIDUAL; UNCORRECTED_RESIDUAL; FLOW_RATE; UNCORRECTED_UNDER_FAULT;"
            + "TEMPERATURE; RAW_TEMPERATURE;ZERO_TEMP; SPAN_TEMP; PRESSURE; RAW_PRESSURE; ZERO_PRESSURE; SPAN_PRESSURE; CORRECTION_FACTOR; Z_FACTOR; DATE;\n";
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDateTime now = LocalDateTime.now();
    private String dataInit = dtf.format(now).toString();
    private final String hostname = "EPI5";
    private String pathname = "/home/" + hostname + "/.Smarts/" + "---------------";
    // private String pathname = "Data" + dtf.format(now).toString() + ".csv";
    private File archivoData = new File(pathname);
    private final String configPath = "/home/" + hostname + "/.Smarts/configSmarts.csv";
    private String serialRaspi;
    

    public DataCsv() {
    }

    public void createdFile(File archivo) {
        try {
            if (!(archivo.exists())) {
                archivo.createNewFile();
                writeHeader(archivo);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void destroyFile(File archivo) {
        try {
            if (archivo.exists() && !(archivo.isDirectory())) {
                archivo.delete();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void writeHeader(File archivoHeader) {
        try {
            FileWriter writer = new FileWriter(archivoHeader);
            try (Scanner scaner = new Scanner(archivoHeader)) {
                if (!(scaner.hasNext())) {
                    writer.write(header);
                    writer.close();
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void writeData(String data, Integer Days,String id) {
        calculateDays(Days,id);
        try {
            try (FileWriter writer = new FileWriter(archivoData, true)) {
                writer.write(data + "\n");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    // //Data_123456789_27/12/23.csv
    private void calculateDays(Integer Days,String id) {
        try {
            if(!archivoData.exists()){
                archivoData=new File("/home/" + hostname + "/.Smarts/" +"Data_"+id+"_"+ dtf.format(now).toString()  + ".csv");
                archivoData.createNewFile();
            }
            
        } catch (Exception e) {
            System.out.println(e);
        }
        DateTimeFormatter dtfActually = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime nowActually = LocalDateTime.now();
        String nowDate = dtfActually.format(now).toString();
        long days = ChronoUnit.DAYS.between(LocalDate.parse(dataInit), LocalDate.parse(nowDate));
        System.out.println(days + "---------------");
        if (days >= Days) {
            pathname = "/home/" + hostname + "/.Smarts/" +"Data_"+id+"_"+ nowDate + ".csv";
            //pathname = "Data" + nowDate + ".csv";
            dataInit = nowDate;
            archivoData = new File(pathname);
            createdFile(archivoData);
        }
    }

    public String readConfig() {
        File archivo = new File(configPath);
        if ((!archivo.exists())) {
            return "Type:DRESSER1;TimeSend:60000;DataCsv:15;";
        }
        try {
            Scanner configRead = new Scanner(archivo);
            String line = "";
            while (configRead.hasNextLine()) {
                line = line + configRead.nextLine();
            }
            System.out.println(line);
            configRead.close();
            return line;
        } catch (FileNotFoundException e) {
            System.out.println(e);
            return "Type:DRESSER1;TimeSend:60000;DataCsv:15;";
        }
    }
    //----------------------------------------
    public void liveDataStreaming(String liveData,String id) {
        File archivoLiveData = new File("/home/" + hostname + "/.Smarts/" + "LiveData_"+id+"_.csv");
        //File archivoLiveData = new File("LiveData.csv");
        destroyFile(archivoLiveData);
        createdFile(archivoLiveData);
        writerLiveData(liveData, archivoLiveData,id);
    }
//------------------------------
    private void writerLiveData(String liveData, File archivo,String id) {
        try {
            try (FileWriter writer = new FileWriter(archivo)) {
                writer.write(liveData);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void documentLog(String data, String[] header, String path) {
        try {
            File log = new File(path);
            if (!(log.exists())) {
                log.createNewFile();
                try (FileWriter writer = new FileWriter(log, true)) {
                    String line = "ID;";
                    for (String header1 : header) {
                        line = line + header1;
                    }
                    writer.write(line + "\n");
                }
            }
            try (FileWriter writer = new FileWriter(log, true)) {
                writer.write(getSerialRaspi() + data + "\n");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String getSerialRaspi() {
        try {
            File serial = new File("/proc/cpuinfo");
            Scanner lector = new Scanner(serial);
            while (lector.hasNext()) {
                String line = lector.nextLine();
                if (line.contains("Serial")) {
                    System.out.println(line);
                    line = line.replaceAll(" ", "");
                    String[] separador = line.split(":");
                    System.out.println(separador[1]);
                    serialRaspi = separador[1] + ";";
                    return serialRaspi;
                }
            }
        } catch (Exception e) {
        }
        return "100;";
    }

}
