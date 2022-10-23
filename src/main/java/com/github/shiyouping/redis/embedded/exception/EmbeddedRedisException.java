package com.github.shiyouping.redis.embedded.exception;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 22/10/2022
 */
public class EmbeddedRedisException extends RuntimeException {

    private static final long serialVersionUID = 1892853615203776244L;

    public EmbeddedRedisException(final String message) {
        super(message);
    }

    public EmbeddedRedisException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
