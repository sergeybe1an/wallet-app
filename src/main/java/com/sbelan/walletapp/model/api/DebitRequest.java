package com.sbelan.walletapp.model.api;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class DebitRequest {

    @NonNull
    private Long userId;
    @NonNull
    private BigDecimal sum;
}
