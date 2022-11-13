package com.github.shiyouping.redis.embedded.util;

import com.github.shiyouping.redis.embedded.exception.EmbeddedRedisException;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 22/10/2022
 */
public interface Preconditions {

    static void checkArgument(final boolean expression, final String message) {
        if (!expression) {
            throw new EmbeddedRedisException(message);
        }
    }

    static String checkNotBlank(final String reference, final String message) {
        if (reference == null || reference.trim().isEmpty()) {
            throw new EmbeddedRedisException(message);
        }

        return reference;
    }

    static <T> T checkNotNull(final T reference, final String message) {
        if (reference == null) {
            throw new EmbeddedRedisException(message);
        }

        return reference;
    }
}
