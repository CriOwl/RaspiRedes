package com.smarts.serialL;

import com.smarts.Comunications.SerialHelper;
public class ManageData {
    private final LiveData liveDataSmarts;
    private final LogsData logsData;

    public ManageData(){
        SerialHelper.findPort();
        liveDataSmarts=new LiveData();
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
