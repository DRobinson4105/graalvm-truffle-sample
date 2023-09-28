package org.example.nodes.stmts;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.example.nodes.exprs.EasyScriptExprNode;
import org.example.runtime.Undefined;
import com.oracle.truffle.api.profiles.ConditionProfile;

public class IfStmtNode extends EasyScriptStmtNode {
    @Child
    private EasyScriptExprNode conditionExpr;
    @Child
    private EasyScriptStmtNode thenStmt;
    @Child
    private EasyScriptStmtNode elseStmt;
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

        return Undefined.INSTANCE;
    }
}
