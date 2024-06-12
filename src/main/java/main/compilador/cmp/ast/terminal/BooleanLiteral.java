package main.compilador.cmp.ast.terminal;

import main.compilador.cmp.visitor.Visitor;

public class BooleanLiteral extends Terminal {

    public BooleanLiteral(String spelling) {
        this.spelling = spelling;
    }

    @Override
    public void visit(Visitor v) {
        v.visitBooleanLiteral(this);
    }
}
