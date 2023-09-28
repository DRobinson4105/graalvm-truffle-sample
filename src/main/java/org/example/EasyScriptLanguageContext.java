package org.example;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;
import org.example.runtime.EvaluatorEnvironment;
import org.example.runtime.GlobalScopeObject;

public final class EasyScriptLanguageContext {
    private static final TruffleLanguage.ContextReference<EasyScriptLanguageContext> REF =
            TruffleLanguage.ContextReference.create(EasyScriptTruffleLanguage.class);

    public static EasyScriptLanguageContext get(Node node) {
        return REF.get(node);
    }
    public final StringPrototype stringPrototype;

    public final EvaluatorEnvironment evaluatorEnvironment;

    public EasyScriptLanguageContext(StringPrototype stringPrototype) {
        this.stringPrototype = stringPrototype;
        this.evaluatorEnvironment = new EvaluatorEnvironment();
    }
}
