package org.example;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;

public class SimpleBenchmark extends TruffleBenchmark {
    private static final String FIBONACCI_EZS_PROGRAM = """
                let i = 0;
                let j = 0;
                while (i < 10) {
                    i = i + 1;
                }
                return 1;
            """;

    private static final String FIBONACCI_EZS_PROGRAM2 = """
                let i = 0;
                let j = 0;
                while (i < 10) {
                    i = i + 1;
                    j = j + 1;
                }
                return 1;
            """;
    private static final String FIBONACCI_JS_PROGRAM = """
                var i = 1;
                1;
            """;

    @Benchmark
    public int recursive_ezs_eval() {
        return this.truffleContext.eval("ezs", FIBONACCI_EZS_PROGRAM).asInt();
    }

    @Benchmark
    public int recursive_ezs_eval2() {
        return this.truffleContext.eval("ezs", FIBONACCI_EZS_PROGRAM2).asInt();
    }

//    @Benchmark
//    public int recursive_js_eval() {
//        return this.truffleContext.eval("js", FIBONACCI_JS_PROGRAM).asInt();
//    }
}