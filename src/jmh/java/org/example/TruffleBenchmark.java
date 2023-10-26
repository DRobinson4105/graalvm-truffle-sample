package org.example;

import org.graalvm.polyglot.Context;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(value = 1, jvmArgsAppend = "-Dgraalvm.locatorDisabled=true")
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class TruffleBenchmark {
    protected Context truffleContext;

    @Setup
    public void setup() {
        this.truffleContext = Context.create();
    }

    @TearDown
    public void tearDown() {
        this.truffleContext.close();
    }
}