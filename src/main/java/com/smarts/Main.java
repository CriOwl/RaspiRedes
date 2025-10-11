package com.smarts;
import com.smarts.Comunications.Protocols.SerialHelper;
import com.smarts.serialL.ManageData;

public class Main {
  public static void main(String[] args) throws Exception {
    
   Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SerialHelper.eliminarUsoSerial();
        }));
   ManageData Dresser = new ManageData();
  }
}
