package com.bondzio.bondziosshed.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Random;

// Class for async generation of room ID's
public class KeyGenerator implements Runnable {

    private volatile String key;
    private int keyLength;

    public KeyGenerator(){
        this.keyLength = 10;
    }

    public KeyGenerator(int keyLength){
        this.keyLength = keyLength;
    }

    /// Generate and encrypt unique roomKey
    @Override
    public void run() {
        final String characters = "qwertyuiopasdfghjklzxcvbnm1234567890";
        StringBuilder res = new StringBuilder();
        Random rand=  new Random();
        for (int i = 0; i < this.keyLength; i++) {
            res.append(rand.nextInt(characters.length() - 1));
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.key = encoder.encode(res.toString());
    }

    public String getKey(){
        return this.key;
    }
}
