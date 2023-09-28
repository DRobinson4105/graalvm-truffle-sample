package org.example;

import jdk.internal.module.*;

import java.util.Set;

public class Main {
    public static void main(String[] args){
        exportAndOpenAllPackagesToUnnamed("org.graalvm.truffle");
    }

    public static void exportAndOpenAllPackagesToUnnamed(String name) {
        Module module = ModuleLayer.boot().findModule(name).orElseThrow();
        Set<String> packages = module.getPackages();
        for (String pkg : packages) {
            Modules.addExportsToAllUnnamed(module, pkg);
            Modules.addOpensToAllUnnamed(module, pkg);
        }
    }
}