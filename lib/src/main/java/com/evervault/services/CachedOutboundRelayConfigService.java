package com.evervault.services;

import com.evervault.contracts.IExecuteRepeatableTask;
import com.evervault.contracts.IProvideDecryptionAndIgnoreDomains;
import com.evervault.contracts.IProvideOutboundRelayConfigFromHttpApi;
import com.evervault.contracts.IScheduleRepeatableTask;
import com.evervault.models.OutboundRelayConfigResult;
import com.evervault.utils.DomainRegexHandler;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CachedOutboundRelayConfigService implements IProvideDecryptionAndIgnoreDomains {

    private static final int DEFAULT_POLL_INTERVAL = 5;
    private static final Object lock = new Object();

    private static volatile OutboundRelayConfigResult.OutboundRelayConfig cachedConfig;
    private static volatile String[] cachedDecryptionDomains;
    private static volatile Pattern[] cachedDecryptionDomainRegexes;

    public static void clearCache() {
        cachedConfig = null;
    }

    private final String[] alwaysIgnoreDomains;
    private final Pattern[] alwaysIgnoreDomainRegexes;

    public CachedOutboundRelayConfigService(IProvideOutboundRelayConfigFromHttpApi httpHandler, IScheduleRepeatableTask repeatableTaskScheduler, String evervaultApiUrl, String[] alwaysIgnoreDomains)
            throws Exception {
        this.alwaysIgnoreDomains = alwaysIgnoreDomains;
        this.alwaysIgnoreDomainRegexes = DomainRegexHandler.buildDomainRegexesFromPatterns(alwaysIgnoreDomains);
        if (cachedConfig == null) {
            synchronized (lock) {
                if (cachedConfig == null) {
                    var task = new GetOutboundRelayConfigTask(DEFAULT_POLL_INTERVAL, TimeUnit.SECONDS, httpHandler, evervaultApiUrl);
                    task.execute();
                    repeatableTaskScheduler.schedule(task);
                }
            }
        }
    }

    public String[] getAlwaysIgnoreDomains() {
        return alwaysIgnoreDomains;
    }

    public Pattern[] getAlwaysIgnoreDomainRegexes() {
        return alwaysIgnoreDomainRegexes;
    }

    public String[] getDecryptionDomains() {
        return cachedDecryptionDomains;
    }

    public Pattern[] getDecryptionDomainRegexes() {
        return cachedDecryptionDomainRegexes;
    }

    public static class GetOutboundRelayConfigTask extends IExecuteRepeatableTask {

        private final IProvideOutboundRelayConfigFromHttpApi httpHandler;

        private final String evervaultApiUrl;

        public GetOutboundRelayConfigTask(int delay, TimeUnit timeUnit, IProvideOutboundRelayConfigFromHttpApi httpHandler, String evervaultApiUrl) {
            super(delay, timeUnit);
            this.httpHandler = httpHandler;
            this.evervaultApiUrl = evervaultApiUrl;
        }

        @Override
        public void execute() throws Exception {
            var result = httpHandler.getOutboundRelayConfig(evervaultApiUrl);
            cachedConfig = result.config;
            cachedDecryptionDomains = cachedConfig.outboundDestinations
                .values()
                .stream()
                .map(domain -> domain.destinationDomain)
                .toArray(String[]::new);
            cachedDecryptionDomainRegexes = DomainRegexHandler.buildDomainRegexesFromPatterns(cachedDecryptionDomains);
            updateDelay((result.pollInterval != null) ? result.pollInterval : getDelay(), getTimeUnit());
        }
    }

}
