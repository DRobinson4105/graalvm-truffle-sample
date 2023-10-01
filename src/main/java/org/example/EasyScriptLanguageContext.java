package org.example;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;

public final class EasyScriptLanguageContext {
    private static final TruffleLanguage.ContextReference<EasyScriptLanguageContext> REF =
            TruffleLanguage.ContextReference.create(EasyScriptTruffleLanguage.class);

    public static EasyScriptLanguageContext get(Node node) {
        return REF.get(node);
    }
    public final StringPrototype stringPrototype;

    public EasyScriptLanguageContext(StringPrototype stringPrototype) {
        this.stringPrototype = stringPrototype;
    }
}
