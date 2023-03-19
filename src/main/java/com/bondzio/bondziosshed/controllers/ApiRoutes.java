package com.bondzio.bondziosshed.controllers;


import com.bondzio.bondziosshed.utils.DBDriver;
import com.bondzio.bondziosshed.utils.Room;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
