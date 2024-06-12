package main.compilador.cmp.ast.expressao;

import main.compilador.cmp.visitor.Visitor;

public class ExpressaoUnaria extends Expressao {

    public Expressao expressao;

    public ExpressaoUnaria(Expressao expressao) {
        this.expressao = expressao;
    }

    @Override
    public void visit(Visitor v) {
        v.visitExpressaoUnaria(this);
    }
}
