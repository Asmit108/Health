package com.health.check.Dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String firstName;
    private  String lastName;
    private String email;
    private String password;
    private String role;
}
