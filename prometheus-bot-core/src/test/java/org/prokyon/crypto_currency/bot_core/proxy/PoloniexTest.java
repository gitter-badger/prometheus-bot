package org.prokyon.crypto_currency.bot_core.proxy;

import ch.obermuhlner.math.big.BigDecimalMath;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import edu.princeton.cs.algs4.BellmanFordSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.poloniex2.PoloniexStreamingExchange;
import io.reactivex.disposables.Disposable;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.prokyon.crypto_currency.bot_core.model.BotCurrencyPair;
import org.prokyon.crypto_currency.bot_core.model.GraphCurrency;
import si.mazi.rescu.SynchronizedValueFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PoloniexTest {

    Logger log = LogManager.getLogger(this.getClass());

    private Exchange exchangePoolingAdapter;
    private StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(PoloniexStreamingExchange.class.getName());


    private ExchangeSpecification exSpec;

    public static List<BotCurrencyPair> botCurrencyPairs= Collections.synchronizedList(new ArrayList<>());
    public static Set<GraphCurrency> uniqueCurrencies = new HashSet<>();
    public static ExecutorService executor = Executors.newFixedThreadPool(200);
    public static int vertexSequence = 0;

    // tohle je velikost grafu
    public static int SIZE_OF_REAL_VERTEXES = 0;

    private ConcurrentIndexedCollection<BotCurrencyPair> HARVESTER_BUY_ORDERS =
            new ConcurrentIndexedCollection<BotCurrencyPair>();


    @Test
    public void testDouble(){
            log.debug("Double: " + 0.000099324);
            log.debug("LogDouble: " + Math.log(0.000099324));
            log.debug("NegLogDouble: " + -Math.log(0.000099324));
            MathContext mathContext = new MathContext(100);
            log.debug("BigDecimal: " + new BigDecimal("0.000099324"));
            log.debug("LogBigDecimal: " + BigDecimalMath.log(new BigDecimal("0.000099324"),mathContext));
            log.debug("NegLogBigDecimal" + BigDecimalMath.log(new BigDecimal("0.000099324"),mathContext).negate());
    }

    @Test
    public void testPoloniex(){
        exSpec = new PoloniexExchange().getDefaultExchangeSpecification();
        exSpec.setUserName("glowren79");
        exSpec.setApiKey("HE4DRKGX-RRVMH2O4-MANUGXLM-A43LE0AL");
        exSpec.setSecretKey("10f47e869e60929d1f2b59ed2a18813cffcdd95c0bc33b572cd338f9b4829e366a6d4f93e9f09a223b6b3b90e0a2e2a1b5ca7dad5c38458106855e9672bbaee8");

        exchangePoolingAdapter = ExchangeFactory.INSTANCE.createExchange(exSpec);


        constructREALPairs();
        constructArtificialPair();
        constructHarversters();

        constructArbitrageDetector();


        while (true){
            try {
                Thread.sleep(3000);
                //printList("ALL bot currency objects: " , botCurrencyPairs);
            } catch (InterruptedException e) {
                log.error(e.getStackTrace().toString());
            }
        }






    }

    public void constructREALPairs(){
        // Connect to the Exchange WebSocket API. Blocking wait for the connection.
        exchange.connect().blockingAwait();
        log.debug(exchange.toString() + " exchange manages following currency pairs to trade.");
        final List<CurrencyPair> exchangeSymbols = exchange.getExchangeSymbols();
        exchangeSymbols.stream()
                //.filter(currencyPair -> currencyPair.base.equals(XRP) && currencyPair.counter.equals(BTC))
                /*
                .filter(currencyPair ->
                    //currencyPair.equals(CurrencyPair.LTC_BTC)
                            currencyPair.equals(CurrencyPair.XRP_BTC)
                            || currencyPair.equals(CurrencyPair.XRP_USDT)
                            //|| currencyPair.equals(CurrencyPair.LTC_BTC)
                            || currencyPair.equals(CurrencyPair.BTC_USDT)

                )*/
                .forEach(currencyPair -> {
                    //if (currencyPair.base.equals(Currency.BTC) || currencyPair.counter.equals(Currency.BTC)) {

                       // log.debug("currencyPair" + currencyPair.toString());
                       // log.debug("Unique currency list: " + uniqueCurrencies.size());
                        //log.debug("Unique currency list: " + uniqueCurrencies.toString());
                        //log.debug("vertexSequence: " + vertexSequence);
                        GraphCurrency graphCurrencyBase = null;
                        GraphCurrency graphCurrencyCounter = null;

                        if (!uniqueCurrencies.contains(new GraphCurrency(currencyPair.base))) {

                            graphCurrencyBase = new GraphCurrency(currencyPair.base, vertexSequence++);
                            uniqueCurrencies.add(graphCurrencyBase);
                        }
                        if (!uniqueCurrencies.contains(new GraphCurrency(currencyPair.counter))) {
                            graphCurrencyCounter = new GraphCurrency(currencyPair.counter, vertexSequence++);
                            uniqueCurrencies.add(graphCurrencyCounter);
                        }

                        graphCurrencyBase = uniqueCurrencies
                                .stream()
                                .filter(
                                        graphCurrency -> graphCurrency
                                                .getCurrency().equals(currencyPair.base))
                                .findAny().orElse(null);
                        graphCurrencyCounter = uniqueCurrencies
                                .stream()
                                .filter(
                                        graphCurrency -> graphCurrency
                                                .getCurrency().equals(currencyPair.counter))
                                .findAny().orElse(null);

                        final BotCurrencyPair botCurrencyPair = new BotCurrencyPair(currencyPair, graphCurrencyBase, graphCurrencyCounter);
                        botCurrencyPairs.add(botCurrencyPair);
                   // }

                });

        SIZE_OF_REAL_VERTEXES = botCurrencyPairs.size();
        log.debug("Finished extracting currency pairs."
                + "\n Number of REAL currency pairs: " + botCurrencyPairs.size()
                + "\n Number of unique currencies: " + uniqueCurrencies.size());

        log.debug("Unique set of graph currencies:");
       uniqueCurrencies
               .stream()
               .sorted(Comparator.comparing(GraphCurrency::getVertex))
        .forEach(graphCurrency -> log.debug(graphCurrency) );


        printList("ALL bot currency objects: " , botCurrencyPairs);
    }

    public void printList(String objectListName, List<BotCurrencyPair> objects){
        for (BotCurrencyPair o: objects){
            log.debug(objectListName + ": " +o.toString());
        }
    }
    public void constructArtificialPair(){
         /*
        botCurrencyPairs
                .stream()
                .forEach(botCurrencyPair -> {
                    //log.debug(botCurrencyPair.toString());
                });
                */
        log.debug("Going to prepare ARTIFICIAL currency pairs...");

        List<BotCurrencyPair> oppositeBotCurrencyPairs = Collections.synchronizedList(new ArrayList<>());
        botCurrencyPairs.stream()
                .forEach(botCurrencyPair -> {

                    boolean needArtificial = true;
                    //log.debug("Processing pair: "+botCurrencyPair.toString());

                    final CurrencyPair oppositeCurrencyPair = new CurrencyPair(botCurrencyPair.getCurrencyPair().counter, botCurrencyPair.getCurrencyPair().base);
                    //log.debug("Opposite pair to search for: " + oppositeCurrencyPair );

                    for (BotCurrencyPair existingBotCurrencyPair : botCurrencyPairs){
                        if (existingBotCurrencyPair.getCurrencyPair().equals(oppositeCurrencyPair)){
                            needArtificial = false;
                        }
                    }

                    if (needArtificial){
                        //log.debug(botCurrencyPair.toString() + " opposite pair in form of " + oppositeCurrencyPair + " wasn't found, going to generate one...");
                        BotCurrencyPair oppositeBootCurrencyPair = new BotCurrencyPair();
                        // markinig existing record as one which requires ARTIFICIAL opposite to generate
                        botCurrencyPair.setNeedArtificial(true);
                        // set new opposite object
                        oppositeBootCurrencyPair.setCurrencyPair(oppositeCurrencyPair);
                        oppositeBootCurrencyPair.setBotCurrencyPairType(BotCurrencyPair.BotCurrencyPairType.ARTIFICIAL);
                        oppositeBootCurrencyPair.setGraphCurrencyBase(botCurrencyPair.getGraphCurrencyCounter());
                        oppositeBootCurrencyPair.setGraphCurrencyCounter(botCurrencyPair.getGraphCurrencyBase());
                        oppositeBootCurrencyPair.setNeedArtificial(false);
                        //log.debug("Opposite pair generated: " + oppositeBootCurrencyPair.toString());
                        oppositeBotCurrencyPairs.add(oppositeBootCurrencyPair);
                    } else {
                        //log.debug(botCurrencyPair.toString() + " doesn't need opposite record.");
                    }

                });
        log.debug("Generated new opposite botCurrencyPairs: " + oppositeBotCurrencyPairs.size());


        oppositeBotCurrencyPairs
                .stream()
                .sorted(Comparator.comparing(BotCurrencyPair::getCurrencyPair))
                .forEach(graphCurrency -> log.debug(graphCurrency) );

        botCurrencyPairs.addAll(oppositeBotCurrencyPairs);


        log.debug("Finished generation of ARTIFIAL currency pairs:"
                + "\n Number of REAL currency pairs: " + botCurrencyPairs.size()
                + "\n Number of unique currencies: " + uniqueCurrencies.size());

        log.debug("All bot currency pairs: " + botCurrencyPairs.toString());
    }

    public void constructHarversters(){
        botCurrencyPairs
                .stream()
                .filter(record -> record.getBotCurrencyPairType().equals(BotCurrencyPair.BotCurrencyPairType.REAL))
                .forEach(botCurrencyPair -> {
                    log.debug("CURRENCY PAIR TO HARVEST: " + botCurrencyPair.toString());
                    executor.submit(new PoloniexHarvester(botCurrencyPair));

                });


    }

    public void constructArbitrageDetector(){
        executor.submit(new PoloniexSearchArbitrage());
    }



    public class PoloniexSearchArbitrage implements  Runnable{
        private BigDecimal totalPrice;
        private BigDecimal totalAmount;
        EdgeWeightedDigraph G = null;
        int jump = 0;
        Double stake = 0.001;
        LimitOrder processSell;
        boolean executedOnce = false;
        boolean startswithBTC = false;
        MathContext mathContext = new MathContext(8);
        DecimalFormat df = new DecimalFormat("##########0.00000000");
        @Override
        public void run() {

            try {
                exchangePoolingAdapter.getTradeService().getOpenOrders().getOpenOrders().stream().forEach(limitOrder -> {
                    try {
                        exchangePoolingAdapter.getTradeService().cancelOrder(limitOrder.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.debug("Arbitrage detector started.");
            while (true){
                startswithBTC = false;
                jump = 0;
                G = null;
                processSell = null;

                //log.debug("Loop running....");
                try {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //log.debug("NEW ARBITRAGE DETECTION LOOP STARTING ...");
                    G = new EdgeWeightedDigraph(SIZE_OF_REAL_VERTEXES);
                    totalPrice = new BigDecimal("0.0");
                    botCurrencyPairs
                            .stream()
                            .peek(record -> {
                                //log.debug("printing full record");
                                //log.debug("FULL RECORD PRINT: " + record.toString());
                                //log.debug("full record printed");
                            })
                            .forEach(botCurrencyPair -> {
                                //log.debug("Arbitrage calculation: " + botCurrencyPair.toString());
                                if (botCurrencyPair.getBotCurrencyPairType().equals(BotCurrencyPair.BotCurrencyPairType.REAL) && botCurrencyPair.getHighBidPrice() != null) {
                                    final BigDecimal negateHighBid = (BigDecimalMath.log(botCurrencyPair.getHighBidPrice(), mathContext)).negate();
                                    //log.debug("original HB:" + botCurrencyPair.getHighBidPrice() + ", negateLogHB:" + negateHighBid);
                                    //final DirectedEdge directedEdge = new DirectedEdge(botCurrencyPair.getGraphCurrencyBase().getVertex(), botCurrencyPair.getGraphCurrencyCounter().getVertex(), negateHighBid);
                                    final DirectedEdge directedEdge = new DirectedEdge(botCurrencyPair.getGraphCurrencyBase().getVertex(), botCurrencyPair.getGraphCurrencyCounter().getVertex(), negateHighBid.doubleValue());
                                    G.addEdge(directedEdge);
                                    //log.debug("Adding REAL edge for vertexes: " + directedEdge);
                                }
                                if (botCurrencyPair.getBotCurrencyPairType().equals(BotCurrencyPair.BotCurrencyPairType.ARTIFICIAL) && botCurrencyPair.getHighBidPrice() != null) {
                                    //log.debug("ARTIFICIAL DEBUG: " + botCurrencyPair.toString());



                                    final BigDecimal negateHighBid = (BigDecimalMath.log(botCurrencyPair.getHighBidPrice(), mathContext)).negate();
                                    //log.debug("original LA:" + botCurrencyPair.getLowAskPrice() + ", negateLogLA:" + negateLowAsk);
                                    final DirectedEdge directedEdge = new DirectedEdge(botCurrencyPair.getGraphCurrencyBase().getVertex(), botCurrencyPair.getGraphCurrencyCounter().getVertex(), negateHighBid.doubleValue());
                                    //log.debug("Adding ARTIFICIAL edge for vertexes: " + directedEdge );
                                    G.addEdge(directedEdge);
                                }
                            });
                    //log.debug("EdgeWeightedDigraph:\n " + G.toString());
                    //log.debug("Size of graph: " + G.E());
                    //log.debug("Size of graph: " + G.V());

                    BellmanFordSP spt = new BellmanFordSP(G, 0);

                    if (spt.hasNegativeCycle() && !executedOnce) {
                        //executedOnce = true;

                        Iterable<DirectedEdge> directedEdges = spt.negativeCycle();
                        /*
                        directedEdges.forEach(directedEdge -> {

                            log.debug("directEdge with negative cycle:\n"
                                    + "from: " + directedEdge.from()
                                    + "\nto:" + directedEdge.to()
                                    + "\nweight: " + directedEdge.weight());
                        });*/


                         //new BigDecimal("1.0").doubleValue();
                        //log.debug("amount to trade: " + stake);
                        //log.debug("found arbitrage:");
                        log.debug("============== START OF ARBITRAGE WINDOW ==========");


                        final Iterable<DirectedEdge> negativeCycle = spt.negativeCycle();
                        negativeCycle.forEach(record -> {
                            jump++;
                            if (jump == 1
                                    &&  uniqueCurrencies.stream()
                                    .filter(uniqueCurrency -> record.from() == uniqueCurrency.getVertex())
                                    .findFirst().get().getCurrency().equals(Currency.BTC)
                                    ) {
                                log.debug("CYCLE STARTS WITH BTC - GOOD");

                            startswithBTC = true;

                            }
                        }
                        );
                        log.debug("JUMPS: " + jump);
                        //jump == 3 &&
                        if ( jump >1 ) {

                            jump = 0;


                            log.debug(" WE HAVE 3 JUMPS");
                            for (DirectedEdge e : negativeCycle) {


                                log.debug("Cycle " + ++jump);


                                log.debug("=== START REAL MARKET DATA FOR QA AUDIT");

                                final BotCurrencyPair botCurrencyPair = botCurrencyPairs.stream()
                                        .filter(record -> record.getGraphCurrencyBase().getVertex() == e.from() && record.getGraphCurrencyCounter().getVertex() == e.to())
                                        .findFirst()
                                        .orElse(null);

                                if (true){

                                    log.debug("SUPER - GRAPH STARTING WITH BTC");

                                    log.debug("Arbittrage starts from position: " + botCurrencyPair.toString());
                                    if (botCurrencyPair.getBotCurrencyPairType().equals(BotCurrencyPair.BotCurrencyPairType.ARTIFICIAL)) {
                                        log.debug("ALERT: Arbittrage detected accross artifical record - seek for REAL record.");


                                        final BotCurrencyPair botCurrencyPairREAL = botCurrencyPairs.stream()
                                                .filter(record ->
                                                        record.getGraphCurrencyBase().equals(botCurrencyPair.getGraphCurrencyCounter())
                                                                &&
                                                                record.getGraphCurrencyCounter().equals(botCurrencyPair.getGraphCurrencyBase()))
                                                .findFirst()
                                                .orElse(null);
                                        log.debug("REAL record to make trade on: " + botCurrencyPairREAL.toString());

                                        stake = stake * botCurrencyPair.getLowAskPriceOriginal().doubleValue();

                                        processSell = new LimitOrder
                                                .Builder(Order.OrderType.BID, botCurrencyPairREAL.getCurrencyPair())
                                                //.originalAmount(sell.getOriginalAmount())
                                                //.originalAmount( new BigDecimal(stake, MathContext.DECIMAL64))
                                                .originalAmount( new BigDecimal(stake,mathContext).multiply(botCurrencyPair.getLowAskPrice()))
                                                .limitPrice(botCurrencyPairREAL.getLowAskPrice())
                                                .build();

                                    } else {

                                        log.debug("We have REAL record can do directly trade by using HIGH BID");
                                        processSell = new LimitOrder
                                                .Builder(Order.OrderType.ASK, botCurrencyPair.getCurrencyPair())
                                                //.originalAmount(sell.getOriginalAmount())
                                                .originalAmount( new BigDecimal(stake,mathContext))
                                                //.limitPrice(botCurrencyPair.getHighBidPrice().multiply(new BigDecimal("0.9973")))
                                                .limitPrice(botCurrencyPair.getHighBidPrice())
                                                .build();

                                    }
                                    log.debug("Generated Limit order: " + processSell.toString());
                                    //log.debug("START: amount=" + df.format(stake) + ", record=" + botCurrencyPairs.get(e.from()).get);
                                    //stake = stake.multiply(BigDecimalMath.exp(e.weight().negate(),mathContext));
                                    log.debug("=== END OF REAL MARKET DATA FOR QA AUDIT");
                                    log.debug("=== START OPERATIONAL GRAPH DATA");

                                    log.debug("WE WILL SELL: stake=" + df.format(stake)
                                            + ", currency=" +
                                            uniqueCurrencies.stream()
                                                    .filter(record -> e.from() == record.getVertex())
                                                    .findFirst().get().getCurrency().toString());

                                    // Convert stake
                                    stake *= Math.exp(-e.weight());
                                    log.debug("WE WILL BUY: stake=" + df.format(stake) + ", currency=" + uniqueCurrencies.stream()
                                            .filter(record -> e.to() == record.getVertex())
                                            .findFirst().get().getCurrency().toString()
                                    );


                                    /**
                                     * =========================================================
                                     *                    DO TRADE
                                     *                    ======================================
                                     */

                                    try {
                                        //exchangePoolingAdapter.getTradeService().placeLimitOrder(processSell);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                    boolean ordersprocessed = false;
                                    while (ordersprocessed == false){
                                       log.debug("waiting for trade to close.");

                                            try {

                                                final SynchronizedValueFactory<Long> nonceFactory = exchangePoolingAdapter.getNonceFactory();
                                                nonceFactory.createValue();
                                                final int size = exchangePoolingAdapter.getTradeService().getOpenOrders().getOpenOrders().size();
                                                if (size < 5){
                                                    ordersprocessed = true;

                                                } else {
                                                    Thread.sleep(100);
                                                }
                                            } catch (InterruptedException ie) {
                                                ie.printStackTrace();
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }


                                    }

                                    log.debug("Trade finished: " + processSell.toString());
                                     processSell = null;



                                        log.debug("====== END OF OPERATIONAL GRAPH DATA ====");
                                        log.debug("End of cycle: " + jump);


                                    }

                                }
                                Thread.sleep(1000);

                            log.debug("============== END OF ARBITRAGE WINDOW ==========");
                            log.debug("exiting");
                            } else {
                            log.debug("Window doesn't start with BTC");


                        }
                    } else {
                        //log.debug("NO WINDOW FOUND");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    log.debug("Some error:" + e);
                }

        }
    }
    }



    @Data
    public class PoloniexHarvester implements Runnable{

        private BotCurrencyPair botCurrencyPair;
        private BotCurrencyPair oppositeBotCurrencyPair = null;
        PoloniexHarvester(BotCurrencyPair botCurrencyPair){
            this.botCurrencyPair = botCurrencyPair;
            Thread.currentThread().setName("Thread-" + botCurrencyPair.getCurrencyPair().toString());
        }

        @Override
        public void run() {
            log.debug("Thread started: " + botCurrencyPair.toString());
            // Subscribe order book data with the reference to the subscription.
            try {
                //Subscribe to order book on thread managed pair
                Disposable subscription = exchange.getStreamingMarketDataService()
                        .getOrderBook(botCurrencyPair.getCurrencyPair())
                        .subscribe(orderBook -> {
                            //log.debug("HARVESTER: " + Thread.currentThread().getName());
                            // Get LA
                            final LimitOrder limitOrderLowAsk = orderBook.getAsks()
                                    .stream()
                                    .min(Comparator.comparing(LimitOrder::getLimitPrice))
                                    .orElseThrow(NoSuchElementException::new);

                            // Set in collection
                            //.multiply(new BigDecimal("1.03")
                            botCurrencyPair.setLowAskPrice(limitOrderLowAsk.getLimitPrice().multiply(new BigDecimal("1.003")));
                            botCurrencyPair.setLowAskPriceOriginal(limitOrderLowAsk.getLimitPrice());
                            botCurrencyPair.setLowAskAmount(limitOrderLowAsk.getOriginalAmount());

                            // Get HB
                            final LimitOrder limitOrderHighBid = orderBook.getBids()
                                    .stream()
                                    .max(Comparator.comparing(LimitOrder::getLimitPrice))
                                    .orElseThrow(NoSuchElementException::new);

                            // set in collection
                            //.multiply(new BigDecimal("1.03"))
                            botCurrencyPair.setHighBidPrice(limitOrderHighBid.getLimitPrice().multiply(new BigDecimal("0.997")));
                            botCurrencyPair.setHighBidPriceOriginal(limitOrderHighBid.getLimitPrice());
                            botCurrencyPair.setHighBidAmount(limitOrderHighBid.getOriginalAmount());

                            // Now populate oposite price opposite record ARTIFICIAL one
                            oppositeBotCurrencyPair = null;
                            botCurrencyPairs
                                    .stream()
                                    .peek(record -> {


                                    })
                                    .filter(record -> record.getBotCurrencyPairType().equals(BotCurrencyPair.BotCurrencyPairType.ARTIFICIAL))
                                    .forEach(record -> {
                                        //log.debug(record.getCurrencyPair().toString() + " seeks its opposite: " + new CurrencyPair(record.getCurrencyPair().counter, record.getCurrencyPair().base));
                                        if (record.getGraphCurrencyBase().equals(botCurrencyPair.getGraphCurrencyCounter())
                                                &&
                                                record.getGraphCurrencyCounter().equals(botCurrencyPair.getGraphCurrencyBase())){
                                            //log.debug("FOUND ARTIFICIAL RECORE FOR REAL ONE");
                                            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!! must negate record
                                            oppositeBotCurrencyPair = record;

                                        } else {

                                        }
                                    });

                            if (oppositeBotCurrencyPair != null) {
                                //log.debug("Found opposite: " + oppositeBotCurrencyPair.toString());
                                oppositeBotCurrencyPair.setLowAskPrice(new BigDecimal("1.0").divide(botCurrencyPair.getHighBidPrice(),8, RoundingMode.HALF_UP));
                                final BigDecimal divide = new BigDecimal("1.0").divide(botCurrencyPair.getHighBidPriceOriginal(), 8, RoundingMode.HALF_UP);
                                oppositeBotCurrencyPair.setLowAskPriceOriginal(divide);


                                oppositeBotCurrencyPair.setHighBidPrice(new BigDecimal("1.0").divide(botCurrencyPair.getLowAskPrice(),8, RoundingMode.HALF_DOWN));
                                oppositeBotCurrencyPair.setHighBidPrice(new BigDecimal("1.0").divide(botCurrencyPair.getLowAskPriceOriginal(),8, RoundingMode.HALF_DOWN));
                                // todo - calculate opposite amount
                                oppositeBotCurrencyPair.setOriginalAmount(divide.multiply(limitOrderHighBid.getOriginalAmount()));

                            } else {
                                throw new Exception("Opposite pair wasn't found, some bug");
                            }


                            //log.debug("Harvesting - " + botCurrencyPair.toString());

                        });
                log.debug("THREAD FINISHED");

            } catch (Exception e){
                log.error(Thread.currentThread().getName() + " - " + e.toString());
            }
        }
    }




}
