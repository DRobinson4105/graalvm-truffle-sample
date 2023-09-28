package org.example.nodes;

import com.oracle.truffle.api.nodes.Node;
import org.example.EasyScriptLanguageContext;
import org.example.EasyScriptTruffleLanguage;

public abstract class EasyScriptNode extends Node {
    protected final EasyScriptTruffleLanguage currentTruffleLanguage() {
        return EasyScriptTruffleLanguage.get(this);
    }
    protected final EasyScriptLanguageContext currentLanguageContext() {
        return EasyScriptLanguageContext.get(this);
    }
}