package com.smarts.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public abstract class ConfigSensor {
    public static String brokerMQTT;
    public static int timeSendMs;
    public static String topicDataMQTT;
    public static String topicLog1MQTT;
    public static String topicLog2MQTT;
    public static String topicLog3MQTT;
    public static String topicAlarmMQTT;
    public static String topicFaultMQTT;
    public static String topicAuditMQTT;
    public static String topicLog1JsonMQTT;
    public static String topicLog2JsonMQTT;
    public static String topicLog3JsonMQTT;
    public static String topicAlarmJsonMQTT;
    public static String topicFaultJsonMQTT;
    public static String clientIdMQTT;
    public static String userMQTT;
    public static String passwordMQTT;
    public static boolean isLoginMQTT; 
    public static boolean isSLLMQTT; 
    public static String URIWebS;
    public static String topicLog1WebS;
    public static String topicLog2WebS;
    public static String topicLog3WebS;
    public static String topicAlarmWebS;
    public static String topicFaultWebS;
    public static String topicAuditWebS;
    public static String topicLog1JsonWebS;
    public static String topicLog2JsonWebS;
    public static String topicLog3JsonWebS;
    public static String topicAlarmJsonWebS;
    public static String topicFaultJsonWebS;
    public static String TokenWebS;
    public static String userWebS;
    public static String passwordWebS;
    public static boolean isLoginWebS;
    public static boolean isSLLWebS;
    public static boolean isTokenWebS;
    public static String tokenApi;
    public static String uriApi;
    public static String userApi;
    public static String passwordApi;
    public static String apiKey;
    public static String uriLog1Api;
    public static String uriLog2Api;
    public static String uriLog3Api;
    public static String uriAlarmApi;
    public static String uriFaultApi;
    public static String uriAuditApi;
    public static String uriLog1JsonApi;
    public static String uriLog2JsonApi;
    public static String uriLog3JsonApi;
    public static String uriAlarmJsonApi;
    public static String uriFaultJsonApi;
    public static boolean isLoginApi;
    public static boolean isTokenApi;
    public static boolean isApikey;
    public static boolean isMQTT;
    public static boolean isApi;   
    public static boolean isWebsocket;   
    public static boolean isWitsml; 
    private final static String path = "/home/EPI5/.Smarts/config.txt";
    private final static String pathLogs = "/home/EPI5/.Smarts/logConfig.txt";
    private final static String configString="""
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
                            isTokenApi:flase
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
                            isMQTT:true
                            isWebS:false
                            isWitsml:false
                            timeSendMs:60000""";
    
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
                configFile.createNewFile();
                FileWriter writer= new FileWriter(configFile);
                writer.write(configString);
            }
            try (Scanner lector = new Scanner(configFile)) {
                String line;
                while (lector.hasNextLine()) {
                    line = lector.nextLine();
                    if (line.contains("uriApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriApi=line;
                    } else if (line.contains("tokenApi:")){
                        line=line.substring(line.indexOf(':')+1);
                        tokenApi=(line);        
                    }
                    else if (line.contains("userApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        userApi=(line);
                    } else if (line.contains("passwordApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        passwordApi=(line);
                    } else if (line.contains("apiKey:")) {
                        line=line.substring(line.indexOf(':')+1);
                        apiKey=(line);
                    } else if (line.contains("uriLog1Api:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriLog1Api=(line);
                    } else if (line.contains("uriLog2Api:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriLog2Api=(line);
                    } else if (line.contains("uriLog3Api:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriLog3Api=(line);
                    }else if (line.contains("uriAlarmApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriAlarmApi=(line);
                    } else if (line.contains("uriFaultApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriFaultApi=(line);
                    } else if (line.contains("uriAuditApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriAuditApi=(line);
                    }
                    else if (line.contains("uriFaultJsonApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriFaultJsonApi=(line);
                    } else if (line.contains("uriAlarmJsonApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriAlarmJsonApi=(line);
                    } else if (line.contains("uriLog3JsonApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriLog3JsonApi=(line);
                    }
                     else if (line.contains("uriLog1JsonApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriLog1JsonApi=(line);
                    } else if (line.contains("uriLog2JsonApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        uriLog2JsonApi=(line);
                    } 
                    else if (line.contains("isLoginApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isLoginApi=(line.contains("true"));
                    } else if (line.contains("isTokenApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isTokenApi=(line.contains("true"));
                    }
                    else if (line.contains("isApikey:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isApikey=(line.contains("true"));
                    }
                    else if (line.contains("brokerMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        brokerMQTT=(line);
                    } else if (line.contains("topicDataMQTT:")){
                        line=line.substring(line.indexOf(':')+1);
                        topicDataMQTT=(line);
                    }
                    else if (line.contains("clientIdMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        clientIdMQTT=(line);
                    } else if (line.contains("userMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        userMQTT=(line);
                    } else if (line.contains("passwordMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        passwordMQTT=(line);
                    } else if (line.contains("topicLog1MQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog1MQTT=(line);
                    } else if (line.contains("topicLog2MQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog2MQTT=(line);
                    } else if (line.contains("topicLog3MQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog3MQTT=(line);
                    }
                    else if (line.contains("topicAlarmMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicAlarmMQTT=(line);
                    } else if (line.contains("topicFaultMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicFaultMQTT=(line);
                    } else if (line.contains("topicAuditMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicAuditMQTT=(line);
                    }
                    else if (line.contains("topicAlarmJsonMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicAlarmJsonMQTT=(line);
                    } else if (line.contains("topicFaultJsonMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicFaultJsonMQTT=(line);
                    }
                    else if (line.contains("topicLog1JsonMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog1JsonMQTT=(line);
                    } else if (line.contains("topicLog2JsonMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog2JsonMQTT=(line);
                    } else if (line.contains("topicLog3JsonMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog3JsonMQTT=(line);
                    }
                    else if (line.contains("isLoginMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isLoginMQTT=(line.contains("true"));
                    } else if (line.contains("isSLLMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isSLLMQTT=(line.contains("true"));
                    }else if (line.contains("URIWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        URIWebS=(line);
                    } else if (line.contains("TokenWebS:")){
                        line=line.substring(line.indexOf(':')+1);
                        TokenWebS=(line);
                    }
                    else if (line.contains("userWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        userWebS=(line);
                    } else if (line.contains("passwordWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        passwordWebS=(line);
                        
                    } else if (line.contains("topicLog1WebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog1WebS=(line);
                    }
                    else if (line.contains("topicLog2WebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog2WebS=(line);
                    }
                    else if (line.contains("topicLog3WebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog3WebS=(line);
                    } else if (line.contains("topicAlarmWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicAlarmWebS=(line);
                    } else if (line.contains("topicFaultWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicFaultWebS=(line);
                    } else if (line.contains("topicAuditWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicAuditWebS=(line);
                    }
                    else if (line.contains("topicAlarmJsonWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicAlarmJsonWebS=(line);
                    } else if (line.contains("topicFaultJsonWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                    topicFaultJsonWebS=(line);
                    }else if (line.contains("topicLog1JsonWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog1JsonWebS=(line);
                    }
                    else if (line.contains("topicLog2JsonWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog2JsonWebS=(line);
                    }
                    else if (line.contains("topicLog3JsonWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        topicLog3JsonWebS=(line);
                    }
                    else if (line.contains("isLoginWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isLoginWebS=(line.contains("true"));
                    } else if (line.contains("isSLLWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isSLLWebS=(line.contains("true"));
                    }
                    else if (line.contains("isTokenWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isTokenWebS=(line.contains("true"));
                    }
                    else if (line.contains("isApi:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isApi=(line.contains("true"));
                    }else if (line.contains("isMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isMQTT=(line.contains("true"));
                    }else if (line.contains("isWebS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isWebsocket=(line.contains("true"));
                    }
                    else if (line.contains("isWitsml:")) {
                        line=line.substring(line.indexOf(':')+1);
                        isWitsml=(line.contains("true"));
                    }
                    else if (line.contains("timeSendMs:")) {
                        line=line.substring(line.indexOf(':')+1);
                        try {
                            timeSendMs=(Integer.parseInt(line.trim()));
                        } catch (Exception e) {
                            timeSendMs=(60000);
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
                "brokerMQTT='" + brokerMQTT + '\'' +
                ", timeSendMs=" + timeSendMs +
                ", topicDataMQTT='" + topicDataMQTT + '\'' +
                ", topicLog1MQTT='" + topicLog1MQTT + '\'' +
                ", topicLog2MQTT='" + topicLog2MQTT + '\'' +
                ", topicLog3MQTT='" + topicLog3MQTT + '\'' +
                ", topicAlarmMQTT='" + topicAlarmMQTT + '\'' +
                ", topicFaultMQTT='" + topicFaultMQTT + '\'' +
                ", topicAuditMQTT='" + topicAuditMQTT + '\'' +
                ", topicLog1JsonMQTT='" + topicLog1JsonMQTT + '\'' +
                ", topicLog2JsonMQTT='" + topicLog2JsonMQTT + '\'' +
                ", topicLog3JsonMQTT='" + topicLog3JsonMQTT + '\'' +
                ", topicAlarmJsonMQTT='" + topicAlarmJsonMQTT + '\'' +
                ", topicFaultJsonMQTT='" + topicFaultJsonMQTT + '\'' +
                ", clientIdMQTT='" + clientIdMQTT + '\'' +
                ", userMQTT='" + userMQTT + '\'' +
                ", passwordMQTT='" + passwordMQTT + '\'' +
                ", isLoginMQTT=" + isLoginMQTT +
                ", isSLLMQTT=" + isSLLMQTT +
                ", URIWebS='" + URIWebS + '\'' +
                ", topicLog1WebS='" + topicLog1WebS + '\'' +
                ", topicLog2WebS='" + topicLog2WebS + '\'' +
                ", topicLog3WebS='" + topicLog3WebS + '\'' +
                ", topicAlarmWebS='" + topicAlarmWebS + '\'' +
                ", topicFaultWebS='" + topicFaultWebS + '\'' +
                ", topicAuditWebS='" + topicAuditWebS + '\'' +
                ", topicLog1JsonWebS='" + topicLog1JsonWebS + '\'' +
                ", topicLog2JsonWebS='" + topicLog2JsonWebS + '\'' +
                ", topicLog3JsonWebS='" + topicLog3JsonWebS + '\'' +
                ", topicAlarmJsonWebS='" + topicAlarmJsonWebS + '\'' +
                ", topicFaultJsonWebS='" + topicFaultJsonWebS + '\'' +
                ", TokenWebS='" + TokenWebS + '\'' +
                ", userWebS='" + userWebS + '\'' +
                ", passwordWebS='" + passwordWebS + '\'' +
                ", isLoginWebS=" + isLoginWebS +
                ", isSLLWebS=" + isSLLWebS +
                ", isTokenWebS=" + isTokenWebS +
                ", tokenApi='" + tokenApi + '\'' +
                ", uriApi='" + uriApi + '\'' +
                ", userApi='" + userApi + '\'' +
                ", passwordApi='" + passwordApi + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", uriLog1Api='" + uriLog1Api + '\'' +
                ", uriLog2Api='" + uriLog2Api + '\'' +
                ", uriLog3Api='" + uriLog3Api + '\'' +
                ", uriAlarmApi='" + uriAlarmApi + '\'' +
                ", uriFaultApi='" + uriFaultApi + '\'' +
                ", uriAuditApi='" + uriAuditApi + '\'' +
                ", uriLog1JsonApi='" + uriLog1JsonApi + '\'' +
                ", uriLog2JsonApi='" + uriLog2JsonApi + '\'' +
                ", uriLog3JsonApi='" + uriLog3JsonApi + '\'' +
                ", uriAlarmJsonApi='" + uriAlarmJsonApi + '\'' +
                ", uriFaultJsonApi='" + uriFaultJsonApi + '\'' +
                ", isLoginApi=" + isLoginApi +
                ", isTokenApi=" + isTokenApi +
                ", isApikey=" + isApikey +
                ", isMQTT=" + isMQTT +
                ", isApi=" + isApi +
                ", isWebsocket=" + isWebsocket +
                ", isWitsml=" + isWitsml +
                '}';
    }
}
