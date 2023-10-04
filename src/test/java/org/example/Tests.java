package org.example;

import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.example.Main.exportAndOpenAllPackagesToUnnamed;
import static org.example.TestRunner.runInline;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    @BeforeAll
    public static void setup(){
        exportAndOpenAllPackagesToUnnamed("org.graalvm.truffle");
    }


    @Test public void caseInt() {
        assertEquals(0, runInline("""
                return 0;
                """).asInt()
        );
        assertEquals(0, runInline("""
                return -0;
                """).asInt()
        );
        assertEquals(1, runInline("""
                return 1;
                """).asInt()
        );
        assertEquals(-1, runInline("""
                return -1;
                """).asInt()
        );
        assertEquals(2, runInline("""
                return 2;
                """).asInt()
        );
        assertEquals(200, runInline("""
                return 200;
                """).asInt()
        );
        assertEquals(Integer.MAX_VALUE, runInline("""
                return 2147483647;
                """).asInt()
        );
        assertEquals(Integer.MIN_VALUE, runInline("""
                return -2147483648;
                """).asInt()
        );
    }
    @Test public void caseString() {
        assertEquals("", runInline("""
                return "";
                """).asString()
        );

        assertEquals("a", runInline("""
                return "a";
                """).asString()
        );
        assertEquals("b", runInline("""
                return "b";
                """).asString()
        );

        assertEquals("A", runInline("""
                return "A";
                """).asString()
        );
        assertEquals("B", runInline("""
                return "B";
                """).asString()
        );

        assertEquals(" ", runInline("""
                return " ";
                """).asString()
        );

        assertEquals("1234567890", runInline("""
                return "1234567890";
                """).asString()
        );
        assertEquals("This is Easy Script!", runInline("""
                return "This is Easy Script!";
                """).asString()
        );

        assertEquals("\n", runInline("""
                return "\\n";
                """).asString()
        );
        assertEquals("\t", runInline("""
                return "\\t";
                """).asString()
        );
        assertEquals("\0", runInline("""
                return "\\0";
                """).asString()
        );
        assertEquals("\\", runInline("""
                return "\\\\";
                """).asString()
        );

        assertEquals("\uD83C\uDF47", runInline("""
                return "\uD83C\uDF47";
                """).asString()
        );
    }

    @Test public void caseClosures() {
        assertEquals(1234, runInline("""
                const l = () => {
                    return 1234;
                }
                return l();
                """).asInt()
        );
        assertEquals(1234, runInline("""
                const a = 1234
                const l = () => {
                    return a;
                }
                return l();
                """).asInt()
        );
        assertEquals(1234, runInline("""
                const l = () => {
                    return a;
                }
                const a = 1234
                return l();
                """).asInt()
        );
        assertEquals(1234, runInline("""
                let a = 0
                const l = () => {
                    return a;
                }
                a = 1234
                return l();
                """).asInt()
        );
        assertEquals(1234, runInline("""
                let a = 0
                const l = () => {
                    a = 1234
                    return a;
                }
                l();
                return a;
                """).asInt()
        );
        assertEquals(1234, runInline("""
                let a = 0
                const l = () => {
                    a = 1234
                    return a;
                }
                return l();
                """).asInt()
        );
        assertEquals(1234, runInline("""
                let a = 1234
                const f = () => {
                    const g = () => {
                        return a
                    }
                    return g;
                }
                return f()();
                """).asInt()
        );
        assertEquals(1234, runInline("""
                let a = 1234
                const f = () => {
                    const g = () => {
                        return a
                    }
                    return g();
                }
                return f();
                """).asInt()
        );

        assertEquals(1234, runInline("""
                let a = 1234
                const f = (x) => {
                    return x
                }
                return f(a);
                """).asInt()
        );
        assertEquals(1234, runInline("""
                let a = 1234
                const f = (x) => {
                    x = 0
                }
                f(a)
                return a
                """).asInt()
        );
        assertEquals(1234, runInline("""
                const f = (x, y) => {
                    return x + y
                }
                return f(1000, 234)
                """).asInt()
        );

        assertEquals(10, runInline("""
                const tri = (x) => {
                    if(x < 1) {
                        return 0;
                    }
                    return x + tri(x - 1);
                }
                return tri(4)
                """).asInt()
        );
        assertEquals(1234, runInline("""
                const f = (a) => {
                    const g = () => {
                        a = 1234
                        return a;
                    }
                    return g()
                }
                return f(23);
                """).asInt()
        );
    }
    @Test public void caseStringProperties() {
        assertEquals("H", runInline("""
                let str = "Hello";
                return str.charAt("H");
                """).asString()
        );
        assertEquals(5, runInline("""
                let str = "Hello";
                return str.length;
                """).asInt()
        );
    }

    @Test public void caseArray() {
        assertEquals(1, runInline("""
                let arr = [1,2,3,4]
                return arr[0]
                """).asInt()
        );
        assertEquals(5, runInline("""
                let arr = [1,2,3,4]
                arr[0] = 5
                return arr[0]
                """).asInt()
        );
        assertEquals(5, runInline("""
                let arr = [1,2,3,4]
                if (true) {
                    arr[0] = 5
                }
                return arr[0]
                """).asInt()
        );
        assertEquals(5, runInline("""
                let arr = [1,2,3,4]
                const update = (array, index, value) => {
                    array[index] = value
                    return array[index]
                }
                const read = (array, index) => {
                    return array[index]
                }
                update(arr, 0, 5)
                return read(arr, 0)
                """).asInt()
        );
        assertEquals(5, runInline("""
                let arr = [1,2,3,4,5]
                return arr.length
                """).asInt()
        );
    }
}
