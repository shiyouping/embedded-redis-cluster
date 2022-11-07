# Embedded Redis Cluster

An embedded Redis cluster for Java integration test.

## Supported Platforms

More platforms except Windows will be supported in the future. Currently only the following platforms are supported.

- macOS

## Usage

Add this library to Maven pom.xml

````xml

<dependency>
    <groupId>io.github.shiyouping</groupId>
    <artifactId>embedded-redis-cluster</artifactId>
    <version>0.0.3</version>
    <scope>test</scope>
</dependency>
````

By default, three master nodes will be created, and the ports of master nodes are 16379, 16380 and 16381.

````
Config config = new ConfigBuilder().build();
RedisCluster redisCluster=new RedisCluster(config);
redisCluster.start();
redisCluster.stop();
````

You may change the default settings by Java code:

````
// The ports of master nodes are 12000, 12001, 12002 and 12003
Config config = new ConfigBuilder().masterNodes(4).port(12000).clusterNodeTimeout(5000).build();
RedisCluster redisCluster = new RedisCluster(config);
````

or in environment variables:

````
REDIS_PORT=12000
REDIS_MASTER_NODES=4
REDIS_CLUSTER_NODE_TIMEOUT=5000
````

