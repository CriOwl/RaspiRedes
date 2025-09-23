package com.smarts.serialL;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.smarts.Comunications.APIRest;
import com.smarts.Comunications.SerialHelper;
import com.smarts.ManageDataStorage.DataCsv;

public class LogsData {

    private int maxBufferRead;
    private Integer id;
    private byte[] startAddressLogginArea;
    private byte[] EndAddressLogginArea;
    private byte[] startAddressLogs1;
    private byte[] startAddressAudit;
    private Integer ptrLog1;
    private int maxLogs1;
    private int logginTerminalTime;
    private byte[] startAddressLogs2;
    private int maxLogs2;
    private int loggin2TerminalTime;
    private Integer ptrLog2;
    private byte[] startAddressLogs3;
    private int maxLogs3;
    private int loggin3TerminalTime;
    private Integer ptrLog3;
    private byte[] startAddressFaultLog;
    private int maxLogsFault;
    private int ptrLogFault;
    private byte[] startAddressAlarm;
    private int maxLogsAlarm;
    private int ptrLogAlarm;
    private Integer logginParameters;
    private Integer loggedParameters;
    private byte sizeData;
    private final DataCsv writerLogs;
    public int timeLog1;
    public int timeLog2;
    public int timeLog3;
    private String JsonLogsData;
    private String JsonAlarm;
    private String JsonFault;
    private String linenew;
    private String linenold;
    private Integer maxAuditLogs;
    private Map<Integer, String> auditLogMap;
    private final String[] faultHeader = { "Serial;", "Date_and_Time;", "Parameter;" };
    private final String[] alarmHeader = { "Serial;", "Date_and_Time;", "Parameter;" };
    private final String[] auditHeader = { "Serial;", "Date_and_Time;", "Parameter;", "Parameter_Old;", "Parameter_New;" };
    private final String urlLog1 = "http://54.167.252.128:3030/api/v1/upload/logs1";
    private final String urlLog2 = "http://54.167.252.128:3030/api/v1/upload/logs2";
    private final String urlLog3 = "http://54.167.252.128:3030/api/v1/upload/logs3";
    private final String urlAlarm = "http://54.167.252.128:3030/api/v1/upload/alarms-logs";
    private final String urlFault = "http://54.167.252.128:3030/api/v1/upload/change-history";
    private final String urlAudit = "http://54.167.252.128:3030/api/v1/upload/audit-logs";
    private final String urlLog1Json = "http://54.167.252.128:3030/api/v1/logs1/webhook";
    private final String urlLog2Json = "http://54.167.252.128:3030/api/v1/logs2/webhook";
    private final String urlLog3Json = "http://54.167.252.128:3030/api/v1/logs3/webhook";
    private final String urlAlarmJson = "http://54.167.252.128:3030/api/v1/alarms/webhook";
    private final String urlFaultJson = "http://54.167.252.128:3030/api/v1/change-history/webhook";

    private String[][] meterTypeAndSize = {
            {
                    "1.5M", "8C", "1.5M", "8C", "G16", "RM1000", "RM30"
            },
            {
                    "2M", "11C", "2M", "11C", "G25", "RM1500", "RM40"
            },
            {
                    "3M", "15C", "3M", "15C", "G40", "RM2000", "RM55"
            },
            {
                    "5M", "2", "5M", "2", "G65", "RM3000", "RM85"
            },
            {
                    "7M", "3", "7M", "3", "G100", "RM5000", "RM140"
            },
            {
                    "11M", "5", "11M", "5", "G160", "RM11000", "RM200"
            },
            {
                    "16M", "7", "16M", "7", "G250", "RM16000", "RM300"
            },
            {
                    "23M", "8.8", "23M", "8.8", "G400", "RM16000L", "RM450"
            },
            {
                    "38M", "11", "38M", "11", "G650", "RM23000", "RM650"
            },
            {
                    "56M", "16", "56M", "16", "G1000", "", ""
            },
            {
                    "102M", "23M4", "102M", "23M4", "", "", ""
            },

    };
    private final String[] logsHeader = {
            "Serial;",
            "Date_and_Time;",
            "Corrected_Volume;",
            "Uncorrected_Volume;",
            "Correction_Factor;",
            "Uncorrected_Volume_Under_Fault;",
            "Average_Measurement_Pressure;",
            "Average_Temperature;",
            "Average_Uncorrected Flow;",
            "Peak_Uncorrected Flow;",
            "Super_Compressibility;",
            "Minimum_Measurement_Pressure;",
            "Maximum_Measurement_Pressure;",
            "End_Measurement_Pressure;",
            "Minimum_Temperature;",
            "Maximum_Temperature;",
            "End_Temperature;",
            "Battery_Voltage;",
            "",
            "",
            "",
            "", "", "", "", "", "", "", "", "", "", "", ""
    };
    private final String[] parametersBytes = {
            "Date_and_Time;",
            "Corrected_Volume;",
            "Uncorrected_Volume;",
            "Correction_Factor;",
            "Uncorrected_Volume_Under_Fault;",
            "Average_Measurement_Pressure;",
            "Average_Temperature;",
            "Average_Uncorrected_Flow;",
            "Peak_Uncorrected_Flow;",
            "Super_Compressibility;",
            "Minimum_Measurement_Pressure;",
            "Maximum_Measurement_Pressure;",
            "End_Measurement_Pressure ;",
            "Minimum_Temperature;",
            "Maximum_Temperature;",
            "End_Temperature;",
            "Battery_Voltage;",
            "",
            "",
            "",
            "", "", "", "", "", "", "", "", "", "", "", "" };
    private List<String> loggerList;
    private Map<String, String> mapLogger;

    public LogsData() {
        loadAuditLogs();
        writerLogs = new DataCsv();
        maxBuffer();
        readLogginSettings();
        firstLogsData();
        secondLogsData();
        thirdLogsData();
        getFaultLogs("/home/EPI5/Smarts/faultLogs"+id+".csv", urlFault);
        getAuditLogs("/home/EPI5/Smarts/AuditLogs"+id+".csv", urlAudit);
        getAlarmanLogs("/home/EPI5/Smarts/AlarmanLogs"+id+".csv", urlAlarm); 
     /*    getFaultLogs("faultLogs"+id+".csv", urlFault);
        getAuditLogs("AuditLogs"+id+".csv", urlAudit);
        getAlarmanLogs("AlarmanLogs"+id+".csv", urlAlarm);  */

    }

    private String getId() {
        serialData(SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x6E), 14));
        return id.toString() + ";";
    }

    private void serialData(byte[] readBuffer) {
        id = 0;
        id = ByteBuffer.wrap(Arrays.copyOfRange(readBuffer, readBuffer.length - 6, readBuffer.length - 2))
                .order(ByteOrder.LITTLE_ENDIAN).getInt();
        System.out.println(id);
    }

    private void loadAuditLogs() {
        auditLogMap = new HashMap<>();
        auditLogMap.put(1, "Meter Type");
        auditLogMap.put(2, "Meter Size");
        auditLogMap.put(4, "Rev/Unit Volume");
        auditLogMap.put(7, "Flow Sense");
        auditLogMap.put(8, "Temp Units");
        auditLogMap.put(9, "Base Temp");
        auditLogMap.put(10, "Temp Mode");
        auditLogMap.put(11, "Fixed Temp");
        auditLogMap.put(12, "Pressure Units");
        auditLogMap.put(13, "Base Pressure");
        auditLogMap.put(14, "Atmos. Pressure");
        auditLogMap.put(15, "Pressure Factor");
        auditLogMap.put(16, "Fixed Pressure");
        auditLogMap.put(17, "Pressure Mode");
        auditLogMap.put(18, "Supercomp");
        auditLogMap.put(19, "Cor Multiplier");
        auditLogMap.put(20, "Uncor Multiplier");
        auditLogMap.put(21, "Cor Pulse Output");
        auditLogMap.put(22, "Uncor Pulse Output");
        auditLogMap.put(30, "Telemetry Terminal 1 Output Selection");
        auditLogMap.put(31, "Telemetry Terminal 2 Output Selection");
        auditLogMap.put(32, "Telemetry Terminal 3 Output Selection");
        auditLogMap.put(33, "Output Terminal Mode");
        auditLogMap.put(34, "Maximum Daily Consumption");
        auditLogMap.put(35, "Digital Input Alarm State");
        auditLogMap.put(36, "Digital Output Alarm State");
        auditLogMap.put(37, "Daily Consumption Start Time");
        auditLogMap.put(39, "Pressure Monitor");
        auditLogMap.put(40, "Comms Device");
        auditLogMap.put(111, "Telemetry Pulse Width");
        auditLogMap.put(129, "Corrected Volume");
        auditLogMap.put(130, "Uncorrected Volume");
        auditLogMap.put(131, "Uncorrected Volume Under Fault");
        auditLogMap.put(132, "Date And Time");
        auditLogMap.put(133, "Output Pulses");
        auditLogMap.put(134, "Trim Table Enable");
        auditLogMap.put(135, "Trim Table Changed");
        auditLogMap.put(136, "Password");
        auditLogMap.put(137, "Advanced Password");
        auditLogMap.put(138, "Pressure Calculation Type");
        auditLogMap.put(139, "User Temp. Calibration Offset");
        auditLogMap.put(140, "User Temp. Calibration Span");
        auditLogMap.put(141, "Drive Rate");
        auditLogMap.put(142, "User Pres. Calibration Offset");
        auditLogMap.put(143, "User Pres. Calibration Span");
        auditLogMap.put(144, "HP Alarm Limit");
        auditLogMap.put(145, "LP Alarm Limit");
        auditLogMap.put(146, "HT Alarm Limit");
        auditLogMap.put(147, "LT Alarm Limit");
        auditLogMap.put(148, "HF Alarm Limit");
        auditLogMap.put(149, "LF Alarm Limit");
        auditLogMap.put(155, "Trim Table Type");
        auditLogMap.put(156, "User Pres Mon Cal Offset");
        auditLogMap.put(157, "User Pres Mon Cal Span");
    }

    private void maxBuffer() {
        byte[] salida = ((SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x44), 16)));
        for (byte b : salida) {
            System.out.printf("%02X ", b);
        }
        byte max = salida[8];
        maxBufferRead = Byte.toUnsignedInt(max);
    }

    private void getDataLogs(byte[] starAddressdataLogs, int countLogs, String nameLogs, String api) {
        if (countLogs > 2000) {
            return;
        }
        if (countLogs > 504) {
            byte[] startAddressOld = starAddressdataLogs;
            int logsWriter = countLogs - 504;
            for (int i = 0; i < logsWriter; i++) {
                starAddressdataLogs = calculateNextAdreesLogs(starAddressdataLogs, loggerList.size() * 4);
            }
            for (int i = logsWriter; i < 504; i++) {
                byte[] lastloggin = SerialHelper.stablishConnection(
                        SerialHelper.createDataRequestPacketAddress((byte) 0x72, starAddressdataLogs[0],
                                starAddressdataLogs[1],
                                sizeData),
                        78);
                for (byte b : lastloggin) {
                    System.out.printf("%02X ", b);
                }
                writerLogs.documentLog(
                        getId() + traslateAllData(Arrays.copyOfRange(lastloggin, 8, (loggerList.size() * 4) + 8)),
                        logsHeader, nameLogs);
                starAddressdataLogs = calculateNextAdreesLogs(starAddressdataLogs, loggerList.size() * 4);
            }
            for (int i = 1; i < logsWriter; i++) {
                byte[] lastloggin2 = SerialHelper.stablishConnection(
                        SerialHelper.createDataRequestPacketAddress((byte) 0x72, startAddressOld[0],
                                startAddressOld[1],
                                sizeData),
                        78);
                for (byte b : lastloggin2) {
                    System.out.printf("%02X ", b);
                }
                writerLogs.documentLog(
                        getId() + traslateAllData(Arrays.copyOfRange(lastloggin2, 8, (loggerList.size() * 4) + 8)),
                        logsHeader, nameLogs);
                startAddressOld = calculateNextAdreesLogs(startAddressOld, loggerList.size() * 4);
            }
            APIRest.sendDataFileLogs(nameLogs, api);
            return;
        }
        for (int i = 1; i < countLogs; i++) {
            byte[] lastloggin3 = SerialHelper.stablishConnection(
                    SerialHelper.createDataRequestPacketAddress((byte) 0x72, starAddressdataLogs[0],
                            starAddressdataLogs[1],
                            sizeData),
                    78);
            for (byte b : lastloggin3) {
                System.out.printf("%02X ", b);
            }
            writerLogs.documentLog(
                    getId() + traslateAllData(Arrays.copyOfRange(lastloggin3, 8, (loggerList.size() * 4) + 8)),
                    logsHeader, nameLogs);
            starAddressdataLogs = calculateNextAdreesLogs(starAddressdataLogs, loggerList.size() * 4);
        }
        APIRest.sendDataFileLogs(nameLogs, api);

    }

    public void firstLogsData() {
       getDataLogs(startAddressLogs1, ptrLog1, "/home/EPI5/.Smarts/logs1"+id+".csv", urlLog1);
        //getDataLogs(startAddressLogs1, ptrLog1, "logs1"+id+".csv", urlLog1);

    }

    public void secondLogsData() {
        getDataLogs(startAddressLogs2, ptrLog2, "/home/EPI5/.Smarts/logs2"+id+".csv", urlLog2);
       // getDataLogs(startAddressLogs2, ptrLog2, "logs2"+id+".csv", urlLog2);

    }

    public void thirdLogsData() {
        getDataLogs(startAddressLogs3, ptrLog3, "/home/EPI5/.Smarts/logs3"+id+".csv", urlLog3);
        //getDataLogs(startAddressLogs3, ptrLog3, "logs3"+id+".csv", urlLog3);

    }

    private void getLastLogs(byte[] starAddressdataLogs, int countLogs, String nameLogs, String api) {
        if (countLogs > 2000) {
            return;
        }
        if (countLogs > 504) {
            int logsWriter = countLogs - 504;
            for (int i = 2; i < logsWriter; i++) {
                starAddressdataLogs = calculateNextAdreesLogs(starAddressdataLogs, loggerList.size() * 4);
            }
            byte[] lastloggin = SerialHelper.stablishConnection(
                    SerialHelper.createDataRequestPacketAddress((byte) 0x72, starAddressdataLogs[0],
                            starAddressdataLogs[1],
                            sizeData),
                    78);
            for (byte b : lastloggin) {
                System.out.printf("%02X ", b);
            }
            writerLogs.documentLog(
                    getId() + traslateAllData(Arrays.copyOfRange(lastloggin, 8, (loggerList.size() * 4) + 8)),
                    logsHeader, nameLogs);
            APIRest.senDataApiLogs(toJsonData(), api);
            return;
        }
        for (int i = 1; i < countLogs; i++) {
            starAddressdataLogs = calculateNextAdreesLogs(starAddressdataLogs, loggerList.size() * 4);
        }
        byte[] lastloggin = SerialHelper.stablishConnection(
                SerialHelper.createDataRequestPacketAddress((byte) 0x72, starAddressdataLogs[0],
                        starAddressdataLogs[1],
                        sizeData),
                78);
        for (byte b : lastloggin) {
            System.out.printf("%02X ", b);
        }
        APIRest.senDataApiLogs(toJsonData(), api);

        writerLogs.documentLog(
                getId() + traslateAllData(Arrays.copyOfRange(lastloggin, 8, (loggerList.size() * 4) + 8)), logsHeader,
                nameLogs);
    }

    public void getLastlog1() {
        System.out.println("Lastlogs");
        getLastLogs(startAddressLogs1, ptrLog1, "/home/EPI5/.Smarts/logs1"+id+".csv", urlLog1Json);
       // getLastLogs(startAddressLogs1, ptrLog1, "logs1"+id+".csv", urlLog1Json);

    }

    public void getLastlog2() {
        System.out.println("Lastlogs2");

        getLastLogs(startAddressLogs2, ptrLog2, "/home/EPI5/.Smarts/logs2"+id+".csv",urlLog2Json);
        //getLastLogs(startAddressLogs2, ptrLog2, "logs2"+id+".csv", urlLog2Json);

    }

    public void getLastlog3() {
        System.out.println("Lastlogs3");
    getLastLogs(startAddressLogs3, ptrLog3, "/home/EPI5/.Smarts/logs3"+id+".csv",urlLog3Json);
       //getLastLogs(startAddressLogs3, ptrLog3, "logs3"+id+".csv", urlLog3Json);

    }

    private void getAlarmanLogs(String nameLogs, String api) {
        try {
            for (int i = 1; i < getPtrLogAlarm(); i++) {
                byte[] dataAlarm = SerialHelper.stablishConnection(
                        SerialHelper.createDataRequestPacketAddress((byte) 0x72, (byte) startAddressAlarm[0],
                                startAddressAlarm[1], (byte) 0x05),
                        20);
                for (byte b : dataAlarm) {
                    System.out.printf("%02X ", b);
                }
                String dataWriter;
                dataWriter = getId() + dataAndTime(
                        ByteBuffer.wrap(Arrays.copyOfRange(dataAlarm, 8, 12)).order(ByteOrder.LITTLE_ENDIAN).getInt())
                        + ";"
                        + traslateAlarmLogs(dataAlarm[12]) + ";";
                JsonAlarm = dataWriter;
                System.out.println(traslateAlarmLogs(dataAlarm[12]));
                startAddressAlarm = calculateNextAdreesLogs(startAddressAlarm, 5);
                writerLogs.documentLog(dataWriter,
                        alarmHeader, nameLogs);
            }
            APIRest.sendDataFileLogs(nameLogs, api);
        } catch (Exception e) {
        }
    }

    public void getLastAlarmLogs() {
        try {
            for (int i = 1; i < getPtrLogAlarm(); i++) {
                startAddressAlarm = calculateNextAdreesLogs(startAddressAlarm, 5);
            }
            byte[] dataAlarm = SerialHelper.stablishConnection(
                    SerialHelper.createDataRequestPacketAddress((byte) 0x72, (byte) startAddressAlarm[0],
                            startAddressAlarm[1], (byte) 0x05),
                    20);
            for (byte b : dataAlarm) {
                System.out.printf("%02X ", b);
            }
            String dataWriter;
            dataWriter = getId() + dataAndTime(
                    ByteBuffer.wrap(Arrays.copyOfRange(dataAlarm, 8, 12)).order(ByteOrder.LITTLE_ENDIAN).getInt()) + ";"
                    + traslateAlarmLogs(dataAlarm[12]) + ";";
            System.out.println(traslateAlarmLogs(dataAlarm[12]));
            JsonAlarm = dataWriter;
            writerLogs.documentLog(dataWriter,
                    alarmHeader, "/home/EPI5/.Smarts/AlarmanLogs"+id+".csv");
            APIRest.senDataApiLogs(toJsonAlarm(), urlAlarmJson);

        } catch (Exception e) {
        }

    }

    public void getLastFaultLogs() {
        try {
            for (int i = 1; i < getPtrLogFault(); i++) {
                startAddressFaultLog = calculateNextAdreesLogs(startAddressFaultLog, 5);
            }
            byte[] dataFault = SerialHelper.stablishConnection(
                    SerialHelper.createDataRequestPacketAddress((byte) 0x72, (byte) startAddressFaultLog[0],
                            startAddressFaultLog[1], (byte) 0x05),
                    20);
            for (byte b : dataFault) {
                System.out.printf("%02X ", b);
            }
            String dataWriter;
            dataWriter = getId() + dataAndTime(
                    ByteBuffer.wrap(Arrays.copyOfRange(dataFault, 8, 12)).order(ByteOrder.LITTLE_ENDIAN).getInt()) + ";"
                    + traslateFaultLogs(dataFault[12]) + ";";
            System.out.println(traslateFaultLogs(dataFault[12]));
            writerLogs.documentLog(dataWriter,
                    faultHeader, "/home/EPI5/.Smarts/faultLogs"+id+".csv");
            JsonFault = dataWriter;
            APIRest.senDataApiLogs(toJsonFault(), urlFaultJson);
        } catch (Exception e) {
        }
    }

    private void getFaultLogs(String nameLogs, String api) {
        try {
            for (int i = 1; i < getPtrLogFault(); i++) {
                byte[] dataFault = SerialHelper.stablishConnection(
                        SerialHelper.createDataRequestPacketAddress((byte) 0x72, (byte) startAddressFaultLog[0],
                                startAddressFaultLog[1], (byte) 0x05),
                        20);
                for (byte b : dataFault) {
                    System.out.printf("%02X ", b);
                }
                String dataWriter;
                dataWriter = getId() + dataAndTime(
                        ByteBuffer.wrap(Arrays.copyOfRange(dataFault, 8, 12)).order(ByteOrder.LITTLE_ENDIAN).getInt())
                        + ";"
                        + traslateFaultLogs(dataFault[12]) + ";";
                JsonFault = dataWriter;
                startAddressFaultLog = calculateNextAdreesLogs(startAddressFaultLog, 5);
                writerLogs.documentLog(dataWriter,
                        faultHeader, nameLogs);
            }
            APIRest.sendDataFileLogs(nameLogs, api);

        } catch (Exception e) {
        }

    }

    private String traslateFaultLogs(byte data) {
        byte[] datanew = new byte[2];
        datanew[0] = data;
        datanew[1] = 0x00;
        char[] dataBinary = Integer.toBinaryString(ByteBuffer.wrap(datanew).order(ByteOrder.LITTLE_ENDIAN).getShort())
                .toCharArray();
        char[] dataBinary2 = new char[dataBinary.length];
        for (int i = dataBinary.length - 1; i >= 0; i--) {
            dataBinary2[dataBinary.length - 1 - i] = dataBinary[i];
        }
        if (dataBinary2.length > 0) {
            for (int i = 0; i < dataBinary2.length; i++) {
                if (dataBinary2[i] == '1') {
                    System.out.println(i);
                    return switch (i) {
                        case 0 ->
                            "Pressure Meansurement Fault";
                        case 1 ->
                            "Pressure MOnitor Fault";
                        case 2 ->
                            "Temperature Fault";
                        case 3 ->
                            "Prover Mode Started";
                        case 4 ->
                            "Prover Mode Stopped";
                        case 5 ->
                            "Volume input fault";
                        case 6 ->
                            "Internal operations fault";
                        case 7 ->
                            "Low battery";
                        case 8 ->
                            "Meter Over-Speed Fault";
                        case 9 ->
                            "Meter Fault";
                        case 10 ->
                            "Meter Lockup";
                        case 11 ->
                            "DP Zero Fault";
                        default ->
                            "Fault";
                    };
                }
            }
            return "Fault register Cleared";
        }
        return "null";
    }

    private String traslateAlarmLogs(byte data) {
        byte[] datanew = new byte[2];
        datanew[0] = data;
        datanew[1] = 0x00;
        char[] dataBinary = Integer.toBinaryString(ByteBuffer.wrap(datanew).order(ByteOrder.LITTLE_ENDIAN).getShort())
                .toCharArray();
        char[] dataBinary2 = new char[dataBinary.length];
        for (int i = dataBinary.length - 1; i >= 0; i--) {
            dataBinary2[dataBinary.length - 1 - i] = dataBinary[i];
        }
        if (dataBinary2.length > 0) {
            for (int i = 0; i < dataBinary2.length; i++) {
                if (dataBinary2[i] == '1') {
                    System.out.println(i);
                    return switch (i) {
                        case 0 ->
                            "High Pressure";
                        case 1 ->
                            "Low Pressure";
                        case 2 ->
                            "High Temperature";
                        case 3 ->
                            "Low Temperature";
                        case 4 ->
                            "High Flow Rate";
                        case 5 ->
                            "Low Flow Rate";
                        case 6 ->
                            "Consumption";
                        case 7 ->
                            "Digital Input";
                        case 8 ->
                            "Meter Alarm";
                        case 9 ->
                            "High Pressure2";
                        case 10 ->
                            "Low Pressure2";
                        case 11 ->
                            "High Temperature Meter Alarm";
                        case 12 ->
                            "Low Temperature Meter Alarm";
                        default ->
                            "Alarm";
                    };
                }
            }
            return "Alarm register Cleared";
        }
        return "";
    }

    private void getAuditLogs(String nameLogs, String api) {
        startAddressAudit = startAddressLogginArea;
        byte[] auditLogs = SerialHelper.stablishConnection(
                SerialHelper.createDataRequestPacketAddress((byte) 0x72, startAddressLogginArea[0],
                        startAddressLogginArea[1], (byte) 0x01),
                14);
        for (byte b : auditLogs) {
            System.out.printf("%02X ", b);
        }
        maxAuditLogs = (int) auditLogs[8];
        System.out.println(maxAuditLogs + "maxAuditLogs");
        if (maxAuditLogs > 0) {
            try {
                startAddressAudit = calculateNextAdreesLogs(startAddressAudit, 5);
                byte[] firstAudit = SerialHelper.stablishConnection(
                        SerialHelper.createDataRequestPacketAddress((byte) 0x72, startAddressAudit[0],
                                startAddressAudit[1], (byte) 0x0D),
                        30);
                for (byte b : firstAudit) {
                    System.out.printf("%02X ", b);
                }
                writerLogs.documentLog(traslateDataAuditlog(firstAudit),
                        auditHeader, "AuditLogs");
                for (int i = 0; i < maxAuditLogs - 1; i++) {
                    byte[] auditlogs = SerialHelper.stablishConnection(
                            SerialHelper.createDataRequestPacketAddress((byte) 0x72, startAddressAudit[0],
                                    startAddressAudit[1], (byte) 0x0D),
                            30);
                    for (byte b : auditlogs) {
                        System.out.printf("%02X ", b);
                    }
                    writerLogs.documentLog(traslateDataAuditlog(auditlogs),
                            auditHeader, nameLogs);

                    startAddressAudit = calculateNextAdreesLogs(startAddressAudit, 13);
                }
            } catch (Exception e) {
            }
            APIRest.sendDataFileLogs(nameLogs, api);
        }

    }

    public void readLogginSettings() {
        byte[] logginSettings = SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x69), 90);
        for (byte b : logginSettings) {
            System.out.printf("%02X ", b);
        }
        byte[] logginSettings2 = SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x2D), 90);
        for (byte b : logginSettings2) {
            System.out.printf("%02X ", b);
        }
        setStartAddressLogginArea(Arrays.copyOfRange(logginSettings, 8, 10));
        setEndAddressLogginArea(Arrays.copyOfRange(logginSettings, 10, 12));
        setLogginParameters(Arrays.copyOfRange(logginSettings, 12, 14));
        setLoggedParameters(Arrays.copyOfRange(logginSettings, 14, 16));
        setStartAddressLogs1(Arrays.copyOfRange(logginSettings2, 9, 13));
        setMaxLogs1(Arrays.copyOfRange(logginSettings, 18, 20));
        setTimeLog1(Arrays.copyOfRange(logginSettings, 20, 29));
        setLogginTerminalTime(Arrays.copyOfRange(logginSettings, 21, 25));
        setPtrLog1(Arrays.copyOfRange(logginSettings, 29, 33));
        setStartAddressLogs2(Arrays.copyOfRange(logginSettings2, 13, 17));
        setMaxLogs2(Arrays.copyOfRange(logginSettings, 35, 37));
        setTimeLog2(Arrays.copyOfRange(logginSettings, 37, 46));
        setLoggin2TerminalTime(Arrays.copyOfRange(logginSettings, 38, 42));
        setPtrLog2(Arrays.copyOfRange(logginSettings, 46, 50));
        setStartAddressLogs3(Arrays.copyOfRange(logginSettings2, 17, 21));
        setMaxLogs3(Arrays.copyOfRange(logginSettings, 52, 54));
        setLoggin3TerminalTime(Arrays.copyOfRange(logginSettings, 55, 59));
        setTimeLog3(Arrays.copyOfRange(logginSettings, 54, 63));
        setPtrLog3(Arrays.copyOfRange(logginSettings, 63, 67));
        setStartAddressFaultLog(Arrays.copyOfRange(logginSettings, 67, 69));
        setMaxLogsFault(Arrays.copyOfRange(logginSettings, 69, 70));
        setPtrLogFault(Arrays.copyOfRange(logginSettings, 70, 74));
        setStartAddressAlarm(Arrays.copyOfRange(logginSettings, 74, 76));
        setMaxLogsAlarm(Arrays.copyOfRange(logginSettings, 76, 77));
        setPtrLogAlarm(Arrays.copyOfRange(logginSettings, 77, 81));
        setSettingsLoggedParameters();
    }

    private void setSettingsLoggedParameters() {
        loggerList = new ArrayList<>();
        char[] binary = Integer.toBinaryString(getLoggedParameters()).toCharArray();
        System.out.println(Integer.toBinaryString(getLogginParameters()));
        System.out.println(Integer.toBinaryString(getLoggedParameters()));
        for (int i = 0; i < binary.length; i++) {
            if (binary[i] == '1') {
                if (!(parametersBytes[i].isEmpty())) {
                    loggerList.add(parametersBytes[i]);
                }
            }
        }
        int i = loggerList.size() * 4;
        System.out.println(i);
        sizeData = (byte) i;
    }

    private String traslateAllData(byte[] data) {
        mapLogger = new HashMap<>();
        int contador = 0;
        for (int i = 0; i < loggerList.size(); i++) {
            try {
                if (loggerList.get(i).contains("Corrected_Volume") || loggerList.get(i).contains("Uncorrected_Volume")
                        || loggerList.get(i).contains("Uncorrected_Volume_Under_Fault")) {
                    System.out.println(loggerList.get(i) + " "
                            + translateDataInt(Arrays.copyOfRange(data, contador, contador + 4)).toString());
                    mapLogger.put(loggerList.get(i),
                            translateDataInt(Arrays.copyOfRange(data, contador, contador + 4)).toString());
                } else if (loggerList.get(i).contains("Date_and_Time")) {
                    System.out.println(loggerList.get(i) + " "
                            + dataAndTime(translateDataInt(Arrays.copyOfRange(data, contador, contador + 4))));
                    mapLogger.put(loggerList.get(i),
                            dataAndTime(translateDataInt(Arrays.copyOfRange(data, contador, contador + 4))));
                } else {
                    mapLogger.put(loggerList.get(i),
                            translateDataFloat(Arrays.copyOfRange(data, contador, contador + 4)).toString());
                    System.out.println(loggerList.get(i) + " "
                            + translateDataFloat(Arrays.copyOfRange(data, contador, contador + 4)).toString());
                }
                contador = contador + 4;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return toStringData();
    }

    private String toStringData() {
        String line = "";
        for (int i = 0; i < parametersBytes.length; i++) {
            try {
                if (!(mapLogger.get(parametersBytes[i]).toLowerCase().contains("null"))) {
                    line = line + mapLogger.get(parametersBytes[i]) + ";";
                }
            } catch (Exception e) {
                line = line + "NO DATA;";
            }
        }
        return line;
    }

    private String dataAndTime(Integer seconds) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(946684800 + seconds, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateTime.format(formatter);
        return formattedDate;
    }

    private int getTime(byte[] time) {
        int timetoSleep;
        try {
            switch (time[0]) {
                case (byte) 0x00 -> {
                    timetoSleep = ByteBuffer.wrap(Arrays.copyOfRange(time, 1, 5)).order(ByteOrder.LITTLE_ENDIAN)
                            .getInt();
                    for (byte b : time) {
                        System.out.printf("%02X ", b);
                    }
                }
                case (byte) 0x01 -> {
                    timetoSleep = ByteBuffer.wrap(Arrays.copyOfRange(time, 1, 5)).order(ByteOrder.LITTLE_ENDIAN)
                            .getInt();
                }
                case (byte) 0x02 -> {
                    timetoSleep = ByteBuffer.wrap(Arrays.copyOfRange(time, 1, 5)).order(ByteOrder.LITTLE_ENDIAN)
                            .getInt();
                    int timeoffset = (ByteBuffer.wrap(Arrays.copyOfRange(time, 5, 9)).order(ByteOrder.LITTLE_ENDIAN)
                            .getInt());
                    Calendar calendar = Calendar.getInstance();
                    int i = calendar.get(Calendar.HOUR_OF_DAY);
                    int h = calendar.get(Calendar.MINUTE);
                    int seconds = i * 3600 + h * 60;
                    timetoSleep = timeoffset - seconds + 86400;
                }
                case (byte) 0x03 -> {
                    timetoSleep = 2592000;
                }
                default ->
                    timetoSleep = 40;
            }
        } catch (Exception e) {
            System.out.println(e);
            timetoSleep = 60;
        }
        return timetoSleep * 1000;
    }

    private byte[] calculateNextAdreesLogs(byte[] starAddress, int lenght) {
        Integer numstartAddress = (int) ByteBuffer.wrap(starAddress).order(ByteOrder.LITTLE_ENDIAN).getShort();
        Integer nextAddres = numstartAddress + lenght;
        byte byte1 = (byte) (nextAddres >> 8);
        byte byte2 = (byte) (nextAddres & 0xFF);
        byte[] actuallyAdress = new byte[2];
        actuallyAdress[1] = byte1;
        actuallyAdress[0] = byte2;
        return actuallyAdress;
    }

    private byte[] calculateNextExtendAdresLogs(byte[] starAddress, int lenght) {
        if (starAddress.length > 3) {
            Integer numstartAddress = (int) ByteBuffer.wrap(starAddress).order(ByteOrder.LITTLE_ENDIAN).getInt();
            Integer nextAddres = numstartAddress + lenght;
            byte byte1 = (byte) (nextAddres >> 8);
            byte byte2 = (byte) (nextAddres & 0xFF);
            byte[] actuallyAdress = new byte[2];
            actuallyAdress[1] = byte1;
            actuallyAdress[0] = byte2;
            return actuallyAdress;
        }
        return calculateNextAdreesLogs(starAddress, lenght);
    }

    private String traslateDataAuditlog(byte[] dataTransferOld) {
        linenew = "";
        linenold = "";
        String parameter = "";
        Integer timechange = 0;
        if (dataTransferOld.length > 12) {
            byte[] typeData = new byte[2];
            typeData[0] = dataTransferOld[12];
            parameter = auditLogMap
                    .get((int) ByteBuffer.wrap(typeData).order(ByteOrder.LITTLE_ENDIAN).getShort());
            System.out.println(parameter);
            typeData[1] = 0x00;
            System.out.println((int) ByteBuffer.wrap(typeData).order(ByteOrder.LITTLE_ENDIAN).getShort());
            byte[] dataTransfer = Arrays.copyOfRange(dataTransferOld, 8, dataTransferOld.length);
            timechange = ByteBuffer.wrap(Arrays.copyOfRange(dataTransfer, 0, 4)).order(ByteOrder.LITTLE_ENDIAN)
                    .getInt();
            switch (parameter) {
                case "Meter Type" -> {
                    switch (dataTransfer[5]) {
                        case (byte) 0x00 ->
                            linenold = "Series A";
                        case (byte) 0x01 ->
                            linenold = "Series B";
                        case (byte) 0x02 ->
                            linenold = "Series A Metric";
                        case (byte) 0x03 ->
                            linenold = "Series B Metric";
                        case (byte) 0x04 ->
                            linenold = "Series G";
                        case (byte) 0x05 ->
                            linenold = "Romet Imperial";
                        case (byte) 0x06 ->
                            linenold = "Romet Metric";
                    }
                    switch (dataTransfer[10]) {
                        case (byte) 0x00 ->
                            linenew = "Flow sense";
                        case (byte) 0x01 ->
                            linenew = "Series B";
                        case (byte) 0x02 ->
                            linenew = "Series A Metric";
                        case (byte) 0x03 ->
                            linenew = "Series B Metric";
                        case (byte) 0x04 ->
                            linenew = "Series G";
                        case (byte) 0x05 ->
                            linenew = "Romet Imperial";
                        case (byte) 0x06 ->
                            linenew = "Romet Metric";
                    }
                }
                case "Meter Size" -> {
                    switch (dataTransfer[5]) {
                        case (byte) 0x00 ->
                            linenold = "Series A";
                        case (byte) 0x01 ->
                            linenold = "Series B";
                        case (byte) 0x02 ->
                            linenold = "Series A Metric";
                        case (byte) 0x03 ->
                            linenold = "Series B Metric";
                        case (byte) 0x04 ->
                            linenold = "Series G";
                        case (byte) 0x05 ->
                            linenold = "Romet Imperial";
                        case (byte) 0x06 ->
                            linenold = "Romet Metric";
                    }
                    switch (dataTransfer[10]) {
                        case (byte) 0x00 ->
                            linenew = "Flow sense";
                        case (byte) 0x01 ->
                            linenew = "Series B";
                        case (byte) 0x02 ->
                            linenew = "Series A Metric";
                        case (byte) 0x03 ->
                            linenew = "Series B Metric";
                        case (byte) 0x04 ->
                            linenew = "Series G";
                        case (byte) 0x05 ->
                            linenew = "Romet Imperial";
                        case (byte) 0x06 ->
                            linenew = "Romet Metric";
                    }
                    linenold = linenold + meterTypeAndSize[(int) dataTransfer[6]][(int) dataTransfer[5]];
                    linenew = linenew + meterTypeAndSize[(int) dataTransfer[11]][(int) dataTransfer[10]];
                }
                case "Rev/Unit Volume" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Flow Sense" -> {
                    switch (dataTransfer[5]) {
                        case (byte) 0x00 ->
                            linenold = "Flow sense";
                        case (byte) 0x01 ->
                            linenold = "Forward-Reverse";
                        case (byte) 0x02 ->
                            linenold = "Reverse-Forward";
                        case (byte) 0x03 ->
                            linenold = "Reverse";
                        case (byte) 0x04 ->
                            linenold = "Forward";
                        case (byte) 0x05 ->
                            linenold = "Forward+reverse";
                    }
                    switch (dataTransfer[10]) {
                        case (byte) 0x00 ->
                            linenew = "Flow sense";
                        case (byte) 0x01 ->
                            linenew = "Forward-Reverse";
                        case (byte) 0x02 ->
                            linenew = "Reverse-Forward";
                        case (byte) 0x03 ->
                            linenew = "Reverse";
                        case (byte) 0x04 ->
                            linenew = "Forward";
                        case (byte) 0x05 ->
                            linenew = "Forward+reverse";
                    }
                }
                case "Temp Units" -> {
                    linenold = (dataTransfer[5] == (byte) 0x01) ? "C" : "F";
                    linenew = (dataTransfer[10] == (byte) 0x01) ? "C" : "F";
                }
                case "Base Temp" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Temp Mode" -> {
                    linenold = (dataTransfer[5] == (byte) 0x01) ? "Live" : "Fixed";
                    linenew = (dataTransfer[10] == (byte) 0x01) ? "Live" : "Fixed";
                }
                case "Fixed Temp" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Pressure Units" -> {
                    switch (dataTransfer[5]) {
                        case (byte) 0x00 ->
                            linenold = "Pressure Units";
                        case (byte) 0x01 ->
                            linenold = "Bar";
                        case (byte) 0x02 ->
                            linenold = "PSI";
                        case (byte) 0x03 ->
                            linenold = "Other";
                    }
                    switch (dataTransfer[10]) {
                        case (byte) 0x00 ->
                            linenew = "Pressure Units";
                        case (byte) 0x01 ->
                            linenew = "Bar";
                        case (byte) 0x02 ->
                            linenew = "PSI";
                        case (byte) 0x03 ->
                            linenew = "Other";
                    }
                }
                case "Base Pressure" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Atmos. Pressure" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Pressure Factor" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Fixed Pressure" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Pressure Mode" -> {
                    linenold = (dataTransfer[5] == (byte) 0x01) ? "Live" : "Fixed";
                    linenew = (dataTransfer[10] == (byte) 0x01) ? "Live" : "Fixed";
                }
                case "Supercomp" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Cor Multiplier" -> {
                    linenold = getDataInt(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataInt(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Uncor Multiplier" -> {
                    linenold = getDataInt(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataInt(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Cor Pulse Output" -> {
                    switch (dataTransfer[5]) {
                        case (byte) 0x00 ->
                            linenold = linenold + "0.1";
                        case (byte) 0x01 ->
                            linenold = linenold + "1";
                        case (byte) 0x02 ->
                            linenold = linenold + "10";
                        case (byte) 0x03 ->
                            linenold = linenold + "1000";
                        default ->
                            linenold = linenold + (int) dataTransfer[5];

                    }
                    switch (dataTransfer[10]) {
                        case (byte) 0x00 ->
                            linenew = linenew + "0.1";
                        case (byte) 0x01 ->
                            linenew = linenew + "1";
                        case (byte) 0x02 ->
                            linenew = linenew + "10";
                        case (byte) 0x03 ->
                            linenew = linenew + "100";
                        default ->
                            linenew = linenew + (int) dataTransfer[10];

                    }
                    switch (dataTransfer[6]) {
                        case (byte) 0x01 ->
                            linenold = linenold + " m3";
                        case (byte) 0x02 ->
                            linenold = linenold + " ft3";
                    }
                    switch (dataTransfer[11]) {
                        case (byte) 0x01 ->
                            linenew = linenew + " m3";
                        case (byte) 0x02 ->
                            linenew = linenew + " ft3";
                        default ->
                            linenew = linenew + "m3";
                    }
                }
                case "Uncor Pulse Output" -> {
                    switch (dataTransfer[5]) {
                        case (byte) 0x00 ->
                            linenold = linenold + "0.1";
                        case (byte) 0x01 ->
                            linenold = linenold + "1";
                        case (byte) 0x02 ->
                            linenold = linenold + "10";
                        case (byte) 0x03 ->
                            linenold = linenold + "1000";
                        default ->
                            linenold = linenold + (int) dataTransfer[5];

                    }
                    switch (dataTransfer[10]) {
                        case (byte) 0x00 ->
                            linenew = linenew + "0.1";
                        case (byte) 0x01 ->
                            linenew = linenew + "1";
                        case (byte) 0x02 ->
                            linenew = linenew + "10";
                        case (byte) 0x03 ->
                            linenew = linenew + "100";
                        default ->
                            linenew = linenew + (int) dataTransfer[10];

                    }
                    switch (dataTransfer[6]) {
                        case (byte) 0x01 ->
                            linenold = linenold + " m3";
                        case (byte) 0x02 ->
                            linenold = linenold + " ft3";
                    }
                    switch (dataTransfer[11]) {
                        case (byte) 0x01 ->
                            linenew = linenew + " m3";
                        case (byte) 0x02 ->
                            linenew = linenew + " ft3";
                    }
                }
                case "Telemetry Terminal 1 Output Selection" -> {
                    char[] uncorpulseold = Integer
                            .toBinaryString(ByteBuffer.wrap(Arrays.copyOfRange(dataTransfer, 5, 9)).getInt())
                            .toCharArray();
                    char[] uncorpulsnew = Integer
                            .toBinaryString(ByteBuffer.wrap(Arrays.copyOfRange(dataTransfer, 9, 13)).getInt())
                            .toCharArray();
                    for (int i = 0; i < 3; i++) {
                        if (uncorpulseold[i] == '1' && i == 0) {
                            linenold = "Uncorrected";
                        } else if (uncorpulseold[i] == '1' && i == 1) {
                            linenold = "Corrected";
                        } else if (uncorpulseold[i] == '1' && i == 2) {
                            linenold = "Fault";
                        }
                    }
                    for (int i = 0; i < 3; i++) {
                        if (uncorpulsnew[i] == '1' && i == 0) {
                            linenew = "Uncorrected";
                        } else if (uncorpulsnew[i] == '1' && i == 1) {
                            linenew = "Corrected";
                        } else if (uncorpulsnew[i] == '1' && i == 2) {
                            linenew = "Fault";
                        }
                    }
                }
                case "Telemetry Terminal 2 Output Selection" -> {
                    char[] uncorpulseold = Integer
                            .toBinaryString(ByteBuffer.wrap(Arrays.copyOfRange(dataTransfer, 5, 9)).getInt())
                            .toCharArray();
                    char[] uncorpulsnew = Integer
                            .toBinaryString(ByteBuffer.wrap(Arrays.copyOfRange(dataTransfer, 9, 13)).getInt())
                            .toCharArray();
                    for (int i = 0; i < 3; i++) {
                        if (uncorpulseold[i] == '1' && i == 0) {
                            linenold = "Uncorrected";
                        } else if (uncorpulseold[i] == '1' && i == 1) {
                            linenold = "Corrected";
                        } else if (uncorpulseold[i] == '1' && i == 2) {
                            linenold = "Fault";
                        }
                    }
                    for (int i = 0; i < 3; i++) {
                        if (uncorpulsnew[i] == '1' && i == 0) {
                            linenew = "Uncorrected";
                        } else if (uncorpulsnew[i] == '1' && i == 1) {
                            linenew = "Corrected";
                        } else if (uncorpulsnew[i] == '1' && i == 2) {
                            linenew = "Fault";
                        }
                    }
                }
                case "Telemetry Terminal 3 Output Selection" -> {
                    char[] uncorpulseold = Integer
                            .toBinaryString(ByteBuffer.wrap(Arrays.copyOfRange(dataTransfer, 5, 9)).getInt())
                            .toCharArray();
                    char[] uncorpulsnew = Integer
                            .toBinaryString(ByteBuffer.wrap(Arrays.copyOfRange(dataTransfer, 9, 13)).getInt())
                            .toCharArray();
                    for (int i = 0; i < 3; i++) {
                        if (uncorpulseold[i] == '1' && i == 0) {
                            linenold = "Uncorrected";
                        } else if (uncorpulseold[i] == '1' && i == 1) {
                            linenold = "Corrected";
                        } else if (uncorpulseold[i] == '1' && i == 2) {
                            linenold = "Fault";
                        }
                    }
                    for (int i = 0; i < 3; i++) {
                        if (uncorpulsnew[i] == '1' && i == 0) {
                            linenew = "Uncorrected";
                        } else if (uncorpulsnew[i] == '1' && i == 1) {
                            linenew = "Corrected";
                        } else if (uncorpulsnew[i] == '1' && i == 2) {
                            linenew = "Fault";
                        }
                    }
                }
                case "Output Terminal Mode" -> {
                    switch (dataTransfer[5]) {
                        case (byte) 0x00 ->
                            linenold = "Disabled";
                        case (byte) 0x01 ->
                            linenold = "Consumption Alarm";
                        case (byte) 0x02 ->
                            linenold = "Not Used";
                        case (byte) 0x03 ->
                            linenold = "Digital alarm Input";
                        case (byte) 0x04 ->
                            linenold = "Tamper Alarm Input";

                    }
                    switch (dataTransfer[10]) {
                        case (byte) 0x00 ->
                            linenew = "Disabled";
                        case (byte) 0x01 ->
                            linenew = "Consumption Alarm";
                        case (byte) 0x02 ->
                            linenew = "Not Used";
                        case (byte) 0x03 ->
                            linenew = "Digital alarm Input";
                        case (byte) 0x04 ->
                            linenew = "Tamper Alarm Input";
                    }
                }
                case "Maximum Daily Consumption" -> {
                    linenold = getDataInt(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataInt(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Digital Input Alarm State" -> {
                    linenold = (dataTransfer[5] == (byte) 0x00) ? "Alarm State Low" : "Alarm State High";
                    linenew = (dataTransfer[10] == (byte) 0x00) ? "Alarm State Low" : "Alarm State High";
                }
                case "Digital Output Alarm State" -> {
                    linenold = (dataTransfer[5] == (byte) 0x00) ? "Output Low During Alarm"
                            : "Output High during Alarm";
                    linenew = (dataTransfer[10] == (byte) 0x00) ? "Output Low During Alarm"
                            : "Output High during Alarm";
                }
                case "Daily Consumption Start Time" -> {
                    linenold = getDataInt(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataInt(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Pressure Monitor" -> {
                    linenold = (dataTransfer[5] == (byte) 0x00) ? "Disabled" : "Enabled";
                    linenew = (dataTransfer[10] == (byte) 0x00) ? "Disabled" : "Enabled";
                }
                case "Comms Device" -> {
                    linenold = (dataTransfer[5] == (byte) 0x00) ? "Modem" : "Printer";
                    linenew = (dataTransfer[10] == (byte) 0x00) ? "Modem" : "Printer";
                }
                case "Telemetry Pulse Width" -> {
                    Float old = ByteBuffer.wrap(Arrays.copyOfRange(dataTransfer, 5, 9)).getInt() * 62.5f;
                    Float newdata = ByteBuffer.wrap(Arrays.copyOfRange(dataTransfer, 9, 13)).getInt() * 62.5f;
                    linenold = old.toString() + "ms";
                    linenew = newdata.toString() + "ms";
                }
                case "Corrected Volume" -> {
                    linenold = getDataInt(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataInt(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Uncorrected Volume" -> {
                    linenold = getDataInt(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataInt(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Uncorrected Volume Under Fault" -> {
                    linenold = getDataInt(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataInt(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Date And Time" -> {
                    linenold = dataAndTime(ByteBuffer.wrap(Arrays.copyOfRange(dataTransfer, 5, 9))
                            .order(ByteOrder.LITTLE_ENDIAN).getInt());
                    linenew = dataAndTime(ByteBuffer.wrap(Arrays.copyOfRange(dataTransfer, 9, 13))
                            .order(ByteOrder.LITTLE_ENDIAN).getInt());
                }
                case "Output Pulses" -> {
                    linenold = "Not used";
                    linenew = "Not used";
                }
                case "Trim Table Enable" -> {
                    linenold = (dataTransfer[5] == (byte) 0x00) ? "Disabled" : "Enabled";
                    linenew = (dataTransfer[10] == (byte) 0x00) ? "Disabled" : "Enabled";
                }
                case "Trim Table Changed" -> {
                    linenold = "Not used";
                    linenew = "Not used";
                }
                case "Password" -> {
                    linenold = "Not used";
                    linenew = "Not used";
                }
                case "Advanced Password" -> {
                    linenold = "Not used";
                    linenew = "Not used";
                }
                case "Pressure Calculation Type" -> {
                    linenold = (dataTransfer[5] == (byte) 0x00) ? "Gauge" : "Absolute";
                    linenew = (dataTransfer[10] == (byte) 0x00) ? "Gauge" : "Absolute";
                }
                case "User Temp. Calibration Offset" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "User Temp. Calibration Span" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "Drive Rate" -> {
                    if (dataTransfer[6] == 0x01) {
                        switch (dataTransfer[5]) {
                            case (byte) 0x00 ->
                                linenold = "0.1 m3";
                            case (byte) 0x01 ->
                                linenold = "1 m3";
                            case (byte) 0x02 ->
                                linenold = "2 m3";
                            case (byte) 0x03 ->
                                linenold = "5 m3";
                            case (byte) 0x04 ->
                                linenold = "10 m3";
                            case (byte) 0x05 ->
                                linenold = "100 m3";
                            case (byte) 0x06 ->
                                linenold = "Manual";
                        }
                    } else if (dataTransfer[6] == 0x02) {
                        switch (dataTransfer[5]) {
                            case (byte) 0x00 ->
                                linenold = "1 ft3";
                            case (byte) 0x01 ->
                                linenold = "2 ft3";
                            case (byte) 0x02 ->
                                linenold = "5 ft3";
                            case (byte) 0x03 ->
                                linenold = "10 ft3";
                            case (byte) 0x04 ->
                                linenold = "100 ft3";
                            case (byte) 0x05 ->
                                linenold = "1000 ft3";
                            case (byte) 0x06 ->
                                linenold = "Manual";
                        }
                    }
                    if (dataTransfer[10] == 0x01) {
                        switch (dataTransfer[9]) {
                            case (byte) 0x00 ->
                                linenew = "0.1 m3";
                            case (byte) 0x01 ->
                                linenew = "1 m3";
                            case (byte) 0x02 ->
                                linenew = "2 m3";
                            case (byte) 0x03 ->
                                linenew = "5 m3";
                            case (byte) 0x04 ->
                                linenew = "10 m3";
                            case (byte) 0x05 ->
                                linenew = "100 m3";
                            case (byte) 0x06 ->
                                linenew = "Manual";
                        }
                    } else if (dataTransfer[10] == 0x02) {
                        switch (dataTransfer[9]) {
                            case (byte) 0x00 ->
                                linenew = "1 ft3";
                            case (byte) 0x01 ->
                                linenew = "2 ft3";
                            case (byte) 0x02 ->
                                linenew = "5 ft3";
                            case (byte) 0x03 ->
                                linenew = "10 ft3";
                            case (byte) 0x04 ->
                                linenew = "100 ft3";
                            case (byte) 0x05 ->
                                linenew = "1000 ft3";
                            case (byte) 0x06 ->
                                linenew = "Manual";
                        }
                    }

                }
                case "User Pres. Calibration Offset" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "User Pres. Calibration Span" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "HP Alarm Limit" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "LP Alarm Limit" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "HT Alarm Limit" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "LT Alarm Limit" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "HF Alarm Limit" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "LF Alarm Limit" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                // -------------------------->
                case "Trim Table Type" -> {
                    linenold = (dataTransfer[5] == (byte) 0x00) ? "Factory default" : "User Defined";
                    linenew = (dataTransfer[10] == (byte) 0x00) ? "Factory default" : "User Defined";
                }
                case "User Pres Mon Cal Offset" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                case "User Pres Mon Cal Span" -> {
                    linenold = getDataFLoat(Arrays.copyOfRange(dataTransfer, 5, 9));
                    linenew = getDataFLoat(Arrays.copyOfRange(dataTransfer, 9, 13));
                }
                default -> {
                    System.out.println("Valor no encontrado.");
                }
            }
        }
        JsonLogsData = getId() + dataAndTime(timechange) + ";" + parameter + ";" + linenold + ";" + linenew + ";";
        return JsonLogsData;
    }

    private String getDataFLoat(byte[] dataTransfer) {
        Float data = ByteBuffer.wrap(dataTransfer).order(ByteOrder.LITTLE_ENDIAN)
                .getFloat();
        return data.toString();
    }

    private String getDataInt(byte[] dataTransfer) {
        Integer data = ByteBuffer.wrap(dataTransfer).order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
        return data.toString();
    }

    private Integer translateDataInt(byte[] dataTranslate) {
        return ByteBuffer.wrap(dataTranslate).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private Float translateDataFloat(byte[] dataTranslate) {
        return ByteBuffer.wrap(dataTranslate).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public byte[] getStartAddressLogginArea() {
        return startAddressLogginArea;
    }

    public void setStartAddressLogginArea(byte[] startAddressLogginArea) {
        this.startAddressLogginArea = startAddressLogginArea;

    }

    public byte[] getEndAddressLogginArea() {
        return EndAddressLogginArea;
    }

    public void setEndAddressLogginArea(byte[] endAddressLogginArea) {
        EndAddressLogginArea = endAddressLogginArea;

    }

    public byte[] getStartAddressLogs1() {
        return startAddressLogs1;
    }

    public void setStartAddressLogs1(byte[] startAddressLogs1) {
        this.startAddressLogs1 = startAddressLogs1;

    }

    public Integer getPtrLog1() {
        return ptrLog1;
    }

    public void setPtrLog1(byte[] ptrLog1) {
        this.ptrLog1 = ByteBuffer.wrap(ptrLog1).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public int getMaxLogs1() {
        return maxLogs1;
    }

    public void setMaxLogs1(byte[] maxLogs1) {
        this.maxLogs1 = ByteBuffer.wrap(maxLogs1).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;
    }

    public int getLogginTerminalTime() {
        return logginTerminalTime;
    }

    public void setLogginTerminalTime(byte[] logginTerminalTime) {
        this.logginTerminalTime = ByteBuffer.wrap(logginTerminalTime).order(ByteOrder.LITTLE_ENDIAN).getInt();

    }

    public byte[] getStartAddressLogs2() {
        return startAddressLogs2;
    }

    public void setStartAddressLogs2(byte[] startAddressLogs2) {
        this.startAddressLogs2 = startAddressLogs2;

    }

    public int getMaxLogs2() {
        return maxLogs2;
    }

    public void setMaxLogs2(byte[] maxLogs2) {
        this.maxLogs2 = ByteBuffer.wrap(maxLogs2).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;

    }

    public int getLoggin2TerminalTime() {
        return loggin2TerminalTime;
    }

    public void setLoggin2TerminalTime(byte[] loggin2TerminalTime) {
        this.loggin2TerminalTime = ByteBuffer.wrap(loggin2TerminalTime).order(ByteOrder.LITTLE_ENDIAN).getInt();

    }

    public Integer getPtrLog2() {
        return ptrLog2;
    }

    public void setPtrLog2(byte[] ptrLog2) {

        this.ptrLog2 = ByteBuffer.wrap(ptrLog2).order(ByteOrder.LITTLE_ENDIAN).getInt();

    }

    public byte[] getStartAddressLogs3() {
        return startAddressLogs3;
    }

    public void setStartAddressLogs3(byte[] startAddressLogs) {
        this.startAddressLogs3 = startAddressLogs;
        for (byte b : this.startAddressLogs3) {
            System.out.printf("%02X ", b);
        }
    }

    public int getMaxLogs3() {
        return maxLogs3;
    }

    public void setMaxLogs3(byte[] maxLogs3) {
        this.maxLogs3 = ByteBuffer.wrap(maxLogs3).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;

    }

    public int getLoggin3TerminalTime() {
        return loggin3TerminalTime;
    }

    public void setLoggin3TerminalTime(byte[] loggin3TerminalTime) {
        this.loggin3TerminalTime = ByteBuffer.wrap(loggin3TerminalTime).order(ByteOrder.LITTLE_ENDIAN).getInt();

    }

    public int getPtrLog3() {
        return ptrLog3;
    }

    public void setPtrLog3(byte[] ptrLog3) {
        this.ptrLog3 = ByteBuffer.wrap(ptrLog3).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public byte[] getStartAddressFaultLog() {
        return startAddressFaultLog;
    }

    public void setStartAddressFaultLog(byte[] startAddressFaultLog) {
        this.startAddressFaultLog = startAddressFaultLog;
    }

    public int getMaxLogsFault() {
        return maxLogsFault;
    }

    public void setMaxLogsFault(byte[] maxLogsFault) {
        this.maxLogsFault = Byte.toUnsignedInt(maxLogsFault[0]);
    }

    public int getPtrLogFault() {
        return ptrLogFault;
    }

    public void setPtrLogFault(byte[] ptrLogFault) {
        this.ptrLogFault = ByteBuffer.wrap(ptrLogFault).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public byte[] getStartAddressAlarm() {
        return startAddressAlarm;
    }

    public void setStartAddressAlarm(byte[] startAddressAlarm) {
        this.startAddressAlarm = startAddressAlarm;

    }

    public int getMaxLogsAlarm() {
        return maxLogsAlarm;
    }

    public void setMaxLogsAlarm(byte[] maxLogsAlarm) {
        this.maxLogsAlarm = Byte.toUnsignedInt(maxLogsAlarm[0]);
    }

    public int getPtrLogAlarm() {
        return ptrLogAlarm;
    }

    public void setPtrLogAlarm(byte[] ptrLogAlarm) {
        this.ptrLogAlarm = ByteBuffer.wrap(ptrLogAlarm).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public Integer getLogginParameters() {
        return logginParameters;
    }

    public void setLogginParameters(byte[] logginParameters) {
        this.logginParameters = (int) ByteBuffer.wrap(logginParameters).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public Integer getLoggedParameters() {
        return loggedParameters;
    }

    public void setLoggedParameters(byte[] loggedParameters) {
        this.loggedParameters = (int) ByteBuffer.wrap(loggedParameters).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    private void setBaudRate() {
        byte[] respuesta = SerialHelper
                .stablishConnection(SerialHelper.createDataRequestPacketData((byte) 0x42, (byte) 0x03), 14);
        for (byte b : respuesta) {
            System.out.printf("%02X ", b);
        }
        SerialHelper.setupSerialPort(9600);
    }

    public int getTimeLog1() {
        return timeLog1;
    }

    public void setTimeLog1(byte[] timeLog1) {
        this.timeLog1 = getTime(timeLog1);
    }

    public int getTimeLog2() {
        return timeLog2;
    }

    public void setTimeLog2(byte[] timeLog2) {
        this.timeLog2 = getTime(timeLog2);
    }

    public int getTimeLog3() {
        return timeLog3;
    }

    public void setTimeLog3(byte[] timeLog3) {
        this.timeLog3 = getTime(timeLog3);
    }

    private String toStringJsonData() {
        String line = "";
        for (int i = 0; i < parametersBytes.length; i++) {
            try {
                if (!(mapLogger.get(parametersBytes[i]).toLowerCase().contains("null"))) {
                    mapLogger.get(parametersBytes[i]);
                    line = line + '"' + parametersBytes[i] + '"' + ": ";
                    line = line + mapLogger.get(parametersBytes[i]) + ",";
                }
            } catch (Exception e) {
            }
        }
        return line;
    }

    private String toJsonData() {
        String Json = "{" + '"' + "Id" + '"' + ": " + getSerialRaspi() + ","
                + '"' + "SERIAL" + '"' + ": " + getId() + ","
                + toStringJsonData() +
                +'"' + "" + '"' + ": " + "" + "" +
                "}";
        System.out.println(Json);
        return Json;

    }

    private String toJsonAlarm() {
        try {
            Scanner separador = new Scanner(JsonAlarm).useDelimiter(";");
            String Json = "{" + '"' + "Id" + '"' + ": " + getSerialRaspi() + ","
                    + '"' + "SERIAL" + '"' + ": " + getId() + ","
                    + '"' + "Date_and_Time" + '"' + ": " + separador.next() + ","
                    + '"' + "Parameter" + '"' + ": " + separador.next() + "}";
            return Json;
        } catch (Exception e) {
        }
        return "";
    }

    private String toJsonFault() {
        try {
            Scanner separador = new Scanner(JsonFault).useDelimiter(";");
            String Json = "{" + '"' + "Id" + '"' + ": " + getSerialRaspi() + ","
                    + '"' + "SERIAL" + '"' + ": " + getId() + ","
                    + '"' + "Date_and_Time" + '"' + ": " + separador.next() + ","
                    + '"' + "Parameter" + '"' + ": " + separador.next() + "}";
            return Json;
        } catch (Exception e) {
        }
        return "";
    }

    private String getSerialRaspi() {
      return writerLogs.getSerialRaspi();
    }

}
