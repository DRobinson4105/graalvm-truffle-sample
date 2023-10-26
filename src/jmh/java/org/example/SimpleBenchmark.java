package org.example;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;

public class SimpleBenchmark extends TruffleBenchmark {
    private static final String FIBONACCI_EZS_PROGRAM = """
                var n = 0;
                var i = 1;
                const fun = (n) => {
                
                while (n != 10000) {
                    n = n + 1;
                }
                return n;
                }
                return fun(n)
            """;
    private static final String FIBONACCI_JS_PROGRAM = """
                var n = 0;
                while (n != 10000) {
                    n = n + 1;
                }
                n;
            """;

    @Benchmark
    public int recursive_ezs_eval() {
        return this.truffleContext.eval("ezs", FIBONACCI_EZS_PROGRAM).asInt();
    }

//    @Benchmark
    public int recursive_js_eval() {
        return this.truffleContext.eval("js", FIBONACCI_JS_PROGRAM).asInt();
    }
}