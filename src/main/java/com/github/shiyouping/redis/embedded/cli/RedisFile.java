package com.github.shiyouping.redis.embedded.cli;

import java.nio.file.Path;
import lombok.Builder;
import lombok.Value;

/**
 * RedisFile.
 *
 * @author ricky.shiyouping@gmail.com
 * @since 23/10/2022
 */
@Value
@Builder
public class RedisFile {

    Path baseDir;
    Path redisDir;
    Path binDir;
    String tgzName;
    String tgzExtension;

    public String getTgzFullName() {
        return this.tgzName + this.tgzExtension;
    }
}
