package com.sbelan.walletapp.repository;

import com.sbelan.walletapp.model.dto.TransactionDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionDto, Long> {

    List<TransactionDto> findAllByUserId(Long userId);
}
