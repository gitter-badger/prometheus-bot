package org.prokyon.crypto_currency.bot_core.model;


import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.prokyon.crypto_currency.bot_core.bot_manager.BotStorageEngine;
import org.prokyon.crypto_currency.bot_core.constant.BotExchange;
import org.prokyon.crypto_currency.bot_core.fee.model.Side;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table (name = "market_record")
public class MarketRecord implements Serializable {



    /**
     * This is surogate key constructed from:
     * - limit price
     * - amount
     * - date
     * - type
     * - crypto exchange
     */
    @Id
    private String id;

    /**
     *
     */
    @Column(name = "business_hash", columnDefinition = "text")
    private String businessHash;

    /**
     * Represents one batch received from xchange, this might be not big use when websocket based updates will be
     * in place.
     *
     * Is generate in parent thread.
     */
    private String setId;


    @Column(name = "related_id")
    private String relatedId;

    @Column(name = "exchange_name", columnDefinition="text")
    @Enumerated(EnumType.STRING)
    private BotExchange botExchange;

    @Column(name = "currency_pair", columnDefinition="text")
    @Convert(converter = CurrencyPairConverter.class)
    private CurrencyPair currencyPair;

    @Column(columnDefinition="text")
    @Enumerated(EnumType.STRING)
    private Side side;

    @Column(columnDefinition="text")
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "order_status", columnDefinition="text")
    @Enumerated(EnumType.STRING)
    private Order.OrderStatus orderStatus;

    //@Column(precision = 11, scale = 8)
    @Column(columnDefinition = "NUMERIC(19,8)")
    //@Digits(integer=11, fraction=11)
    private BigDecimal price;

    //@Column(name="original_amount", precision = 11, scale = 8)
    @Column(columnDefinition = "NUMERIC(19,8)")
    //@Digits(integer=11, fraction=11)
    private BigDecimal originalAmount;
    // I should test in more detail other available amounts coming from some xchanges
    @Column(columnDefinition="text")
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Only related to FOREIGN records, ie. comming from external xchanges
     */
    private LocalDateTime requested;
    private LocalDateTime created;
    private LocalDateTime locked;
    private LocalDateTime unLocked;
    private LocalDateTime removed;


    /**
     * Only related to LOCAL records, ie. created internally and posted on exchange as part of trading mechanism.
     */
    private LocalDateTime sent;
    private LocalDateTime accepted;
    private LocalDateTime processed;






    public MarketRecord(
            String setId,
            BotExchange botExchange,
            CurrencyPair currencyPair,
            Side side,
            Type type,
            Order.OrderStatus orderStatus,
            BigDecimal price,
            BigDecimal originalAmount,
            Status status,
            LocalDateTime requested,
            LocalDateTime created

    ){

        /*
        logger.info("New MarketRecord arrived: " + "Side: " + side
        + "Exchange: " + marketExchange.name()
        + "CurrencyPair: " + currencyPair
        + "Timestamp: " + timestamp
        + "OriginalAmount: " + originalAmount
        + "LimitPrice:" + limitPrice
        + "OrderStatus: " + status);
        */

        this.setId = setId;
        this.botExchange = botExchange;
        this.currencyPair = currencyPair;
        this.side = side;
        this.type = type;
        this.orderStatus = orderStatus;
        this.price = price;
        this.originalAmount = originalAmount;
        this.status = status;
        // Initial default state of every new MarketRecord
        this.requested = requested;
        this.created = created;

        // First need to be generated non-unique (possibly unique on some exchanges :-) ) hash from business records
        this.businessHash = generateBusinessHash(
                this.botExchange,
                this.currencyPair,
                this.side,
                this.type,
                this.orderStatus,
                this.price,
                this.originalAmount
        );

        // And now I can get unique recordId (maybe I will remove this later completely for saving data storage)
        this.id = generateRecordId(this.created , this.businessHash);




       // logger.info(this.toString());

    }

    MarketRecord(LimitOrder limitOrder){

    }
    /**
     * Generate recordID in form of hash from object field except timestamp. By design, there can be coming from sources
     * same orders but with different timestamps. Exchanges unfortunately do not provide timestamp in most cases, so it
     * is generated internaly on order record arrival.
     *
     * @param created
     * @param businessHash
     *
     * @return
     */
    private String generateRecordId(LocalDateTime created, String businessHash ) {

        return created.format(BotStorageEngine.formatter) + "_" + businessHash;
    }

    /**
     * Due to nature of data received from exchange this has more nature of NON-UNIQUE index source.
     *
     * Task: Analyze if there is option to obtain real UNIQUE BUSINESS KEY from Xchanges. (it can vary per xchange)
     *
     * @param botExchange
     * @param currencyPair
     * @param side
     * @param type
     * @param orderStatus
     * @param price
     * @param originalAmount
     * @return
     */
    private String generateBusinessHash(BotExchange botExchange,
                                    CurrencyPair currencyPair,
                                    Side side,
                                    Type type,
                                    Order.OrderStatus orderStatus,
                                    BigDecimal price,
                                    BigDecimal originalAmount
                                    ) {

        return  DigestUtils.sha256Hex(
                botExchange
                        + currencyPair.toString()
                        + side.toString()
                        + type.toString()
                        + orderStatus.toString()
                        + price.toPlainString()
                        + originalAmount.toPlainString()
                       );
    }



    public static final Attribute<MarketRecord, String> ID = new SimpleAttribute<MarketRecord, String>() {
        @Override
        public String getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getId();
        }
    };

    public static final Attribute<MarketRecord, String> BUSINESS_HASH = new SimpleAttribute<MarketRecord, String>() {
        @Override
        public String getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getBusinessHash();
        }
    };

    public static final Attribute<MarketRecord, String> SET_ID = new SimpleAttribute<MarketRecord, String>() {
        @Override
        public String getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getSetId();
        }
    };


    /**
     * Method for CQEngine attribute binding
     */
    public static final Attribute<MarketRecord, BotExchange> EXCHANGE_NAME = new SimpleAttribute<MarketRecord, BotExchange>() {
        @Override
        public BotExchange getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getBotExchange();
        }
    };

    /**
     * Method for CQEngine attribute binding
     */
    public static final Attribute<MarketRecord, CurrencyPair> CURRENCY_PAIR = new SimpleAttribute<MarketRecord, CurrencyPair>() {
        @Override
        public CurrencyPair getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getCurrencyPair();
        }
    };

    /**
     * Method for CQEngine attribute binding
     */
    public static final Attribute<MarketRecord, Side> SIDE = new SimpleAttribute<MarketRecord, Side>() {
        @Override
        public Side getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getSide();
        }
    };

    /**
     * Method for CQEngine attribute binding
     */
    public static final Attribute<MarketRecord, Type> TYPE = new SimpleAttribute<MarketRecord, Type>() {
        @Override
        public Type getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getType();
        }
    };

    /**
     * Method for CQEngine attribute binding
     */
    public static final Attribute<MarketRecord,  Order.OrderStatus> ORDER_STATUS = new SimpleAttribute<MarketRecord,  Order.OrderStatus>() {
        @Override
        public  Order.OrderStatus getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getOrderStatus();
        }
    };

    /**
     * Method for CQEngine attribute binding
     */
    public static final Attribute<MarketRecord, BigDecimal> LIMIT_PRICE = new SimpleAttribute<MarketRecord, BigDecimal>() {
        @Override
        public BigDecimal getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getPrice();
        }
    };

    /**
     * Method for CQEngine attribute binding
     */
    public static final Attribute<MarketRecord, BigDecimal> ORIGINAL_AMOUNT = new SimpleAttribute<MarketRecord, BigDecimal>() {
        @Override
        public BigDecimal getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getOriginalAmount();
        }
    };

    /**
     * Method for CQEngine attribute binding
     */
    public static final Attribute<MarketRecord,  Status> STATUS = new SimpleAttribute<MarketRecord,  Status>() {
        @Override
        public  Status getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getStatus();
        }
    };

    public static final Attribute<MarketRecord,  LocalDateTime> REQUESTED = new SimpleAttribute<MarketRecord,  LocalDateTime>() {
        @Override
        public  LocalDateTime getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getRequested();
        }
    };

    public static final Attribute<MarketRecord,  LocalDateTime> CREATED = new SimpleAttribute<MarketRecord,  LocalDateTime>() {
        @Override
        public  LocalDateTime getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getCreated();
        }
    };

    public static final Attribute<MarketRecord,  LocalDateTime> LOCKED = new SimpleAttribute<MarketRecord,  LocalDateTime>() {
        @Override
        public  LocalDateTime getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getLocked();
        }
    };

    public static final Attribute<MarketRecord,  LocalDateTime> UNLOCKED = new SimpleAttribute<MarketRecord,  LocalDateTime>() {
        @Override
        public  LocalDateTime getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getUnLocked();
        }
    };

    public static final Attribute<MarketRecord,  LocalDateTime> REMOVED = new SimpleAttribute<MarketRecord,  LocalDateTime>() {
        @Override
        public  LocalDateTime getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getRemoved();
        }
    };



    public static final Attribute<MarketRecord,  LocalDateTime> PROCESSED = new SimpleAttribute<MarketRecord,  LocalDateTime>() {
        @Override
        public  LocalDateTime getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getProcessed();
        }
    };

    public static final Attribute<MarketRecord,  LocalDateTime> ENT = new SimpleAttribute<MarketRecord,  LocalDateTime>() {
        @Override
        public  LocalDateTime getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getSent();
        }
    };

    public static final Attribute<MarketRecord,  LocalDateTime> ACCEPTED = new SimpleAttribute<MarketRecord,  LocalDateTime>() {
        @Override
        public  LocalDateTime getValue(MarketRecord object, QueryOptions queryOptions) {
            return object.getAccepted();
        }
    };



    public enum Type{
        FOREIGN(""),
        LOCAL("");

        Type(String description){
            this.description = description;
        }
        private String description;
    }


   public enum Status{


       CREATED("")
       ,REMOVED("")
       //,LOCKED("")
       ,SENT("")
       ,ACCEPTED("")
       ,COMPLETED("")
       ,TOP("");

       Status(String description){
           this.description = description;
       }
       private String description;
   }
}
