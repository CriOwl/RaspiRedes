package com.smarts.serialL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;

import com.smarts.Comunications.ManageComunications;
import com.smarts.Comunications.Protocols.SerialHelper;
import com.smarts.Config.ConfigM2;
import com.smarts.Config.ConfigSensor;

public class LiveData {
    private Long  id;
    private Integer correctedVolumen;
    private Integer uncorrectedVolumen;
    private Float correctedResidual;
    private Float uncorrectedResidual;
    private Float temperature;
    private Float rawtemperature;
    private Float pressure;
    private Float rawpressure;
    private Float correctionFactor;
    private Float zFactor;
    private Float flowRate;
    private Integer UncorrectUnderFail;
    private Float zeroTemp;
    private Float spanTemp;
    private Float zeroPressure;
    private Float spanPressure;
    private String baterryVoltage;
    private Byte  presentFaults;
    private Byte  occurredFaults;
    private Byte  presentAlarms;
    private Byte  occurredAlarms;
    private String  presentFaultsV2;
    private String  occurredFaultsV2;
    private String  presentAlarmsV2;
    private String  occurredAlarmsV2;
    private String  dateTimeMC;
    private String type;
    private ConfigSensor fileCsv;
    private String timeSend;
    private String dataCsv;
    public Integer time=ConfigSensor.timeSendMs;
    private Integer back;
    private Integer readBufferDresser;
    private Integer accumulatedCorrectVolumencurrentDay;
    private Integer accumulatedCorrectVolumenPastDay;
    private Integer accumulatedCorrectVolumencurrentMonth;
    private Integer accumulatedCorrectVolumenPreviousMonth;
    private Integer HighestDailyVolumenCurrentMonth;
    private Integer HighestDailyVolumenPreviousMonth;
    private String path="";
    private final String pathLogs="/home/EPI5/.Smarts/logLiveData.txt";
    
    public LiveData() {
        
    }
    private void writeLogs(Exception ex) {
        try {
            File logMqtt = new File(pathLogs);
            if (!logMqtt.exists()) {
                logMqtt.getParentFile().mkdirs();
                logMqtt.createNewFile();
            }
            try (FileWriter fw = new FileWriter(logMqtt, true);
            PrintWriter pw = new PrintWriter(fw)) {
                pw.println("[" + java.time.LocalDateTime.now() + "] ERROR:");
                ex.printStackTrace(pw);
                pw.println("--------------------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
    public void liveDataRequest() {
        System.out.println("[DEBUG] Iniciando liveDataRequest"+ManageData.version);
        if(ManageData.version==0){
            serialData(SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x6E,0), 14));
            readDataLive(SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x6C,0), 70));
            readDataCalibration(SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x75,0), 30));
        }else{
            serialData(SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x6E,1), 16));
            readDataLiveV2(SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x6C,1), 76));
            readDataCalibrationV2(SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x75,1), 32));
        }
        // readAccumulatedVolumen(SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte)0x2A),
        // 40));
        if (id != 0
        && !(correctedResidual + zeroPressure + uncorrectedResidual + spanTemp + temperature + pressure == 0)) {
            ManageComunications.sendData(toJson());
            //Dao.collectionLiveData(toJson(),ConfigM2.getLiveDataCollection(),getSerialRaspi());
            //ConfigM2.setDataCollection(Dao.addJsonCollection(toJson(),ConfigM2.getDataCollection(),id.toString(),"DataCollection"));
            ConfigM2.updateConfigM2();
        }
    }
    
    private void readDataCalibration(byte[] readData) {
        if(readData==null || readData.length<20){
            System.out.println("Error: El buffer de lectura es demasiado corto para Data Calibration V1.");
            return;
        }
        setZeroTemp(Arrays.copyOfRange(readData, 8, 12));
        setSpanTemp(Arrays.copyOfRange(readData, 12, 16));
        setZeroPressure(Arrays.copyOfRange(readData, 16, 20));
        setSpanPressure(Arrays.copyOfRange(readData, 20, 24));
    }
    private void readDataCalibrationV2(byte[] readData) {
        if(readData==null || readData.length<20){
            System.out.println("Error: El buffer de lectura es demasiado corto para Data Calibration V1.");
            return;
        }
        setZeroTemp(Arrays.copyOfRange(readData, 10, 14));
        setSpanTemp(Arrays.copyOfRange(readData, 14, 18));
        setZeroPressure(Arrays.copyOfRange(readData, 18, 22));
        setSpanPressure(Arrays.copyOfRange(readData, 22, 26));
    }
    public String serialData(byte[] readBuffer) {
        id = 0L;
        if (readBuffer == null || readBuffer.length < 6) {
            System.out.println("Error: El buffer de lectura es nulo o demasiado corto.");
            return "0";
        }
        
        if (ManageData.version == 0) {
            id = Integer.toUnsignedLong(
            ByteBuffer.wrap(Arrays.copyOfRange(readBuffer, 8, 12))
            .order(ByteOrder.LITTLE_ENDIAN)
            .getInt()
            );
        } else {
            id = Integer.toUnsignedLong(
            ByteBuffer.wrap(Arrays.copyOfRange(readBuffer, 10, 14))
            .order(ByteOrder.LITTLE_ENDIAN)
            .getInt()
            );
        }
        System.out.println(id);
        return Long.toString(id);
        
    }
    
    
    private void readAccumulatedVolumen(byte[] dataAccumulated) {
        for (byte elem : dataAccumulated) {
            System.out.printf("%02X ", elem);
        }
        setAccumulatedCorrectVolumencurrentDay(Arrays.copyOfRange(dataAccumulated, 8, 12));
        setAccumulatedCorrectVolumenPastDay(Arrays.copyOfRange(dataAccumulated, 12, 16));
        setAccumulatedCorrectVolumencurrentMonth(Arrays.copyOfRange(dataAccumulated, 16, 20));
        setAccumulatedCorrectVolumenPreviousMonth(Arrays.copyOfRange(dataAccumulated, 20, 24));
        setHighestDailyVolumenCurrentMonth(Arrays.copyOfRange(dataAccumulated, 24, 28));
        setHighestDailyVolumenPreviousMonth(Arrays.copyOfRange(dataAccumulated, 28, 32));
    }
    
    private void readDataLive(byte[] readBufferLive) {
        if(readBufferLive==null || readBufferLive.length<70){
            System.out.println("Error: El buffer de lectura es demasiado corto para LiveData V2.");
            return;
        }
        setCorrectedVolumen(Arrays.copyOfRange(readBufferLive, 8, 12));
        setUncorrectedVolumen(Arrays.copyOfRange(readBufferLive, 12, 16));
        setCorrectedResidualString(Arrays.copyOfRange(readBufferLive, 16, 20));
        setUncorrectedResidualString(Arrays.copyOfRange(readBufferLive, 20, 24));
        setFlowRate(Arrays.copyOfRange(readBufferLive, 24, 28));
        setUncorrectUnderFail(Arrays.copyOfRange(readBufferLive, 28, 32));
        setTemperature(Arrays.copyOfRange(readBufferLive, 32, 36));
        setRawtemperature(Arrays.copyOfRange(readBufferLive, 36, 40));
        setPressure(Arrays.copyOfRange(readBufferLive, 40, 44));
        setRawpressure(Arrays.copyOfRange(readBufferLive, 44, 48));
        setCorrectionFactor(Arrays.copyOfRange(readBufferLive, 48, 52));
        setzFactor(Arrays.copyOfRange(readBufferLive, 52, 56));
        setPresentFaults(readBufferLive[56]);
        setOccurredFaults(readBufferLive[57]);
        setPresentAlarms(readBufferLive[58]);
        setOccurredAlarms(readBufferLive[59]);
        setDateTimeMC(Arrays.copyOfRange(readBufferLive, 60, 64));
        setBaterryVoltage(Arrays.copyOfRange(readBufferLive, 64, 68));
    }
    private void readDataLiveV2(byte[] readBufferLive) {
        if(readBufferLive==null || readBufferLive.length<76){
            System.out.println("Error: El buffer de lectura es demasiado corto para LiveData V2.");
            return;
        }
        setCorrectedVolumen(Arrays.copyOfRange(readBufferLive, 10, 14));
        setUncorrectedVolumen(Arrays.copyOfRange(readBufferLive, 14, 18));
        setCorrectedResidualString(Arrays.copyOfRange(readBufferLive, 18, 22));
        setUncorrectedResidualString(Arrays.copyOfRange(readBufferLive, 22, 26));
        setFlowRate(Arrays.copyOfRange(readBufferLive, 26, 30));
        setUncorrectUnderFail(Arrays.copyOfRange(readBufferLive, 30, 34));
        setTemperature(Arrays.copyOfRange(readBufferLive, 34, 38));
        setRawtemperature(Arrays.copyOfRange(readBufferLive, 38, 42));
        setPressure(Arrays.copyOfRange(readBufferLive, 42, 46));
        setRawpressure(Arrays.copyOfRange(readBufferLive, 46, 50));
        setCorrectionFactor(Arrays.copyOfRange(readBufferLive, 50, 54));
        setzFactor(Arrays.copyOfRange(readBufferLive, 54, 58));
        setPresentFaultsV2(Arrays.copyOfRange(readBufferLive, 58, 60));
        setOccurredFaultsV2(Arrays.copyOfRange(readBufferLive, 60, 62));
        setPresentAlarmsV2(Arrays.copyOfRange(readBufferLive, 62, 64));
        setOccurredAlarmsV2(Arrays.copyOfRange(readBufferLive, 64, 66));
        setDateTimeMC(Arrays.copyOfRange(readBufferLive, 66, 70));
        setBaterryVoltage(Arrays.copyOfRange(readBufferLive, 70, 74));
        
    }
    
    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String data =id + ";"
        + getCorrectedVolumen() + ";"
        + getUncorrectedVolumen() + ";"
        + getCorrectedResidualString() + ";"
        + getUncorrectedResidualString() + ";"
        + getFlowRate() + ";"
        + getUncorrectUnderFail() + ";"
        + getTemperature() + ";"
        + getRawtemperature() + ";"
        + getZeroTemp() + ";"
        + getSpanTemp() + ";"
        + getPressure() + ";"
        + getRawpressure() + ";"
        + getZeroPressure() + ";"
        + getSpanPressure() + ";"
        + getCorrectionFactor() + ";"
        + getzFactor() + ";"
        /*
        * + getAccumulatedCorrectVolumencurrentDay() + ";"
        * + getAccumulatedCorrectVolumenPastDay() + ";"
        * + getAccumulatedCorrectVolumencurrentMonth() + ";"
        * + getAccumulatedCorrectVolumenPreviousMonth() + ";"
        * + getHighestDailyVolumenCurrentMonth() + ";"
        * + getHighestDailyVolumenPreviousMonth() + ";"
        */
        + dtf.format(now);
        return data;
    }
    
    private String toJson() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String Json;
        if (ManageData.version==0){
            Json = "{" 
            //+ '"'+ "Id" + '"' + ": " + getSerialRaspi() + ","
            + '"' + "DeviceUID" + '"' + ": "+'"'+ "12345678"+'"' + ","
            + '"' + "CorrectedVolume" + '"' + ": " + getCorrectedVolumen() + ","
            + '"' + "UncorrectedVolume" + '"' + ": " + getUncorrectedVolumen() + ","
            + '"' + "CorrectedResidual" + '"' + ": " + getCorrectedResidualString() + ","
            + '"' + "UncorrectedResidual" + '"' + ": " + getUncorrectedResidualString() + ","
            + '"' + "FlowRate" + '"' + ": " + getFlowRate() + ","
            + '"' + "UncorrectedUnderFault" + '"' + ": " + getUncorrectUnderFail() + ","
            + '"' + "Temperature" + '"' + ": " + getTemperature() + ","
            + '"' + "RawTemperature" + '"' + ": " + getRawtemperature() + ","
            + '"' + "Pressure" + '"' + ": " + getPressure() + ","
            + '"' + "RawPressure" + '"' + ": " + getRawpressure() + ","
            + '"' + "CorrectionFactor" + '"' + ": " + getCorrectionFactor() + ","
            + '"' + "PresentFaults" + '"' + ": " + getPresentFaults() + ","
            + '"' + "OccurredFaults" + '"' + ": " + getOccurredFaults() + ","
            + '"' + "PresentAlarms" + '"' + ": " + getPresentAlarms() + ","
            + '"' + "OccurredAlarms" + '"' + ": " + getOccurredAlarms() + ","
            + '"' + "DateTime" + '"' + ": " + getDateTimeMC() + ","
            + '"' + "BatteryVoltage" + '"' + ": " + getBaterryVoltage()
            //Agregar un ,
            //+ '"' + "ZERO_TEMP" + '"' + ": " + getZeroTemp() + ","
            //+ '"' + "SPAN_TEMP" + '"' + ": " + getSpanTemp() + ","
            //+ '"' + "ZERO_PRESSURE" + '"' + ": " + getZeroPressure() + ","
            //+ '"' + "SPAN_PRESSURE" + '"' + ": " + getSpanPressure() + ","
            /*
            * + '"' + "Accumulated Correct Volumen Day" + '"' + ": " +
            * getAccumulatedCorrectVolumencurrentDay() + ","
            * + '"' + "Accumulated Correct Volumen Past Day" + '"' + ": " +
            * getAccumulatedCorrectVolumenPastDay() + ","
            * + '"' + "Accumulated Correct Volumen Month" + '"' + ": " +
            * getAccumulatedCorrectVolumencurrentMonth() + ","
            * + '"' + "Accumulated Correct Volumen Past Month" + '"' + ": " +
            * getAccumulatedCorrectVolumenPreviousMonth() + ","
            * + '"' + "Highest Daily Volumen Current Month" + '"' + ": " +
            * getHighestDailyVolumenCurrentMonth() + ","
            * + '"' + "Highest Daily Volumen Previous Month" + '"' + ": " +
            * getHighestDailyVolumenPreviousMonth() + ","
            */ 
            + "}";
        }else{
            Json = "{" 
            //+ '"'+ "Id" + '"' + ": " + getSerialRaspi() + ","
            + '"' + "DeviceUID" + '"' + ": " +'"'+ "12345678"+'"' + ","
            + '"' + "CorrectedVolume" + '"' + ": " + getCorrectedVolumen() + ","
            + '"' + "UncorrectedVolume" + '"' + ": " + getUncorrectedVolumen() + ","
            + '"' + "CorrectedResidual" + '"' + ": " + getCorrectedResidualString() + ","
            + '"' + "UncorrectedResidual" + '"' + ": " + getUncorrectedResidualString() + ","
            + '"' + "FlowRate" + '"' + ": " + getFlowRate() + ","
            + '"' + "UncorrectedUnderFault" + '"' + ": " + getUncorrectUnderFail() + ","
            + '"' + "Temperature" + '"' + ": " + getTemperature() + ","
            + '"' + "RawTemperature" + '"' + ": " + getRawtemperature() + ","
            + '"' + "Pressure" + '"' + ": " + getPressure() + ","
            + '"' + "RawPressure" + '"' + ": " + getRawpressure() + ","
            + '"' + "CorrectionFactor" + '"' + ": " + getCorrectionFactor() + ","
            + '"' + "PresentFaults" + '"' + ": " + getPresentFaultsV2() + ","
            + '"' + "OccurredFaults" + '"' + ": " + getOccurredFaultsV2() + ","
            + '"' + "PresentAlarms" + '"' + ": " + getPresentAlarmsV2() + ","
            + '"' + "OccurredAlarms" + '"' + ": " + getOccurredAlarmsV2() + ","
            + '"' + "DateTime" + '"' + ": " + getDateTimeMC() + ","
            + '"' + "BatteryVoltage" + '"' + ": " + getBaterryVoltage()
            //Agregar un ,
            //+ '"' + "ZERO_TEMP" + '"' + ": " + getZeroTemp() + ","
            //+ '"' + "SPAN_TEMP" + '"' + ": " + getSpanTemp() + ","
            //+ '"' + "ZERO_PRESSURE" + '"' + ": " + getZeroPressure() + ","
            //+ '"' + "SPAN_PRESSURE" + '"' + ": " + getSpanPressure() + ","
            /*
            * + '"' + "Accumulated Correct Volumen Day" + '"' + ": " +
            * getAccumulatedCorrectVolumencurrentDay() + ","
            * + '"' + "Accumulated Correct Volumen Past Day" + '"' + ": " +
            * getAccumulatedCorrectVolumenPastDay() + ","
            * + '"' + "Accumulated Correct Volumen Month" + '"' + ": " +
            * getAccumulatedCorrectVolumencurrentMonth() + ","
            * + '"' + "Accumulated Correct Volumen Past Month" + '"' + ": " +
            * getAccumulatedCorrectVolumenPreviousMonth() + ","
            * + '"' + "Highest Daily Volumen Current Month" + '"' + ": " +
            * getHighestDailyVolumenCurrentMonth() + ","
            * + '"' + "Highest Daily Volumen Previous Month" + '"' + ": " +
            * getHighestDailyVolumenPreviousMonth() + ","
            */ 
            +"}";
        }
        System.out.println(Json);
        return Json;
    }
    
    public Integer getReadBufferDresser() {
        return readBufferDresser;
    }
    
    public void setReadBufferDresser(byte[] readBufferDresser) {
        this.readBufferDresser = ByteBuffer.wrap(readBufferDresser).order(ByteOrder.LITTLE_ENDIAN).getInt();
        System.out.println(this.readBufferDresser);
    }
    
    public Integer getCorrectedVolumen() {
        return correctedVolumen;
    }
    
    public void setCorrectedVolumen(byte[] correctedVolumen) {
        this.correctedVolumen = ByteBuffer.wrap(correctedVolumen).order(ByteOrder.LITTLE_ENDIAN).getInt();
        System.out.println(this.correctedVolumen);
    }
    
    public Integer getUncorrectedVolumen() {
        return uncorrectedVolumen;
    }
    
    public void setUncorrectedVolumen(byte[] uncorrectedVolumen) {
        this.uncorrectedVolumen = ByteBuffer.wrap(uncorrectedVolumen).order(ByteOrder.LITTLE_ENDIAN).getInt();
        System.out.println(this.uncorrectedVolumen);
    }
    
    public Float getCorrectedResidualString() {
        return correctedResidual;
    }
    
    public void setCorrectedResidualString(byte[] correctedResidualString) {
        this.correctedResidual = ByteBuffer.wrap(correctedResidualString).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        System.out.println(this.correctedResidual);
        
    }
    
    public Float getUncorrectedResidualString() {
        return uncorrectedResidual;
    }
    
    public void setUncorrectedResidualString(byte[] uncorrectedResidualString) {
        this.uncorrectedResidual = ByteBuffer.wrap(uncorrectedResidualString).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        System.out.println(this.uncorrectedResidual);
        
    }
    
    public Float getTemperature() {
        return temperature;
    }
    
    public void setTemperature(byte[] temperature) {
        this.temperature = ByteBuffer.wrap(temperature).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        System.out.println(this.temperature);
    }
    
    public Float getRawtemperature() {
        return rawtemperature;
    }
    
    public void setRawtemperature(byte[] rawtemperature) {
        this.rawtemperature = ByteBuffer.wrap(rawtemperature).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        System.out.println(this.rawtemperature);
    }
    
    public Float getPressure() {
        return pressure;
    }
    
    public void setPressure(byte[] pressure) {
        this.pressure = ByteBuffer.wrap(pressure).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        System.out.println(this.pressure);
        
    }
    
    public Float getRawpressure() {
        return rawpressure;
    }
    
    public void setRawpressure(byte[] rawpressure) {
        this.rawpressure = ByteBuffer.wrap(rawpressure).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        System.out.println(this.rawpressure);
        
    }
    
    public Float getCorrectionFactor() {
        return correctionFactor;
    }
    
    public void setCorrectionFactor(byte[] correctionFactor) {
        this.correctionFactor = ByteBuffer.wrap(correctionFactor).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        System.out.println(this.correctionFactor);
    }
    
    public Float getzFactor() {
        return zFactor;
    }
    
    public void setzFactor(byte[] zFactor) {
        this.zFactor = ByteBuffer.wrap(zFactor).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        System.out.println(this.zFactor);
        
    }
    
    public String getBaterryVoltage() {
        return baterryVoltage;
    }
    
    
    public Float getFlowRate() {
        return flowRate;
    }
    
    public void setFlowRate(byte[] flowRate) {
        this.flowRate = ByteBuffer.wrap(flowRate).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        System.out.println(this.flowRate);
        
    }
    
    public Integer getUncorrectUnderFail() {
        return UncorrectUnderFail;
    }
    
    public void setUncorrectUnderFail(byte[] uncorrectUnderFail) {
        this.UncorrectUnderFail = ByteBuffer.wrap(uncorrectUnderFail).order(ByteOrder.LITTLE_ENDIAN).getInt();
        System.out.println(this.UncorrectUnderFail);
    }
    
    public Float getZeroTemp() {
        return zeroTemp;
    }
    
    public void setZeroTemp(byte[] zeroTemp) {
        this.zeroTemp = ByteBuffer.wrap(zeroTemp).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
    
    public Float getSpanTemp() {
        return spanTemp;
    }
    
    public void setSpanTemp(byte[] spanTemp) {
        this.spanTemp = ByteBuffer.wrap(spanTemp).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
    
    public Float getZeroPressure() {
        return zeroPressure;
    }
    
    public void setZeroPressure(byte[] zeroPressure) {
        this.zeroPressure = ByteBuffer.wrap(zeroPressure).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
    
    public Float getSpanPressure() {
        return spanPressure;
    }
    
    public void setSpanPressure(byte[] spanPressure) {
        this.spanPressure = ByteBuffer.wrap(spanPressure).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
    
    public String getAccumulatedCorrectVolumencurrentDay() {
        return accumulatedCorrectVolumencurrentDay.toString();
    }
    
    public void setAccumulatedCorrectVolumencurrentDay(byte[] accumulatedCorrectVolumencurrentDay) {
        this.accumulatedCorrectVolumencurrentDay = ByteBuffer.wrap(accumulatedCorrectVolumencurrentDay)
        .order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    public String getAccumulatedCorrectVolumenPastDay() {
        return accumulatedCorrectVolumenPastDay.toString();
    }
    
    public void setAccumulatedCorrectVolumenPastDay(byte[] accumulatedCorrectVolumenPastDay) {
        this.accumulatedCorrectVolumenPastDay = ByteBuffer.wrap(accumulatedCorrectVolumenPastDay)
        .order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    public String getAccumulatedCorrectVolumencurrentMonth() {
        return accumulatedCorrectVolumencurrentMonth.toString();
    }
    
    public void setAccumulatedCorrectVolumencurrentMonth(byte[] accumulatedCorrectVolumencurrentMonth) {
        this.accumulatedCorrectVolumencurrentMonth = ByteBuffer.wrap(accumulatedCorrectVolumencurrentMonth)
        .order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    public String getAccumulatedCorrectVolumenPreviousMonth() {
        return accumulatedCorrectVolumenPreviousMonth.toString();
    }
    
    public void setAccumulatedCorrectVolumenPreviousMonth(byte[] accumulatedCorrectVolumenPreviousMonth) {
        this.accumulatedCorrectVolumenPreviousMonth = ByteBuffer.wrap(accumulatedCorrectVolumenPreviousMonth)
        .order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    public String getHighestDailyVolumenCurrentMonth() {
        return HighestDailyVolumenCurrentMonth.toString();
    }
    
    public void setHighestDailyVolumenCurrentMonth(byte[] highestDailyVolumenCurrentMonth) {
        HighestDailyVolumenCurrentMonth = ByteBuffer.wrap(highestDailyVolumenCurrentMonth)
        .order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    public String getHighestDailyVolumenPreviousMonth() {
        return HighestDailyVolumenPreviousMonth.toString();
    }
    
    public void setHighestDailyVolumenPreviousMonth(byte[] highestDailyVolumenPreviousMonth) {
        HighestDailyVolumenPreviousMonth = ByteBuffer.wrap(highestDailyVolumenPreviousMonth)
        .order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    public void setBaterryVoltage(byte[] baterryVoltage) {
        Long transformation = ByteBuffer.wrap(baterryVoltage).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL; 
        this.baterryVoltage = transformation.toString();
    }
    public Byte getOccurredFaults() {
        return occurredFaults;
    }
    public void setOccurredFaults(Byte occurredFaults) {
        this.occurredFaults = occurredFaults;
    }
    public Byte getPresentAlarms() {
        return presentAlarms;
    }
    public void setPresentAlarms(Byte presentAlarms) {
        this.presentAlarms = presentAlarms;
    }
    public Byte getOccurredAlarms() {
        return occurredAlarms;
    }
    public void setOccurredAlarms(Byte occurredAlarms) {
        this.occurredAlarms = occurredAlarms;
    }
    public String getDateTimeMC() {
        return dateTimeMC;
    }
    public void setDateTimeMC(byte[] dateTimeMC) {
        Long transformation = ByteBuffer.wrap(dateTimeMC).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL; 
        this.dateTimeMC = transformation.toString();
    }
    private  String getSerialRaspi(){
        try {
            File serial=new File("/proc/cpuinfo");
            Scanner lector=new Scanner(serial);
            while (lector.hasNext()) { 
                String line=lector.nextLine();
                if(line.contains("Serial")){
                    System.out.println(line);
                    line=line.replaceAll(" ", "");
                    String[] separador=line.split(":");
                    System.out.println(separador[1]);
                    line=separador[1];
                    return line;
                }
            }
        } catch (Exception e) {
            writeLogs(e);
        }
        return "100";
    }
    
    public Byte getPresentFaults() {
        return presentFaults;
    }
    
    public void setPresentFaults(Byte presentFaults) {
        this.presentFaults = presentFaults;
    }
    public String getPresentFaultsV2() {
        return presentFaultsV2;
    }
    public void setPresentFaultsV2(byte[] presentFaultsV2) {
        byte low  = presentFaultsV2[0];
        byte high = presentFaultsV2[1];
        Integer valor = ((high & 0xFF) << 8) | (low & 0xFF);
        this.presentFaultsV2 = valor.toString();
    }
    public String getOccurredFaultsV2() {
        return occurredFaultsV2;
    }
    public void setOccurredFaultsV2(byte[] occurredFaultsV2) {
        byte low  = occurredFaultsV2[0];
        byte high = occurredFaultsV2[1];
        Integer valor = ((high & 0xFF) << 8) | (low & 0xFF);
        this.occurredFaultsV2 = valor.toString();
    }
    public String getPresentAlarmsV2() {
        return presentAlarmsV2;
    }
    public void setPresentAlarmsV2(byte[] presentAlarmsV2) {
        byte low  = presentAlarmsV2[0];
        byte high = presentAlarmsV2[1];
        Integer valor = ((high & 0xFF) << 8) | (low & 0xFF);
        this.presentAlarmsV2 = valor.toString();
    }
    public String getOccurredAlarmsV2() {
        return occurredAlarmsV2;
    }
    public void setOccurredAlarmsV2(byte[] occurredAlarmsV2) {
        byte low  = occurredAlarmsV2[0];
        byte high = occurredAlarmsV2[1];
        Integer valor = ((high & 0xFF) << 8) | (low & 0xFF);
        this.occurredAlarmsV2 = valor.toString();
    }
    
}
