package com.github.shiyouping.redis.embedded.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CommandLineTest {

    @Test
    public void shouldGetOutput() {
        assertThat(CommandLine.getOutput("date")).isNotBlank();
    }
}
