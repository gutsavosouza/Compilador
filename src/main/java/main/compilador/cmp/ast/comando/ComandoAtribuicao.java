package main.compilador.cmp.ast.comando;

import main.compilador.cmp.ast.expressao.Expressao;
import main.compilador.cmp.ast.terminal.Identificador;
import main.compilador.cmp.visitor.Visitor;

public class ComandoAtribuicao extends Comando {
    public Identificador id;
    public Expressao expressao;

    public ComandoAtribuicao(Identificador id, Expressao expressao) {
        this.id = id;
        this.expressao = expressao;
    }

    @Override
    public void visit(Visitor v) {
        v.visitComandoAtribuicao(this);
    }
}
