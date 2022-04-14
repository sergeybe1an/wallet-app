package com.sbelan.walletapp.model.api;

import com.sbelan.walletapp.model.dto.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private Long id;
    private Long userId;
    private TransactionType type;
    private BigDecimal sum;
    private LocalDateTime date;

}
