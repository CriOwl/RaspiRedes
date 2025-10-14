package com.smarts;
import com.smarts.Comunications.Protocols.SerialHelper;
import com.smarts.serialL.ManageData;

public class Main {
  public static void main(String[] args) throws Exception {
    
   Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("[DEBUG] Ejecutando shutdown hook");
      SerialHelper.eliminarUsoSerial();
        }));
    System.out.println("[DEBUG] Creando instancia de ManageData");
    ManageData Dresser = new ManageData();
    System.out.println("[DEBUG] Finalizando método main");
  }
}
