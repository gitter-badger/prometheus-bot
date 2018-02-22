package org.prokyon.crypto_currency.bot_core.fee.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@AllArgsConstructor
@Data
public class FeeType {

   private @NonNull  Type type;
   private Side side;

   public enum Type{
       TRADING,
       WITHDRAWAL,
       DEPOSIT;
   }
}
