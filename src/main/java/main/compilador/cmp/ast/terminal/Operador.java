package main.compilador.cmp.ast.terminal;

import main.compilador.cmp.visitor.Visitor;

public class Operador extends Terminal {

    public Operador(String spelling) {
        this.spelling = spelling;
    }

    @Override
    public void visit(Visitor v) {
        v.visitOperador(this);
    }
}
