package com.evervault.utils;

import java.util.regex.Pattern;
import java.util.Arrays;

public class DomainRegexHandler {
    private final static Pattern GLOBSTAR_PATTERN = Pattern.compile("(?<!\\*)\\*\\*+\\.");
    private final static Pattern DOT_PATTERN = Pattern.compile("\\.");
    private final static Pattern STAR_PATTERN = Pattern.compile("(?<!\\*)\\*+");

    private final static Pattern SUBDOMAIN_PATTERN = Pattern.compile("(\\.\\*)([^\\\\*.])");

    public static String buildDomainRegexFromPattern(String pattern) {
        pattern = GLOBSTAR_PATTERN.matcher(pattern).replaceAll("*");
        pattern = DOT_PATTERN.matcher(pattern).replaceAll("\\\\.");
        pattern = STAR_PATTERN.matcher(pattern).replaceAll(".*");
        pattern = SUBDOMAIN_PATTERN.matcher(pattern).replaceAll("$1(^|\\\\.)$2");
        return String.format("^%s$", pattern);
    }

    public static Pattern[] buildDomainRegexesFromPatterns(String[] patterns) {
        return Arrays.stream(patterns)
            .map(DomainRegexHandler::buildDomainRegexFromPattern)
            .map(pattern -> Pattern.compile(pattern, Pattern.CASE_INSENSITIVE))
            .toArray(Pattern[]::new);
    }
}
