package org.example;

public enum DeclarationKind {
    VAR, LET, CONST;

    public static DeclarationKind fromToken(String token) {
        return switch (token) {
            case "var" -> DeclarationKind.VAR;
            case "let" -> DeclarationKind.LET;
            case "const" -> DeclarationKind.CONST;
            default -> throw new EasyScriptException("Unrecognized variable kind: '" + token + "'");
        };
    }
}
