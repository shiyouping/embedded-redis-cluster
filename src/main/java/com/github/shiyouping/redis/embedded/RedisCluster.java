package com.github.shiyouping.redis.embedded;

import static com.github.shiyouping.redis.embedded.util.Preconditions.checkNotNull;

import com.github.shiyouping.redis.embedded.cli.RedisCli;
import com.github.shiyouping.redis.embedded.cli.RedisFile;
import com.github.shiyouping.redis.embedded.config.Config;
import com.github.shiyouping.redis.embedded.exception.EmbeddedRedisException;
import com.github.shiyouping.redis.embedded.util.TgzUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

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

    private RedisFile createRedisFile() {
        try {
            final Path baseDir = Files.createTempDirectory("embedded-redis-cluster-");
            final String tgzName = TgzUtil.getTgzName();
            final String tgzExtension = ".tgz";
            final Path redisDir = baseDir.resolve(tgzName);
            final Path binDir = redisDir.resolve("bin");
            return RedisFile.builder()
                    .baseDir(baseDir)
                    .redisDir(redisDir)
                    .binDir(binDir)
                    .tgzName(tgzName)
                    .tgzExtension(tgzExtension)
                    .build();
        } catch (final Exception e) {
            final String message = "Failed to create redis file";
            RedisCluster.log.error(message, e);
            throw new EmbeddedRedisException(message, e);
        }
    }

    void start() {
        this.redisCli.startServers();
        this.redisCli.createCluster();
        RedisCluster.log.info("Redis cluster started");
    }

    void stop() {
        this.redisCli.stopCluster();
        this.redisCli.cleanCluster();
        RedisCluster.log.info("Redis cluster stopped");
    }
}
