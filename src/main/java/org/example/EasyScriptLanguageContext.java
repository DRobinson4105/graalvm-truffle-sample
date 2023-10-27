package org.example;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;

/**
 * Allows the {@link EasyScriptTruffleLanguage} to be evaluated as a Truffle guest language
 * @param stringPrototype contains the call targets for the string built-in methods
 */
public record EasyScriptLanguageContext(StringPrototype stringPrototype) {
    private static final TruffleLanguage.ContextReference<EasyScriptLanguageContext> REF =
            TruffleLanguage.ContextReference.create(EasyScriptTruffleLanguage.class);

    public static EasyScriptLanguageContext get(Node node) {
        return REF.get(node);
    }
}
