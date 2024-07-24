package com.example.demo.dto;


import com.example.demo.validation.constraints.ValidDecimal;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TestRequest {
    @ValidDecimal
    BigDecimal nominal;
}
