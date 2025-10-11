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
    private static Mqtt mqtt;
    private static APIRest api;
    private static Websockets webS;

    public ManageComunications(){
        setIsMQTT(ConfigSensor.isMQTT);
        setIsApi(ConfigSensor.isApi);
        setIsWebsocket(ConfigSensor.isWebsocket);
        setIsWitsml(ConfigSensor.isWitsml);
        mqtt= new Mqtt();
        api=new APIRest();
        webS= new Websockets();
    }
    public static void sendData(String Json){
        if(isMQTT){
            mqtt.sendMessages(Json);
        }
        if(isApi){
            api.sendDataApi(Json);
        }
        if(isWebsocket){
            webS.sendData(Json);
        }
        if(isWitsml){
            //generar el formato en wtisml
        }
    }
    public static void sendDocuments(int type, String path){
        if(isMQTT){
            switch (type) {
                case 1 -> mqtt.sendLog1(new File(path));
                case 2 -> mqtt.sendLog2(new File(path));
                case 3 -> mqtt.sendLog3(new File(path));
                case 4 -> mqtt.sendAlarm(new File(path));
                case 5 -> mqtt.sendFault(new File(path));
                case 6 -> mqtt.sendAudit(new File(path));
            }
        }
        if(isApi){
            switch (type) {
                case 1 -> api.sendLog1(path);
                case 2 -> api.sendLog2(path);
                case 3 -> api.sendLog3(path);
                case 4 -> api.sendAlarm(path);
                case 5 -> api.sendFault(path);
                case 6 -> api.sendAudit(path);
            }
        }
        if(isWebsocket){
            switch (type) {
                case 1 -> webS.sendLog1(path);
                case 2 -> webS.sendLog2(path);
                case 3 -> webS.sendLog3(path);
                case 4 -> webS.sendAlarm(path);
                case 5 -> webS.sendFault(path);
                case 6 -> webS.sendAudit(path);
            }
        }
    }
    public static void sendJsonLogs(int type, String Json){
        if(isMQTT){
            switch (type) {
                case 1 -> mqtt.sendLog1Json(Json);
                case 2 -> mqtt.sendLog2Json(Json);
                case 3 -> mqtt.sendLog3Json(Json);
                case 4 -> mqtt.sendAlarmJson(Json);
                case 5 -> mqtt.sendFaultJson(Json);
            }
        }
        if(isApi){
            switch (type) {
                case 1 -> api.sendLog1Json(Json);
                case 2 -> api.sendLog2Json(Json);
                case 3 -> api.sendLog3Json(Json);
                case 4 -> api.sendAlarmJson(Json);
                case 5 -> api.sendFaultJson(Json);
            }
        }
        if(isWebsocket){
            switch (type) {
                case 1 -> webS.sendLog1Json(Json);
                case 2 -> webS.sendLog2Json(Json);
                case 3 -> webS.sendLog3Json(Json);
                case 4 -> webS.sendAlarmJson(Json);
                case 5 -> webS.sendFaultJson(Json);
            }
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
