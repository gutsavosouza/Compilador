package main.compilador.cmp.ast.expressao;

import main.compilador.cmp.ast.terminal.Terminal;
import main.compilador.cmp.visitor.Visitor;

public class Fator extends Expressao {
    public Terminal terminal;

    public Fator(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void visit(Visitor v) {
        v.visitFator(this);
    }
}
