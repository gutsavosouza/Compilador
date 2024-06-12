package main.compilador.cmp.ast.comando;

import main.compilador.cmp.ast.expressao.Expressao;
import main.compilador.cmp.visitor.Visitor;

public class ComandoCondicionalCompleto extends Comando {
    public Expressao expressao;
    public Comando comando1, comando2;

    public ComandoCondicionalCompleto(Expressao expressao, Comando comando1, Comando comando2) {
        this.expressao = expressao;
        this.comando1 = comando1;
        this.comando2 = comando2;
    }

    @Override
    public void visit(Visitor v) {
        v.visitComandoCondicionalCompleto(this);
    }
}
