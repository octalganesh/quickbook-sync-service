package com.octal.supa.repositories;

import com.octal.supa.entities.CustomerType;
import com.octal.supa.entities.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customers, Long>, JpaSpecificationExecutor<Customers> {

    Optional<Customers> findByListId(String listId);

}
