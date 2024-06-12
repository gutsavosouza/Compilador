package main.compilador.cmp.ast;

import main.compilador.cmp.ast.terminal.Identificador;
import main.compilador.cmp.visitor.Visitor;

public class Programa extends AST {
    public Identificador id;
    public Corpo corpo;

    public Programa(Identificador id, Corpo corpo) {
        this.id = id;
        this.corpo = corpo;
    }

    @Override
    public void visit(Visitor v) {
        v.visitPrograma(this);
    }
}
