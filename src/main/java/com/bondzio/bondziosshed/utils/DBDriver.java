package com.bondzio.bondziosshed.utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
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
    public enum RoomLoginStatus{
        RoomNotExists(false),
        BadPassword(false),
        Success(true);

        private boolean isSuccessful;

        private RoomLoginStatus(boolean status){
            isSuccessful = status;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }
    }

    public record RoomCreationResult(RoomCreationStatus status, Optional<Room> result) {}
    public record RoomDeletionResult(String message, boolean wasSuccessful){}
    public record RoomLoginResult(RoomLoginStatus status, Optional<Room> result){}


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

    public RoomDeletionResult DeleteRoom(String roomId){
        Bson filter = Filters.eq("roomId", roomId);
        try {
            this.rooms.findOneAndDelete(filter);
            return new RoomDeletionResult("Deleted successfully", true);
        } catch (Exception e) {
            return new RoomDeletionResult(e.toString(), false);
        }
    }

    public RoomLoginResult LoginRoom(String roomId, String password){
        Bson filter = Filters.eq("roomId", roomId);
        Room room = new Room();
        String hashedPassword = "";
        try {
            FindIterable<Document> dbRooms = this.rooms.find(filter);
            for (Document document: dbRooms) { // should only run once, if not, logical integrity of db is gone
                room = new Room(roomId, password, (String) document.get("roomKey"));
                hashedPassword = (String) document.get("password");
            }
        } catch (Exception e) {
            return new RoomLoginResult(RoomLoginStatus.RoomNotExists, Optional.empty());
        }
        if(room.comparePassword(hashedPassword)){
            return new RoomLoginResult(RoomLoginStatus.Success, Optional.of(room));
        } else { // authorization failed
            return new RoomLoginResult(RoomLoginStatus.BadPassword, Optional.empty());
        }
    }


}
