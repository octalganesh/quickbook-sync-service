package com.octal.fsm.controller.rest;

import com.octal.fsm.dto.ApiResponse;
import com.octal.fsm.dto.rest.AssemblyCreateRequest;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.service.rest.CreateAssemblyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("assembly")
@RestController
public class AssemblyController {

    @Autowired
    private CreateAssemblyService createAssemblyService;

    @PostMapping("/add-queue")
    public ResponseEntity<?> createAssembly(@RequestBody AssemblyCreateRequest.AssemblyComponentAdd request) throws CodeException {
        if (request.getComponents() == null || request.getComponents().isEmpty()) {
            throw new IllegalArgumentException("Assembly must have components");
        }
        createAssemblyService.saveAssemblyQueue(request);
        return new ResponseEntity<>(new ApiResponse("Assembly data stored in queue!", null, "200", HttpStatus.OK), HttpStatus.OK);
    }

}
