package main.compilador.cmp.ast;

import main.compilador.cmp.ast.comando.Comando;
import main.compilador.cmp.ast.declaracao.Declaracao;
import main.compilador.cmp.visitor.Visitor;

public class Corpo extends AST {
    public Declaracao declaracao;
    public Comando comando;

    public Corpo(Declaracao declaracao, Comando comando) {
        this.declaracao = declaracao;
        this.comando = comando;
    }

    @Override
    public void visit(Visitor v) {
        v.visitCorpo(this);
    }
}
