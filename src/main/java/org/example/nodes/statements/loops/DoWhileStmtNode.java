package org.example.nodes.statements.loops;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ControlFlowException;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.js.nodes.control.DirectBreakException;
import com.oracle.truffle.js.runtime.objects.Undefined;
import org.example.nodes.expressions.EasyScriptExprNode;
import org.example.nodes.statements.EasyScriptStmtNode;

public class DoWhileStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child private LoopNode loopNode;

    public DoWhileStmtNode(EasyScriptExprNode condition, EasyScriptStmtNode body) {
        this.loopNode = Truffle.getRuntime().createLoopNode(new DoWhileRepeatingNode(condition, body));
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        this.loopNode.execute(frame);
        return Undefined.instance;
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