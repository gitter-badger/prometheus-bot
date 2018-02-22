package org.prokyon.crypto_currency.bot_core.model;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.knowm.xchange.currency.CurrencyPair;
import org.prokyon.crypto_currency.bot_core.constant.BotExchange;

import java.util.ArrayList;
import java.util.List;

@Data

public class CurrencyPairExchanges {

    Logger log = LogManager.getLogger(CurrencyPairExchanges.class);

    private List<CurrencyPairExchange> currencyPairExchangeList = new ArrayList<>();

    public void process(BotExchange botExchange, CurrencyPair currencyPair) {
        // Currency not exists in the list
        if (currencyPairExchangeList.stream().noneMatch(currencyPairExchange ->

            currencyPairExchange.getCurrencyPair().equals(currencyPair))){
                // Just add it as new
                currencyPairExchangeList.add (new CurrencyPairExchange(botExchange, currencyPair));
                log.debug("Adding ");
        } else {
                // Just add exchange to the list
                currencyPairExchangeList.stream().filter(currencyPairExchange ->
                currencyPairExchange.getCurrencyPair().equals(currencyPair)).findFirst().get().getBotExchanges().add(botExchange);

            }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        currencyPairExchangeList

                .stream()
                .filter(currencyPairExchange -> currencyPairExchange.getBotExchanges().size() > 1)
                .forEach(currencyPairExchange ->
                {
                    stringBuilder.append("Currency pair: \t" + currencyPairExchange.getCurrencyPair() + "\n");
                    currencyPairExchange.getBotExchanges().stream().forEach(
                            botExchange ->
                        stringBuilder.append("Exchange: \t" + botExchange + "\n")
                    );

                }

        );

        return stringBuilder.toString();
    }
}
