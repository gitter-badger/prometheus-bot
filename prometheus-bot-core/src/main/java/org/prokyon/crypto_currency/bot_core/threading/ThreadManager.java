package org.prokyon.crypto_currency.bot_core.threading;

import net.openhft.affinity.AffinityLock;
import net.openhft.affinity.AffinityStrategies;

public class ThreadManager {

    public final static AffinityLock al = AffinityLock.acquireLock();

    public AffinityLock assignThread(){

        return al.acquireLock(
                AffinityStrategies.SAME_SOCKET,
                AffinityStrategies.DIFFERENT_CORE);
    }
}
