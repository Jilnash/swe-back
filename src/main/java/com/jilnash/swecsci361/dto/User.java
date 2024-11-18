package com.jilnash.swecsci361.dto;

import lombok.Data;

@Data
public class User {
    private String uuid;
    private String phone;
    private String email;
    private String first_name;
    private String last_name;
    private String middle_name;
    private String role;
    private String created_at;
    private String updated_at;
    private String deleted_at;
}
