package org.example.nodes.stmts;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ControlFlowException;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.js.nodes.control.BreakException;
import com.oracle.truffle.js.nodes.control.DirectBreakException;
import org.example.nodes.exprs.EasyScriptExprNode;
import org.example.runtime.Undefined;

public final class ForStmtNode extends EasyScriptStmtNode {
    @Child private EasyScriptStmtNode init;
    @Child private LoopNode loopNode;

    public ForStmtNode(
            EasyScriptStmtNode init,
            EasyScriptExprNode condition,
            EasyScriptExprNode update,
            EasyScriptStmtNode body
    ) {
        this.init = init;
        this.loopNode = Truffle.getRuntime().createLoopNode(new ForRepeatingNode(condition, update, body));
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        if (this.init != null)
            this.init.executeStatement(frame);

        loopNode.execute(frame);
        return Undefined.INSTANCE;
    }

    private static final class ForRepeatingNode extends Node implements RepeatingNode {
        @Child private EasyScriptExprNode condition;
        @Child private EasyScriptExprNode update;
        @Child private EasyScriptStmtNode body;

        public ForRepeatingNode(EasyScriptExprNode condition, EasyScriptExprNode update, EasyScriptStmtNode body) {
            this.condition = condition;
            this.update = update;
            this.body = body;
        }

        @Override
        public boolean executeRepeating(VirtualFrame frame) {
            if (this.condition != null && !(this.condition.executeBool(frame)))
                return false;

            try {
                this.body.executeStatement(frame);
            } catch (ControlFlowException stop) {
                if (stop instanceof DirectBreakException) return false;
            }

            if (this.update != null)
                this.update.executeGeneric(frame);

            return true;
        }
    }
}
