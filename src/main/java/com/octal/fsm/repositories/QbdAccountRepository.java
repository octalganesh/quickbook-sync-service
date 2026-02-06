package com.octal.fsm.repositories;

import com.octal.fsm.entities.InventoryPart;
import com.octal.fsm.entities.QbdAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface QbdAccountRepository  extends JpaRepository<QbdAccount, Long>, JpaSpecificationExecutor<QbdAccount> {

    Optional<QbdAccount> findByListId(String listId);

    List<QbdAccount> findByListIdIn(List<String> listIds);
}
