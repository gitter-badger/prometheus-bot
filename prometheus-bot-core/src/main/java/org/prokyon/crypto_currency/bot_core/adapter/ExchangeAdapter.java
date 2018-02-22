package org.prokyon.crypto_currency.bot_core.adapter;

import com.jcabi.aspects.Loggable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.known.xchange.acx.AcxExchange;
import org.prokyon.crypto_currency.bot_core.constant.BotExchange;

@Data
@NoArgsConstructor
public class ExchangeAdapter {

    Logger logger = LogManager.getLogger(this.getClass());

    private BotExchange botExchange;
    private Exchange exchange;

    /**
     * Creates public instance, we can feed data from
     * @param exchange name
     * @return public instance of exchange
     */
    public ExchangeAdapter(BotExchange botExchange){
        this.botExchange = botExchange;
        this.exchange = createInstance(botExchange);
    }
    public Exchange createInstance(BotExchange botExchange){
        ExchangeSpecification exSpec = getExchangeDefaultSpecification(botExchange);
        return ExchangeFactory.INSTANCE.createExchange(exSpec);
    }

    public Exchange createPollingInstance(BotExchange botExchange, String proxyHost, Integer proxyPort){

        ExchangeSpecification exSpec = getExchangeDefaultSpecification(botExchange);
        exSpec.setProxyHost(proxyHost);
        exSpec.setProxyPort(proxyPort);
        //exSpec.setExchangeSpecificParameters(new Map<"limig",100>);
        return ExchangeFactory.INSTANCE.createExchange(exSpec);

    }
    public Exchange createPollingInstance(BotExchange botExchange, String userName, String apiKey, String secretKey){

        ExchangeSpecification exSpec = getExchangeDefaultSpecification(botExchange);
        exSpec.setUserName(userName);
        exSpec.setApiKey(apiKey);
        exSpec.setSecretKey(secretKey);
        return ExchangeFactory.INSTANCE.createExchange(exSpec);

    }

    public Exchange createInstance(BotExchange botExchange,
                                   String userName,
                                   String apiKey,
                                   String secretKey,
                                   String proxyHost,
                                   Integer proxyPort){

        ExchangeSpecification exSpec = getExchangeDefaultSpecification(botExchange);
        exSpec.setUserName(userName);
        exSpec.setApiKey(apiKey);
        exSpec.setSecretKey(secretKey);
        exSpec.setProxyHost(proxyHost);
        exSpec.setProxyPort(proxyPort);
        return ExchangeFactory.INSTANCE.createExchange(exSpec);

    }

    /**
     * Gets default specification for selected exchange system.
     * @param botExchange name of exchange system
     * @return default exchange specification
     */
    @Loggable
    private ExchangeSpecification getExchangeDefaultSpecification(@NonNull BotExchange botExchange){

        ExchangeSpecification exchangeSpecification = null;
        switch (botExchange){
            case ACX: return new AcxExchange().getDefaultExchangeSpecification();
            case ANXPRO: return new AcxExchange().getDefaultExchangeSpecification();
            //case HITBTC: return new HitbtcExchange().getDefaultExchangeSpecification();
        }

        return exchangeSpecification;
    }



}
