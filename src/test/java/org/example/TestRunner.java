package org.example;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestRunner {
    public static Value runString(String code){
        Context context = Context.create();
        return context.eval("ezs", code);
    }

    public static Value runInline(String code){
        return runString(code.stripIndent());
    }

    @SuppressWarnings("unused")
    public static Value runTestFile(String name){
        String fileName = "./src/test/java/org/example/code.txt";
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return runString(content);
    }
}
