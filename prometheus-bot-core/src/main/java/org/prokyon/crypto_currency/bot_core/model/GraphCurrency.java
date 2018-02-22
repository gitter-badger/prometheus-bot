package org.prokyon.crypto_currency.bot_core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.knowm.xchange.currency.Currency;

@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude={"vertex"})
public class GraphCurrency {
    Currency currency;
    int vertex;

    public GraphCurrency(Currency currency){
        this.currency = currency;
    }



    @Override
    public String toString() {
        return "GraphCurrency: currency=" + this.currency +", vertex=" + this.vertex;
    }
}