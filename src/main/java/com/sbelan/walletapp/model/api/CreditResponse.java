package com.sbelan.walletapp.model.api;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditResponse {

    private String responseMessage;
    private BigDecimal balance;
}
