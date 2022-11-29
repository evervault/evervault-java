package com.evervault.services;

import com.evervault.contracts.IProvideDecryptionAndIgnoreDomains;
import com.evervault.contracts.IProvideOutboundRelayConfigFromHttpApi;
import com.evervault.exceptions.HttpFailureException;
import com.evervault.models.OutboundRelayConfigResult;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CachedOutboundRelayConfigService implements IProvideDecryptionAndIgnoreDomains {

    private static final Object lock = new Object();

    private static OutboundRelayConfigResult cachedConfig;

    public static void clearCache() {
        cachedConfig = null;
    }

    private String[] alwaysIgnoreDomains;

    public CachedOutboundRelayConfigService(IProvideOutboundRelayConfigFromHttpApi httpHandler, String evervaultApiUrl, String[] alwaysIgnoreDomains)
            throws HttpFailureException, IOException, InterruptedException {
        this(httpHandler, evervaultApiUrl, new ExecutableSchedulerService(1), alwaysIgnoreDomains);
    }

    private CachedOutboundRelayConfigService(IProvideOutboundRelayConfigFromHttpApi httpHandler, String evervaultApiUrl, ExecutableSchedulerService executableSchedulerService, String[] alwaysIgnoreDomains)
            throws HttpFailureException, IOException, InterruptedException {
        this.alwaysIgnoreDomains = alwaysIgnoreDomains;
        if (cachedConfig == null) {
            synchronized (lock) {
                if (cachedConfig == null) {
                    cachedConfig = httpHandler.getOutboundRelayConfig(evervaultApiUrl);
                    executableSchedulerService.schedule(() -> {
                        cachedConfig = httpHandler.getOutboundRelayConfig(evervaultApiUrl);
                        return cachedConfig;
                    }, 1, 1, TimeUnit.MINUTES);
                }
            }
        }
    }

    public String[] getAlwaysIgnoreDomains() {
        return alwaysIgnoreDomains;
    }

    public String[] getDecryptionDomains() {
        return cachedConfig.outboundDestinations
                .values()
                .stream()
                .map(domain -> domain.destinationDomain)
                .toArray(String[]::new);
    }

}
