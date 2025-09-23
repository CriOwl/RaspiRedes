package com.smarts.Comunications;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

public class APIRest {
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
    
    public static void senDataApiLive(String dataJson) {
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://dev-lmi.com:3030/api/v1/sensor/webhook"))
                    .header("Content-Type", "application/json").POST(BodyPublishers.ofString(dataJson)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Código de estado: " + response.statusCode());
            System.out.println("Cuerpo de la respuesta: " + response.body());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println(e);
        }
    }

    public static void sendDataFileLogs(String path, String api) {

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

    public static void senDataApiLogs(String dataJson, String url) {
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
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

    public boolean isSLL() {
        return isSLL;
    }

    public void setSLL(boolean isSLL) {
        this.isSLL = isSLL;
    }

    public boolean isHeaderHttp() {
        return isHeaderHttp;
    }

    public void setHeaderHttp(boolean isHeaderHttp) {
        this.isHeaderHttp = isHeaderHttp;
    }

}
