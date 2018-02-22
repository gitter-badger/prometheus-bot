package org.prokyon.crypto_currency.bot_core;

import edu.princeton.cs.algs4.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *  The {@code Arbitrage} class provides a client that finds an arbitrage
 *  opportunity in a currency exchange table by constructing a
 *  complete-digraph representation of the exchange table and then finding
 *  a negative cycle in the digraph.
 *  <p>
 *  This implementation uses the Bellman-Ford algorithm to find a
 *  negative cycle in the complete digraph.
 *  The running time is proportional to <em>V</em><sup>3</sup> in the
 *  worst case, where <em>V</em> is the number of currencies.
 *  <p>
 *  For additional documentation,
 *  see <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Arbitrage {

    private static final Logger log = LogManager.getLogger(Arbitrage.class);
    // this class cannot be instantiated
    private Arbitrage() { }

    /**
     *  Reads the currency exchange table from standard input and
     *  prints an arbitrage opportunity to standard output (if one exists).
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        // V currencies
        int V = 3;

        String[] name = {"BTC","ETH","REP"};
        /*
        3    BTC         ETH          REP
        BTC  1           0            0
        ETH  0.741  1            0
        REP  0.00609727  0.06085691   1
        */
        // create complete network
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(V);

        G.addEdge(new DirectedEdge(0, 0, -Math.log(1)));
        G.addEdge(new DirectedEdge(0, 1, -Math.log(0.741)));
        G.addEdge(new DirectedEdge(0, 2, -Math.log(1.005)));

        G.addEdge(new DirectedEdge(1, 0, -Math.log(1.349)));
        G.addEdge(new DirectedEdge(1, 1, -Math.log(1)));
        G.addEdge(new DirectedEdge(1, 2, -Math.log(1.366)));

        //REP
        G.addEdge(new DirectedEdge(2, 0, -Math.log(0.995)));
        G.addEdge(new DirectedEdge(2, 1, -Math.log(0.732)));
        G.addEdge(new DirectedEdge(2, 2, -Math.log(1)));

        log.debug("My graph: " + G.toString());
        // find negative cycle
        BellmanFordSP spt = new BellmanFordSP(G, 0);

        if (spt.hasNegativeCycle()) {
            Iterable<DirectedEdge> directedEdges = spt.negativeCycle();
            directedEdges.forEach(directedEdge -> {
                log.debug("directEdge with negative cycle:\n"
                        + "from: " + directedEdge.from()
                        + "\nto:" + directedEdge.to()
                        + "\nweight: " + directedEdge.weight());
            });

            double stake = 1000.0;
            log.debug("amount to trade: " + stake);
            log.debug("found arbitrage:");
            for (DirectedEdge e : spt.negativeCycle()) {
                StdOut.printf("%10.5f %s ", stake, name[e.from()]);
                stake *= Math.exp(-e.weight());
                StdOut.printf("= %10.5f %s\n", stake, name[e.to()]);
            }
        }
        else {
            StdOut.println("No arbitrage opportunity");
        }
    }

}

/******************************************************************************
 *  Copyright 2002-2016, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
