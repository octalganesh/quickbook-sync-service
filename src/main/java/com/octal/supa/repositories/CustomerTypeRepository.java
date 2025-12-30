package com.octal.supa.repositories;

import com.octal.supa.entities.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CustomerTypeRepository extends JpaRepository<CustomerType, Long>, JpaSpecificationExecutor<CustomerType> {

    Optional<CustomerType> findByListId(String listId);

    Optional<CustomerType> findByName(String name);

}
