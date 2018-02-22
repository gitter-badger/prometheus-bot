package org.prokyon.crypto_currency.bot_core.proxy;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prokyon.crypto_currency.bot_core.core_struct.ProxyBufferFactory;
import org.prokyon.crypto_currency.bot_core.proxy.model.Proxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class ProxyMetadata  {

    public static Logger logger = LogManager.getLogger(ProxyMetadata.class);
    public static List<Proxy> PROXIES = new ArrayList<>();
    public static int PROXIES_AVAILABLE;

    public static ProxyBufferFactory PROXY_OBJECT_FACTORY = new ProxyBufferFactory();
    public static GenericObjectPoolConfig PROXY_POOL_CONFIG = new GenericObjectPoolConfig();
    public static GenericObjectPool<Proxy> PROXY_POOL;
    //Pool<Proxy> PROXY_POOL = new BoundedBlockingPool< Proxy >();





    public void init() {
        final List<Proxy> availableProxies = getAvailableProxies();
        PROXIES_AVAILABLE = availableProxies.size();
        PROXY_POOL_CONFIG.setMaxTotal(PROXIES_AVAILABLE);
        PROXY_POOL_CONFIG.setBlockWhenExhausted(true);
        PROXY_POOL_CONFIG.setTestOnBorrow(true);
        //PROXY_POOL = new BoundedBlockingPool< Proxy >();
        try {
            PROXY_POOL = new GenericObjectPool<Proxy>(PROXY_OBJECT_FACTORY, PROXY_POOL_CONFIG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Proxy> initializator = new ArrayList<>();
        logger.debug("Pool management starting...");

        logger.debug("Pool start idle:" + PROXY_POOL.getNumIdle());
        logger.debug("Pool start active:" + PROXY_POOL.getNumActive());
        for (Proxy proxyToList : availableProxies){
            try {

                //final Proxy proxy = new Proxy();
                final Proxy proxy = PROXY_POOL.borrowObject();
                proxy.setProxyHost(proxyToList.getProxyHost());
                proxy.setProxyPort(proxyToList.getProxyPort());
                //PROXY_OBJECT_FACTORY.wrap(proxy);
                initializator.add(proxy);
                //PROXY_POOL.returnObject(proxy);
                //logger.debug("Pool idle - loop " + String.valueOf(i)+ ": " + PROXY_POOL.getNumIdle());
                //logger.debug("Pool active - loop"+ String.valueOf(i) + ": " + PROXY_POOL.getNumActive());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logger.debug("ALL PROXIES BORROWED");
        logger.debug("Pool after borrow idle:" + PROXY_POOL.getNumIdle());
        logger.debug("Pool after borrow active:" + PROXY_POOL.getNumActive());
        logger.debug("Going to return");
        for (Proxy proxy : initializator){
            PROXY_POOL.returnObject(proxy);
            logger.debug("Pool after return idle:" + PROXY_POOL.getNumIdle());
            logger.debug("Pool after return active:" + PROXY_POOL.getNumActive());
        }

        logger.debug("Clearing buffer: " + initializator.size());
        initializator.clear();
        logger.debug("Clearing buffer: " + initializator.size());
        logger.debug("Pool status before reborrow.");
        logger.debug("Pool idle:" + PROXY_POOL.getNumIdle());
        logger.debug("Pool active:" + PROXY_POOL.getNumActive());
        /*
        for (int i= 0; i < PROXIES_AVAILABLE; i++){
            try {

                final Proxy proxy = PROXY_POOL.borrowObject();


                logger.debug("Pool idle:" + PROXY_POOL.getNumIdle());
                logger.debug("Pool active:" + PROXY_POOL.getNumActive());
                logger.debug("Borrowed final proxy:" + proxy.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

    }






    private List<Proxy> getAvailableProxies(){

        String proxyLineDelimiter = "&";
        String proxyRecordDelimiter = "@";
        List<Proxy> proxies = new ArrayList<>();
        String proxiesRaw = readProxies();
        //logger.debug(proxiesRaw.toString());
        Arrays.asList(proxiesRaw.split(proxyLineDelimiter)).stream()
               // .peek(s -> logger.debug("Proxy record extracted from properties: " + s))
                .forEach(proxyRecord -> {
                    //logger.debug("Proxy record: " + proxyRecord);
                    final String[] split = proxyRecord.split(proxyRecordDelimiter);
                    //logger.debug(split.length);
                    proxies.add(new Proxy(split[0],Integer.valueOf(split[1])));
                });
        if (proxies.size() < 1){
            throw new RuntimeException("No proxies available, cannot continue");
        }

        PROXIES_AVAILABLE = proxies.size();

        return proxies;
    }

    private String readProxies(){
        //String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        ClassLoader classLoader = getClass().getClassLoader();
        String propFileName = "proxy.properties";
        Properties prop = new Properties();

        InputStream inputStream = null;
        String proxies = "";

        try {

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);

            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            //prop.load(new FileInputStream(propFileName));
            Date time = new Date(System.currentTimeMillis());

            //logger.debug(prop.toString());
            // get the property value and print it out
            proxies = prop.getProperty("proxies");
            //logger.debug(proxies.toString());
        } catch (FileNotFoundException e) {
            System.out.println("Exception: " + e);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return proxies;
    }




}
