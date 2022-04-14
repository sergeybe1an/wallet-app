package com.sbelan.walletapp.model.api;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Balance {

    private Long userId;
    private BigDecimal balance;
}
