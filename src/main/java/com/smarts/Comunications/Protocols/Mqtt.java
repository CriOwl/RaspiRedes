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
    private String topic;
    private String user;
    private String password;
    private final String pathLogs = "/var/log/.Smarts/logMqtt"+ConfigSensor.port+".txt";
    private boolean isLogin;
    private boolean isSLL;
    private final String delimeter = ",";
    
    public Mqtt() {
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
    private void openConnect(String json){
        try {
            String brokerCliend=(isSLL)?"ssl://":"tcp://";
            brokerCliend=brokerCliend+broker;
            IMqttClient client= new MqttClient(brokerCliend, clientID);
            MqttConnectOptions options= new MqttConnectOptions();
            options.setCleanSession(true);
            if(isLogin){
                options.setUserName(user);
                options.setPassword(password.toCharArray());
            }
            client.connect(options);
            MqttMessage message = new MqttMessage(json.getBytes());
            message.setQos(1);
            client.publish(topic, message);
            Thread.sleep(1000);
            client.disconnect();            
        } catch (Exception e) {
            writeLogs(e);
        }
    }
    
    public void sendMessages(String json){
        openConnect(json);
    }
    public void sendLog1(File document){
        openConnectFile(document, topicL1);
    }
    public void sendLog2(File document){
        openConnectFile(document, topicL2);
    }
    public void sendLog3(File document){
        openConnectFile(document, topicL3);
    }
    private void openConnectFile(File document, String topicS) {
        try {
            String protocol = isSLL ? "ssl://" : "tcp://";
            IMqttClient client = new MqttClient(protocol + broker, clientID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            if (isLogin) {
                options.setUserName(user);
                options.setPassword(password.toCharArray());
            }
            client.connect(options);
            System.out.println("Conectado al broker: " + broker + " (" + (isSLL ? "TLS" : "No TLS") + ")");
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
            client.publish(topic, eof);
            System.out.println("Envío completo de archivo: " + document.getName());
            Thread.sleep(1000);
            client.disconnect();
            client.close();
            System.out.println("Desconectado.");
            
        } catch (Exception e) {
            writeLogs(e);
        }
    }
    
    
    private void setConfiguration() {
        try {
                setClientID(ConfigSensor.getClientIdMQTT());
                setBroker(ConfigSensor.getBrokerMQTT());
                setTopic(ConfigSensor.getTopicDataMQTT());
                setUser(ConfigSensor.getUserMQTT());
                setTopicL1(ConfigSensor.getTopicLog1MQTT());
                setTopicL2(ConfigSensor.getTopicLog2MQTT());
                setTopicL3(ConfigSensor.getTopicLog3MQTT());
                setPassword(ConfigSensor.getPasswordMQTT());
                setSLL(ConfigSensor.isSLLMQTT());
                setLogin(ConfigSensor.isLoginMQTT());
        } catch (Exception e) {
            writeLogs(e);
        }
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
    
}
