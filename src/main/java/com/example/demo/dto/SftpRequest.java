package com.example.demo.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SftpRequest {

     @NotBlank
     String host;

     @NotBlank
     int port;

     @NotBlank
     String user;

     @NotBlank
     String password;

     @NotBlank
     String pathDestination;

     @NotBlank
     String pathSource;

     @NotBlank
     String file;

     @NotBlank
     String newExtension;

     @NotBlank
     boolean moveFile = false;
}