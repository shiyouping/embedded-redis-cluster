package com.github.shiyouping.redis.embedded.config;

import lombok.Value;

/**
 * @author ricky.shiyouping@gmail.com
 * @since 22/10/2022
 */
@Value
public class Config {

    String clusterEnabled;
    String clusterConfigFile;
    String clusterNodeTimeout;
    String protectedMode;
    String appendOnly;
    String appendFileName;
    String dbFileName;
    String logFile;
    String daemonize;
    String host;
    int masterNodes;
    int port;
    int clusterReplicas;
}
