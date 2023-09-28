package org.example;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRunner {
    public static void run() {
        String fileName = "/Users/drobinson/IdeaProjects/GraalVMLearning/src/test/java/org/example/input.txt";
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            content = "0";
        }
        Context context = Context.create();
        Value result = context.eval("ezs",
            content
        );
        assertEquals(5, result.asInt());
    }
}
