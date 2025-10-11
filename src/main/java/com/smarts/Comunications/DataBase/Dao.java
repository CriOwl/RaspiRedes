package com.smarts.Comunications.DataBase;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Dao {
    public static String addJsonCollection(String Json, String nameCollection, String id,String type){
        MongoDatabase database= DatabaseHelp.getConnection();
        MongoCollection<Document> collection=database.getCollection(nameCollection);
        if(collection==null){
            database.createCollection(nameCollection);
            
        }else{ 
            long totalDocuments= collection.estimatedDocumentCount();
            if(totalDocuments>1440){
                DateTimeFormatter dtfActually = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime nowActually = LocalDateTime.now();
                String nowDate = dtfActually.format(LocalDateTime.now()).toString();
                nameCollection=type+"_"+id+"_"+nowDate;
            }
        }
        collection= database.getCollection(nameCollection);
        Document jsonValue=Document.parse(Json);
        collection.insertOne(jsonValue);
        DatabaseHelp.closeConnection();
        return nameCollection;
    }
    public static String date(String id){
        DateTimeFormatter dtfActually = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime nowActually = LocalDateTime.now();
        String nowDate = dtfActually.format(LocalDateTime.now()).toString();
        String nameCollection=id+"_"+nowDate;
        System.out.println(nameCollection);
        DatabaseHelp.closeConnection();
        return nameCollection;
    }
    public static void dropCollection(String nameCollection){
        MongoDatabase database= DatabaseHelp.getConnection();
        MongoCollection<Document> collection=database.getCollection(nameCollection);
        if(collection==null){
            DatabaseHelp.closeConnection();
            return;
        }
        collection.drop();
        DatabaseHelp.closeConnection();
    }
    public static String generateCSV(String nameCollection, String path){
        MongoDatabase database= DatabaseHelp.getConnection();
        MongoCollection<Document> collection=database.getCollection(nameCollection);
        if(collection==null){
            return null;
        } Set<String> campos = new LinkedHashSet<>();
        FindIterable<Document> docsForFields = collection.find().batchSize(1000);
        for (Document doc : docsForFields) {
            campos.addAll(doc.keySet());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(String.join(",", campos));
            bw.newLine();
            FindIterable<Document> docs = collection.find().batchSize(1000);
            for (Document doc : docs) {
                StringBuilder fila = new StringBuilder();
                int i = 0;
                for (String campo : campos) {
                    Object valor = doc.get(campo);
                    String valorStr = valor != null ? valor.toString().replace("\"", "\"\"") : "";
                    if (valorStr.contains(",") || valorStr.contains("\"")) {
                        valorStr = "\"" + valorStr + "\"";
                    }
                    fila.append(valorStr);
                    if (i < campos.size() - 1) {
                        fila.append(",");
                    }
                    i++;
                }
                bw.write(fila.toString());
                bw.newLine();
            }
            DatabaseHelp.closeConnection();
            return path;
        }catch(Exception e){
            e.printStackTrace(); 
        }
        return null;
    }
    
    public static String collectionLiveData(String Json, String nameCollection, String id){
        MongoDatabase database= DatabaseHelp.getConnection();
        database.drop(); 
        MongoCollection<Document> collection=database.getCollection(nameCollection);
        if(collection==null){
            database.createCollection(nameCollection);
        }
        collection= database.getCollection(nameCollection);
        Document jsonValue=Document.parse(Json);
        collection.insertOne(jsonValue);
        DatabaseHelp.closeConnection();
        return nameCollection;
    }
}
