package org.example;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;

public class FibonacciBenchmark extends TruffleBenchmark {
    private static final String FIBONACCI_EZS_PROGRAM = "" +
            "const fib = (n) => { " +
            "    if (n < 2) { " +
            "        return 1; " +
            "    } " +
            "    return fib(n - 1) + fib(n - 2); " +
            "}" +
            "return fib(20)";
    private static final String FIBONACCI_JS_PROGRAM = "" +
            "function fib(n) { " +
            "    if (n < 2) { " +
            "        return 1; " +
            "    } " +
            "    return fib(n - 1) + fib(n - 2); " +
            "}" +
            "fib(20)";

//    @Fork(jvmArgsPrepend = {
//            "-Dgraal.Dump=:1",
//            "-Dgraal.PrintGraph=Network"
//    })
    @Benchmark
    public int recursive_ezs_eval() {
        return this.truffleContext.eval("ezs", FIBONACCI_EZS_PROGRAM).asInt();
    }

    public static int fibonacciRecursive(int n) {
        return n < 2
                ? 1
                : fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }

    @Benchmark
    public int recursive_java() {
        return fibonacciRecursive(20);
    }

    @Benchmark
    public int recursive_js_eval() {
        return this.truffleContext.eval("js", FIBONACCI_JS_PROGRAM).asInt();
    }
}