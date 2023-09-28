package org.example;

import static org.example.Main.exportAndOpenAllPackagesToUnnamed;

public class Test {
    @org.junit.jupiter.api.Test
    public void run() {
        exportAndOpenAllPackagesToUnnamed("org.graalvm.truffle");
        TestRunner.run();
    }
}
