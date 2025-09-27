package com.smarts.Comunications.Protocols;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Base64;

import com.smarts.Config.ConfigSensor;

public class APIRest {
    private  String uri;
    private  String uriLog1;
    private  String uriLog2;
    private  String uriLog3;
    private  String token;
    private  String user;
    private  String password;
    private  String apiKey;
    private  boolean isLogin;
    private  boolean isToken;
    private  boolean isApiKey;
    private final String delimeter = ",";
    private final String pathLogs = "/var/log/.Smarts/logApiRest"+ConfigSensor.port+".txt";
    
    
    public APIRest() {
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
    private void setConfiguration() {
        try {
                setUri(uri);
                setToken(token);
                setUser(user);
                setPassword(password);
                setUriLog1(uriLog1);
                setUriLog2(uriLog2);
                setUriLog3(uriLog3);
                setApiKey(apiKey);
                setIsApiKey(isApiKey);
                setToken(isToken);
                setIsLogin(isLogin);            
        } catch (Exception e) {
            writeLogs(e);
        }
    }
    
    public  void senDataApiLive(String dataJson) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = null;
        try {
            if(isLogin){
                String auth = user + ":" + password;
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic "+encodedAuth)
                .POST(HttpRequest.BodyPublishers.ofString(dataJson))
                .build();
            }
            else if(isToken){
                request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+token)
                .POST(HttpRequest.BodyPublishers.ofString(dataJson))
                .build();
            }
            else if(isApiKey){
                request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Content-Type", "application/json")
                .header( "x-api-key",apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(dataJson))
                .build();
            }else{
                request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(dataJson))
                .build();  
            }
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Código de estado: " + response.statusCode());
            System.out.println("Cuerpo de la respuesta: " + response.body());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println(e);
        }
    }
    public void sendLog1(String path){
        sendDataFileLogs(path,uriLog1);
    }
    public void sendLog2(String path){
        sendDataFileLogs(path,uriLog2);
        
    }
    public void sendLog3(String path){
        sendDataFileLogs(path,uriLog3);
        
    }
    private  void sendDataFileLogs(String path, String api) {
        try {
            String targetURL = api;
            File fileToUpload = new File(path);
            
            if (!fileToUpload.exists()) {
                System.err.println("El archivo no existe.");
                return;
            }
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            String LINE_FEED = "\r\n";
            HttpURLConnection connection = (HttpURLConnection) new URL(targetURL).openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            if (isLogin) {
                String auth = user + ":" + password;
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
            } else if (isToken) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            } else if (isApiKey) {
                connection.setRequestProperty("x-api-key", apiKey);
            }
            try (OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true)) {
                
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(fileToUpload.getName()).append("\"").append(LINE_FEED);
                writer.append("Content-Type: text/csv").append(LINE_FEED);
                writer.append(LINE_FEED).flush();
                
                try (FileInputStream inputStream = new FileInputStream(fileToUpload)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }
                
                writer.append(LINE_FEED).flush();
                
                writer.append("--").append(boundary).append("--").append(LINE_FEED);
                writer.close();
            }
            
            int responseCode = connection.getResponseCode();
            System.out.println("Código de respuesta: " + responseCode);
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            responseCode < 400 ? connection.getInputStream() : connection.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            
            connection.disconnect();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void senDataApiLogs(String dataJson, String url) {
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Content-Type", "application/json").POST(BodyPublishers.ofString(dataJson)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Código de estado: " + response.statusCode());
            System.out.println("Cuerpo de la respuesta: " + response.body());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println(e);
        }
    }
    public  String getUriLog1() {
        return uriLog1;
    }
    public  void setUriLog1(String uriLog1) {
        this.uriLog1 = uriLog1;
    }
    public  String getUriLog2() {
        return uriLog2;
    }
    public  void setUriLog2(String uriLog2) {
        this.uriLog2 = uriLog2;
    }
    public  String getUriLog3() {
        return uriLog3;
    }
    public  void setUriLog3(String uriLog3) {
        this.uriLog3 = uriLog3;
    }
    
    public String getUri() {
        return uri;
    }
    
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
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
    
    public boolean isLogin() {
        return isLogin;
    }
    
    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
    
    public boolean isToken() {
        return isToken;
    }
    
    public void setToken(boolean isToken) {
        this.isToken = isToken;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public boolean isIsLogin() {
        return isLogin;
    }
    
    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
    
    public boolean isIsToken() {
        return isToken;
    }
    
    public void setIsToken(boolean isToken) {
        this.isToken = isToken;
    }
    
    public boolean isIsApiKey() {
        return isApiKey;
    }
    
    public void setIsApiKey(boolean isApiKey) {
        this.isApiKey = isApiKey;
    }
    
    
}
