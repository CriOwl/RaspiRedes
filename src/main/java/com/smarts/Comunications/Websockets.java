package com.smarts.Comunications;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.util.Base64;
import java.util.Collections;
import java.util.Scanner;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;


public  class Websockets {
    private String uri;
    private String port;
    private String token;
    private String user;
    private String password;
    private final String path = "/etc/.Smarts/config.txt";
    private final String pathLogs = "/var/log/.Smarts/logWebsockets.txt";
    private boolean isLogin;
    private boolean isToken;
    private boolean isSLL;
    private boolean isHeaderHttp;
    private final String delimeter = ",";
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
                setUri("");
                setPort("");
                setPassword("");
                setUser("");
                setToken(false);
                setLogin(false);
                setHeaderHttp(false);
                setSLL(false);
                setLogin(false);
                setToken("");
                return;
                
            }
            try (Scanner lector = new Scanner(configFile)) {
                lector.useDelimiter(delimeter);
                String line;
                while (lector.hasNextLine()) {
                    line = lector.nextLine();
                    if (line.contains("uriWEBSOCKETS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setUri(line);
                    } else if (line.contains("portWEBSOCKETS:")){
                        line=line.substring(line.indexOf(':')+1);
                        setPort(line);
                    }
                    else if (line.contains("tokenWEBSOCKETS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setToken(line);
                    } else if (line.contains("userWEBSOCKETS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setUser(line);
                    } else if (line.contains("passwordWEBSOCKETS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setPassword(line);
                    } else if (line.contains("isLoginWEBSOCKETS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setLogin(line.contains("true"));
                    } else if (line.contains("isSLLWEBSOCKETS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setSLL(line.contains("true"));
                    }
                    else if (line.contains("isTokenWEBSOCKETS:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setSLL(line.contains("true"));
                    }
                }
                setUri(uri);
            }
        } catch (Exception e) {
            writeLogs(e);
        }
    }
    public void openConnect(String Json){
        try {
            if(isHeaderHttp && isTokenB()){
            URI uri = new URI(getUri());
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            ClientEndpointConfig config = ClientEndpointConfig.Builder.create()
                .configurator(new ClientEndpointConfig.Configurator() {
                    @Override
                    public void beforeRequest(java.util.Map<String, java.util.List<String>> headers) {
                        headers.put("Authorization", Collections.singletonList("Bearer " + token));
                    }
                }).build();
            SocketsS client = new SocketsS();
            Session session = container.connectToServer(client, config, uri);
            client.sendMessage(Json);
            Thread.sleep(2000);
            session.close();
            }
            
        } catch (Exception e) {
        }
        
    }
    
    private String getUri() {
        return uri;
    }
    private String getPort() {
        return port;
    }
    private String getToken() {
        return token;
    }
    private String getUser() {
        return user;
    }
    private String getPassword() {
        return password;
    }
    private void setUri(String uri) {
        if(isSLL){
            this.uri="wss://"+uri;
            return;
        }
        this.uri = "ws://"+uri;
    }
    private void setPort(String port) {
        this.port = port;
    }
    private void setToken(String token) {
        this.token = token;
    }
    private void setUser(String user) {
        this.user = user;
    }
    private void setPassword(String password) {
        this.password = password;
    }
    public boolean isLogin() {
        return isLogin;
    }
    private void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
    private boolean isTokenB() {
        return isToken;
    }
    private void setToken(boolean isToken) {
        this.isToken = isToken;
    }
    private boolean isSLL() {
        return isSLL;
    }
    private void setSLL(boolean isSLL) {
        this.isSLL = isSLL;
    }
    private boolean isHeaderHttp() {
        return isHeaderHttp;
    }
    private void setHeaderHttp(boolean isHeaderHttp) {
        this.isHeaderHttp = isHeaderHttp;
    }
}   
