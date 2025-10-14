package com.smarts.serialL;

import com.smarts.Comunications.ManageComunications;
import com.smarts.Comunications.Protocols.SerialHelper;
import com.smarts.Config.ConfigM2;
import com.smarts.Config.ConfigSensor;
public class ManageData {
    private final LiveData liveDataSmarts;
    private final LogsData logsData=null;

    public ManageData(){
        System.out.println("[DEBUG] Iniciando constructor ManageData");
        ConfigSensor.setConfiguration();
        System.out.println("[DEBUG] Configuración de sensor establecida");
        SerialHelper.findPort();
        System.out.println("[DEBUG] Puerto serial encontrado");
        ManageComunications.manageComunications();
        liveDataSmarts=new LiveData();
        System.out.println("[DEBUG] Instancia de LiveData creada");
        ConfigM2.idMC2=liveDataSmarts.serialData(SerialHelper.stablishConnection(SerialHelper.createDataRequestPacket((byte) 0x6E), 14));
        System.out.println("[DEBUG] idMC2 asignado: " + ConfigM2.idMC2);
        ConfigM2.setConfiguration();
        System.out.println("[DEBUG] Configuración de MC2 establecida");
        setupDataOneProcess();
        System.out.println("[DEBUG] Constructor ManageData finalizado");
    }
    private void setupDataOneProcess(){
        System.out.println("[DEBUG] Iniciando setupDataOneProcess");
        while (true) {
            try {
                System.out.println("[DEBUG] Ejecutando ciclo de setupDataOneProcess");
                //int countLogs1=logsData.getPtrLog1();
                //int countLogs2=logsData.getPtrLog2(); 
                //int countLogs3=logsData.getPtrLog3(); 
                //int countAlarmn=logsData.getPtrLogAlarm();
                //int countFault=logsData.getPtrLogFault();
                liveDataSmarts.liveDataRequest();
                //logsData.readLogginSettings();
                //if(countLogs1!=logsData.getPtrLog1()){
                //    logsData.getLastlog1();
                //}
                //if(countLogs2!=logsData.getPtrLog2()){
                //    logsData.getLastlog2();
                //}
                //if(countLogs3!=logsData.getPtrLog3()){
                //    logsData.getLastlog3();
                //}
                //if(countAlarmn!=logsData.getPtrLogAlarm()){
                //    logsData.getLastAlarmLogs();
                //}
                //if(countFault!=logsData.getPtrLogFault()){
                //    logsData.getLastFaultLogs();                
                //} 
                Thread.sleep(liveDataSmarts.time);
            } catch (InterruptedException e) {
                System.out.println("Falla en el manejo de procesos");
            }
        }

    }
}
