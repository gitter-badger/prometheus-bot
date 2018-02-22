package org.prokyon.crypto_currency.bot_core.model.harvester;

public enum TargetDataStructure{

    ORDER_BOOK(AccessType.PUBLIC),
    ASK(AccessType.PUBLIC),
    BID(AccessType.PUBLIC),
    TICKER(AccessType.PUBLIC),
    ACCOUNT_ORDERS(AccessType.PRIVATE),
    ACCOUNT_BALANCE(AccessType.PRIVATE);


    private final AccessType accessType;

    TargetDataStructure(AccessType accessType){
        this.accessType = accessType;
    }

    public AccessType getAccessType() {
        return this.accessType;
    }


}