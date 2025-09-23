package com.smarts.Comunications;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public  class Mqtt {
    private String clientID;
    private String broker;
    private String topic;
    private String user;
    private String password;
    private final String path = "/etc/.Smarts/config.txt";
    private final String pathLogs = "/var/log/.Smarts/logMqtt.txt";
    private boolean isLogin;
    private boolean isSLL;
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
    public void openConnect(String Json){
        if(!isLogin && !isSLL){
            try {
                IMqttClient client = new MqttClient("tcp://"+broker,clientID);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                client.connect(options);
                System.out.println("Conectado al broker: " + broker);              
                String contenido = Json;
                MqttMessage message = new MqttMessage(contenido.getBytes());
                message.setQos(1);
                client.publish(topic, message);
                Thread.sleep(2000);
                client.disconnect();
                System.out.println("Desconectado.");
                client.close();
                
            } catch (Exception e) {
                writeLogs(e);
            }
            return;
        }else if(!isLogin && isSLL){
            try {
                IMqttClient client = new MqttClient("ssl://"+broker, clientID);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                client.connect(options);
                System.out.println("Conectado al broker con TLS");
                String contenido =Json;
                MqttMessage message = new MqttMessage(contenido .getBytes());
                message.setQos(1);
                client.publish(topic, message);
                System.out.println("Publicado: " + contenido);
                Thread.sleep(2000);
                client.disconnect();
                System.out.println("Desconectado.");
                client.close();
                
            } catch (Exception e) {
                writeLogs(e);
            }
        }else if(isLogin && !isSLL){
            try {
                IMqttClient client = new MqttClient("tcp://"+broker, clientID);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                options.setUserName(user);
                options.setPassword(password.toCharArray());
                client.connect(options);
                String contenido = Json;
                MqttMessage message = new MqttMessage(contenido.getBytes());
                message.setQos(1);
                client.publish(topic, message);
                System.out.println(": " + contenido);
                Thread.sleep(2000); 
                client.disconnect();
                client.close();
                
            } catch (Exception e) {
                writeLogs(e);
            }
        }else{
            try {
                IMqttClient client = new MqttClient("ssl://"+broker, clientID);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                options.setUserName(user);
                options.setPassword(password.toCharArray());
                client.connect(options);
                System.out.println("Conectado al broker con TLS");
                String contenido = Json;
                MqttMessage message = new MqttMessage(contenido.getBytes());
                message.setQos(1);
                client.publish(topic, message);
                System.out.println("Publicado: " + contenido);
                Thread.sleep(2000);
                client.disconnect();
                System.out.println("Desconectado.");
                client.close();
            } catch (Exception e) {
                writeLogs(e);
            }
        }
        
    }
    public void sendMessages(String json){
        openConnect(json);
    }
    
    private void setConfiguration() {
        try {
            File configFile = new File(path);
            if (!configFile.exists()) {
                setClientID(clientID);
                setBroker(broker);
                setTopic(topic);
                setUser(user);
                setPassword(password);
                setSLL(false);
                setLogin(false);
            }
            try (Scanner lector = new Scanner(configFile)) {
                lector.useDelimiter(delimeter);
                String line;
                while (lector.hasNextLine()) {
                    line = lector.nextLine();
                    if (line.contains("brokerMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setBroker(line);
                    } else if (line.contains("topicMQTT:")){
                        line=line.substring(line.indexOf(':')+1);
                        setTopic(line);
                    }
                    else if (line.contains("clientIdMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setClientID(line);
                    } else if (line.contains("userMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setUser(line);
                    } else if (line.contains("passwordMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setPassword(line);
                    } else if (line.contains("isLoginMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setLogin(line.contains("true"));
                    } else if (line.contains("isSLLMQTT:")) {
                        line=line.substring(line.indexOf(':')+1);
                        setSLL(line.contains("true"));
                    }
                }
            }
            
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
}
