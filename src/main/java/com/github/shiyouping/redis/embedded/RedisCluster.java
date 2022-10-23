package com.github.shiyouping.redis.embedded;

import com.github.shiyouping.redis.embedded.cli.RedisCli;
import com.github.shiyouping.redis.embedded.cli.RedisFile;
import com.github.shiyouping.redis.embedded.config.Config;
import com.github.shiyouping.redis.embedded.exception.EmbeddedRedisException;
import com.github.shiyouping.redis.embedded.util.TgzUtil;
import lombok.extern.slf4j.Slf4j;

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
        final RedisFile redisFile = this.createRedisFile();
        this.redisCli = new RedisCli(config, redisFile);
        this.redisCli.init();
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

    private RedisFile createRedisFile() {
        try {
            final Path baseDir = Files.createTempDirectory("embedded-redis-cluster-");
            final String tgzName = TgzUtil.getTgzName();
            final String tgzExtension = ".tgz";
            final Path redisDir = baseDir.resolve(tgzName);
            final Path binDir = redisDir.resolve("bin");
            return RedisFile.builder().baseDir(baseDir).redisDir(redisDir).binDir(binDir).tgzName(tgzName).tgzExtension(tgzExtension).build();
        } catch (final Exception e) {
            final String message = "Failed to create redis file";
            RedisCluster.log.error(message, e);
            throw new EmbeddedRedisException(message, e);
        }
    }
}
