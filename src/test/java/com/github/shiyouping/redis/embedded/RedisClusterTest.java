package com.github.shiyouping.redis.embedded;

import com.github.shiyouping.redis.embedded.config.Config;
import com.github.shiyouping.redis.embedded.config.ConfigBuilder;
import org.junit.jupiter.api.Test;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 22/10/2022
 */
public class RedisClusterTest {

    @Test
    public void runTest() {
        final Config config = new ConfigBuilder().build();
        final RedisCluster redisCluster = new RedisCluster(config);
        redisCluster.start();
        redisCluster.stop();
    }
}