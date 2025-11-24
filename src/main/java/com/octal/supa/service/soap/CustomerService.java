package com.octal.supa.service.soap;

import com.octal.supa.dto.soap.CreateCustomerResponse;
import org.springframework.stereotype.Service;
@Service
public interface CustomerService {
    void syncCustomersFromQuickBookWebConnector(String xmlPayload) throws Exception;
    void processCustomerIntoDB(CreateCustomerResponse.QBXMLMsgsRs.CustomerAddRs.CustomerRet customerRet);
}