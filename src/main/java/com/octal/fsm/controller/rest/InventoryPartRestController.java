package com.octal.fsm.controller.rest;

import com.octal.fsm.dto.ApiResponse;
import com.octal.fsm.dto.rest.InventoryRequestDTO;
import com.octal.fsm.service.soap.InventoryPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/rest/inventory-type")
public class InventoryPartRestController {

    @Autowired
    private InventoryPartService inventoryPartService;

    @GetMapping(value = "/sync")
    public ResponseEntity<ApiResponse> syncInventory(HttpServletRequest request) {
        try {
            inventoryPartService.syncInventoryToJobService();
            return new ResponseEntity<>(new ApiResponse("Inventory sync completed.", null, "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null, "101", HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<ApiResponse> updateInventory(@RequestBody List<InventoryRequestDTO.Add> dto, HttpServletRequest request) {
        try {
            inventoryPartService.updateInventoryQuantities(dto);
            return new ResponseEntity<>(new ApiResponse("Inventory update completed.", null, "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null, "101", HttpStatus.OK), HttpStatus.OK);
        }
    }
}
