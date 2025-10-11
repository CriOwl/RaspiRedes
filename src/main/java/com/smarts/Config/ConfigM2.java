package com.smarts.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ConfigM2 {
    private static String liveDataCollection;
    private static String commonCollection;
    private static String log1Collection;
    private static String log2Collection;
    private static String log3Collection;
    private static String alarmCollection;
    private static String faultCollection;
    private static String auditCollection;
    private static String DataCollection;
    public static String idMC2="0";
    private static String path = "/etc/.Smarts/config_MC2_"+idMC2+".txt";
    private static String pathLogs = "/var/log/.Smarts/Config.txt";
    
    private static void writeLogs(Exception ex) {
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
    public static void setConfiguration() {
        try {
            File configFile = new File(path);
            if (!configFile.exists()) {
                 DateTimeFormatter dtfActually = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime nowActually = LocalDateTime.now();
                String nowDate = dtfActually.format(LocalDateTime.now()).toString();
                configFile.createNewFile();
                FileWriter writerFile = new FileWriter(configFile);
                String confString=
                "liveDataCollection:"+"LiveData_"+idMC2+"_"+nowDate
                +"\ncommonCollection:"+"CommonCollection_"+idMC2+"_"+nowDate
                +"\nlog1Collection:"+"Log1_"+idMC2+"_"+nowDate
                +"\nlog2Collection:"+"Log2_"+idMC2+"_"+nowDate
                +"\nlog3Collection:"+"Log3_"+idMC2+"_"+nowDate
                +"\nalarmCollection:"+"AlarmLogs_"+idMC2+"_"+nowDate
                +"\nfaultCollection:"+"FaultLogs_"+idMC2+"_"+nowDate
                +"\nauditCollection:"+"AuditLogs_"+idMC2+"_"+nowDate
                +"\nDataCollection:"+"DataCollection_"+idMC2+"_"+nowDate;                
            }
            try (Scanner lector = new Scanner(configFile)) {
                String line;
                while (lector.hasNextLine()) {
                    line = lector.nextLine();
                    if (line.contains("liveDataCollection:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setLiveDataCollection(line);
                    } else if (line.contains("commonCollection:")){
                        line=line.substring(line.indexOf(':')+1);
                        setCommonCollection(line);        
                    }
                    else if (line.contains("log1Collection:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setLog1Collection(line);
                    } else if (line.contains("log2Collection:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setlog2Collection(line);
                    } else if (line.contains("log3Collection:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setlog3Collection(line);
                    } else if (line.contains("alarmCollection:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setlog2Collection(line);
                    } else if (line.contains("faultCollection:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setlog3Collection(line);
                    } else if (line.contains("auditCollection:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setlog3Collection(line);
                    }  
                    else if (line.contains("DataCollection:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setDataCollection(line);
                    }
                }
            }
        } catch (Exception e) {
            writeLogs(e);
        }
    }
    public static void updateConfigM2(){
        try {
            File configM2 = new File(path);
            try (FileWriter writer = new FileWriter(configM2,false)) {
                writer.write("liveDataCollection:"+liveDataCollection+"\n");
                writer.write("commonCollection:"+commonCollection+"\n");
                writer.write("log1Collection:"+log1Collection+"\n");
                writer.write("log2Collection:"+log2Collection+"\n");
                writer.write("log3Collection:"+log3Collection+"\n");
                writer.write("alarmCollection:"+alarmCollection+"\n");
                writer.write("faultCollection:"+faultCollection+"\n");
                writer.write("auditCollection:"+auditCollection+"\n");
                writer.write("DataCollection:"+DataCollection+"\n");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public static String getLiveDataCollection() {
        return liveDataCollection;
    }
    public static void setLiveDataCollection(String liveDataCollection) {
        ConfigM2.liveDataCollection = liveDataCollection;
    }
    public static String getCommonCollection() {
        return commonCollection;
    }
    public static void setCommonCollection(String commonCollection) {
        ConfigM2.commonCollection = commonCollection;
    }
    public static String getLog1Collection() {
        return log1Collection;
    }
    public static void setLog1Collection(String log1Collection) {
        ConfigM2.log1Collection = log1Collection;
    }
    public static String getlog2Collection() {
        return log2Collection;
    }
    public static void setlog2Collection(String log2Collection) {
        ConfigM2.log2Collection = log2Collection;
    }
    public static String getlog3Collection() {
        return log3Collection;
    }
    public static void setlog3Collection(String log3Collection) {
        ConfigM2.log3Collection = log3Collection;
    }
    public static String getDataCollection() {
        return DataCollection;
    }
    public static void setDataCollection(String dataCollection) {
        DataCollection = dataCollection;
    }
    public static String getAlarmCollection() {
        return alarmCollection;
    }
    public static void setAlarmCollection(String alarmCollection) {
        ConfigM2.alarmCollection = alarmCollection;
    }
    public static String getFaultCollection() {
        return faultCollection;
    }
    public static void setFaultCollection(String faultCollection) {
        ConfigM2.faultCollection = faultCollection;
    }
    public static String getAuditCollection() {
        return auditCollection;
    }
    public static void setAuditCollection(String auditCollection) {
        ConfigM2.auditCollection = auditCollection;
    }
    
    
}
