package com.smarts.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ConfigSensor {
    private static String brokerMQTT;
    private static String topicDataMQTT;
    private static String topicLog1MQTT;
    private static String topicLog2MQTT;
    private static String topicLog3MQTT;
    private static String clientIdMQTT;
    private static String userMQTT;
    private static String passwordMQTT;
    private static boolean isLoginMQTT; 
    private static boolean isSLLMQTT; 
    private static String URIWebS;
    private static String topicLog1WebS;
    private static String topicLog2WebS;
    private static String topicLog3WebS;
    private static String TokenWebS;
    private static String userWebS;
    private static String passwordWebS;
    private static boolean isLoginWebS;
    private static boolean isSLLWebS;
    private static boolean isTokenWebS;
    private static String tokenApi;
    private static String uriApi;
    private static String userApi;
    private static String passwordApi;
    private static String apiKey;
    private static String uriLog1Api;
    private static String uriLog2Api;
    private static String uriLog3Api;
    private static boolean isLoginApi;
    private static boolean isTokenApi;
    private static boolean isApikey;
    private static boolean isMQTT;
    private static boolean isApi;   
    private static boolean isWebsocket;   
    private static boolean isWitsml; 
    public static String port;
    private final String path = "/etc/.Smarts/config_MC2.txt";
    private final String pathLogs = "/var/log/.Smarts/logConfig"+port+".txt";

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
    private void setConfiguration() {
           try {
            File configFile = new File(path);
            if (!configFile.exists()) {
                
            }
            try (Scanner lector = new Scanner(configFile)) {
                String line;
                while (lector.hasNextLine()) {
                    line = lector.nextLine();
                    if (line.contains("uriApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setUriApi(line);
                    } else if (line.contains("tokenApi:")){
                        line=line.substring(line.indexOf(':')+1);
                        setTokenApi(line);        
                    }
                    else if (line.contains("userApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setUserApi(line);
                    } else if (line.contains("passwordApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setPasswordApi(line);
                    } else if (line.contains("apiKey:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setApiKey(line);
                    } else if (line.contains("uriLog1Api:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setUriLog1Api(line);
                    } else if (line.contains("uriLog2Api:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setUriLog2Api(line);
                    } else if (line.contains("uriLog3Api:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setUriLog3Api(line);
                    }
                    else if (line.contains("isLoginApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setLoginApi(line.contains("true"));
                    } else if (line.contains("isTokenApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setTokenApi(line.contains("true"));
                    }
                    else if (line.contains("isApikey:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setApikey(line.contains("true"));
                    }
                    else if (line.contains("brokerMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setBrokerMQTT(line);
                    } else if (line.contains("topicDataMQTT:")){
                        line=line.substring(line.indexOf(':')+1);
                        setTopicDataMQTT(line);
                    }
                    else if (line.contains("clientIdMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setClientIdMQTT(line);
                    } else if (line.contains("userMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setUserMQTT(line);
                    } else if (line.contains("passwordMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setPasswordMQTT(line);
                    } else if (line.contains("topicLog1MQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setTopicLog1MQTT(line);
                    } else if (line.contains("topicLog2MQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setTopicLog2MQTT(line);
                    } else if (line.contains("topicLog3MQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setTopicLog3MQTT(line);
                    }
                    else if (line.contains("isLoginMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setLoginMQTT(line.contains("true"));
                    } else if (line.contains("isSLLMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setSLLMQTT(line.contains("true"));
                    }else if (line.contains("URIWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setURIWebS(line);
                    } else if (line.contains("TokenWebS:")){
                        line=line.substring(line.indexOf(':')+1);
                        setTokenWebS(line);
                    }
                    else if (line.contains("userWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setUserWebS(line);
                    } else if (line.contains("passwordWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setPasswordWebS(line);
                        
                    } else if (line.contains("topicLog1WebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setTopicLog1WebS(line);
                    }
                    else if (line.contains("topicLog2WebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setTopicLog2WebS(line);
                    }
                    else if (line.contains("topicLog3WebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setTopicLog3WebS(line);
                    }
                    else if (line.contains("isLoginWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setLoginWebS(line.contains("true"));
                    } else if (line.contains("isSLLWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setSLLWebS(line.contains("true"));
                    }
                    else if (line.contains("isTokenWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setTokenWebS(line.contains("true"));
                    }
                    else if (line.contains("isApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setApi(line.contains("true"));
                    }else if (line.contains("isMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setMQTT(line.contains("true"));
                    }else if (line.contains("isWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setWebsocket(line.contains("true"));
                    }
                    else if (line.contains("isWitsml:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setWitsml(line.contains("true"));
                    }
                }
            }
        } catch (Exception e) {
            writeLogs(e);
        }
    }
    
    public ConfigSensor(String path, String port){

    }
    public static String getBrokerMQTT() {
        return brokerMQTT;
    }
    public static void setBrokerMQTT(String brokerMQTT) {
        ConfigSensor.brokerMQTT = brokerMQTT;
    }
    public static String getTopicDataMQTT() {
        return topicDataMQTT;
    }
    public static void setTopicDataMQTT(String topicDataMQTT) {
        ConfigSensor.topicDataMQTT = topicDataMQTT;
    }
    public static String getTopicLog1MQTT() {
        return topicLog1MQTT;
    }
    public static void setTopicLog1MQTT(String topicLog1MQTT) {
        ConfigSensor.topicLog1MQTT = topicLog1MQTT;
    }
    public static String getTopicLog2MQTT() {
        return topicLog2MQTT;
    }
    public static void setTopicLog2MQTT(String topicLog2MQTT) {
        ConfigSensor.topicLog2MQTT = topicLog2MQTT;
    }
    public static String getTopicLog3MQTT() {
        return topicLog3MQTT;
    }
    public static void setTopicLog3MQTT(String topicLog3MQTT) {
        ConfigSensor.topicLog3MQTT = topicLog3MQTT;
    }
    public static String getClientIdMQTT() {
        return clientIdMQTT;
    }
    public static void setClientIdMQTT(String clientIdMQTT) {
        ConfigSensor.clientIdMQTT = clientIdMQTT;
    }
    public static String getUserMQTT() {
        return userMQTT;
    }
    public static void setUserMQTT(String userMQTT) {
        ConfigSensor.userMQTT = userMQTT;
    }
    public static String getPasswordMQTT() {
        return passwordMQTT;
    }
    public static void setPasswordMQTT(String passwordMQTT) {
        ConfigSensor.passwordMQTT = passwordMQTT;
    }
    public static boolean isLoginMQTT() {
        return isLoginMQTT;
    }
    public static void setLoginMQTT(boolean isLoginMQTT) {
        ConfigSensor.isLoginMQTT = isLoginMQTT;
    }
    public static boolean isSLLMQTT() {
        return isSLLMQTT;
    }
    public static void setSLLMQTT(boolean isSLLMQTT) {
        ConfigSensor.isSLLMQTT = isSLLMQTT;
    }
    public static String getURIWebS() {
        return URIWebS;
    }
    public static void setURIWebS(String uRIWebS) {
        URIWebS = uRIWebS;
    }
    public static String getTopicLog1WebS() {
        return topicLog1WebS;
    }
    public static void setTopicLog1WebS(String topicLog1WebS) {
        ConfigSensor.topicLog1WebS = topicLog1WebS;
    }
    public static String getTopicLog2WebS() {
        return topicLog2WebS;
    }
    public static void setTopicLog2WebS(String topicLog2WebS) {
        ConfigSensor.topicLog2WebS = topicLog2WebS;
    }
    public static String getTopicLog3WebS() {
        return topicLog3WebS;
    }
    public static void setTopicLog3WebS(String topicLog3WebS) {
        ConfigSensor.topicLog3WebS = topicLog3WebS;
    }
    public static String getTokenWebS() {
        return TokenWebS;
    }
    public static void setTokenWebS(String tokenWebS) {
        TokenWebS = tokenWebS;
    }
    public static String getUserWebS() {
        return userWebS;
    }
    public static void setUserWebS(String userWebS) {
        ConfigSensor.userWebS = userWebS;
    }
    public static String getPasswordWebS() {
        return passwordWebS;
    }
    public static void setPasswordWebS(String passwordWebS) {
        ConfigSensor.passwordWebS = passwordWebS;
    }
    public static boolean isLoginWebS() {
        return isLoginWebS;
    }
    public static void setLoginWebS(boolean isLoginWebS) {
        ConfigSensor.isLoginWebS = isLoginWebS;
    }
    public static boolean isSLLWebS() {
        return isSLLWebS;
    }
    public static void setSLLWebS(boolean isSLLWebS) {
        ConfigSensor.isSLLWebS = isSLLWebS;
    }
    public static boolean isTokenWebS() {
        return isTokenWebS;
    }
    public static void setTokenWebS(boolean isTokenWebS) {
        ConfigSensor.isTokenWebS = isTokenWebS;
    }
    public static String getTokenApi() {
        return tokenApi;
    }
    public static void setTokenApi(String tokenApi) {
        ConfigSensor.tokenApi = tokenApi;
    }
    public static String getUriApi() {
        return uriApi;
    }
    public static void setUriApi(String uriApi) {
        ConfigSensor.uriApi = uriApi;
    }
    public static String getUserApi() {
        return userApi;
    }
    public static void setUserApi(String userApi) {
        ConfigSensor.userApi = userApi;
    }
    public static String getPasswordApi() {
        return passwordApi;
    }
    public static void setPasswordApi(String passwordApi) {
        ConfigSensor.passwordApi = passwordApi;
    }
    public static String getApiKey() {
        return apiKey;
    }
    public static void setApiKey(String apiKey) {
        ConfigSensor.apiKey = apiKey;
    }
    public static String getUriLog1Api() {
        return uriLog1Api;
    }
    public static void setUriLog1Api(String uriLog1Api) {
        ConfigSensor.uriLog1Api = uriLog1Api;
    }
    public static String getUriLog2Api() {
        return uriLog2Api;
    }
    public static void setUriLog2Api(String uriLog2Api) {
        ConfigSensor.uriLog2Api = uriLog2Api;
    }
    public static String getUriLog3Api() {
        return uriLog3Api;
    }
    public static void setUriLog3Api(String uriLog3Api) {
        ConfigSensor.uriLog3Api = uriLog3Api;
    }
    public static boolean isLoginApi() {
        return isLoginApi;
    }
    public static void setLoginApi(boolean isLoginApi) {
        ConfigSensor.isLoginApi = isLoginApi;
    }
    public static boolean isTokenApi() {
        return isTokenApi;
    }
    public static void setTokenApi(boolean isTokenApi) {
        ConfigSensor.isTokenApi = isTokenApi;
    }
    public static boolean isApikey() {
        return isApikey;
    }
    public static void setApikey(boolean isApikey) {
        ConfigSensor.isApikey = isApikey;
    }
    public static boolean isMQTT() {
        return isMQTT;
    }
    public void setMQTT(boolean isMQTT) {
        ConfigSensor.isMQTT = isMQTT;
    }
    public static boolean isApi() {
        return isApi;
    }
    public void setApi(boolean isApi) {
        ConfigSensor.isApi = isApi;
    }
    public static boolean isWebsocket() {
        return isWebsocket;
    }
    public void setWebsocket(boolean isWebsocket) {
        ConfigSensor.isWebsocket = isWebsocket;
    }
    public static  boolean isWitsml() {
        return isWitsml;
    }
    public void setWitsml(boolean isWitsml) {
        ConfigSensor.isWitsml = isWitsml;
    }
    
    
}
