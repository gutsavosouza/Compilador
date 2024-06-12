package main.compilador.cmp.ast.declaracao;

import main.compilador.cmp.visitor.Visitor;

public class DeclaracaoSequencial extends Declaracao {
    public Declaracao declaracao1, declaracao2;

    public DeclaracaoSequencial(Declaracao declaracao1, Declaracao declaracao2) {
        this.declaracao1 = declaracao1;
        this.declaracao2 = declaracao2;
    }

    @Override
    public void visit(Visitor v) {
        v.visitDeclaracaoSequencial(this);
    }
}
