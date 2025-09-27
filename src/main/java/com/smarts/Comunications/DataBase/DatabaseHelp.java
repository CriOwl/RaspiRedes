package com.smarts.Comunications.DataBase;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public abstract class DatabaseHelp {
    private static final String Bd_Path = "mongodb://localhost:27017/";
    private static final String Bd_Base = "Smarts";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoDatabase getConnection() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(Bd_Path);
            database = mongoClient.getDatabase(Bd_Base);
        }
        return database;
    }
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }
}
