package com.github.shiyouping.redis.embedded.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandLineTest {

    @Test
    public void shouldGetOutput() {
        assertThat(CommandLine.getOutput("date")).isNotBlank();
    }
}