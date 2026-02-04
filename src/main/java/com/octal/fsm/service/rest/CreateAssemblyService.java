package com.octal.fsm.service.rest;

import com.octal.fsm.dto.rest.AssemblyCreateRequest;
import com.octal.fsm.exceptions.CodeException;

public interface CreateAssemblyService {

    void saveAssemblyQueue(AssemblyCreateRequest.AssemblyComponentAdd req) throws CodeException;
}
