package com.smarts.serialL;

import com.smarts.Comunications.Protocols.SerialHelper;
import com.smarts.Config.ConfigM2;
import com.smarts.Config.ConfigSensor;
public class ManageData {
    private final LiveData liveDataSmarts;
    private final LogsData logsData;

    public ManageData(){
        ConfigSensor.setConfiguration();
        SerialHelper.findPort();
        ///--se necesita un id para generar las update ConfigM2.setConfiguration();
        liveDataSmarts=new LiveData();
        while (liveDataSmarts.serialData(SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x6E), 14)).equals("0")){ 
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
        ConfigM2.idMC2=liveDataSmarts.serialData(SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x6E), 14));
        ConfigM2.setConfiguration();
        logsData=new LogsData();
        setupDataOneProcess();
    }
    private void setupDataOneProcess(){
        while (true) {
            try {
                 int countLogs1=logsData.getPtrLog1();
                int countLogs2=logsData.getPtrLog2(); 
                int countLogs3=logsData.getPtrLog3(); 
                int countAlarmn=logsData.getPtrLogAlarm();
                int countFault=logsData.getPtrLogFault();
                liveDataSmarts.liveDataRequest();
                logsData.readLogginSettings();
                if(countLogs1!=logsData.getPtrLog1()){
                    logsData.getLastlog1();
                }
                if(countLogs2!=logsData.getPtrLog2()){
                    logsData.getLastlog2();
                }
                if(countLogs3!=logsData.getPtrLog3()){
                    logsData.getLastlog3();
                }
                if(countAlarmn!=logsData.getPtrLogAlarm()){
                    logsData.getLastAlarmLogs();
                }
                if(countFault!=logsData.getPtrLogFault()){
                    logsData.getLastFaultLogs();                
                } 
                Thread.sleep(liveDataSmarts.time);
            } catch (InterruptedException e) {
                System.out.println("Falla en el manejo de procesos");
            }
        }

    }
}
