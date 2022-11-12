package com.github.shiyouping.redis.embedded.util;

import static com.github.shiyouping.redis.embedded.Platform.ARCH;
import static com.github.shiyouping.redis.embedded.Platform.OS;

import com.github.shiyouping.redis.embedded.Platform;
import com.github.shiyouping.redis.embedded.exception.EmbeddedRedisException;
import lombok.extern.slf4j.Slf4j;

/**
 * PlatformUtil.
 *
 * @author ricky.shiyouping@gmail.com
 * @since 08/11/2022
 */
@Slf4j
public final class PlatformUtil {

    private PlatformUtil() {}

    public static Platform getPlatform() {
        final String os = System.getProperty("os.name").toLowerCase();
        PlatformUtil.log.info("The operation system is {}", os);

        if (!os.contains("mac") && !os.contains("linux")) {
            throw new EmbeddedRedisException("Unsupported OS: " + os);
        }

        final Platform platform = new Platform();
        platform.setArch(PlatformUtil.getArch());

        if (os.contains("mac")) {
            platform.setOs(OS.MACOS);
            return platform;
        }

        platform.setOs(PlatformUtil.getLinuxOs());
        return platform;
    }

    private static ARCH getArch() {
        String arch = CommandLine.getOutput("arch");
        PlatformUtil.log.info("The architecture is {}", arch);

        if (arch == null) {
            throw new EmbeddedRedisException("Unable to get macos architecture");
        }

        arch = arch.toLowerCase();

        if (arch.contains("arm") || arch.contains("aarch")) {
            return ARCH.ARM64;
        }

        return ARCH.X86_64;
    }

    private static OS getLinuxOs() {
        String os = CommandLine.getOutput("grep '^NAME' /etc/os-release");
        if (os == null) {
            throw new EmbeddedRedisException("Unable to get linux os");
        }

        os = os.toLowerCase();

        if (os.contains("ubuntu")) {
            return OS.UBUNTU;
        }

        if (os.contains("debian")) {
            return OS.DEBIAN;
        }

        if (os.contains("red hat")) {
            return OS.REDHAT;
        }

        throw new EmbeddedRedisException("Unsupported linux os " + os);
    }
}
