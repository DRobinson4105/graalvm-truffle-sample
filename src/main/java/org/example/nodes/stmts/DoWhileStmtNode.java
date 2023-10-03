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

public class DoWhileStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child private LoopNode loopNode;

    public DoWhileStmtNode(EasyScriptExprNode condition, EasyScriptStmtNode body) {
        this.loopNode = Truffle.getRuntime().createLoopNode(new DoWhileRepeatingNode(condition, body));
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        this.loopNode.execute(frame);
        return Undefined.INSTANCE;
    }

    private static final class DoWhileRepeatingNode extends Node implements RepeatingNode {
        @SuppressWarnings("FieldMayBeFinal")
        @Child private EasyScriptExprNode condition;
        @SuppressWarnings("FieldMayBeFinal")
        @Child private EasyScriptStmtNode body;

        public DoWhileRepeatingNode(EasyScriptExprNode condition, EasyScriptStmtNode body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public boolean executeRepeating(VirtualFrame frame) {
            try {
                this.body.executeStatement(frame);
            } catch (ControlFlowException stop) {
                if (stop instanceof DirectBreakException)
                    return false;
            }

            return this.condition.executeBool(frame);
        }
    }
}