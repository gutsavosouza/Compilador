package main.compilador.cmp.ast.terminal;

import main.compilador.cmp.visitor.Visitor;

public class Tipo extends Terminal {

    public Tipo(String spelling) {
        this.spelling = spelling;
    }

    @Override
    public void visit(Visitor v) {
        v.visitTipo(this);
    }
}
