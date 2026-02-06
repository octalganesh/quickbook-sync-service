package com.octal.fsm.controller.soap;

import com.octal.fsm.service.rest.CreateAssemblyService;
import com.octal.fsm.service.soap.CreateAssemblyServiceQBD;
import com.octal.fsm.utils.SOAPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync/assembly")
public class QuickBookCreateAssemblyController {

    @Autowired
    private CreateAssemblyServiceQBD createAssemblyServiceQBD;

    @PostMapping(
            value = "/create",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE}
    )
    public ResponseEntity<String> syncAssembly(@RequestBody String xmlPayload) {

        try {
            if (xmlPayload.contains("<authenticate")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(createAssemblyServiceQBD.getSyncAuthToken());

            } else if (xmlPayload.contains("<sendRequestXML")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(createAssemblyServiceQBD.syncAssemblyFromQueue());

            } else if (xmlPayload.contains("<receiveResponseXML")) {
                createAssemblyServiceQBD.createSyncAssemblyFromQBWC(xmlPayload);
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(SOAPUtil.receiveResponseXMLResponse());

            } else if (xmlPayload.contains("<getLastError")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(SOAPUtil.getLastErrorResponse());

            } else if (xmlPayload.contains("<closeConnection")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(SOAPUtil.closeConnectionResponse());
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_XML)
                    .body(SOAPUtil.errorUnknownMethod());

        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_XML)
                    .body(SOAPUtil.errorSOAP(e.getMessage()));
        }
    }
}
