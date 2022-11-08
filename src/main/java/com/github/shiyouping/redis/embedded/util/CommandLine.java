package com.github.shiyouping.redis.embedded.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;

import static com.github.shiyouping.redis.embedded.util.Preconditions.checkNotBlank;

/**
 * CommandLine.
 *
 * @author ricky.shiyouping@gmail.com
 * @since 08/11/2022
 */
@Slf4j
public final class CommandLine {


    public static String getOutput(final String command) {
        checkNotBlank(command, "command cannot be null");

        try {
            final Process process = Runtime.getRuntime().exec(command);
            final String output = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());
            CommandLine.log.info("The output of command={} is {}", command, output);
            return output.trim();
        } catch (final Exception e) {
            CommandLine.log.error("Failed to getOutput the command " + command, e);
            return null;
        }
    }
}
