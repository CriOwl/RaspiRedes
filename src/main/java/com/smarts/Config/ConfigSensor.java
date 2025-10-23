package com.smarts.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
public abstract class ConfigSensor {
    public static String[] brokerMQTT = new String[0];
    public static int timeSendMs = 60000;
    public static String[] topicDataMQTT = new String[0];
    public static String[] topicLog1MQTT = new String[0];
    public static String[] topicLog2MQTT = new String[0];
    public static String[] topicLog3MQTT = new String[0];
    public static String[] topicAlarmMQTT = new String[0];
    public static String[] topicFaultMQTT = new String[0];
    public static String[] topicAuditMQTT = new String[0];
    public static String[] topicLog1JsonMQTT = new String[0];
    public static String[] topicLog2JsonMQTT = new String[0];
    public static String[] topicLog3JsonMQTT = new String[0];
    public static String[] topicAlarmJsonMQTT = new String[0];
    public static String[] topicFaultJsonMQTT = new String[0];
    public static String[] clientIdMQTT = new String[0];
    public static String[] userMQTT = new String[0];
    public static String[] passwordMQTT = new String[0];
    public static boolean[] isLoginMQTT = new boolean[0];
    public static boolean[] isSLLMQTT = new boolean[0];
    public static String[] URIWebS = new String[0];
    public static String[] topicLog1WebS = new String[0];
    public static String[] topicLog2WebS = new String[0];
    public static String[] topicLog3WebS = new String[0];
    public static String[] topicAlarmWebS = new String[0];
    public static String[] topicFaultWebS = new String[0];
    public static String[] topicAuditWebS = new String[0];
    public static String[] topicLog1JsonWebS = new String[0];
    public static String[] topicLog2JsonWebS = new String[0];
    public static String[] topicLog3JsonWebS = new String[0];
    public static String[] topicAlarmJsonWebS = new String[0];
    public static String[] topicFaultJsonWebS = new String[0];
    public static String[] TokenWebS = new String[0];
    public static String[] userWebS = new String[0];
    public static String[] passwordWebS = new String[0];
    public static boolean[] isLoginWebS = new boolean[0];
    public static boolean[] isSLLWebS = new boolean[0];
    public static boolean[] isTokenWebS = new boolean[0];
    public static String[] tokenApi = new String[0];
    public static String[] uriApi = new String[0];
    public static String[] userApi = new String[0];
    public static String[] passwordApi = new String[0];
    public static String[] apiKey = new String[0];
    public static String[] uriLog1Api = new String[0];
    public static String[] uriLog2Api = new String[0];
    public static String[] uriLog3Api = new String[0];
    public static String[] uriAlarmApi = new String[0];
    public static String[] uriFaultApi = new String[0];
    public static String[] uriAuditApi = new String[0];
    public static String[] uriLog1JsonApi = new String[0];
    public static String[] uriLog2JsonApi = new String[0];
    public static String[] uriLog3JsonApi = new String[0];
    public static String[] uriAlarmJsonApi = new String[0];
    public static String[] uriFaultJsonApi = new String[0];
    public static boolean[] isLoginApi = new boolean[0];
    public static boolean[] isTokenApi = new boolean[0];
    public static boolean[] isApikey = new boolean[0];
    public static boolean[] isMQTT = new boolean[0];
    public static boolean[] isApi = new boolean[]{true};
    public static boolean[] isWebsocket = new boolean[0];
    public static boolean[] isWitsml = new boolean[0];
    private final static String PATH = "/home/EPI5/.Smarts/config.txt";
    private final static String PATH_LOGS = "/home/EPI5/.Smarts/logConfig.txt";
    private final static String DEFAULT_CONFIG = """
                     uriApi:http://dev-lmi.com:3030/api/v1/sensor/webhook
                     tokenApi:null
                     userApi:null
                     passwordApi:null
                     apiKey:null
                     uriLog1Api:http://54.167.252.128:3030/api/v1/upload/logs1
                     uriLog2Api:http://54.167.252.128:3030/api/v1/upload/logs2
                     uriLog3Api:http://54.167.252.128:3030/api/v1/upload/logs3
                     uriAlarmApi:http://54.167.252.128:3030/api/v1/upload/alarms-logs
                     uriFaultApi:http://54.167.252.128:3030/api/v1/upload/change-history
                     uriAuditApi:http://54.167.252.128:3030/api/v1/upload/audit-logs
                     uriFaultJsonApi:http://54.167.252.128:3030/api/v1/change-history/webhook
                     uriAlarmJsonApi:http://54.167.252.128:3030/api/v1/alarms/webhook
                     uriLog1JsonApi:http://54.167.252.128:3030/api/v1/logs1/webhook
                     uriLog2JsonApi:http://54.167.252.128:3030/api/v1/logs2/webhook
                     uriLog3JsonApi:http://54.167.252.128:3030/api/v1/logs3/webhook
                     isLoginApi:false
                     isTokenApi:false
                     isApikey:false
                     brokerMQTT:null
                     topicDataMQTT:null
                     clientIdMQTT:null
                     userMQTT:null
                     passwordMQTT:null
                     topicLog1MQTT:null
                     topicLog2MQTT:null
                     topicLog3MQTT:null
                     topicAlarmMQTT:null
                     topicFaultMQTT:null
                     topicAuditMQTT:null
                     topicAlarmJsonMQTT:null
                     topicFaultJsonMQTT:null
                     topicLog1JsonMQTT:null
                     topicLog2JsonMQTT:null
                     topicLog3JsonMQTT:null
                     isLoginMQTT:false
                     isSLLMQTT:false
                     URIWebS:null
                     TokenWebS:null
                     userWebS:null
                     passwordWebS:null
                     topicLog1WebS:null
                     topicLog2WebS:null
                     topicLog3WebS:null
                     topicAlarmWebS:null
                     topicFaultWebS:null
                     topicAuditWebS:null
                     topicAlarmJsonWebS:null
                     topicFaultJsonWebS:null
                     topicLog1JsonWebS:null
                     topicLog2JsonWebS:null
                     topicLog3JsonWebS:null
                     isLoginWebS:false
                     isSLLWebS:false
                     isTokenWebS:false
                     isApi:true
                     isMQTT:false
                     isWebS:false
                     isWitsml:false
                     timeSendMs:60000
                    """;
    private static void writeLogs(Exception ex) {
        try {
            File logMqtt = new File(PATH_LOGS);
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
    private static String[] parseArray(String value) {
        if (value == null) return new String[10];
        String v = value.trim();
        if (v.equalsIgnoreCase("null") || v.isBlank()) return new String[10];
        String[] parts = v.split(",");
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
        return parts;
    }
    
    private static boolean[] parseBooleanArray(String value) {
        String[] parts = parseArray(value);
        boolean[] res = new boolean[parts.length];
        for (int i = 0; i < parts.length; i++) res[i] = parts[i].equalsIgnoreCase("true");
        return res;
    }
    public static void setConfiguration() {
        try {
            File configFile = new File(PATH);
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write(DEFAULT_CONFIG);
                }
            }
            try (Scanner lector = new Scanner(configFile)) {
                while (lector.hasNextLine()) {
                    String line = lector.nextLine();
                    if (line.contains(":")) {
                        String key = line.substring(0, line.indexOf(':')).trim();
                        String value = line.substring(line.indexOf(':') + 1).trim();
                        switch (key) {
                            case "uriApi" -> uriApi = parseArray(value);
                            case "tokenApi" -> tokenApi = parseArray(value);
                            case "userApi" -> userApi = parseArray(value);
                            case "passwordApi" -> passwordApi = parseArray(value);
                            case "apiKey" -> apiKey = parseArray(value);
                            case "uriLog1Api" -> uriLog1Api = parseArray(value);
                            case "uriLog2Api" -> uriLog2Api = parseArray(value);
                            case "uriLog3Api" -> uriLog3Api = parseArray(value);
                            case "uriAlarmApi" -> uriAlarmApi = parseArray(value);
                            case "uriFaultApi" -> uriFaultApi = parseArray(value);
                            case "uriAuditApi" -> uriAuditApi = parseArray(value);
                            case "uriFaultJsonApi" -> uriFaultJsonApi = parseArray(value);
                            case "uriAlarmJsonApi" -> uriAlarmJsonApi = parseArray(value);
                            case "uriLog1JsonApi" -> uriLog1JsonApi = parseArray(value);
                            case "uriLog2JsonApi" -> uriLog2JsonApi = parseArray(value);
                            case "uriLog3JsonApi" -> uriLog3JsonApi = parseArray(value);
                            case "isLoginApi" -> isLoginApi = parseBooleanArray(value);
                            case "isTokenApi" -> isTokenApi = parseBooleanArray(value);
                            case "isApikey" -> isApikey = parseBooleanArray(value);
                            case "brokerMQTT" -> brokerMQTT = parseArray(value);
                            case "topicDataMQTT" -> topicDataMQTT = parseArray(value);
                            case "clientIdMQTT" -> clientIdMQTT = parseArray(value);
                            case "userMQTT" -> userMQTT = parseArray(value);
                            case "passwordMQTT" -> passwordMQTT = parseArray(value);
                            case "topicLog1MQTT" -> topicLog1MQTT = parseArray(value);
                            case "topicLog2MQTT" -> topicLog2MQTT = parseArray(value);
                            case "topicLog3MQTT" -> topicLog3MQTT = parseArray(value);
                            case "topicAlarmMQTT" -> topicAlarmMQTT = parseArray(value);
                            case "topicFaultMQTT" -> topicFaultMQTT = parseArray(value);
                            case "topicAuditMQTT" -> topicAuditMQTT = parseArray(value);
                            case "topicAlarmJsonMQTT" -> topicAlarmJsonMQTT = parseArray(value);
                            case "topicFaultJsonMQTT" -> topicFaultJsonMQTT = parseArray(value);
                            case "topicLog1JsonMQTT" -> topicLog1JsonMQTT = parseArray(value);
                            case "topicLog2JsonMQTT" -> topicLog2JsonMQTT = parseArray(value);
                            case "topicLog3JsonMQTT" -> topicLog3JsonMQTT = parseArray(value);
                            case "isLoginMQTT" -> isLoginMQTT = parseBooleanArray(value);
                            case "isSLLMQTT" -> isSLLMQTT = parseBooleanArray(value);
                            case "URIWebS" -> URIWebS = parseArray(value);
                            case "TokenWebS" -> TokenWebS = parseArray(value);
                            case "userWebS" -> userWebS = parseArray(value);
                            case "passwordWebS" -> passwordWebS = parseArray(value);
                            case "topicLog1WebS" -> topicLog1WebS = parseArray(value);
                            case "topicLog2WebS" -> topicLog2WebS = parseArray(value);
                            case "topicLog3WebS" -> topicLog3WebS = parseArray(value);
                            case "topicAlarmWebS" -> topicAlarmWebS = parseArray(value);
                            case "topicFaultWebS" -> topicFaultWebS = parseArray(value);
                            case "topicAuditWebS" -> topicAuditWebS = parseArray(value);
                            case "topicAlarmJsonWebS" -> topicAlarmJsonWebS = parseArray(value);
                            case "topicFaultJsonWebS" -> topicFaultJsonWebS = parseArray(value);
                            case "topicLog1JsonWebS" -> topicLog1JsonWebS = parseArray(value);
                            case "topicLog2JsonWebS" -> topicLog2JsonWebS = parseArray(value);
                            case "topicLog3JsonWebS" -> topicLog3JsonWebS = parseArray(value);
                            case "isLoginWebS" -> isLoginWebS = parseBooleanArray(value);
                            case "isSLLWebS" -> isSLLWebS = parseBooleanArray(value);
                            case "isTokenWebS" -> isTokenWebS = parseBooleanArray(value);
                            case "isApi" -> isApi = parseBooleanArray(value);
                            case "isMQTT" -> isMQTT = parseBooleanArray(value);
                            case "isWebS" -> isWebsocket = parseBooleanArray(value);
                            case "isWitsml" -> isWitsml = parseBooleanArray(value);
                            case "timeSendMs" -> {
                                try {
                                    timeSendMs = Integer.parseInt(value.trim());
                                } catch (Exception e) {
                                    timeSendMs = 60000;
                                }
                            }
                            default -> {
                                
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            writeLogs(e);
        }
        System.out.println(toStringConfig());
    }
    public static String toStringConfig() {
        return "ConfigSensor{" +
        "brokerMQTT=" + Arrays.toString(brokerMQTT) +
        ", timeSendMs=" + timeSendMs +
        ", topicDataMQTT=" + Arrays.toString(topicDataMQTT) +
        ", topicLog1MQTT=" + Arrays.toString(topicLog1MQTT) +
        ", topicLog2MQTT=" + Arrays.toString(topicLog2MQTT) +
        ", topicLog3MQTT=" + Arrays.toString(topicLog3MQTT) +
        ", topicAlarmMQTT=" + Arrays.toString(topicAlarmMQTT) +
        ", topicFaultMQTT=" + Arrays.toString(topicFaultMQTT) +
        ", topicAuditMQTT=" + Arrays.toString(topicAuditMQTT) +
        ", topicLog1JsonMQTT=" + Arrays.toString(topicLog1JsonMQTT) +
        ", topicLog2JsonMQTT=" + Arrays.toString(topicLog2JsonMQTT) +
        ", topicLog3JsonMQTT=" + Arrays.toString(topicLog3JsonMQTT) +
        ", topicAlarmJsonMQTT=" + Arrays.toString(topicAlarmJsonMQTT) +
        ", topicFaultJsonMQTT=" + Arrays.toString(topicFaultJsonMQTT) +
        ", clientIdMQTT=" + Arrays.toString(clientIdMQTT) +
        ", userMQTT=" + Arrays.toString(userMQTT) +
        ", passwordMQTT=" + Arrays.toString(passwordMQTT) +
        ", isLoginMQTT=" + Arrays.toString(isLoginMQTT) +
        ", isSLLMQTT=" + Arrays.toString(isSLLMQTT) +
        ", URIWebS=" + Arrays.toString(URIWebS) +
        ", topicLog1WebS=" + Arrays.toString(topicLog1WebS) +
        ", topicLog2WebS=" + Arrays.toString(topicLog2WebS) +
        ", topicLog3WebS=" + Arrays.toString(topicLog3WebS) +
        ", topicAlarmWebS=" + Arrays.toString(topicAlarmWebS) +
        ", topicFaultWebS=" + Arrays.toString(topicFaultWebS) +
        ", topicAuditWebS=" + Arrays.toString(topicAuditWebS) +
        ", topicLog1JsonWebS=" + Arrays.toString(topicLog1JsonWebS) +
        ", topicLog2JsonWebS=" + Arrays.toString(topicLog2JsonWebS) +
        ", topicLog3JsonWebS=" + Arrays.toString(topicLog3JsonWebS) +
        ", topicAlarmJsonWebS=" + Arrays.toString(topicAlarmJsonWebS) +
        ", topicFaultJsonWebS=" + Arrays.toString(topicFaultJsonWebS) +
        ", TokenWebS=" + Arrays.toString(TokenWebS) +
        ", userWebS=" + Arrays.toString(userWebS) +
        ", passwordWebS=" + Arrays.toString(passwordWebS) +
        ", isLoginWebS=" + Arrays.toString(isLoginWebS) +
        ", isSLLWebS=" + Arrays.toString(isSLLWebS) +
        ", isTokenWebS=" + Arrays.toString(isTokenWebS) +
        ", tokenApi=" + Arrays.toString(tokenApi) +
        ", uriApi=" + Arrays.toString(uriApi) +
        ", userApi=" + Arrays.toString(userApi) +
        ", passwordApi=" + Arrays.toString(passwordApi) +
        ", apiKey=" + Arrays.toString(apiKey) +
        ", uriLog1Api=" + Arrays.toString(uriLog1Api) +
        ", uriLog2Api=" + Arrays.toString(uriLog2Api) +
        ", uriLog3Api=" + Arrays.toString(uriLog3Api) +
        ", uriAlarmApi=" + Arrays.toString(uriAlarmApi) +
        ", uriFaultApi=" + Arrays.toString(uriFaultApi) +
        ", uriAuditApi=" + Arrays.toString(uriAuditApi) +
        ", uriLog1JsonApi=" + Arrays.toString(uriLog1JsonApi) +
        ", uriLog2JsonApi=" + Arrays.toString(uriLog2JsonApi) +
        ", uriLog3JsonApi=" + Arrays.toString(uriLog3JsonApi) +
        ", uriAlarmJsonApi=" + Arrays.toString(uriAlarmJsonApi) +
        ", uriFaultJsonApi=" + Arrays.toString(uriFaultJsonApi) +
        ", isLoginApi=" + Arrays.toString(isLoginApi) +
        ", isTokenApi=" + Arrays.toString(isTokenApi) +
        ", isApikey=" + Arrays.toString(isApikey) +
        ", isMQTT=" + Arrays.toString(isMQTT) +
        ", isApi=" + Arrays.toString(isApi) +
        ", isWebsocket=" + Arrays.toString(isWebsocket) +
        ", isWitsml=" + Arrays.toString(isWitsml) +
        '}';
    }
}

