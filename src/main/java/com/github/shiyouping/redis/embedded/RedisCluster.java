package com.github.shiyouping.redis.embedded;

import com.github.shiyouping.redis.embedded.cli.CliInitializer;
import com.github.shiyouping.redis.embedded.cli.RedisCli;
import com.github.shiyouping.redis.embedded.config.Config;
import com.github.shiyouping.redis.embedded.exception.EmbeddedRedisException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.shiyouping.redis.embedded.util.Preconditions.checkNotNull;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 22/10/2022
 */
@Slf4j
public class RedisCluster {

    private final RedisCli redisCli;

    public RedisCluster(final Config config) {
        checkNotNull(config, "config cannot be null");

        try {
            final Path workingDir = Files.createTempDirectory("embedded-redis-cluster-");
            RedisCluster.log.info("Working directory was created at {}", workingDir);

            this.redisCli = new RedisCli(config, workingDir);
            CliInitializer.init(workingDir);
        } catch (final IOException e) {
            final String message = "Failed to create the working directory";
            RedisCluster.log.error(message, e);
            throw new EmbeddedRedisException(message, e);
        }
    }

    public void start() {
        this.redisCli.startServers();
        this.redisCli.createCluster();
        RedisCluster.log.info("Redis cluster started");
    }

    public void stop() {
        this.redisCli.stopCluster();
        this.redisCli.cleanCluster();
        RedisCluster.log.info("Redis cluster stopped");
    }
}
