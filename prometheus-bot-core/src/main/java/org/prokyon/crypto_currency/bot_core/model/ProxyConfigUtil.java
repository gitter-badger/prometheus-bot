package org.prokyon.crypto_currency.bot_core.model;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProxyConfigUtil {

    ConcurrentIndexedCollection<ProxyConfig> proxyConfigs = new ConcurrentIndexedCollection<ProxyConfig>();


}
