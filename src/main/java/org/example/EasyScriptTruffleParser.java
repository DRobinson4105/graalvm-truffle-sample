package org.example;

import com.oracle.truffle.api.object.Shape;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.text.StringEscapeUtils;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.example.nodes.expressions.EasyScriptExprNode;
import org.example.nodes.expressions.variables.*;
import org.example.nodes.expressions.arithmetic.*;
import org.example.nodes.expressions.arrays.*;
import org.example.nodes.expressions.functions.*;
import org.example.nodes.expressions.literals.*;
import org.example.nodes.expressions.properties.*;
import org.example.nodes.expressions.comparisons.*;
import org.example.nodes.statements.EasyScriptStmtNode;
import org.example.nodes.statements.ExprStmtNode;
import org.example.nodes.statements.blocks.*;
import org.example.nodes.statements.controlflow.*;
import org.example.nodes.statements.loops.*;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.EasyScriptParser.*;

public final class EasyScriptTruffleParser {
    public static ParsingResult parse(Reader program, Shape arrayShape) throws IOException {
        var lexer = new EasyScriptLexer(CharStreams.fromReader(program));
        lexer.removeErrorListeners();

        var parser = new EasyScriptParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.setErrorHandler(new BailErrorStrategy());

        var easyScriptTruffleParser = new EasyScriptTruffleParser(arrayShape);
        List<EasyScriptStmtNode> stmts = easyScriptTruffleParser.parseStmtsList(parser.start().stmt());
        return new ParsingResult(
                new UserFuncBodyStmtNode(stmts),
                easyScriptTruffleParser.frameDescriptorBuilder.build()
        );
    }

    private static abstract class FrameMember {
        public int depth;
        public int index;
    }
    private static final class FunctionArgument extends FrameMember {
        FunctionArgument(int argumentIndex, int depth) {
            this.index = argumentIndex;
            this.depth = depth;
        }
    }

    private static final class LocalVariable extends FrameMember {
        public final DeclarationKind declarationKind;
        LocalVariable(int variableIndex, DeclarationKind declarationKind, int depth) {
            this.index = variableIndex;
            this.declarationKind = declarationKind;
            this.depth = depth;
        }
    }
    private FrameDescriptor.Builder frameDescriptorBuilder;
    private final Stack<Map<String, FrameMember>> localScopes;
    private final Shape arrayShape;
    private int currentDepth;

    private FrameMember findFrameMember(String memberName) {
        FrameMember ret;
        Stack<Map<String, FrameMember>> storedScopes = new Stack<>();

        while (!this.localScopes.empty()) {
            ret = this.localScopes.peek().get(memberName);

            if (ret != null) {
                while (!storedScopes.empty()) {
                    this.localScopes.push(storedScopes.pop());
                }

                return ret;
            }

            storedScopes.push(this.localScopes.pop());
        }
        while (!storedScopes.isEmpty()) {
            this.localScopes.push(storedScopes.pop());
        }
        return null;
    }
    private EasyScriptTruffleParser(Shape arrayShape) {
        this.frameDescriptorBuilder = FrameDescriptor.newBuilder();
        this.localScopes = new Stack<>();
        this.localScopes.push(new HashMap<>());
        this.arrayShape = arrayShape;
        this.currentDepth = 0;
    }

    private List<EasyScriptStmtNode> parseStmtsList(List<StmtContext> stmts) {
        ArrayList<EasyScriptStmtNode> result = new ArrayList<>();

        for (StmtContext stmt : stmts) {
            if (stmt instanceof VarDeclStmtContext varDeclStmt) {
                for (BindingContext binding : varDeclStmt.binding()) {
                    DeclarationKind declarationKind = DeclarationKind.fromToken(varDeclStmt.kind.getText());
                    String variableId = binding.ID().getText();
                    int frameSlot = this.frameDescriptorBuilder.addSlot(FrameSlotKind.Illegal, variableId, declarationKind);
                    if (this.localScopes.peek().putIfAbsent(variableId, new LocalVariable(frameSlot, declarationKind, currentDepth)) != null) {
                        throw new EasyScriptException("Identifier '" + variableId + "' has already been declared");
                    }
                }
            }
        }

        for (StmtContext stmt : stmts) {
            result.add(parseStmt(stmt));
        }

        return result;
    }

    private EasyScriptStmtNode parseStmt(StmtContext stmt) {
        if (stmt instanceof VarDeclStmtContext varDeclStmt) {
            return parseVarDeclStmt(varDeclStmt);
        } else if (stmt instanceof ExprStmtContext exprStmt) {
            return parseExprStmt(exprStmt);
        } else if (stmt instanceof ReturnStmtContext returnStmt) {
            return parseReturnStmt(returnStmt);
        } else if (stmt instanceof BlockStmtContext blockStmt) {
            return parseBlockStmt(blockStmt);
        } else if (stmt instanceof IfStmtContext ifStmt) {
            return parseIfStmt(ifStmt);
        } else if (stmt instanceof WhileStmtContext whileStmt) {
            return parseWhileStmt(whileStmt);
        } else if (stmt instanceof DoWhileStmtContext doWhileStmt) {
            return parseDoWhileStmt(doWhileStmt);
        } else if (stmt instanceof ForStmtContext forStmt) {
            return parseForStmt(forStmt);
        } else if (stmt instanceof BreakStmtContext) {
            return parseBreakStmt();
        } else if (stmt instanceof ContinueStmtContext) {
            return parseContinueStmt();
        } else {
            throw new EasyScriptException("Statement does not exist");
        }
    }

    private EasyScriptStmtNode parseVarDeclStmt(VarDeclStmtContext varDeclStmt) {
        DeclarationKind declarationKind = DeclarationKind.fromToken(varDeclStmt.kind.getText());
        for (BindingContext varBinding : varDeclStmt.binding()) {
            String variableId = varBinding.ID().getText();
            FrameMember frameMember = this.localScopes.peek().get(variableId);
            var bindingExpr = varBinding.expr1();
            EasyScriptExprNode initializerExpr;
            if (bindingExpr == null) {
                if (declarationKind == DeclarationKind.CONST) {
                    throw new EasyScriptException("Missing initializer in const declaration '" + variableId + "'");
                }
                initializerExpr = new UndefinedLiteralExprNode();
            } else {
                initializerExpr = this.parseExpr1(bindingExpr);
            }

            VarAssignmentExprNode assignmentExpr = VarAssignmentExprNodeGen.create(initializerExpr, frameMember.index, this.currentDepth - frameMember.depth);
            return new ExprStmtNode(assignmentExpr, true);
        }

        throw new EasyScriptException("No bindings given");
    }

    private ExprStmtNode parseExprStmt(ExprStmtContext exprStmt) {
        return new ExprStmtNode(parseExpr1(exprStmt.expr1()));
    }

    private ReturnStmtNode parseReturnStmt(ReturnStmtContext returnStmt) {
        return new ReturnStmtNode(returnStmt.expr1() != null ? parseExpr1(returnStmt.expr1()) : null);
    }

    private BlockStmtNode parseBlockStmt(BlockStmtContext blockStmt) {
        this.localScopes.push(new HashMap<>());
        List<EasyScriptStmtNode> ret = this.parseStmtsList(blockStmt.stmt());
        this.localScopes.pop();

        return new BlockStmtNode(ret);
    }

    private IfStmtNode parseIfStmt(IfStmtContext ifStmt) {
        var condition = parseExpr1(ifStmt.cond);

        this.localScopes.push(new HashMap<>());
        var thenStmt = parseStmt(ifStmt.then_stmt);
        this.localScopes.pop();

        this.localScopes.push(new HashMap<>());
        var elseStmt = ifStmt.else_stmt != null ? parseStmt(ifStmt.else_stmt) : null;
        this.localScopes.pop();

        return new IfStmtNode(condition, thenStmt, elseStmt);
    }

    private WhileStmtNode parseWhileStmt(WhileStmtContext whileStmt) {
        var condition = parseExpr1(whileStmt.cond);

        this.localScopes.push(new HashMap<>());
        var body = parseStmt(whileStmt.body);
        this.localScopes.pop();

        return new WhileStmtNode(condition, body);
    }

    private DoWhileStmtNode parseDoWhileStmt(DoWhileStmtContext doWhileStmt) {
        var condition = parseExpr1(doWhileStmt.cond);

        this.localScopes.push(new HashMap<>());
        var body = parseStmt(doWhileStmt.body);
        this.localScopes.pop();

        return new DoWhileStmtNode(condition, body);
    }

    private ForStmtNode parseForStmt(ForStmtContext forStmt) {
        var init = parseStmt(forStmt.init);
        var condition = parseExpr1(forStmt.cond);
        var update = parseExpr1(forStmt.updt);

        this.localScopes.push(new HashMap<>());
        var body = parseStmt(forStmt.body);
        this.localScopes.pop();

        return new ForStmtNode(init, condition, update, body);
    }

    private BreakStmtNode parseBreakStmt() {
        return new BreakStmtNode();
    }

    private ContinueStmtNode parseContinueStmt() {
        return new ContinueStmtNode();
    }

    private EasyScriptExprNode parseExpr1(Expr1Context expr1) {
        if (expr1 instanceof AssignmentExpr1Context assignmentExpr) {
            return parseAssignmentExpr(assignmentExpr);
        } else if (expr1 instanceof ArrayIndexWriteExpr1Context arrayIndexWriteExpr) {
            return parseArrayIndexWriteExpr(arrayIndexWriteExpr);
        } else {
            return parseExpr2(((PrecedenceTwoExpr1Context) expr1).expr2());
        }
    }

    private EasyScriptExprNode parseAssignmentExpr(AssignmentExpr1Context assignmentExpr) {
        String variableId = assignmentExpr.ID().getText();
        FrameMember frameMember = findFrameMember(variableId);
        EasyScriptExprNode initializerExpr = parseExpr1(assignmentExpr.expr1());

        if (frameMember == null)
            throw new EasyScriptException("'" + variableId + "' is not defined");

        if (frameMember instanceof FunctionArgument functionArgument) {
            return new WriteFunctionArgExprNode(initializerExpr, functionArgument.index, this.currentDepth - frameMember.depth);
        }

        var localVariable = (LocalVariable) frameMember;
        if (localVariable.declarationKind == DeclarationKind.CONST)
            throw new EasyScriptException("Assignment to constant variable '" + variableId + "'");

        return VarAssignmentExprNodeGen.create(initializerExpr, localVariable.index, this.currentDepth - frameMember.depth);
    }

    private ArrayIndexWriteExprNode parseArrayIndexWriteExpr(ArrayIndexWriteExpr1Context arrayIndexWriteExpr) {
        return ArrayIndexWriteExprNodeGen.create(
                parseExpr5(arrayIndexWriteExpr.arr),
                parseExpr1(arrayIndexWriteExpr.index),
                parseExpr1(arrayIndexWriteExpr.rValue)
        );
    }
    private EasyScriptExprNode parseExpr2(Expr2Context expr2) {
        if (expr2 instanceof EqNotEqExpr2Context eqNotEqExpr)
            return parseEqNotEqExpr(eqNotEqExpr);
        else
            return parseExpr3(((PrecedenceThreeExpr2Context) expr2).expr3());
    }

    private EasyScriptExprNode parseEqNotEqExpr(EqNotEqExpr2Context eqNotEqExpr) {
        EasyScriptExprNode left = parseExpr2(eqNotEqExpr.left);
        EasyScriptExprNode right = parseExpr3(eqNotEqExpr.right);
        if (eqNotEqExpr.c.getText().equals("==="))
            return EqualityExprNodeGen.create(left, right);
        else
            return InequalityExprNodeGen.create(left, right);
    }

    private EasyScriptExprNode parseExpr3(Expr3Context expr3) {
        if (expr3 instanceof ComparisonExpr3Context comparisonExpr)
            return parseComparisonExpr(comparisonExpr);
        else
            return parseExpr4(((PrecedenceFourExpr3Context) expr3).expr4());
    }

    private EasyScriptExprNode parseComparisonExpr(ComparisonExpr3Context comparisonExpr) {
        EasyScriptExprNode left = parseExpr3(comparisonExpr.left);
        EasyScriptExprNode right = parseExpr4(comparisonExpr.right);

        return switch (comparisonExpr.c.getText()) {
            case "<" -> LessThanExprNodeGen.create(left, right);
            case "<=" -> LessThanEqualToExprNodeGen.create(left, right);
            case ">" -> GreaterThanExprNodeGen.create(left, right);
            case ">=" -> GreaterThanEqualToExprNodeGen.create(left, right);
            default -> throw new EasyScriptException("Unexpected value: " + comparisonExpr.c.getText());
        };
    }

    private EasyScriptExprNode parseExpr4(Expr4Context expr4) {
        if (expr4 instanceof AddSubtractExpr4Context addExpr)
            return parseAddSubtractExpr(addExpr);
        else if (expr4 instanceof NegationExpr4Context negationExpr)
            return parseUnaryMinusExpr(negationExpr);
        else
            return parseExpr5(((PrecedenceFiveExpr4Context) expr4).expr5());
    }

    private EasyScriptExprNode parseAddSubtractExpr(AddSubtractExpr4Context addSubtractExpr) {
        EasyScriptExprNode left = parseExpr4(addSubtractExpr.left);
        EasyScriptExprNode right = parseExpr5(addSubtractExpr.right);

        return switch (addSubtractExpr.o.getText()) {
            case "+" -> AdditionExprNodeGen.create(left, right);
            case "-" -> SubtractionExprNodeGen.create(left, right);
            default -> throw new EasyScriptException("Unexpected value: " + addSubtractExpr.o.getText());
        };
    }

    private NegationExprNode parseUnaryMinusExpr(NegationExpr4Context negExpr) {
        return NegationExprNodeGen.create(parseExpr5(negExpr.expr5()));
    }

    private EasyScriptExprNode parseExpr5(Expr5Context expr5) {
        if (expr5 instanceof PropertyReadExpr5Context propertyReadExpr) {
            return parsePropertyReadExpr(propertyReadExpr);
        } else if (expr5 instanceof ArrayLiteralExpr5Context arrayLiteralExpr) {
            return parseArrayLiteralExpr(arrayLiteralExpr);
        } else if (expr5 instanceof ArrayIndexReadExpr5Context arrayIndexReadExpr) {
            return parseArrayIndexReadExpr(arrayIndexReadExpr);
        } else if (expr5 instanceof LiteralExpr5Context literalExpr) {
            return parseLiteralExpr(literalExpr);
        } else if (expr5 instanceof ClosureLiteralExpr5Context closureLiteralExpr) {
            return parseClosureLiteralExpr(closureLiteralExpr);
        } else if (expr5 instanceof ReferenceExpr5Context referenceExpr) {
            return parseReference(referenceExpr.ID().getText());
        } else if (expr5 instanceof CallExpr5Context callExpr) {
            return parseCallExpr(callExpr);
        } else {
            return parseExpr1(((PrecedenceOneExpr5Context) expr5).expr1());
        }
    }

    private PropertyReadExprNode parsePropertyReadExpr(PropertyReadExpr5Context propertyReadExpr) {
        return PropertyReadExprNodeGen.create(
                parseExpr5(propertyReadExpr.expr5()),
                propertyReadExpr.ID().getText()
        );
    }

    private ArrayLiteralExprNode parseArrayLiteralExpr(ArrayLiteralExpr5Context arrayLiteralExpr) {
        return new ArrayLiteralExprNode(this.arrayShape, arrayLiteralExpr.expr1().stream()
                .map(this::parseExpr1)
                .toList());
    }

    private ArrayIndexReadExprNode parseArrayIndexReadExpr(ArrayIndexReadExpr5Context arrayIndexReadExpr) {
        return ArrayIndexReadExprNodeGen.create(
                parseExpr5(arrayIndexReadExpr.arr),
                parseExpr1(arrayIndexReadExpr.index)
        );
    }

    private EasyScriptExprNode parseLiteralExpr(LiteralExpr5Context literalExpr) {
        if (literalExpr.literal().INT() != null) {
            return parseIntLiteral(literalExpr.literal().INT().getText());
        } else if (literalExpr.literal().DOUBLE() != null) {
            return parseDoubleLiteral(literalExpr.literal().DOUBLE().getText());
        } else if (literalExpr.literal().BOOLEAN() != null) {
            return parseBooleanLiteral(literalExpr.literal().BOOLEAN().getText());
        } else if (literalExpr.literal().STRING() != null) {
            return parseStringLiteral(literalExpr.literal().STRING().getText());
        }

        return new UndefinedLiteralExprNode();
    }

    private ClosureLiteralExprNode parseClosureLiteralExpr(ClosureLiteralExpr5Context closureLiteralExpr) {
        this.currentDepth++;
        FrameDescriptor.Builder previousFrameDescriptorBuilder = this.frameDescriptorBuilder;
        this.frameDescriptorBuilder = FrameDescriptor.newBuilder();

        var functionArguments = new HashMap<String, FrameMember>();
        List<TerminalNode> funcArgs = closureLiteralExpr.args.ID();

        for (int i = 0; i < funcArgs.size(); i++) {
            functionArguments.put(funcArgs.get(i).getText(), new FunctionArgument(i + 1, this.currentDepth));
        }

        this.localScopes.push(functionArguments);

        List<EasyScriptStmtNode> funcStmts = this.parseStmtsList(closureLiteralExpr.stmt());
        FrameDescriptor frameDescriptor = this.frameDescriptorBuilder.build();

        this.localScopes.pop();
        this.currentDepth--;
        this.frameDescriptorBuilder = previousFrameDescriptorBuilder;

        return new ClosureLiteralExprNode(
                frameDescriptor,
                new UserFuncBodyStmtNode(funcStmts),
                funcArgs.size()
        );
    }

    private EasyScriptExprNode parseReference(String variableId) {
        FrameMember frameMember = findFrameMember(variableId);

        if (frameMember == null) {
            throw new EasyScriptException("'" + variableId + "' is not defined");
        } else if (frameMember instanceof FunctionArgument) {
            return new ReadFunctionArgExprNode(frameMember.index, this.currentDepth - frameMember.depth);
        } else {
            return VarReferenceExprNodeGen.create(frameMember.index, this.currentDepth - frameMember.depth);
        }
    }

    private FunctionCallExprNode parseCallExpr(CallExpr5Context callExpr) {
        return new FunctionCallExprNode(parseExpr5(callExpr.expr5()),
                callExpr.expr1().stream()
                        .map(this::parseExpr1)
                        .collect(Collectors.toList()));
    }

    private EasyScriptExprNode parseIntLiteral(String text) {
        try {
            return new IntLiteralExprNode(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return parseDoubleLiteral(text);
        }
    }

    private DoubleLiteralExprNode parseDoubleLiteral(String text) {
        return new DoubleLiteralExprNode(Double.parseDouble(text));
    }

    private BooleanLiteralExprNode parseBooleanLiteral(String text) {
        return new BooleanLiteralExprNode(text.equals("true"));
    }

    private StringLiteralExprNode parseStringLiteral(String text) {
        return new StringLiteralExprNode(StringEscapeUtils.unescapeJson(
                text.substring(1, text.length() - 1)
        ));
    }
}
