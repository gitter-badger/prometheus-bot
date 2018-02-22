package org.prokyon.crypto_currency.bot_core.model;

import lombok.Data;
import lombok.ToString;
import org.knowm.xchange.currency.CurrencyPair;
import org.prokyon.crypto_currency.bot_core.constant.BotExchange;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class CurrencyPairExchange{
    private CurrencyPair currencyPair;
    private List<BotExchange> botExchanges = new ArrayList<>();


    public CurrencyPairExchange(BotExchange botExchange, CurrencyPair currencyPair) {
        this.botExchanges.add(botExchange);
        this.currencyPair = currencyPair;
    }
}