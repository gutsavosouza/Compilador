package main.compilador.cmp.ast.terminal;

import main.compilador.cmp.visitor.Visitor;

public class IntegerLiteral extends Terminal {

    public IntegerLiteral(String spelling) {
        this.spelling = spelling;
    }

    @Override
    public void visit(Visitor v) {
        v.visitIntegerLiteral(this);
    }
}
