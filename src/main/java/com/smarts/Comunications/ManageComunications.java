package com.smarts.Comunications;

import java.io.File;

import com.smarts.Comunications.Protocols.APIRest;
import com.smarts.Comunications.Protocols.Mqtt;
import com.smarts.Comunications.Protocols.Websockets;
import com.smarts.Config.ConfigSensor;

public class ManageComunications {
    private static boolean isMQTT;
    private static boolean isApi;   
    private static boolean isWebsocket;   
    private static boolean isWitsml;   
    private static Mqtt mqtt[]= new Mqtt[10];
    private static APIRest[] api= new APIRest[10];
    private static Websockets[] webS= new Websockets[10];
    
    public static void manageComunications() {
        System.out.println("[DEBUG] Iniciando constructor ManageComunications");
        System.out.println("[DEBUG] Constructor ManageComunications finalizado");
    }
    private void setComunications(){
        for (int i =0;i<ConfigSensor.brokerMQTT.length;i++) {
            mqtt[i]= new Mqtt(i);
        }
        for (int i =0;i<ConfigSensor.brokerMQTT.length;i++) {
            api[i]= new APIRest(i);
        }
        for (int i =0;i<ConfigSensor.brokerMQTT.length;i++) {
            webS[i]= new Websockets(i);
        }
    }
    private static void sendData(String Json,int index){
        try {
            isMQTT=ConfigSensor.isMQTT[index];
            isApi=ConfigSensor.isApi[index];
            isWebsocket=ConfigSensor.isWebsocket[index];
            isWitsml=ConfigSensor.isWitsml[index];
            System.out.println("[DEBUG] Iniciando sendData con Json: " + Json);
            System.out.println("[DEBUG] isMQTT: " + isMQTT + ", isApi: " + isApi + ", isWebsocket: " + isWebsocket + ", isWitsml: " + isWitsml);
            if(isMQTT){
                System.out.println("[DEBUG] Enviando el Json por MQTT");
                mqtt[index].sendMessages(Json);
            }
            if(isApi){
                System.out.println("[DEBUG] Enviando el Json por API");
                api[index].sendDataApi(Json);
            }
            if(isWebsocket){
                System.out.println("[DEBUG] Enviando el Json por WebSocket");
                webS[index].sendData(Json);
            }
            System.out.println("[DEBUG] Finalizando sendData");
            if(isWitsml){
                //generar el formato en wtisml
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] Error en sendData: " + e.getMessage());
        }
    }
    public static void sendData(String Json){
        for (int i = 0; i < ConfigSensor.brokerMQTT.length; i++) {
            sendData(Json,i);
        }
    }
    public static void sendDocuments(int type, String path){
        for (int i = 0; i < ConfigSensor.uriLog1Api.length; i++) {
            sendDocuments(type,path,i);
        }
    }
    private static void sendDocuments(int type, String path,int index){
        try {
            isMQTT=ConfigSensor.isMQTT[index];
            isApi=ConfigSensor.isApi[index];
            isWebsocket=ConfigSensor.isWebsocket[index];
            isWitsml=ConfigSensor.isWitsml[index];
            if(isMQTT){
            switch (type) {
                case 1 -> mqtt[index].sendLog1(new File(path));
                case 2 -> mqtt[index].sendLog2(new File(path));
                case 3 -> mqtt[index].sendLog3(new File(path));
                case 4 -> mqtt[index].sendAlarm(new File(path));
                case 5 -> mqtt[index].sendFault(new File(path));
                case 6 -> mqtt[index].sendAudit(new File(path));
            }
        }
        if(isApi){
            switch (type) {
                case 1 -> api[index].sendLog1(path);
                case 2 -> api[index].sendLog2(path);
                case 3 -> api[index].sendLog3(path);
                case 4 -> api[index].sendAlarm(path);
                case 5 -> api[index].sendFault(path);
                case 6 -> api[index].sendAudit(path);
            }
        }
        if(isWebsocket){
            switch (type) {
                case 1 -> webS[index].sendLog1(path);
                case 2 -> webS[index].sendLog2(path);
                case 3 -> webS[index].sendLog3(path);
                case 4 -> webS[index].sendAlarm(path);
                case 5 -> webS[index].sendFault(path);
                case 6 -> webS[index].sendAudit(path);
            }
        }
        } catch (Exception e) {
            System.out.println("Error en sendDocuments: " + e.getMessage());
        }
           
    }
    public static void sendJsonLogs(int type, String path){
        for (int i = 0; i < ConfigSensor.uriLog1JsonApi.length; i++) {
            sendJsonLogs(type,path,i);
        }
    }
    private static void sendJsonLogs(int type, String Json, int index){
        try {
            if(isMQTT){
            switch (type) {
                case 1 -> mqtt[index].sendLog1Json(Json);
                case 2 -> mqtt[index].sendLog2Json(Json);
                case 3 -> mqtt[index].sendLog3Json(Json);
                case 4 -> mqtt[index].sendAlarmJson(Json);
                case 5 -> mqtt[index].sendFaultJson(Json);
            }
        }
        if(isApi){
            switch (type) {
                case 1 -> api[index].sendLog1Json(Json);
                case 2 -> api[index].sendLog2Json(Json);
                case 3 -> api[index].sendLog3Json(Json);
                case 4 -> api[index].sendAlarmJson(Json);
                case 5 -> api[index].sendFaultJson(Json);
            }
        }
        if(isWebsocket){
            switch (type) {
                case 1 -> webS[index].sendLog1Json(Json);
                case 2 -> webS[index].sendLog2Json(Json);
                case 3 -> webS[index].sendLog3Json(Json);
                case 4 -> webS[index].sendAlarmJson(Json);
                case 5 -> webS[index].sendFaultJson(Json);
            }
        }
        } catch (Exception e) {
            System.out.println("Error en sendJsonLogs: " + e.getMessage());
        }
    }
    public boolean isIsMQTT() {
        return isMQTT;
    }
    public void setIsMQTT(boolean isMQTT) {
        ManageComunications.isMQTT = isMQTT;
    }
    
    public boolean isIsApi() {
        return isApi;
    }
    
    public void setIsApi(boolean isApi) {
        ManageComunications.isApi = isApi;
    }
    
    public boolean isIsWebsocket() {
        return isWebsocket;
    }
    
    public void setIsWebsocket(boolean isWebsocket) {
        ManageComunications.isWebsocket = isWebsocket;
    }
    
    public boolean isIsWitsml() {
        return isWitsml;
    }
    
    public void setIsWitsml(boolean isWitsml) {
        ManageComunications.isWitsml = isWitsml;
    }
    
}
