package com.octal.fsm.repositories;

import com.octal.fsm.entities.CreateAssemblyQueue;
import com.octal.fsm.entities.CreateInvoiceQueue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreateAssemblyQueueRepository extends JpaRepository<CreateAssemblyQueue, Long> {

    Optional<CreateAssemblyQueue> findByUuid(String uuid);

    boolean existsByAssemblyName(String name);

    @Query("SELECT q FROM CreateAssemblyQueue q WHERE q.syncStatus='QUEUED' ORDER BY q.createdAt ASC")
    List<CreateAssemblyQueue> findNextQueued(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE CreateAssemblyQueue c SET c.activeToken = null")
    void resetAllActiveTokens();

    @Modifying
    @Transactional
    @Query("UPDATE CreateAssemblyQueue q SET q.listId = :listId, q.editSequence = :editSequence, q.syncStatus = 'CREATED' WHERE q.assemblyName = :name")
    void updateAfterCreate(@Param("listId") String listId,@Param("editSequence") String editSequence,@Param("name") String name);

    @Query("SELECT q FROM CreateAssemblyQueue q WHERE q.syncStatus = 'CREATED' ORDER BY q.createdAt ASC")
    List<CreateAssemblyQueue> findNextCreated(PageRequest page);

    Optional<CreateAssemblyQueue> findByListId(String listId);

}
