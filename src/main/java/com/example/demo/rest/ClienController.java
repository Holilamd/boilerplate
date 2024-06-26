package com.example.demo.rest;


import com.example.demo.baseResponse.BaseResponse;
import com.example.demo.dto.ClienRequest;
import com.example.demo.dto.ClienResponse;
import com.example.demo.dto.SftpRequest;
import com.example.demo.entity.Clien;
import com.example.demo.repository.ClienRepository;
import com.example.demo.services.FileProcessingService;
import com.example.demo.type.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/clien")
public class ClienController {
    
    @Autowired
    ClienRepository clienRepository;

    @Autowired
    FileProcessingService fileProcessingService;

    @PostMapping("/create")
    BaseResponse<?>create(@Valid @RequestBody ClienRequest req){
        log.info("{}",req);
        Clien cl = new Clien();
        cl.setId("CL-01");
        cl.setName(req.getName());
        cl.setAddress(req.getAddress());
        cl.setPhone(req.getPhone());
        cl.setEmail(req.getEmail());
        cl.setDescription(req.getDescription());
        cl.setStatus(Constant.Status.ACTIVE);
        clienRepository.saveAndFlush(cl);

       return new BaseResponse<>().setData(
               ClienResponse.builder()
                       .id(cl.getId())
                       .name(cl.getName())
                       .build()
       );
    }
    @PostMapping("/test-read-sftp")
    public String processFile(@Valid @RequestBody SftpRequest req){
        fileProcessingService.processFileFromSftp(req);
        return "File processed successfully";
    }
}
