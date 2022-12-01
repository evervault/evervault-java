package com.evervault.services;

import com.evervault.contracts.IExecuteRepeatableTask;
import com.evervault.contracts.IProvideDecryptionAndIgnoreDomains;
import com.evervault.contracts.IProvideOutboundRelayConfigFromHttpApi;
import com.evervault.contracts.IScheduleRepeatableTask;
import com.evervault.models.OutboundRelayConfigResult;

import java.util.concurrent.TimeUnit;

public class CachedOutboundRelayConfigService implements IProvideDecryptionAndIgnoreDomains {

    private static final int DEFAULT_POLL_INTERVAL = 5;
    private static final Object lock = new Object();

    private static volatile OutboundRelayConfigResult.OutboundRelayConfig cachedConfig;

    public static void clearCache() {
        cachedConfig = null;
    }

    private final String[] alwaysIgnoreDomains;

    public CachedOutboundRelayConfigService(IProvideOutboundRelayConfigFromHttpApi httpHandler, IScheduleRepeatableTask repeatableTaskScheduler, String evervaultApiUrl, String[] alwaysIgnoreDomains)
            throws Exception {
        this.alwaysIgnoreDomains = alwaysIgnoreDomains;
        if (cachedConfig == null) {
            synchronized (lock) {
                if (cachedConfig == null) {
                    var task = new GetOutboundDelayConfigTask(DEFAULT_POLL_INTERVAL, TimeUnit.SECONDS, httpHandler, evervaultApiUrl);
                    task.execute();
                    repeatableTaskScheduler.schedule(task);
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

    public static class GetOutboundDelayConfigTask extends IExecuteRepeatableTask {

        private final IProvideOutboundRelayConfigFromHttpApi httpHandler;

        private final String evervaultApiUrl;

        public GetOutboundDelayConfigTask(int delay, TimeUnit timeUnit, IProvideOutboundRelayConfigFromHttpApi httpHandler, String evervaultApiUrl) {
            super(delay, timeUnit);
            this.httpHandler = httpHandler;
            this.evervaultApiUrl = evervaultApiUrl;
        }

        @Override
        public void execute() throws Exception {
            var result = httpHandler.getOutboundRelayConfig(evervaultApiUrl);
            cachedConfig = result.config;
            updateDelay((result.pollInterval != null) ? result.pollInterval : getDelay(), getTimeUnit());
        }
    }

}
