package com.github.shiyouping.redis.embedded.util;

import com.github.shiyouping.redis.embedded.Platform;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlatformUtilTest {

    @Test
    public void shouldGetPlatform() {
        final Platform platform = PlatformUtil.getPlatform();
        assertThat(platform).isNotNull();
        assertThat(platform.getArch()).isNotNull();
        assertThat(platform.getOs()).isNotNull();
    }
}