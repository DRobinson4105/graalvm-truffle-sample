package org.example;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRunner {



    public static Value runString(String code){
        Context context = Context.create();
        Value result = context.eval("ezs", code);
        return result;
    }

    public static Value runInline(String code){
        return runString(code.stripIndent());
    }

    public static Value runTestFile(String name){
        String fileName = "/Users/drobinson/IdeaProjects/GraalVMLearning/src/test/java/org/example/" + name;
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return runString(content);
    }
}
