package com.appbito.appbito.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentialsDTO {

    private Long id;
    
    private String email;

    private String password;

    private boolean enabled;
    
}
