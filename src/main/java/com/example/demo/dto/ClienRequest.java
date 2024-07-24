package com.example.demo.dto;


import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ClienRequest {

    @NotBlank
    String name;

    @NotBlank
    String address;

    @NotBlank(message = "Phone number cannot be blank")
    String phone;

    @Email(message = "Invalid email address")
    String email;

    String description;


}
