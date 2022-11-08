package com.github.shiyouping.redis.embedded;

import lombok.Data;

/**
 * Platform.
 *
 * @author ricky.shiyouping@gmail.com
 * @since 08/11/2022
 */
@Data
public class Platform {

    private OS os;
    private ARCH arch;

    public enum OS {
        MACOS, DEBIAN, REDHAT, UBUNTU
    }

    public enum ARCH {
        X86_64, ARM64
    }
}
