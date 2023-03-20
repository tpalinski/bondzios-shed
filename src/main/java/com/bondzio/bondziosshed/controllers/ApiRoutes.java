package com.bondzio.bondziosshed.controllers;


import com.bondzio.bondziosshed.utils.DBDriver;
import com.bondzio.bondziosshed.utils.Room;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = {"*"})
@RestController
public class ApiRoutes {

    static DBDriver driver = new DBDriver();
    private record RoomResponse(String description, Optional<Room> room){};
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
    public ResponseEntity<RoomResponse> createRoom(@RequestParam(name = "id") String roomId,
                                           @RequestParam(name = "password") String roomPassword){
        DBDriver.RoomCreationResult res = driver.CreateRoom(roomId, roomPassword);
        switch(res.status()){
            case RoomExistsError -> {
                return new ResponseEntity<RoomResponse>(new RoomResponse("Error: Room already exists", res.result()), HttpStatus.CONFLICT);
            }
            case CreatedSucessfully -> {
                return new ResponseEntity<RoomResponse>(new RoomResponse("Created Sucessfully", res.result()), HttpStatus.CREATED);
            }
            case InternalRoomCreationError -> {
                return new ResponseEntity<>(new RoomResponse("Error: Internal database error", res.result()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return null;
    }

    @PostMapping("/delete")
    public ResponseEntity<DBDriver.RoomDeletionResult> deleteRoom(@RequestParam(name = "id") String roomId){
        DBDriver.RoomDeletionResult res = driver.DeleteRoom(roomId);
        return new ResponseEntity<>(res, res.wasSuccessful() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<RoomResponse> loginRoom(@RequestParam(name = "id") String roomId,
                                                   @RequestParam(name = "password") String roomPassword) {
        DBDriver.RoomLoginResult res = driver.LoginRoom(roomId, roomPassword);
        switch (res.status()){
            case Success -> {
                return new ResponseEntity<>(new RoomResponse("Logged in successfully", res.result()), HttpStatus.OK);
            }
            case BadPassword -> {
                return new ResponseEntity<>(new RoomResponse("Error: wrong password", res.result()), HttpStatus.UNAUTHORIZED);
            }
            case RoomNotExists -> {
                return new ResponseEntity<>(new RoomResponse("Error: room does not exist", res.result()), HttpStatus.BAD_REQUEST);
            }
        }
        return null;
    }
}
