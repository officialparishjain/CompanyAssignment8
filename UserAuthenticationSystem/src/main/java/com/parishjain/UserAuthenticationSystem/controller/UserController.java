package com.parishjain.UserAuthenticationSystem.controller;

import com.parishjain.UserAuthenticationSystem.model.User;
import com.parishjain.UserAuthenticationSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(value = "/register")
    public ResponseEntity<String> createUser(@RequestBody User user){
        User user1 = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User Registered Successfully !!");
    }

   @GetMapping("/user/home")
   public ResponseEntity<String> userHome(){
        return ResponseEntity.ok().body("User Home Page");
   }

    @GetMapping("/user/profile")
    public ResponseEntity<String> userProfile(){
        return ResponseEntity.ok().body("User Profile Page");
    }

    // ONLY ADMIN CAN ACCESS THIS API
    // BECAUSE OF ROLE BASE AUTHENTICATION THAT IS PROVIDE SECURITY FILTER

    @GetMapping("/admin/home")
    public ResponseEntity<String> adinHome(){
        return ResponseEntity.ok().body("Admin Home Page");
    }


    @GetMapping("/forgotPassword/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable String email){
        String res = userService.requestForgotPassLink(email);
        return ResponseEntity.ok().body(res);
    }

    @PutMapping("/forgot/{email}")
    public ResponseEntity<String> updatePassword(@PathVariable String email, @RequestParam String temp,@RequestParam String password){
        String res = userService.updatePassword(email,temp,password);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
