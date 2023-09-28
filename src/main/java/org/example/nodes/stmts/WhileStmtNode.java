package org.example.nodes.stmts;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ControlFlowException;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.js.nodes.control.DirectBreakException;
import org.example.nodes.exprs.EasyScriptExprNode;
import org.example.runtime.Undefined;

public class WhileStmtNode extends EasyScriptStmtNode {
    @Child private LoopNode loopNode;

    public WhileStmtNode(EasyScriptExprNode condition, EasyScriptStmtNode body) {
        this.loopNode = Truffle.getRuntime().createLoopNode(new WhileRepeatingNode(condition, body));
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        this.loopNode.execute(frame);
        return Undefined.INSTANCE;
    }

    private static final class WhileRepeatingNode extends Node implements RepeatingNode {
        @SuppressWarnings("FieldMayBeFinal")
        @Child private EasyScriptExprNode condition;
        @SuppressWarnings("FieldMayBeFinal")
        @Child private EasyScriptStmtNode body;

        public WhileRepeatingNode(EasyScriptExprNode condition, EasyScriptStmtNode body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public boolean executeRepeating(VirtualFrame frame) {
            if (!this.condition.executeBool(frame))
                return false;

            try {
                this.body.executeStatement(frame);
            } catch (ControlFlowException stop) {
                if (stop instanceof DirectBreakException) return false;
            }

            return true;
        }
    }
}
