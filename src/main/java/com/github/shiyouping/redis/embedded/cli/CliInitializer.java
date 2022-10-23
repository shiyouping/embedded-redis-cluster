package com.github.shiyouping.redis.embedded.cli;

import com.github.shiyouping.redis.embedded.exception.EmbeddedRedisException;
import com.github.shiyouping.redis.embedded.util.TgzUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static com.github.shiyouping.redis.embedded.util.Preconditions.checkArgument;
import static com.github.shiyouping.redis.embedded.util.Preconditions.checkNotNull;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 23/10/2022
 */
@Slf4j
public final class CliInitializer {

    private CliInitializer() {}

    public static void init(final Path workingDir) {
        checkNotNull(workingDir, "workingDir cannot be null");
        checkArgument(Files.isDirectory(workingDir), "workingDir must be a directory");

        try (final InputStream inputStream =
                CliInitializer.class.getClassLoader().getResourceAsStream("config.properties")) {
            final Properties properties = new Properties();
            properties.load(inputStream);

            final String version = properties.getProperty("redis.version");
            final String source = String.format("macos-arm64-redis-%s.tgz", version);

            TgzUtil.copy(source, workingDir);
            TgzUtil.decompress(source, workingDir);
        } catch (final Exception e) {
            final String message = "Failed to init redis cli";
            CliInitializer.log.error(message, e);
            throw new EmbeddedRedisException(message, e);
        }
    }
}
