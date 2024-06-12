package main.compilador.cmp.ast.terminal;

import main.compilador.cmp.visitor.Visitor;

public class Identificador extends Terminal {

    public Identificador(String spelling) {
        this.spelling = spelling;
    }

    @Override
    public void visit(Visitor v) {
        v.visitIdentificador(this);
    }
}
