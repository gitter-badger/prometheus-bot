package org.prokyon.crypto_currency.bot_core.fee.metadata;

import org.prokyon.crypto_currency.bot_core.fee.model.Fee;

import java.util.ArrayList;
import java.util.List;

public class FeeMetadata {

    public static List<Fee> FEES = new ArrayList<>();

    static{
        //FEES.add(new TradingFee(BotExchange.ACX,new BigDecimal("0.2"), Unit.PERCENTAGE, new FeeType(FeeType.Type.TRADING, Side.BUY), null));
        //FEES.add(new TradingFee(BotExchange.ACX,new BigDecimal("0.2"), Unit.PERCENTAGE, new FeeType(FeeType.Type.TRADING, Side.SELL), null));
        //FEES.add(new DepositFee())
    }
}
