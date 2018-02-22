package org.prokyon.crypto_currency.bot_core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.knowm.xchange.currency.CurrencyPair;
import org.prokyon.crypto_currency.bot_core.constant.BotExchange;
import org.prokyon.crypto_currency.bot_core.proxy.model.Proxy;

@AllArgsConstructor
@Data
public class ProxyConfig {
    private BotExchange botExchange;
    private CurrencyPair currencyPair;
    private Proxy proxy;
}
