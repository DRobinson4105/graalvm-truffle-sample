package org.example.nodes.statements.controlflow;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.nodes.expressions.EasyScriptExprNode;
import org.example.nodes.statements.EasyScriptStmtNode;
import com.oracle.truffle.api.profiles.ConditionProfile;

public final class IfStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child private EasyScriptExprNode conditionExpr;
    @SuppressWarnings("FieldMayBeFinal")
    @Child private EasyScriptStmtNode thenStmt;
    @SuppressWarnings("FieldMayBeFinal")
    @Child private EasyScriptStmtNode elseStmt;
    private final ConditionProfile condition = ConditionProfile.createCountingProfile();

    public IfStmtNode(EasyScriptExprNode conditionExpr, EasyScriptStmtNode thenStmt, EasyScriptStmtNode elseStmt) {
        this.conditionExpr = conditionExpr;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        if (this.condition.profile(this.conditionExpr.executeBool(frame)))
            return this.thenStmt.executeStatement(frame);
        else if (this.elseStmt != null)
            return this.elseStmt.executeStatement(frame);

        return Undefined.instance;
    }
}
