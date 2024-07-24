package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder
public class ClienResponse {

    String id;
    String name;


}
