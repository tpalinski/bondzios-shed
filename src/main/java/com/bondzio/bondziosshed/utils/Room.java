package com.bondzio.bondziosshed.utils;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Room {
    private String roomID;
    private String roomPassword;
    private String roomKey;

    public Room(String id, String password){
        KeyGenerator generator = new KeyGenerator();
        Thread hasher = new Thread(generator);
        hasher.run();
        this.roomID = id;
        this.roomPassword = password;
        try{
            hasher.join();
            this.roomKey = generator.getKey();
        } catch (InterruptedException e){
            System.out.println(e.toString());
            this.roomKey = "errerrerrerr";
        }
    }

    public Room(String id, String password, String roomKey){
        this.roomID = id;
        this.roomPassword = password;
        this.roomKey = roomKey;
    }

    public Room(){
        this.roomKey = "";
        this.roomPassword = "";
        this.roomID = "";
    }

    public String getRoomID() {
        return roomID;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public String hashPassword(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(this.roomPassword);
    }

}
