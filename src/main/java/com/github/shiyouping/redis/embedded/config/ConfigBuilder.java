package com.github.shiyouping.redis.embedded.config; // NOPMD - suppressed TooManyStaticImports

import static com.github.shiyouping.redis.embedded.util.Environment.REDIS_CLUSTER_NODE_TIMEOUT;
import static com.github.shiyouping.redis.embedded.util.Environment.REDIS_MASTER_NODES;
import static com.github.shiyouping.redis.embedded.util.Environment.REDIS_PORT;
import static com.github.shiyouping.redis.embedded.util.Environment.getInt;
import static com.github.shiyouping.redis.embedded.util.Preconditions.checkArgument;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 22/10/2022
 */
public class ConfigBuilder {
    private String clusterNodeTimeout;
    private int masterNodes;
    private int port;

    public ConfigBuilder() {
        this.clusterNodeTimeout(getInt(REDIS_CLUSTER_NODE_TIMEOUT, 2000));
        this.masterNodes(getInt(REDIS_MASTER_NODES, 3));
        this.port(getInt(REDIS_PORT, 16379));
    }

    public Config build() {
        final String clusterEnabled = "yes";
        final String clusterConfigFile = "nodes";
        final String protectedMode = "no";
        final String appendOnly = "yes";
        final String appendFileName = "appendonly";
        final String dbFileName = "db";
        final String logFile = "log";
        final String daemonize = "yes";
        final int clusterReplicas = 1;
        final String host = "127.0.0.1"; // NOPMD - suppressed AvoidUsingHardCodedIP

        return new Config(
                clusterEnabled,
                clusterConfigFile,
                this.clusterNodeTimeout,
                protectedMode,
                appendOnly,
                appendFileName,
                dbFileName,
                logFile,
                daemonize,
                host,
                this.masterNodes,
                this.port,
                clusterReplicas);
    }

    private ConfigBuilder clusterNodeTimeout(final long clusterNodeTimeout) {
        checkArgument(clusterNodeTimeout > 0, "clusterNodeTimeout must be > 0");
        this.clusterNodeTimeout = String.valueOf(clusterNodeTimeout);
        return this;
    }

    private ConfigBuilder masterNodes(final int masterNodes) {
        checkArgument(masterNodes >= 3, "masterNodes must be >= 3");
        this.masterNodes = masterNodes;
        return this;
    }

    private ConfigBuilder port(final int port) {
        checkArgument(port >= 1024, "port must be >= 1024");
        this.port = port;
        return this;
    }
}
