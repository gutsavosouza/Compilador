package main.compilador.cmp.visitor;

import main.compilador.cmp.ast.*;
import main.compilador.cmp.ast.Programa;
import main.compilador.cmp.ast.comando.*;
import main.compilador.cmp.ast.declaracao.DeclaracaoSequencial;
import main.compilador.cmp.ast.declaracao.DeclaracaoVariavel;
import main.compilador.cmp.ast.expressao.ExpressaoBinaria;
import main.compilador.cmp.ast.expressao.ExpressaoUnaria;
import main.compilador.cmp.ast.expressao.Fator;
import main.compilador.cmp.ast.terminal.*;

public interface Visitor {
    public void visitPrograma(Programa programa);

    public void visitCorpo(Corpo corpo);

    public void visitComandoAtribuicao(ComandoAtribuicao comandoAtribuicao);

    public void visitComandoCondicional(ComandoCondicional comandoCondicional);

    public void visitComandoCondicionalCompleto(ComandoCondicionalCompleto comandoCondicionalCompleto);

    public void visitComandoIterativo(ComandoIterativo comandoIterativo);

    public void visitComandoSequencial(ComandoSequencial comandoSequencial);

    public void visitDeclaracaoVariavel(DeclaracaoVariavel declaracaoVariavel);

    public void visitDeclaracaoSequencial(DeclaracaoSequencial declaracaoSequencial);

    public void visitExpressaoBinaria(ExpressaoBinaria expressaoBinaria);

    public void visitExpressaoUnaria(ExpressaoUnaria expressaoUnaria);

    public void visitFator(Fator fator);

    public void visitBooleanLiteral(BooleanLiteral booleanLiteral);

    public void visitIdentificador(Identificador identificador);

    public void visitIntegerLiteral(IntegerLiteral integerLiteral);

    public void visitOperador(Operador operador);

    public void visitTipo(Tipo tipo);


}
