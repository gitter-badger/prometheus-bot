package org.prokyon.crypto_currency.bot_core.fee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.knowm.xchange.currency.CurrencyPair;
import org.prokyon.crypto_currency.bot_core.constant.BotExchange;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@Builder
public class Fee {

    private BotExchange botExchange;
    private CurrencyPair currencyPair;
    private CurrencyType currencyType;
    private FeeType feeType;
    private BigDecimal number;
    private Unit unit;
    private Side side;


    public Fee(BotExchange botExchange,
               FeeType feeType,
               BigDecimal number,
               Unit unit){
        this.botExchange = botExchange;
        this.feeType = feeType;
        this.number = number;
        this.unit = unit;
    }

    public Fee(BotExchange botExchange,
               CurrencyPair currencyPair,
               FeeType feeType,
               BigDecimal number,
               Unit unit){
        this.botExchange = botExchange;
        this.currencyPair = currencyPair;
        this.feeType = feeType;
        this.number = number;
        this.unit = unit;
    }

    public Fee(BotExchange botExchange,
               FeeType feeType,
               BigDecimal number,
               Unit unit,
               Side side){
        this(botExchange,feeType,number,unit);
        this.side = side;
    }
}
