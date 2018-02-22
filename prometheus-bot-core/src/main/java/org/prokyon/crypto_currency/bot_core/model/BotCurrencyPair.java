package org.prokyon.crypto_currency.bot_core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.currency.CurrencyPair;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BotCurrencyPair {
    private CurrencyPair currencyPair;
    private GraphCurrency graphCurrencyBase;
    private GraphCurrency graphCurrencyCounter;
    private BotCurrencyPairType botCurrencyPairType;
    private BigDecimal originalAmount;
    private BigDecimal lowAskPrice;
    private BigDecimal lowAskAmount;
    private BigDecimal lowAskPriceOriginal;


    private BigDecimal highBidPrice;
    private BigDecimal highBidPriceOriginal;
    private BigDecimal highBidAmount;

    private boolean needArtificial = false;

    //private int tailVertex;
    //private int headVertex;

    public BotCurrencyPair(CurrencyPair currencyPair,
                           GraphCurrency graphCurrencyBase,
                           GraphCurrency graphCurrencyCounter){
        this.currencyPair = currencyPair;
        this.graphCurrencyBase = graphCurrencyBase;
        this.graphCurrencyCounter = graphCurrencyCounter;
        this.botCurrencyPairType = BotCurrencyPairType.REAL;
    }

    @Override
    public String toString(){
        return "\n" + this.currencyPair.toString() + " - " + botCurrencyPairType +"}:"
                + " HIGH BID: price=" + this.getHighBidPrice() + ", priceOriginal=" + this.getHighBidPriceOriginal() +", amount=" + this.getHighBidAmount()
                + " LOW ASK: price" + this.getLowAskPrice() + ", priceOriginal=" + this.getLowAskPriceOriginal() + ", amount=" + this.getLowAskAmount();


    }

    public enum BotCurrencyPairType{
        REAL,ARTIFICIAL;
    }
}
