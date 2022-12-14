package com.github.shiyouping.redis.embedded.cli;

import static com.github.shiyouping.redis.embedded.util.Preconditions.checkNotNull;

import com.github.shiyouping.redis.embedded.config.Config;
import com.github.shiyouping.redis.embedded.exception.EmbeddedRedisException;
import com.github.shiyouping.redis.embedded.util.TgzUtil;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 22/10/2022
 */
@Slf4j
public class RedisCli {

    private static final String COMMAND_KILL = "kill -9 $(lsof -t -i:%d)";
    private static final String COMMAND_REDIS_CLI = "./redis-cli";
    private static final String COMMAND_REDIS_SERVER = "./redis-server";

    private static final String OPTION_CLUSTER_ENABLED = "--cluster-enabled";
    private static final String OPTION_CLUSTER_CONFIG_FILE = "--cluster-config-file";
    private static final String OPTION_CLUSTER_NODE_TIMEOUT = "--cluster-node-timeout";
    private static final String OPTION_PORT = "--port";
    private static final String OPTION_PROTECTED_MODE = "--protected-mode";
    private static final String OPTION_APPEND_ONLY = "--appendonly";
    private static final String OPTION_APPEND_FILE_NAME = "--appendfilename";
    private static final String OPTION_DB_FILE_NAME = "--dbfilename";
    private static final String OPTION_LOG_FILE = "--logfile";
    private static final String OPTION_DAEMONIZE = "--daemonize";
    private static final String OPTION_CLUSTER = "--cluster";
    private static final String OPTION_CLUSTER_REPLICAS = "--cluster-replicas";
    private static final String OPTION_CLUSTER_YES = "--cluster-yes";
    private static final String OPTION_P = "-p";
    private static final String OPTION_SHUT_DOWN = "shutdown";
    private static final String OPTION_NO_SAVE = "nosave";
    private static final String OPTION_CREATE = "create";

    private static final String HYPHEN = "-";
    private static final String SPACE = " ";
    private static final String COLON = ":";

    private final Config config;
    private final RedisFile redisFile;

    public RedisCli(final Config config, final RedisFile redisFile) {
        checkNotNull(config, "config cannot be null");
        checkNotNull(redisFile, "redisFile cannot be null");
        this.config = config;
        this.redisFile = redisFile;
    }

    public void cleanCluster() {
        final Path baseDir = this.redisFile.getBaseDir();
        final boolean succeed = FileUtils.deleteQuietly(baseDir.toFile());

        if (succeed) {
            RedisCli.log.info("Deleted redis temp dir={}", baseDir);
        } else {
            RedisCli.log.warn("Failed to deleted redis temp dir={}", baseDir);
        }

        RedisCli.log.info("Redis cluster cleaned");
    }

    public void createCluster() {
        final List<String> commands = this.buildCreateClusterCommands();
        this.execute(commands);
        RedisCli.log.info("Redis cluster created");
    }

    private List<String> buildCreateClusterCommands() {
        final List<String> commands = new ArrayList<>();
        commands.add(RedisCli.COMMAND_REDIS_CLI);
        commands.add(RedisCli.OPTION_CLUSTER);
        commands.add(RedisCli.OPTION_CREATE);

        IntStream.range(this.config.getPort(), this.config.getPort() + this.getNumOfNode())
                .forEach(port -> commands.add(this.config.getHost() + RedisCli.COLON + port));

        commands.add(RedisCli.OPTION_CLUSTER_REPLICAS);
        commands.add(String.valueOf(this.config.getClusterReplicas()));
        commands.add(RedisCli.OPTION_CLUSTER_YES);

        return commands;
    }

    private void execute(final List<String> commandList) {
        final String command = String.join(RedisCli.SPACE, commandList);
        RedisCli.log.info("Executing command={}", command);

        try {
            final ProcessBuilder builder = new ProcessBuilder(commandList);
            builder.directory(this.redisFile.getBinDir().toFile());
            builder.inheritIO();
            builder.redirectErrorStream(true);

            final Process process = builder.start();
            this.logOutput(process);

            final int result = process.waitFor();
            RedisCli.log.info("Execution result={}", result);
        } catch (final Exception e) {
            final String message = "Failed to getOutput command=" + command;
            RedisCli.log.error(message, e);
            throw new EmbeddedRedisException(message, e);
        }
    }

    private int getNumOfNode() {
        return this.config.getMasterNodes() * (this.config.getClusterReplicas() + 1);
    }

    private void logOutput(final Process process) {
        checkNotNull(process, "process cannot be null");

        try {
            RedisCli.log.info(IOUtils.toString(process.getInputStream(), Charset.defaultCharset()));
            RedisCli.log.error(IOUtils.toString(process.getErrorStream(), Charset.defaultCharset()));
        } catch (final Exception e) {
            final String message = "Failed to log the command output";
            RedisCli.log.error(message, e);
            throw new EmbeddedRedisException(message, e);
        }
    }

    public void init() {
        RedisCli.log.info("Initializing redis environment with redis file={}", this.redisFile);
        final String source = this.redisFile.getTgzFullName();
        TgzUtil.copyTgz(source, this.redisFile.getBaseDir());
        TgzUtil.extractTgz(source, this.redisFile.getBaseDir());
    }

    public void startServers() {
        this.buildStartServersCommands().forEach(this::execute);
        RedisCli.log.info("{} redis servers started", this.getNumOfNode());
    }

    private List<List<String>> buildStartServersCommands() {
        final List<List<String>> commands = new ArrayList<>(this.getNumOfNode());
        IntStream.range(this.config.getPort(), this.config.getPort() + this.getNumOfNode())
                .forEach(port -> {
                    final List<String> command = new ArrayList<>();
                    commands.add(command);
                    command.add(RedisCli.COMMAND_REDIS_SERVER);

                    command.add(RedisCli.OPTION_PORT);
                    command.add(String.valueOf(port));

                    command.add(RedisCli.OPTION_PROTECTED_MODE);
                    command.add(this.config.getProtectedMode());

                    command.add(RedisCli.OPTION_CLUSTER_ENABLED);
                    command.add(this.config.getClusterEnabled());

                    command.add(RedisCli.OPTION_CLUSTER_NODE_TIMEOUT);
                    command.add(this.config.getClusterNodeTimeout());

                    command.add(RedisCli.OPTION_APPEND_ONLY);
                    command.add(this.config.getAppendOnly());

                    command.add(RedisCli.OPTION_CLUSTER_CONFIG_FILE);
                    command.add(this.config.getClusterConfigFile() + RedisCli.HYPHEN + port + ".conf");

                    command.add(RedisCli.OPTION_APPEND_FILE_NAME);
                    command.add(this.config.getAppendFileName() + RedisCli.HYPHEN + port + ".aof");

                    command.add(RedisCli.OPTION_DB_FILE_NAME);
                    command.add(this.config.getDbFileName() + RedisCli.HYPHEN + port + ".rdb");

                    command.add(RedisCli.OPTION_LOG_FILE);
                    command.add(this.config.getLogFile() + RedisCli.HYPHEN + port + ".log");

                    command.add(RedisCli.OPTION_DAEMONIZE);
                    command.add(this.config.getDaemonize());
                });

        return commands;
    }

    public void stopCluster() {
        this.buildStopClusterCommands().forEach(this::execute);
        this.buildKillPortCommands().forEach(this::execute);
        RedisCli.log.info("Redis cluster stopped");
    }

    private List<List<String>> buildStopClusterCommands() {
        final List<List<String>> commands = new ArrayList<>(this.getNumOfNode());
        IntStream.range(this.config.getPort(), this.config.getPort() + this.getNumOfNode())
                .forEach(port -> {
                    final List<String> command = new ArrayList<>();
                    commands.add(command);
                    command.add(RedisCli.COMMAND_REDIS_CLI);

                    command.add(RedisCli.OPTION_P);
                    command.add(String.valueOf(port));

                    command.add(RedisCli.OPTION_SHUT_DOWN);
                    command.add(RedisCli.OPTION_NO_SAVE);
                });

        return commands;
    }

    private List<String> buildKillPortCommands() {
        final List<String> commands = new ArrayList<>();
        IntStream.range(this.config.getPort(), this.config.getPort() + this.getNumOfNode())
                .forEach(port -> commands.add(String.format(RedisCli.COMMAND_KILL, port)));

        return commands;
    }

    private void execute(final String command) {
        try {
            RedisCli.log.info("Executing command: {}", command);
            final Process process = Runtime.getRuntime().exec(command);
            this.logOutput(process);
            final int result = process.waitFor();
            RedisCli.log.info("Execution result={}", result);
        } catch (final Exception e) {
            final String message = "Failed to getOutput command=" + command;
            RedisCli.log.error(message, e);
            throw new EmbeddedRedisException(message, e);
        }
    }
}
