package main.compilador.cmp.ast.declaracao;

import main.compilador.cmp.ast.terminal.Identificador;
import main.compilador.cmp.ast.terminal.Tipo;
import main.compilador.cmp.visitor.Visitor;

public class DeclaracaoVariavel extends Declaracao {
    public Identificador id;
    public Tipo tipo;
    public short address;

    public DeclaracaoVariavel(Identificador id, Tipo tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    @Override
    public void visit(Visitor v) {
        v.visitDeclaracaoVariavel(this);
    }
}
