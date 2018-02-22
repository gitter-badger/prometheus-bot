package org.prokyon.crypto_currency.bot_core.model.harvester;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.currency.CurrencyPair;
import org.prokyon.crypto_currency.bot_core.constant.BotExchange;
import org.prokyon.crypto_currency.bot_core.fee.model.Side;
import org.prokyon.crypto_currency.bot_core.proxy.model.Proxy;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HarvesterConfig {

    /**
     * Main structures passed from manager
     */


    private BotExchange botExchange;
    private CurrencyPair currencyPair;
    private Side side;
    private PersistMode operationMode;
    private Proxy proxy;




}
