package com.smarts.Comunications.DataBase;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public abstract class DatabaseHelp {
    private static final String Bd_Path = "mongodb+srv://sujetodeprueba1234_db_user:6mGda81Q2SN8X5VF@cluster0.17z9r15.mongodb.net/";
    private static final String Bd_Base = "smarts";
    //6mGda81Q2SN8X5VF
    //
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoDatabase getConnection() {
        System.out.println("[DEBUG] Obteniendo conexión a la base de datos");
        if (mongoClient == null) {
            System.out.println("[DEBUG] Creando nuevo cliente MongoDB");
            mongoClient = MongoClients.create(Bd_Path);
            database = mongoClient.getDatabase(Bd_Base);
        }
        System.out.println("[DEBUG] Conexión obtenida");
        return database;
    }
    public static void closeConnection() {
        System.out.println("[DEBUG] Cerrando conexión a la base de datos");
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("[DEBUG] Conexión cerrada");
        }
    }
}
