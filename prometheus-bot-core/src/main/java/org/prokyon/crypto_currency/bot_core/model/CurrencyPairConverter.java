package org.prokyon.crypto_currency.bot_core.model;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CurrencyPairConverter implements AttributeConverter<CurrencyPair, String> {

    private static final String SEPARATOR = "_";

    /**
     * Convert Color object to a String
     * with format red|green|blue|alpha
     */
    @Override
    public String convertToDatabaseColumn(CurrencyPair currencyPair) {
        return currencyPair.toString();
    }


    /**
     * Convert a String with format red|green|blue|alpha
     * to a Color object
     */
    @Override
    public CurrencyPair convertToEntityAttribute(String currencyPair) {
        //String[] currencyArray = currencyPair.split(SEPARATOR);

        //return new CurrencyPair(Currency.getInstance(currencyArray[0]), Currency.getInstance(currencyArray[1]));
        return new CurrencyPair(Currency.LTC,Currency.BTC);
    }
}
