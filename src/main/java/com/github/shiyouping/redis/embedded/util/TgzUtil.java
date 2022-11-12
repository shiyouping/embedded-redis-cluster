package com.github.shiyouping.redis.embedded.util;

import static com.github.shiyouping.redis.embedded.util.Preconditions.checkArgument;
import static com.github.shiyouping.redis.embedded.util.Preconditions.checkNotBlank;
import static com.github.shiyouping.redis.embedded.util.Preconditions.checkNotNull;

import com.github.shiyouping.redis.embedded.Platform;
import com.github.shiyouping.redis.embedded.exception.EmbeddedRedisException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 23/10/2022
 */
@Slf4j
public final class TgzUtil {

    private TgzUtil() {}

    public static void copyTgz(final String source, final Path targetDir) {
        checkNotBlank(source, "source cannot be blank");
        checkNotNull(targetDir, "targetDir cannot be null");
        checkArgument(Files.isDirectory(targetDir), "targetDir must be a directory");

        if (!source.contains(".tar.gz") && !source.contains(".tgz")) {
            throw new EmbeddedRedisException(source + " is not a tgz file");
        }

        try {
            final URL url = TgzUtil.class.getClassLoader().getResource(source);
            FileUtils.copyURLToFile(
                    checkNotNull(url, "url cannot be null"),
                    targetDir.resolve(source).toFile());
            TgzUtil.log.info("{} was copied to {}", source, targetDir);
        } catch (final IOException e) {
            final String message = "Failed to copyTgz source=" + source + " to the targetDir=" + targetDir;
            TgzUtil.log.error(message, e);
            throw new EmbeddedRedisException(message, e);
        }
    }

    public static void extractTgz(final String source, final Path targetDir) {
        checkNotNull(source, "source cannot be null");
        checkNotNull(targetDir, "targetDir cannot be null");
        checkArgument(Files.isDirectory(targetDir), "targetDir must be a directory");

        try (final InputStream fis = Files.newInputStream(targetDir.resolve(source));
                final InputStream bis = new BufferedInputStream(fis);
                final InputStream gzis = new GzipCompressorInputStream(bis);
                final ArchiveInputStream tis = new TarArchiveInputStream(gzis)) {

            ArchiveEntry entry;
            while ((entry = tis.getNextEntry()) != null) {
                if (!tis.canReadEntryData(entry)) {
                    continue;
                }

                final File f = targetDir.resolve(entry.getName()).toFile();
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) {
                        throw new EmbeddedRedisException("Failed to create directory " + f);
                    }
                } else {
                    final File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new EmbeddedRedisException("Failed to create directory " + parent);
                    }

                    try (final OutputStream o = Files.newOutputStream(f.toPath())) {
                        IOUtils.copy(tis, o);
                    }

                    TgzUtil.setPermissions(f.toPath());
                }
            }

            TgzUtil.log.info("{} was extracted to {}", source, targetDir);
        } catch (final Exception e) {
            final String message = "Failed to extractTgz " + source + " to " + targetDir;
            TgzUtil.log.error(message, e);
            throw new EmbeddedRedisException(message, e);
        }
    }

    public static String getTgzName() {
        try (final InputStream inputStream = TgzUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            final Properties properties = new Properties();
            properties.load(inputStream);
            final String version = properties.getProperty("redis.version");

            final Platform platform = PlatformUtil.getPlatform();
            final String os = platform.getOs().toString().toLowerCase();
            final String arch = platform.getArch().toString().toLowerCase();

            return String.format("%s-%s-%s", os, arch, version);
        } catch (final Exception e) {
            final String message = "Failed to get tgz name";
            TgzUtil.log.error(message, e);
            throw new EmbeddedRedisException(message, e);
        }
    }

    private static void setPermissions(final Path path) throws IOException {
        if (!path.toString().contains("bin")) {
            return;
        }

        final Set<PosixFilePermission> permissions = new HashSet<>();
        permissions.add(PosixFilePermission.OWNER_READ);
        permissions.add(PosixFilePermission.OWNER_WRITE);
        permissions.add(PosixFilePermission.OWNER_EXECUTE);

        permissions.add(PosixFilePermission.OTHERS_READ);
        permissions.add(PosixFilePermission.OTHERS_WRITE);
        permissions.add(PosixFilePermission.OTHERS_EXECUTE);

        permissions.add(PosixFilePermission.GROUP_READ);
        permissions.add(PosixFilePermission.GROUP_WRITE);
        permissions.add(PosixFilePermission.GROUP_EXECUTE);

        Files.setPosixFilePermissions(path, permissions);
    }
}
