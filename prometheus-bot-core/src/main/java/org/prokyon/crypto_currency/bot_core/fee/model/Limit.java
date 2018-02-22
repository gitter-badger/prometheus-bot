package org.prokyon.crypto_currency.bot_core.fee.model;

import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Currency;

public class Limit {
    private @NonNull LimitType limitType;
    private @NonNull BigDecimal amount;
    private @NonNull Unit unit;
    private Currency currency;

}
