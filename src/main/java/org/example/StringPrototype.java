package org.example;

import com.oracle.truffle.api.CallTarget;

/**
 * contains the call targets for the string built-in methods
 */
public record StringPrototype(CallTarget charAtMethod) {}
