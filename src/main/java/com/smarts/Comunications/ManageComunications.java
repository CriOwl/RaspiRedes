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
        setIsMQTT(ConfigSensor.isMQTT());
        setIsApi(ConfigSensor.isApi());
        setIsWebsocket(ConfigSensor.isWebsocket());
        setIsWitsml(ConfigSensor.isWitsml());
        mqtt= new Mqtt();
        api=new APIRest();
        webS= new Websockets();
    }
    public static void sendData(String Json){
        if(isMQTT){
            mqtt.sendMessages(Json);
        }
        if(isApi){
            api.senDataApiLive(Json);
        }
        if(isWebsocket){
            webS.sendJson(Json);
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
            }
        }
        if(isApi){
            switch (type) {
                case 1 -> api.sendLog1(path);
                case 2 -> api.sendLog2(path);
                case 3 -> api.sendLog3(path);
            }
        }
        if(isWebsocket){
            switch (type) {
                case 1 -> webS.sendLog1(path);
                case 2 -> webS.sendLog2(path);
                case 3 -> webS.sendLog3(path);
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
