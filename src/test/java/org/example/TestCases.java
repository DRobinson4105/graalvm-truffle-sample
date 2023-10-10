package org.example;

import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.example.Main.exportAndOpenAllPackagesToUnnamed;
import static org.example.TestRunner.runInline;
import static org.junit.jupiter.api.Assertions.*;

public class TestCases {
    @BeforeAll
    public static void setup(){
        exportAndOpenAllPackagesToUnnamed("org.graalvm.truffle");
    }

    @Test public void caseInt() {
        assertEquals(Value.asValue(0), runInline("""
                return 0;
                """)
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

    @Test public void caseArithmetic() {
        assertEquals(5, runInline("""
                return 2 + 3
                """).asInt()
        );
        assertEquals(5, runInline("""
                return 8 - 3
                """).asInt()
        );
        assertEquals(6, runInline("""
                return 2 * 3
                """).asInt()
        );
        assertEquals(5, runInline("""
                return 10 / 2
                """).asInt()
        );
        assertEquals(2, runInline("""
                return 8 % 3
                """).asInt()
        );
        assertEquals(5, runInline("""
                return --5
                """).asInt()
        );
    }

    @Test public void caseRequire() {
        assertEquals(Value.asValue(5), (runInline("""
                const Math = require("Math")
                return Math.abs(-5)
                """))
        );
        assertEquals(8, runInline("""
                const Math = require("Math")
                return Math.pow(2, 3)
                """).asInt()
        );
    }

    @Test public void caseComparison() {
        assertTrue(runInline("""
               return 1 < 2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 1
               let num2 = 2
               return num1 < num2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let num1 = 2
               let num2 = 1
               return num1 < num2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 2
               let num2 = 1
               return num1 > num2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let num1 = 1
               let num2 = 2
               return num1 > num2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let str1 = 'a'
               let str2 = 'b'
               return str1 < str2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let str1 = 'b'
               let str2 = 'a'
               return str1 < str2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let str1 = 'b'
               let str2 = 'a'
               return str1 > str2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let str1 = 'a'
               let str2 = 'b'
               return str1 > str2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 1.0
               let num2 = 2.0
               return num1 < num2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let num1 = 2.0
               let num2 = 1.0
               return num1 < num2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 2.0
               let num2 = 1.0
               return num1 > num2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let num1 = 1.0
               let num2 = 2.0
               return num1 > num2
               """).asBoolean()
        );
        assertTrue(runInline("""
                return 1 <= 1
                """).asBoolean()
        );
        assertTrue(runInline("""
                return 1 >= 1
                """).asBoolean()
        );
        assertTrue(runInline("""
                return 1 <= 2
                """).asBoolean()
        );
        assertFalse(runInline("""
                return 1 >= 2
                """).asBoolean()
        );
        assertFalse(runInline("""
                return 2 <= 1
                """).asBoolean()
        );
        assertTrue(runInline("""
                return 2 >= 1
                """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 1
               let num2 = 1
               return num1 <= num2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 1
               let num2 = 1
               return num1 >= num2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 1
               let num2 = 2
               return num1 <= num2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let num1 = 2
               let num2 = 1
               return num1 <= num2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 2
               let num2 = 1
               return num1 >= num2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let num1 = 1
               let num2 = 2
               return num1 >= num2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let str1 = 'a'
               let str2 = 'a'
               return str1 <= str2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let str1 = 'a'
               let str2 = 'a'
               return str1 >= str2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let str1 = 'a'
               let str2 = 'b'
               return str1 <= str2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let str1 = 'b'
               let str2 = 'a'
               return str1 <= str2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let str1 = 'b'
               let str2 = 'a'
               return str1 >= str2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let str1 = 'a'
               let str2 = 'b'
               return str1 >= str2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 1.0
               let num2 = 1.0
               return num1 <= num2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 1.0
               let num2 = 1.0
               return num1 >= num2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 1.0
               let num2 = 2.0
               return num1 <= num2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let num1 = 2.0
               let num2 = 1.0
               return num1 <= num2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let num1 = 2.0
               let num2 = 1.0
               return num1 >= num2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let num1 = 1.0
               let num2 = 2.0
               return num1 >= num2
               """).asBoolean()
        );
        assertTrue(runInline("""
                return 1 == 1
                """).asBoolean()
        );
        assertFalse(runInline("""
                return 1 == 2
                """).asBoolean()
        );
        assertTrue(runInline("""
               let one = 1
               let alsoOne = 1
               return one == alsoOne
               """).asBoolean()
        );
        assertFalse(runInline("""
               let one = 1
               let two = 2
               return one == two
               """).asBoolean()
        );
        assertTrue(runInline("""
               let str1 = 'a'
               let str2 = 'a'
               return str1 == str2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let str1 = 'a'
               let str2 = 'b'
               return str1 == str2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let one = 1.0
               let alsoOne = 1.0
               return one == alsoOne
               """).asBoolean()
        );
        assertFalse(runInline("""
               let one = 1.0
               let two = 2.0
               return one == two
               """).asBoolean()
        );
        assertFalse(runInline("""
                return 1 != 1
                """).asBoolean()
        );
        assertTrue(runInline("""
                return 1 != 2
                """).asBoolean()
        );
        assertFalse(runInline("""
               let one = 1
               let alsoOne = 1
               return one != alsoOne
               """).asBoolean()
        );
        assertTrue(runInline("""
               let one = 1
               let two = 2
               return one != two
               """).asBoolean()
        );
        assertFalse(runInline("""
               let str1 = 'a'
               let str2 = 'a'
               return str1 != str2
               """).asBoolean()
        );
        assertTrue(runInline("""
               let str1 = 'a'
               let str2 = 'b'
               return str1 != str2
               """).asBoolean()
        );
        assertFalse(runInline("""
               let one = 1.0
               let alsoOne = 1.0
               return one != alsoOne
               """).asBoolean()
        );
        assertTrue(runInline("""
               let one = 1.0
               let two = 2.0
               return one != two
               """).asBoolean()
        );
    }
    @Test public void caseControlFlow() {
        assertEquals(16, runInline("""
                let i = 1
                if (true) {
                    i = 16
                }
                return i
                """).asInt()
        );
        assertEquals(1, runInline("""
                let i = 1
                if (false) {
                    i = 16
                }
                return i
                """).asInt()
        );
        assertEquals(45, runInline("""
                let count = 0
                for (let i = 0; i < 10; i = i + 1) {
                    count = count + i
                }
                return count
                """).asInt()
        );
        assertEquals(16, runInline("""
                let i = 1
                for (; i < 10; i = i * 2) {
                }
                return i
                """).asInt()
        );
        assertEquals(45, runInline("""
                let count = 0
                let i = 0
                while (i < 10) {
                    count = count + i
                    i = i + 1
                }
                return count
                """).asInt()
        );
        assertEquals(16, runInline("""
                let i = 1
                while (i < 10) {
                    i = i * 2
                }
                return i
                """).asInt()
        );
        assertEquals(16, runInline("""
                let i = 1
                do {
                    i = i * 2
                } while (i < 10);
                return i
                """).asInt()
        );
        assertEquals(2, runInline("""
                let i = 1
                do {
                    i = i * 2
                } while (i < 1);
                return i
                """).asInt()
        );
        assertEquals(1, runInline("""
                let i = 1
                do {
                    break;
                    i = i * 2
                } while (i < 1);
                return i
                """).asInt()
        );
        assertEquals(10, runInline("""
                let i = 1
                do {
                    if (i < 5) {
                        i = i + 1
                        continue
                    }
                    i = i * 2
                } while (i < 10);
                return i
                """).asInt()
        );
        assertEquals(1, runInline("""
                let i = 1
                while (i < 1) {
                    break;
                    i = i * 2
                }
                return i
                """).asInt()
        );
        assertEquals(10, runInline("""
                let i = 1
                while (i < 10) {
                    if (i < 5) {
                        i = i + 1
                        continue
                    }
                    i = i * 2
                }
                return i
                """).asInt()
        );
        assertEquals(0, runInline("""
                let count = 0
                for (let i = 0; i < 10; i = i + 1) {
                    break
                }
                return count
                """).asInt()
        );
        assertEquals(16, runInline("""
                let i = 1
                for (; i < 10; i = i * 2) {
                    if (i < 5) {
                        continue
                    }
                }
                return i
                """).asInt()
        );
    }
}
