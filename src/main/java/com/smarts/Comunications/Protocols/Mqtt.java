package com.smarts.Comunications.Protocols;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.smarts.Config.ConfigSensor;

public class Mqtt {
    private String clientID;
    private String broker;
    private String topicL1;
    private String topicL2;
    private String topicL3;
    private String topicAlarm;
    private String topicFault;
    private String topicAudit;
    private String topicJsonL1;
    private String topicJsonL2;
    private String topicJsonL3;
    private String topicFaultJson;
    private String topicAlarmJson;
    private String topic;
    private String user;
    private String password;
    private final String pathLogs = "/home/EPI5/.Smarts/logMqtt.txt";
    private boolean isLogin;
    private boolean isSLL;
    private int index = 0;
    
    private IMqttClient client;

    public Mqtt(int index) {
        System.out.println("[DEBUG] Iniciando constructor Mqtt");
        this.index = index;
        setConfiguration();
        System.out.println(toStringConfig());
        System.out.println("[DEBUG] Constructor Mqtt finalizado");
    }

    public String toStringConfig() {
        return "Mqtt{" +
                "clientID='" + clientID + '\'' +
                ", broker='" + broker + '\'' +
                ", topic='" + topic + '\'' +
                ", topicL1='" + topicL1 + '\'' +
                ", topicL2='" + topicL2 + '\'' +
                ", topicL3='" + topicL3 + '\'' +
                ", topicAlarm='" + topicAlarm + '\'' +
                ", topicFault='" + topicFault + '\'' +
                ", topicAudit='" + topicAudit + '\'' +
                ", topicJsonL1='" + topicJsonL1 + '\'' +
                ", topicJsonL2='" + topicJsonL2 + '\'' +
                ", topicJsonL3='" + topicJsonL3 + '\'' +
                ", topicFaultJson='" + topicFaultJson + '\'' +
                ", topicAlarmJson='" + topicAlarmJson + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", isLogin=" + isLogin +
                ", isSLL=" + isSLL +
                '}';
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

    private void ensureConnection() throws Exception {
        if (client != null && client.isConnected()) return;

        String brokerClient = (isSLL ? "ssl://" : "tcp://") + broker;
        System.out.println("[INFO] Conectando a broker: " + brokerClient + " con clientID: " + clientID);

        if (client != null) {
            try { client.disconnect(); } catch (Exception ignore) {}
            try { client.close(); } catch (Exception ignore) {}
        }

        client = new MqttClient(brokerClient, clientID);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(25); 
        options.setKeepAliveInterval(60);

        if (isLogin) {
            options.setUserName(user);
            options.setPassword(password.toCharArray());
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try { client.connect(options); } 
            catch (Exception e) { throw new RuntimeException(e); }
        });

        try {
            future.get(30, TimeUnit.SECONDS); 
            System.out.println("[INFO] Conexión MQTT establecida con éxito.");
        } catch (TimeoutException e) {
            future.cancel(true);
            client = null;
            throw new IOException("Timeout al conectar con broker MQTT");
        } catch (Exception e) {
            client = null;
            throw e;
        } finally {
            executor.shutdownNow();
        }
    }

    private void openConnect(String json, String topicL) {
        System.out.println(toStringConfig());
        int retries = 0;
        final int maxRetries = 3;

        while (retries < maxRetries) {
            try {
                ensureConnection();
                if (client == null || !client.isConnected()) throw new IOException("Cliente MQTT no conectado");

                MqttMessage message = new MqttMessage(json.getBytes());
                message.setQos(0);
                client.publish(topicL, message);
                System.out.println("[INFO] Mensaje enviado al topic: " + topicL);
                return;

            } catch (Exception e) {
                writeLogs(e);
                retries++;
                System.err.println("[WARN] Fallo al enviar mensaje, reintento " + retries + "/" + maxRetries);
                safeDisconnect();
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            }
        }

        System.err.println("[ERROR] No se pudo enviar el mensaje después de " + maxRetries + " intentos");
    }

    private void openConnectFile(File document, String topicS) {
        setConfiguration();
        int retries = 0;
        final int maxRetries = 3;
        while (retries < maxRetries) {
            try {
                ensureConnection();
                if (client == null || !client.isConnected()) throw new IOException("Cliente MQTT no conectado");

                System.out.println("[INFO] Envío de archivo a topic: " + topicS);
                int chunkSize = 4096;
                long totalSize = document.length();
                int totalChunks = (int) Math.ceil((double) totalSize / chunkSize);

                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(document))) {
                    int bytesRead;
                    int index = 0;
                    byte[] buffer = new byte[chunkSize];
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        index++;
                        byte[] chunk = new byte[bytesRead];
                        System.arraycopy(buffer, 0, chunk, 0, bytesRead);
                        String header = index + "/" + totalChunks + "|";
                        byte[] headerBytes = header.getBytes();
                        byte[] payload = new byte[headerBytes.length + chunk.length];
                        System.arraycopy(headerBytes, 0, payload, 0, headerBytes.length);
                        System.arraycopy(chunk, 0, payload, headerBytes.length, chunk.length);
                        MqttMessage message = new MqttMessage(payload);
                        message.setQos(0);
                        client.publish(topicS, message);
                        System.out.println("Fragmento " + index + "/" + totalChunks + " enviado (" + bytesRead + " bytes)");
                    }
                }

                MqttMessage eof = new MqttMessage("EOF".getBytes());
                eof.setQos(0);
                client.publish(topicS, eof);
                System.out.println("[INFO] Envío completo de archivo: " + document.getName());
                return;

            } catch (Exception e) {
                writeLogs(e);
                retries++;
                System.err.println("[WARN] Fallo al enviar archivo, reintento " + retries + "/" + maxRetries);
                safeDisconnect();
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            }
        }

        System.err.println("[ERROR] No se pudo enviar el archivo después de " + maxRetries + " intentos");
    }

    public void safeDisconnect() {
        if (client != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(() -> {
                try {
                    if (client.isConnected()) client.disconnect();
                    client.close();
                } catch (Exception e) { writeLogs(e); }
            });
            try {
                future.get(30, TimeUnit.SECONDS); 
            } catch (Exception e) {
                future.cancel(true);
                writeLogs(new Exception("Timeout al desconectar MQTT", e));
            } finally {
                executor.shutdownNow();
                client = null;
            }
        }
    }
    public void sendMessages(String json) {
        openConnect(json, topic);
    }

    public void sendLog1(File document) {
        openConnectFile(document, topicL1);
    }

    public void sendLog2(File document) {
        openConnectFile(document, topicL2);
    }

    public void sendLog3(File document) {
        openConnectFile(document, topicL3);
    }

    public void sendLog1Json(String json) {
        openConnect(json, topicJsonL1);
    }

    public void sendLog2Json(String json) {
        openConnect(json, topicJsonL2);
    }

    public void sendLog3Json(String json) {
        openConnect(json, topicJsonL3);
    }

    public void sendAudit(File document) {
        openConnectFile(document, topicAudit);
    }

    public void sendAlarm(File document) {
        openConnectFile(document, topicAlarm);
    }

    public void sendFault(File document) {
        openConnectFile(document, topicFault);
    }

    public void sendFaultJson(String Json) {
        openConnect(Json, topicFaultJson);
    }

    public void sendAlarmJson(String Json) {
        openConnect(Json, topicAlarmJson);
    }
    private void setConfiguration() {
        try {
            setClientID(ConfigSensor.clientIdMQTT[index]);
            setBroker(ConfigSensor.brokerMQTT[index]);
            setTopic(ConfigSensor.topicDataMQTT[index]);
            setUser(ConfigSensor.userMQTT[index]);
            setTopicL1(ConfigSensor.topicLog1MQTT[index]);
            setTopicL2(ConfigSensor.topicLog2MQTT[index]);
            setTopicL3(ConfigSensor.topicLog3MQTT[index]);
            setTopicAlarm(ConfigSensor.topicAlarmMQTT[index]);
            setTopicAudit(ConfigSensor.topicAuditMQTT[index]);
            setTopicFault(ConfigSensor.topicFaultMQTT[index]);
            setTopicAlarmJson(ConfigSensor.topicAlarmJsonMQTT[index]);
            setTopicFaultJson(ConfigSensor.topicFaultJsonMQTT[index]);
            setTopicJsonL1(ConfigSensor.topicLog1JsonMQTT[index]);
            setTopicJsonL2(ConfigSensor.topicLog2JsonMQTT[index]);
            setTopicJsonL3(ConfigSensor.topicLog3JsonMQTT[index]);
            setPassword(ConfigSensor.passwordMQTT[index]);
            setSLL(ConfigSensor.isSLLMQTT[index]);
            setLogin(ConfigSensor.isLoginMQTT[index]);
        } catch (Exception e) {
            writeLogs(e);
        }
        System.out.println(toStringConfig());
    }

    private void setClientID(String clientID) {
        this.clientID = (clientID == null || clientID.isBlank()) ? "" : clientID;
    }

    private void setBroker(String broker) {
        this.broker = (broker == null || broker.isBlank()) ? "" : broker;
    }

    private void setTopic(String topic) {
        this.topic = (topic == null || topic.isBlank()) ? "" : topic;
    }

    private void setUser(String user) {
        this.user = (user == null || user.isBlank()) ? "" : user;
    }

    private void setPassword(String password) {
        this.password = (password == null || password.isBlank()) ? "" : password;
    }

    private void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    private void setSLL(boolean isSLL) {
        this.isSLL = isSLL;
    }

    public String getTopicL1() {
        return topicL1;
    }

    public void setTopicL1(String topicL1) {
        this.topicL1 = topicL1;
    }

    public String getTopicL2() {
        return topicL2;
    }

    public void setTopicL2(String topicL2) {
        this.topicL2 = topicL2;
    }

    public String getTopicL3() {
        return topicL3;
    }

    public void setTopicL3(String topicL3) {
        this.topicL3 = topicL3;
    }

    public String getTopicJsonL1() {
        return topicJsonL1;
    }

    public void setTopicJsonL1(String topicJsonL1) {
        this.topicJsonL1 = topicJsonL1;
    }

    public String getTopicJsonL2() {
        return topicJsonL2;
    }

    public void setTopicJsonL2(String topicJsonL2) {
        this.topicJsonL2 = topicJsonL2;
    }

    public String getTopicJsonL3() {
        return topicJsonL3;
    }

    public void setTopicJsonL3(String topicJsonL3) {
        this.topicJsonL3 = topicJsonL3;
    }

    public String getTopicAlarm() {
        return topicAlarm;
    }

    public void setTopicAlarm(String topicAlarm) {
        this.topicAlarm = topicAlarm;
    }

    public String getTopicFault() {
        return topicFault;
    }

    public void setTopicFault(String topicFault) {
        this.topicFault = topicFault;
    }

    public String getTopicAudit() {
        return topicAudit;
    }

    public void setTopicAudit(String topicAudit) {
        this.topicAudit = topicAudit;
    }

    public String getTopicFaultJson() {
        return topicFaultJson;
    }

    public void setTopicFaultJson(String topicFaultJson) {
        this.topicFaultJson = topicFaultJson;
    }

    public String getTopicAlarmJson() {
        return topicAlarmJson;
    }

    public void setTopicAlarmJson(String topicAlarmJson) {
        this.topicAlarmJson = topicAlarmJson;
    }
}
