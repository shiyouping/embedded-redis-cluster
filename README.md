# Embedded Redis Cluster

An embedded Redis cluster for Java integration test.

## Supported Platforms

- macOS arm64

## Usage

````java
Config config=new ConfigBuilder().build();
RedisCluster redisCluster=new RedisCluster(config);
redisCluster.start();
redisCluster.stop();
````

By default, three master nodes will be created, and the ports of master nodes are 16379, 16380 and 16381. You may change the default settings by:

````java
// The ports of master nodes are 12000, 12001, 12002 and 12003
Config config = new ConfigBuilder().masterNodes(4).port(12000).clusterNodeTimeout(5000).build();
RedisCluster redisCluster = new RedisCluster(config);
````


