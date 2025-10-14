package com.smarts.Comunications.Protocols;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.smarts.Config.ConfigSensor;

public class Mqtt {
    private String clientID;
    private String broker;              // host:port (sin protocolo)
    private String topic;               // data topic
    private String topicL1, topicL2, topicL3;
    private String topicAlarm, topicFault, topicAudit;
    private String topicJsonL1, topicJsonL2, topicJsonL3;
    private String topicFaultJson, topicAlarmJson;
    private String user, password;
    private boolean isLogin;
    private boolean isTLS;              // (antes isSLL)
    private final String pathLogs = "/home/EPI5/.Smarts/logMqtt.txt";

    private IMqttClient client;         // cliente reutilizable

    public Mqtt() {
        setConfiguration();
        ensureClient();
        System.out.println("[MQTT] Config listo: " + safeConfigString());
    }

    /* ===================== API pública ===================== */

    /** Envía un JSON al tópico principal (QoS 1, no retenido). */
    public void sendMessages(String json) { publishJson(json, topic, null, 0); }

    public void sendLog1(File document){ publishFileChunked(document, topicL1); }
    public void sendLog2(File document){ publishFileChunked(document, topicL2); }
    public void sendLog3(File document){ publishFileChunked(document, topicL3); }

    public void sendLog1Json(String json){ publishJson(json, topicJsonL1, null, 0); }
    public void sendLog2Json(String json){ publishJson(json, topicJsonL2, null, 0); }
    public void sendLog3Json(String json){ publishJson(json, topicJsonL3, null, 0); }

    public void sendAudit(File document){ publishFileChunked(document, topicAudit); }
    public void sendAlarm(File document){ publishFileChunked(document, topicAlarm); }
    public void sendFault(File document){ publishFileChunked(document, topicFault); }

    public void sendFaultJson(String json){ publishJson(json, topicFaultJson, null, 0); }
    public void sendAlarmJson(String json){ publishJson(json, topicAlarmJson, null, 0); }

    public String sendJsonAndWaitResponse(String json, String topicBase, long timeoutMs) {
        Objects.requireNonNull(topicBase, "topicBase");
        String responseTopic = topicBase + "/response";
        final CountDownLatch latch = new CountDownLatch(1);
        final StringBuilder responseHolder = new StringBuilder();

        try {
            ensureClient();
            client.subscribe(responseTopic, (t, msg) -> {
                responseHolder.setLength(0);
                responseHolder.append(new String(msg.getPayload()));
                latch.countDown();
            });

            publishJson(json, topicBase, null, 0);

            boolean ok = latch.await(Math.max(1L, timeoutMs), TimeUnit.MILLISECONDS);
            client.unsubscribe(responseTopic);
            return ok ? responseHolder.toString() : null;
        } catch (Exception e) {
            writeLogs(e);
            return null;
        }
    }

    public void close() {
        if (client != null && client.isConnected()) {
            try { client.disconnect(); } catch (Exception ignored) {}
        }
        try { if (client != null) client.close(); } catch (Exception ignored) {}
    }


    private synchronized void ensureClient() {
        try {
            if (client != null && client.isConnected()) return;
            String protocol = isTLS ? "ssl://" : "tcp://";
            String url = protocol + broker;
            if (clientID == null || clientID.isBlank()) {
                clientID = "smarts-" + UUID.randomUUID();
            }
            if (client != null) {
                try { client.close(); } catch (Exception ignored) {}
            }
            client = new MqttClient(url, clientID);

            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setAutomaticReconnect(true);
            opts.setCleanSession(true);
            if (isLogin) {
                opts.setUserName(user);
                opts.setPassword(password != null ? password.toCharArray() : new char[0]);
            }
            client.connect(opts);
            System.out.println("[MQTT] Conectado a " + url + " con clientID=" + clientID + (isTLS ? " (TLS)" : ""));
        } catch (MqttException e) {
            writeLogs(e);
            throw new RuntimeException("No se pudo conectar al broker MQTT", e);
        }
    }

    private void publishJson(String json, String topicTarget, Integer qos, Integer retained) {
        try {
            ensureClient();
            MqttMessage message = new MqttMessage((json != null ? json : "").getBytes());
            message.setQos(qos != null ? qos : 1);
            message.setRetained(retained != null && retained > 0);
            client.publish(topicTarget, message);
        } catch (Exception e) {
            writeLogs(e);
        }
    }

    private void publishFileChunked(File document, String topicTarget) {
        if (document == null || !document.exists() || !document.isFile()) {
            writeLogs(new IllegalArgumentException("Archivo inválido: " + document));
            return;
        }
        final int chunkSize = 4096;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(document))) {
            ensureClient();
            long totalSize = document.length();
            int totalChunks = (int) Math.ceil((double) totalSize / chunkSize);

            byte[] buffer = new byte[chunkSize];
            int bytesRead;
            int index = 0;

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
                client.publish(topicTarget, message);
            }

            // Señal de fin de archivo en el MISMO tópico del archivo (fix respecto a tu versión)
            MqttMessage eof = new MqttMessage("EOF".getBytes());
            eof.setQos(1);
            client.publish(topicTarget, eof);

        } catch (Exception e) {
            writeLogs(e);
        }
    }

    /* ===================== Config & utilidades ===================== */

    private void setConfiguration() {
        try {
            this.clientID       = nvl(ConfigSensor.clientIdMQTT);
            this.broker         = nvl(ConfigSensor.brokerMQTT);      // ej: "localhost:1883"
            this.topic          = nvl(ConfigSensor.topicDataMQTT);

            this.topicL1        = nvl(ConfigSensor.topicLog1MQTT);
            this.topicL2        = nvl(ConfigSensor.topicLog2MQTT);
            this.topicL3        = nvl(ConfigSensor.topicLog3MQTT);

            this.topicAlarm     = nvl(ConfigSensor.topicAlarmMQTT);
            this.topicFault     = nvl(ConfigSensor.topicFaultMQTT);
            this.topicAudit     = nvl(ConfigSensor.topicAuditMQTT);

            this.topicJsonL1    = nvl(ConfigSensor.topicLog1JsonMQTT);
            this.topicJsonL2    = nvl(ConfigSensor.topicLog2JsonMQTT);
            this.topicJsonL3    = nvl(ConfigSensor.topicLog3JsonMQTT);

            this.topicFaultJson = nvl(ConfigSensor.topicFaultJsonMQTT);
            this.topicAlarmJson = nvl(ConfigSensor.topicAlarmJsonMQTT);

            this.user           = nvl(ConfigSensor.userMQTT);
            this.password       = nvl(ConfigSensor.passwordMQTT);
            this.isTLS          = ConfigSensor.isSLLMQTT; // si tu config se llama isSLLMQTT, úsala aquí pero como TLS
            this.isLogin        = ConfigSensor.isLoginMQTT;
        } catch (Exception e) {
            writeLogs(e);
        }
        System.err.println("safeConfigString() = " + safeConfigString());
    }

    private String nvl(String s) { return (s == null) ? "" : s.trim(); }

    private String safeConfigString() {
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
                ", user='" + (user.isEmpty() ? "" : "***") + '\'' +
                ", isLogin=" + isLogin +
                ", isTLS=" + isTLS +
                '}';
    }

    private void writeLogs(Exception ex) {
        try {
            File logMqtt = new File(pathLogs);
            if (!logMqtt.exists()) {
                File parent = logMqtt.getParentFile();
                if (parent != null) parent.mkdirs();
                logMqtt.createNewFile();
            }
            try (FileWriter fw = new FileWriter(logMqtt, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println("[" + LocalDateTime.now() + "] ERROR:");
                ex.printStackTrace(pw);
                pw.println("--------------------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
