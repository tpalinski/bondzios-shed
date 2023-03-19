package com.bondzio.bondziosshed.controllers;


import com.bondzio.bondziosshed.utils.DBDriver;
import com.bondzio.bondziosshed.utils.Room;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ApiRoutes {

    static DBDriver driver = new DBDriver();
    @GetMapping("/")
    public String homepage(){
        return "Refer to the documentation for all routes";
    }

    @GetMapping("/room")
    public String getRoom(@RequestParam(name = "id") String roomId){
        boolean roomExists = driver.GetRoom(roomId);
        return roomExists ? "true" : "false";
    }

    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(@RequestParam(name = "id") String roomId,
                                           @RequestParam(name = "password") String roomPassword){
        Optional<Room> room = driver.CreateRoom(roomId, roomPassword);
        if(room.isPresent()){
            return new ResponseEntity<Room>(room.get(), HttpStatus.CREATED);
        } else { // insertion unsuccessful
            return new ResponseEntity<Room>(new Room(), HttpStatus.CONFLICT);
        }
    }
}
