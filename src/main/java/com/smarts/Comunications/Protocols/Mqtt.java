package com.smarts.Comunications.Protocols;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
    private final String delimeter = ",";
    
    // 🔹 Cliente persistente MQTT
    private IMqttClient client;

    public Mqtt() {
        System.out.println("[DEBUG] Iniciando constructor Mqtt");
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

    // 🔹 Nuevo método para mantener conexión persistente
    private void ensureConnection() throws Exception {
        if (client != null && client.isConnected()) {
            return; // ya conectado
        }

        String brokerClient = (isSLL) ? "ssl://" : "tcp://";
        brokerClient += broker;
        System.out.println("[INFO] Conectando a broker: " + brokerClient + " con clientID: " + clientID);

        client = new MqttClient(brokerClient, clientID);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false); // mantener sesión activa

        if (isLogin) {
            options.setUserName(user);
            options.setPassword(password.toCharArray());
        }

        client.connect(options);
        System.out.println("[INFO] Conexión MQTT establecida con éxito.");
    }

    private void openConnect(String json, String topicL) {
        System.out.println(toStringConfig());
        try {
            ensureConnection();
            String topicResponse = topicL + "/response";
            if (!client.isConnected()) return;
            client.subscribe(topicResponse, (topic, message) -> {
                String response = new String(message.getPayload());
                System.out.println("Respuesta recibida: " + response);
            });
            MqttMessage message = new MqttMessage(json.getBytes());
            message.setQos(1);
            client.publish(topicL, message);
            System.out.println("[INFO] Mensaje enviado al topic: " + topicL);

        } catch (Exception e) {
            writeLogs(e);
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

    private void openConnectFile(File document, String topicS) {
        setConfiguration();
        try {
            ensureConnection();
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
                    message.setQos(1);
                    client.publish(topicS, message);
                    System.out.println("Fragmento " + index + "/" + totalChunks + " enviado (" + bytesRead + " bytes)");
                }
            }

            MqttMessage eof = new MqttMessage("EOF".getBytes());
            eof.setQos(1);
            client.publish(topicS, eof);
            System.out.println("Envío completo de archivo: " + document.getName());

        } catch (Exception e) {
            writeLogs(e);
        }
    }

    private void setConfiguration() {
        try {
            setClientID(ConfigSensor.clientIdMQTT);
            setBroker(ConfigSensor.brokerMQTT);
            setTopic(ConfigSensor.topicDataMQTT);
            setUser(ConfigSensor.userMQTT);
            setTopicL1(ConfigSensor.topicLog1MQTT);
            setTopicL2(ConfigSensor.topicLog2MQTT);
            setTopicL3(ConfigSensor.topicLog3MQTT);
            setTopicAlarm(ConfigSensor.topicAlarmMQTT);
            setTopicAudit(ConfigSensor.topicAuditMQTT);
            setTopicFault(ConfigSensor.topicFaultMQTT);
            setTopicAlarmJson(ConfigSensor.topicAlarmJsonMQTT);
            setTopicFaultJson(ConfigSensor.topicFaultJsonMQTT);
            setTopicJsonL1(ConfigSensor.topicLog1JsonMQTT);
            setTopicJsonL2(ConfigSensor.topicLog2JsonMQTT);
            setTopicJsonL3(ConfigSensor.topicLog3JsonMQTT);
            setPassword(ConfigSensor.passwordMQTT);
            setSLL(ConfigSensor.isSLLMQTT);
            setLogin(ConfigSensor.isLoginMQTT);
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
