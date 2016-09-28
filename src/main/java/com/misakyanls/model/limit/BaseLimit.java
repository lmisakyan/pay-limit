package com.misakyanls.model.limit;

import java.math.BigDecimal;
import java.time.Clock;

public abstract class BaseLimit implements Limit {
    protected final BigDecimal limitAmount;
    protected BigDecimal currentLimitAmount;
    protected Clock clock;

    public BaseLimit(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

}