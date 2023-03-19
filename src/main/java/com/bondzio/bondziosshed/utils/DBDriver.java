package com.bondzio.bondziosshed.utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Optional;

public class DBDriver {
    private Dotenv env;
    private String password;
    private ConnectionString connectionString;
    private MongoClientSettings settings;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection rooms;


    public enum RoomCreationStatus{
        RoomExistsError(false),
        InternalRoomCreationError(false),
        CreatedSucessfully(true);

        private boolean isSuccessful;

        private RoomCreationStatus(boolean statusType){
            isSuccessful = statusType;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }
    }

    public record RoomCreationResult(RoomCreationStatus status, Optional<Room> result) {}

    public DBDriver(){
        env = Dotenv.configure().load();
        this.connectionString = new ConnectionString("mongodb+srv://admin:" + env.get("DB_PASSWORD") + "@cluster0.ipgs6c8.mongodb.net/?retryWrites=true&w=majority");
        this.settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase("kalambury");
        this.rooms = database.getCollection("rooms");
    }

    public boolean GetRoom(String roomId){
        Bson filter = Filters.eq("roomId", roomId);
        return this.rooms.countDocuments(filter) > 0;
    }

    public RoomCreationResult CreateRoom(String roomId, String password){
        Room createdRoom = new Room(roomId, password);
        Document doc = new Document("roomId", roomId)
                .append("password", createdRoom.hashPassword())
                .append("roomKey", createdRoom.getRoomKey());
        if(GetRoom(roomId)) return new RoomCreationResult(RoomCreationStatus.RoomExistsError, Optional.empty());
        try {
            this.rooms.insertOne(doc);
            return new RoomCreationResult(RoomCreationStatus.CreatedSucessfully, Optional.of(createdRoom));
        } catch (Exception e) {
            System.out.println(e);
            return new RoomCreationResult(RoomCreationStatus.InternalRoomCreationError, Optional.empty());
        }
    }


}
