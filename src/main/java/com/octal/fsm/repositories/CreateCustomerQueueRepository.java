package com.octal.fsm.repositories;

import com.octal.fsm.entities.CreateCustomerQueue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreateCustomerQueueRepository extends JpaRepository<CreateCustomerQueue, Long>, JpaSpecificationExecutor<CreateCustomerQueue> {

    @Query("SELECT csq FROM CreateCustomerQueue csq")
    List<CreateCustomerQueue> findOneQuery(Pageable pageable);

    @Query("SELECT c FROM CreateCustomerQueue c WHERE c.activeToken IS NOT NULL")
    List<CreateCustomerQueue> findActiveTokenRecord(Pageable pageable);

    @Query("SELECT c FROM CreateCustomerQueue c WHERE c.syncStatus = 'QUEUE' ORDER BY c.createdAt ASC")
    List<CreateCustomerQueue> findNextQueued(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE CreateCustomerQueue c SET c.activeToken = null")
    void resetAllActiveTokens();

    Optional<CreateCustomerQueue> findByUuid(String uuid);

    Optional<CreateCustomerQueue> findByFullNameAndCustomerId(String fullName, String customerId);

    @Query("SELECT c FROM CreateCustomerQueue c WHERE c.quickBookCustomerId = :id OR c.uuid = :id")
    Optional<CreateCustomerQueue> findByQuickBookCustomerIdOrUuid(@Param("id") String id);
}
