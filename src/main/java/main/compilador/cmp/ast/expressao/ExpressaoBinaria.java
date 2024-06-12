package main.compilador.cmp.ast.expressao;

import main.compilador.cmp.visitor.Visitor;
import main.compilador.cmp.ast.terminal.Operador;

public class ExpressaoBinaria extends Expressao {
    public Operador operador;
    public Expressao expressao1, expressao2;

    public ExpressaoBinaria(Operador operador, Expressao expressao1, Expressao expressao2) {
        this.operador = operador;
        this.expressao1 = expressao1;
        this.expressao2 = expressao2;
    }

    @Override
    public void visit(Visitor v) {
        v.visitExpressaoBinaria(this);
    }
}
