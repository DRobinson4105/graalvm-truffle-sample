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

public final class ForStmtNode extends EasyScriptStmtNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child private EasyScriptStmtNode init;
    @SuppressWarnings("FieldMayBeFinal")
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
        return Undefined.instance;
    }

    private static final class ForRepeatingNode extends Node implements RepeatingNode {
        @SuppressWarnings("FieldMayBeFinal")
        @Child private EasyScriptExprNode condition;
        @SuppressWarnings("FieldMayBeFinal")
        @Child private EasyScriptExprNode update;
        @SuppressWarnings("FieldMayBeFinal")
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
