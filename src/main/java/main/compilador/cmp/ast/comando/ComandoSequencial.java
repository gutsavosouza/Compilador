package main.compilador.cmp.ast.comando;

import main.compilador.cmp.visitor.Visitor;

public class ComandoSequencial extends Comando {
    public Comando comando1, comando2;

    public ComandoSequencial(Comando comando1, Comando comando2) {
        this.comando1 = comando1;
        this.comando2 = comando2;
    }

    @Override
    public void visit(Visitor v) {
        v.visitComandoSequencial(this);
    }
}
