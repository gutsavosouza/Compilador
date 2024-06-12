package main.compilador.cmp.ast.comando;

import main.compilador.cmp.ast.expressao.Expressao;
import main.compilador.cmp.visitor.Visitor;

public class ComandoIterativo extends Comando {
    public Expressao expressao;
    public Comando comando;

    public ComandoIterativo(Expressao expressao, Comando comando) {
        this.expressao = expressao;
        this.comando = comando;
    }

    @Override
    public void visit(Visitor v) {
        v.visitComandoIterativo(this);
    }
}
