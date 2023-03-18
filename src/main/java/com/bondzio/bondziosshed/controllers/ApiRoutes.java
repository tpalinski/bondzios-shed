package com.bondzio.bondziosshed.controllers;


import com.bondzio.bondziosshed.utils.Room;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiRoutes {

    @GetMapping("/")
    public String homepage(){
        return "Refer to the documentation for all routes";
    }

    @GetMapping("/room")
    public Room getRoom(){
        return new Room("name", "passw8rd");
    }
}
