package com.github.shiyouping.redis.embedded.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 22/10/2022
 */
@Slf4j
public final class Environment {

    public static final String REDIS_PORT = "REDIS_PORT";
    public static final String REDIS_MASTER_NODES = "REDIS_MASTER_NODES";
    public static final String REDIS_CLUSTER_NODE_TIMEOUT = "REDIS_CLUSTER_NODE_TIMEOUT";

    private Environment() {}

    public static int getInt(final String name, final int defaultValue) {
        try {
            final String value = System.getenv(name);
            if (value == null) {
                return defaultValue;
            }
            return Integer.parseInt(value);
        } catch (final Exception e) {
            Environment.log.error(
                    "Failed to get the environment variable for {}, use the default value={} instead. Error={}",
                    name,
                    defaultValue,
                    e.getMessage());
            return defaultValue;
        }
    }

    public static String getString(final String name, final String defaultValue) {
        try {
            final String value = System.getenv(name);
            if (value == null) {
                return defaultValue;
            }
            return value;
        } catch (final Exception e) {
            Environment.log.error(
                    "Failed to get the environment variable for {}, use the default value={} instead. Error={}",
                    name,
                    defaultValue,
                    e.getMessage());
            return defaultValue;
        }
    }
}
