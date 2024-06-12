package main.compilador.cmp.ast;

import main.compilador.cmp.visitor.Visitor;

public abstract class AST {
    public abstract void visit(Visitor v);
}
