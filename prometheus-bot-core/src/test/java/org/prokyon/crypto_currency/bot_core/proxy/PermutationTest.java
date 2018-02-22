package org.prokyon.crypto_currency.bot_core.proxy;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.prokyon.crypto_currency.bot_core.constant.BotExchange;
import org.prokyon.crypto_currency.bot_core.constant.BotExchange;
import org.prokyon.crypto_currency.bot_core.model.CurrencyPairExchange;
import org.prokyon.crypto_currency.bot_core.proxy.model.Proxy;

import java.util.*;

import static com.googlecode.cqengine.stream.StreamFactory.streamOf;

public class PermutationTest {

    @Test
    void printAllCombinations(){
        List<Object> proxyList = new ArrayList<>();
        List<Object> currencyPairExchanges = new ArrayList<>();
        List<List<Object>> collections = new ArrayList<>();
        //proxyList.add(new Proxy("HOST_1",1));
        //proxyList.add(new Proxy("HOST_2",1));
        //proxyList.add(new Proxy("HOST_3",1));
        collections.add(proxyList);

        currencyPairExchanges.add(BotExchange.ACX);
        currencyPairExchanges.add(BotExchange.ANXPRO);
        //currencyPairExchanges.add(BotExchange.HITBTC);

        /*
        currencyPairExchanges.add(new CurrencyPairExchange(BotExchange.ACX,new CurrencyPair(Currency.LTC,Currency.BTC)));
        currencyPairExchanges.add(new CurrencyPairExchange(BotExchange.ACX,new CurrencyPair(Currency.QAR,Currency.BTC)));
        currencyPairExchanges.add(new CurrencyPairExchange(BotExchange.ACX,new CurrencyPair(Currency.ADA,Currency.BTC)));
        currencyPairExchanges.add(new CurrencyPairExchange(BotExchange.HITBTC,new CurrencyPair(Currency.LTC,Currency.BTC)));
        currencyPairExchanges.add(new CurrencyPairExchange(BotExchange.HITBTC,new CurrencyPair(Currency.QAR,Currency.BTC)));
        currencyPairExchanges.add(new CurrencyPairExchange(BotExchange.HITBTC,new CurrencyPair(Currency.ADA,Currency.BTC)));*/
        collections.add(currencyPairExchanges);

        //permutations(collections);
        final Set<List<Object>> combinations = getCombinations(collections);
        streamOf(combinations)
                .forEach(objects -> {
                    objects.stream().forEach(o -> {
                        System.out.println(o.toString());
                    });
                });
    }

    public static <T> Set<List<T>> getCombinations(List<List<T>> lists) {
        Set<List<T>> combinations = new HashSet<List<T>>();
        Set<List<T>> newCombinations;

        int index = 0;

        // extract each of the integers in the first list
        // and add each to ints as a new list
        for(T i: lists.get(0)) {
            List<T> newList = new ArrayList<T>();
            newList.add(i);
            combinations.add(newList);
        }
        index++;
        while(index < lists.size()) {
            List<T> nextList = lists.get(index);
            newCombinations = new HashSet<List<T>>();
            for(List<T> first: combinations) {
                for(T second: nextList) {
                    List<T> newList = new ArrayList<T>();
                    newList.addAll(first);
                    newList.add(second);
                    newCombinations.add(newList);
                }
            }
            combinations = newCombinations;

            index++;
        }

        return combinations;
    }


    public static <T> Collection<List<T>> permutations(List<Collection<T>> collections) {
        if (collections == null || collections.isEmpty()) {
            return Collections.emptyList();
        } else {
            Collection<List<T>> res = Lists.newLinkedList();
            permutationsImpl(collections, res, 0, new LinkedList<T>());
            return res;
        }
    }

    /** Recursive implementation for {@link #permutations(List, Collection)} */
    private static <T> void permutationsImpl(List<Collection<T>> ori, Collection<List<T>> res, int d, List<T> current) {
        // if depth equals number of original collections, final reached, add and return
        if (d == ori.size()) {
            res.add(current);
            return;
        }
        // iterate from current collection and copy 'current' element N times, one for each element
        Collection<T> currentCollection = ori.get(d);
        for (T element : currentCollection) {
            List<T> copy = Lists.newLinkedList(current);
            copy.add(element);
            permutationsImpl(ori, res, d + 1, copy);
        }
    }


}
