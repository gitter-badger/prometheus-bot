package org.prokyon.crypto_currency.bot_core.model.harvester;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import lombok.*;
import net.openhft.affinity.AffinityLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prokyon.crypto_currency.bot_core.bot_manager.BotStorageEngine;
import org.prokyon.crypto_currency.bot_core.model.MarketRecord;
import org.prokyon.crypto_currency.bot_core.threading.ThreadManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Harvester is generic class which is used to especially to RECEIVE ONLY data from xchanges.
 *
 * Each instance is driven by its own thread and focuses only on unique combination of:
 * - xchange
 * - currency pair
 * - bid/ask
 *
 * There are some conditions to follow regarding performance and API restrictions:
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class Harvester implements Runnable{

    private Logger log = LogManager.getLogger(this.getClass());

    private ConcurrentIndexedCollection<MarketRecord> HARVESTER_BUY_ORDERS =
            new ConcurrentIndexedCollection<MarketRecord>();
    private ConcurrentIndexedCollection<MarketRecord> HARVESTER_SELL_ORDERS =
            new ConcurrentIndexedCollection<MarketRecord>();

    private HarvesterConfig harvesterConfig;


    /**
     * Thread management structures to support intureptable
     */
    private Thread worker;
    private int interval = 1000;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean stopped = new AtomicBoolean(true);

    private String threadName;

    BotStorageEngine botStorageEngine = new BotStorageEngine();


    public Harvester(int sleepInterval) {
        interval = sleepInterval;
    }


    public Harvester(HarvesterConfig harvesterConfig){
        //this.exchange = new ExchangeAdapter().createInstance(botExchange,proxy.getProxyHost(), proxy.getProxyPort());
        this.harvesterConfig = harvesterConfig;

        /**
         * Initialization of Harvester object.
         */

    }

    private Integer rounds = 0;

    public void start() {
        worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    public void interrupt() {
        running.set(false);
        worker.interrupt();
    }

    boolean isRunning() {
        return running.get();
    }

    boolean isStopped() {
        return stopped.get();
    }

    private final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("PERSISTENCE");

    private final EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();

    private void initStorage() {



        //HARVESTER_BUY_ORDERS.addIndex(UniqueIndex.onAttribute(ID));
        //HARVESTER_BUY_ORDERS.addIndex(NavigableIndex.onAttribute(ORIGINAL_AMOUNT));
        //HARVESTER_BUY_ORDERS.addIndex(NavigableIndex.onAttribute(LIMIT_PRICE));


        //HARVESTER_SELL_ORDERS.addIndex(UniqueIndex.onAttribute(ID));
        //HARVESTER_SELL_ORDERS.addIndex(NavigableIndex.onAttribute(ORIGINAL_AMOUNT));
        //HARVESTER_SELL_ORDERS.addIndex(NavigableIndex.onAttribute(LIMIT_PRICE));
    }
    @Override
    public void run() {
        try {
            initStorage();
            log.debug("Started harvester thread: " + this.toString());
            //threadName = ("Harvester Thread - " +  exchange + "-" + currencyPair.toString() + "-" + this.getSide());
            log.info("New harvesting started: " + threadName);
            running.set(true);
            stopped.set(false);

            Thread.currentThread().setName(threadName);




        } catch (Exception e){
            log.error(e);
        }

        // Assign thread to free CPU core - I work on minimum 16 core machines minimum
        try (AffinityLock al2 = new ThreadManager().assignThread()) {
            log.info("Thread " + threadName + " with id: " + Thread.currentThread().getId() + " locked");
        }

        log.debug("Putting thread " + threadName + " under interuptable management control.");
        log.debug("Thread can harvest: " + running.get());


        while (running.get()) {
            rounds++;

            try {
                //Thread.sleep(interval);
                //log.debug("REAL harvesting starting");








            } catch (Exception e) {
                Thread.currentThread().interrupt();
                log.error("Thread was interrupted, Failed to complete operation" + e);
                e.printStackTrace();
            }
            // do something
            if (rounds == 600){
                running.set(false);
            }
        }
        stopped.set(true);
    }

    public static <T> Stream<T> iteratorToStream(final Iterator<T> iterator, final boolean parallell) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), parallell);
    }



}
