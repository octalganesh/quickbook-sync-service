package com.octal.supa.repositories;

import com.octal.supa.entities.CreateCustomerQueue;
import com.octal.supa.entities.CreateInvoiceQueue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreateInvoiceQueueRepository extends JpaRepository<CreateInvoiceQueue, Long>, JpaSpecificationExecutor<CreateInvoiceQueue> {

    @Query("SELECT csq FROM CreateInvoiceQueue csq")
    List<CreateInvoiceQueue> findOneQuery(Pageable pageable);

    @Query("SELECT c FROM CreateInvoiceQueue c WHERE c.activeToken IS NOT NULL")
    List<CreateInvoiceQueue> findActiveTokenRecord(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE CreateInvoiceQueue c SET c.activeToken = null")
    void resetAllActiveTokens();

    Optional<CreateInvoiceQueue> findByUuid(String uuid);

    Optional<CreateInvoiceQueue> findByRefId(String refId);
}
