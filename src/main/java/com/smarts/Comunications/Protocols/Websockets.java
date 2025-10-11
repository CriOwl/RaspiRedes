package com.smarts.Comunications.Protocols;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collections;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.smarts.Config.ConfigSensor;


public class Websockets {
    private  String uri;
    private  String user;
    private  String password;
    private  String uriLog1;
    private  String uriLog2;
    private  String uriLog3;
    private  String uriAlarm;
    private  String uriAudit;
    private  String uriFault;
    private  String uriLog1Json;
    private  String uriLog2Json;
    private  String uriLog3Json;
    private  String uriAlarmJson;
    private  String uriFaultJson;
    private  String token;
    private final String pathLogs = "/var/log/.Smarts/logWebS.txt";
    private  boolean isLogin;
    private  boolean isSLL;
    private  boolean isToken;
    private final String delimeter = ",";
     public Websockets() {
        setConfiguration();
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
    private void sendJson(String json,String uriL) {
        try {
            String fullUri = (isSLL) ? "wss://" + uriL : "ws://" + uriL;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            
            ClientEndpointConfig config = null;
            
            if (isLogin) {
                String encoded = Base64.getEncoder().encodeToString((user + ":" + password).getBytes());
                config = ClientEndpointConfig.Builder.create()
                .configurator(new ClientEndpointConfig.Configurator() {
                    @Override
                    public void beforeRequest(java.util.Map<String, java.util.List<String>> headers) {
                        headers.put("Authorization", Collections.singletonList("Basic " + encoded));
                    }
                }).build();
            } else if (isToken) {
                config = ClientEndpointConfig.Builder.create()
                .configurator(new ClientEndpointConfig.Configurator() {
                    @Override
                    public void beforeRequest(java.util.Map<String, java.util.List<String>> headers) {
                        headers.put("Authorization", Collections.singletonList("Bearer " + token));
                    }
                }).build();
            }
            Endpoint endpoint = new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    try {
                        session.getBasicRemote().sendText(json);
                        session.addMessageHandler(String.class, message -> {
                            System.out.println("Mensaje recibido: " + message);
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            if (config != null) {
                container.connectToServer(endpoint, config, URI.create(fullUri));
            } else {
                container.connectToServer(endpoint, URI.create(fullUri));
            }
            
        } catch (IOException | DeploymentException e) {
            e.printStackTrace();
        }
    }
    public  void sendLog1(String path){
        sendFile(path, uriLog1);
    }
    public  void sendLog2(String path){
        sendFile(path, uriLog2);
    }
    public  void sendLog3(String path){
        sendFile(path, uriLog3);
    }
    public  void sendLog1Json(String Json){
        sendJson(Json, uriLog1Json);
    }
    public  void sendLog2Json(String Json){
        sendJson(Json, uriLog2Json);
    }
    public  void sendLog3Json(String Json){
        sendJson(Json, uriLog3Json);
    }
    public  void sendAudit(String path){
        sendFile(path, uriAudit);
    }
    public  void sendAlarm(String path){
        sendFile(path, uriAlarm);
    }
    public  void sendFault(String path){
        sendFile(path, uriFault);
    }
    public void sendFaultJson(String Json){
        sendJson(Json, uriFaultJson);
    }
    public  void sendAlarmJson(String Json){
        sendJson(Json, uriAlarmJson);
    }
    public  void sendData(String Json){
        sendJson(Json, uri);
    }
    
    private void sendFile(String path, String uriL) {
        try {
            String fullUri = (isSLL) ? "wss://" + uriL : "ws://" + uriL;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            ClientEndpointConfig config = null;
            if (isLogin) {
                String encoded = Base64.getEncoder().encodeToString((user + ":" + password).getBytes());
                config = ClientEndpointConfig.Builder.create()
                .configurator(new ClientEndpointConfig.Configurator() {
                    @Override
                    public void beforeRequest(java.util.Map<String, java.util.List<String>> headers) {
                        headers.put("Authorization", Collections.singletonList("Basic " + encoded));
                    }
                }).build();
            } else if (isToken) {
                config = ClientEndpointConfig.Builder.create()
                .configurator(new ClientEndpointConfig.Configurator() {
                    @Override
                    public void beforeRequest(java.util.Map<String, java.util.List<String>> headers) {
                        headers.put("Authorization", Collections.singletonList("Bearer " + token));
                    }
                }).build();
            }
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("El archivo no existe.");
                return;
            }
            
            Endpoint endpoint = new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            session.getBasicRemote().sendBinary(ByteBuffer.wrap(buffer, 0, bytesRead));
                        }
                        session.getBasicRemote().sendText("EOF");
                        session.addMessageHandler(String.class, message -> {
                            System.out.println("Mensaje recibido: " + message);
                        });
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            
            if (config != null) {
                container.connectToServer(endpoint, config, URI.create(fullUri));
            } else {
                container.connectToServer(endpoint, URI.create(fullUri));
            }
            
        } catch (IOException | DeploymentException e) {
            e.printStackTrace();
        }
    }
    
    private void setConfiguration() {
        try {
                seturi(ConfigSensor.URIWebS);
                setUser(ConfigSensor.userWebS);
                setPassword(ConfigSensor.passwordWebS);
                setToken(ConfigSensor.TokenWebS);
                setSLL(ConfigSensor.isSLLWebS);
                setLogin(ConfigSensor.isLoginWebS);
                setToken(ConfigSensor.isTokenWebS);
                setUriLog1(ConfigSensor.topicLog1WebS);
                setUriLog2(ConfigSensor.topicLog2WebS);
                setUriLog3(ConfigSensor.topicLog3WebS);
                setUriFault(ConfigSensor.topicFaultWebS);
                setUriAlarm(ConfigSensor.topicAlarmWebS);
                setUriAudit(ConfigSensor.topicAuditWebS);
                setUriFaultJson(ConfigSensor.topicFaultJsonWebS);
                setUriAlarmJson(ConfigSensor.topicAlarmJsonWebS);
                setUriLog1Json(ConfigSensor.topicLog1JsonWebS);
                setUriLog2Json(ConfigSensor.topicLog2JsonWebS);
                setUriLog3Json(ConfigSensor.topicLog3JsonWebS);

        } catch (Exception e) {
            writeLogs(e);
        }
    }
    public String getURI() {
        return uri;
    }
    public void seturi(String uri) {
        this.uri = uri;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public boolean isLogin() {
        return isLogin;
    }
    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
    public boolean isSLL() {
        return isSLL;
    }
    public void setSLL(boolean isSLL) {
        this.isSLL = isSLL;
    }
    public boolean isToken() {
        return isToken;
    }
    public void setToken(boolean isToken) {
        this.isToken = isToken;
    }
    public String getUriLog1() {
        return uriLog1;
    }
    public void setUriLog1(String uriLog1) {
        this.uriLog1 = uriLog1;
    }
    public String getUriLog2() {
        return uriLog2;
    }
    public void setUriLog2(String uriLog2) {
        this.uriLog2 = uriLog2;
    }
    public String getUriLog3() {
        return uriLog3;
    }
    public void setUriLog3(String uriLog3) {
        this.uriLog3 = uriLog3;
    }
     public String getUriLog1Json() {
        return uriLog1Json;
    }
    public void setUriLog1Json(String uriLog1Json) {
        this.uriLog1Json = uriLog1Json;
    }
    public String getUriLog2Json() {
        return uriLog2Json;
    }
    public void setUriLog2Json(String uriLog2Json) {
        this.uriLog2Json = uriLog2Json;
    }
    public String getUriLog3Json() {
        return uriLog3Json;
    }
    public void setUriLog3Json(String uriLog3Json) {
        this.uriLog3Json = uriLog3Json;
    }
    public String getUriAlarm() {
        return uriAlarm;
    }
    public void setUriAlarm(String uriAlarm) {
        this.uriAlarm = uriAlarm;
    }
    public String getUriAudit() {
        return uriAudit;
    }
    public void setUriAudit(String uriAudit) {
        this.uriAudit = uriAudit;
    }
    public String getUriFault() {
        return uriFault;
    }
    public void setUriFault(String uriFault) {
        this.uriFault = uriFault;
    }
    public String getUriAlarmJson() {
        return uriAlarmJson;
    }
    public void setUriAlarmJson(String uriAlarmJson) {
        this.uriAlarmJson = uriAlarmJson;
    }
    public String getUriFaultJson() {
        return uriFaultJson;
    }
    public void setUriFaultJson(String uriFaultJson) {
        this.uriFaultJson = uriFaultJson;
    }
}
