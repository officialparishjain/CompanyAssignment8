package com.parishjain.UserAuthenticationSystem.config;

import com.parishjain.UserAuthenticationSystem.model.User;
import com.parishjain.UserAuthenticationSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User myUser =  userService.findByEmail(email);
        if(myUser == null){
            throw new UsernameNotFoundException("Username not found");
        }
        else{
            return new CustomUserDetails(myUser);
        }
    }
}
