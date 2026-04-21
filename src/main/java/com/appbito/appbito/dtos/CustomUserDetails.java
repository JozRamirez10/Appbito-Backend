package com.appbito.appbito.dtos;

import io.jsonwebtoken.lang.Collections;
import lombok.Getter;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User{

    private Long id;

    public CustomUserDetails(Long id, String email, String password){
        super(email, password, Collections.emptyList());
        this.id = id;
    }
}