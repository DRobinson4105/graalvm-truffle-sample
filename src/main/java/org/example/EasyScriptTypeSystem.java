package org.example;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

@TypeSystem({
        boolean.class,
        int.class,
        double.class
})
public class EasyScriptTypeSystem {
    @ImplicitCast
    public static double castIntToDouble(int value) {
        return value;
    }
}
