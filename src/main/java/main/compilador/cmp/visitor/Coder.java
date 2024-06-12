package main.compilador.cmp.visitor;

import main.compilador.MainController;
import main.compilador.cmp.ast.Corpo;
import main.compilador.cmp.ast.Programa;
import main.compilador.cmp.ast.comando.*;
import main.compilador.cmp.ast.declaracao.DeclaracaoSequencial;
import main.compilador.cmp.ast.declaracao.DeclaracaoVariavel;
import main.compilador.cmp.ast.expressao.ExpressaoBinaria;
import main.compilador.cmp.ast.expressao.ExpressaoUnaria;
import main.compilador.cmp.ast.expressao.Fator;
import main.compilador.cmp.ast.terminal.*;
import main.compilador.cmp.codegen.LabelGenerator;

import java.util.Objects;

public class Coder implements Visitor {

    private short varAddr;

    private LabelGenerator labelGenerator;

    private MainController controller;

    public Coder(MainController controller){
        this.controller = controller;
    }

    public void code(Programa programa){
        controller.setCodigoGeradoToEmptyText();

        varAddr = 0;

        labelGenerator = new LabelGenerator();

        programa.visit(this);

        controller.appendToLogCompilador("\nGeração de código finalizada com sucesso.");
    }

    @Override
    public void visitPrograma(Programa programa) {
        if(programa != null){
            programa.id.visit(this);
            programa.corpo.visit(this);
            controller.appendToCodigoGerado(labelGenerator.generate() + ": HALT\n");
        }
    }

    @Override
    public void visitCorpo(Corpo corpo) {
        if(corpo != null){
            corpo.declaracao.visit(this);
            corpo.comando.visit(this);
        }
    }

    @Override
    public void visitComandoAtribuicao(ComandoAtribuicao comandoAtribuicao) {
        if(comandoAtribuicao != null){
            comandoAtribuicao.expressao.visit(this);
            encodeAssign(comandoAtribuicao.id);
        }
    }

    @Override
    public void visitComandoCondicional(ComandoCondicional comandoCondicional) {
        if(comandoCondicional != null) {
            comandoCondicional.expressao.visit(this);
            String endLabel = labelGenerator.generate();

            controller.appendToCodigoGerado(labelGenerator.generate() + ": JUMPIF(0) " + endLabel + "\n");
            comandoCondicional.comando.visit(this);
            controller.appendToCodigoGerado(endLabel + ": \n");
        }
    }

    @Override
    public void visitComandoCondicionalCompleto(ComandoCondicionalCompleto comandoCondicionalCompleto) {
        if(comandoCondicionalCompleto != null) {
            comandoCondicionalCompleto.expressao.visit(this);
            String elseLabel = labelGenerator.generate();
            String endLabel = labelGenerator.generate();

            controller.appendToCodigoGerado(labelGenerator.generate() + ": JUMPIF(0) " + elseLabel + "\n");
            comandoCondicionalCompleto.comando1.visit(this);
            controller.appendToCodigoGerado(labelGenerator.generate() + ": JUMP " + endLabel + "\n");
            controller.appendToCodigoGerado(elseLabel + ": \n");
            comandoCondicionalCompleto.comando2.visit(this);
            controller.appendToCodigoGerado(endLabel + ": \n");
        }
    }

    @Override
    public void visitComandoIterativo(ComandoIterativo comandoIterativo) {
        if(comandoIterativo != null) {
            String beginLabel = labelGenerator.generate();
            String endLabel = labelGenerator.generate();

            controller.appendToCodigoGerado(beginLabel + ": \n");
            comandoIterativo.expressao.visit(this);
            controller.appendToCodigoGerado(labelGenerator.generate() + ": JUMPIF(0) " + endLabel + "\n");
            comandoIterativo.comando.visit(this);
            controller.appendToCodigoGerado(labelGenerator.generate() + ": JUMP " + beginLabel + "\n" + endLabel + ": \n");
        }
    }

    @Override
    public void visitComandoSequencial(ComandoSequencial comandoSequencial) {
        if(comandoSequencial != null) {
            comandoSequencial.comando1.visit(this);
            comandoSequencial.comando2.visit(this);
        }
    }

    @Override
    public void visitDeclaracaoVariavel(DeclaracaoVariavel declaracaoVariavel) {
        if(declaracaoVariavel != null) {
            int size = 1;
            controller.appendToCodigoGerado(labelGenerator.generate() + ": PUSH " + size + "[SB]\n");

            declaracaoVariavel.address = varAddr;
            varAddr += size;

//            System.out.println(declaracaoVariavel.id.spelling + " addr: " + ((KnownAddress) declaracaoVariavel.entity).address);
        }
    }

    @Override
    public void visitDeclaracaoSequencial(DeclaracaoSequencial declaracaoSequencial) {
        if(declaracaoSequencial != null) {
            declaracaoSequencial.declaracao1.visit(this);
            declaracaoSequencial.declaracao2.visit(this);
        }
    }

    @Override
    public void visitExpressaoBinaria(ExpressaoBinaria expressaoBinaria) {
        if(expressaoBinaria != null) {
            expressaoBinaria.expressao1.visit(this);
            expressaoBinaria.expressao2.visit(this);
            expressaoBinaria.operador.visit(this);
        }
    }

    @Override
    public void visitExpressaoUnaria(ExpressaoUnaria expressaoUnaria) {

    }

    @Override
    public void visitFator(Fator fator) {
        if(fator != null) {
            fator.terminal.visit(this);
        }
    }

    @Override
    public void visitBooleanLiteral(BooleanLiteral booleanLiteral) {
        if(booleanLiteral != null) {
            switch (booleanLiteral.spelling) {
                case "true" -> {
                    controller.appendToCodigoGerado(labelGenerator.generate() + ": LOADL 1\n");
                }
                case "false" -> {
                    controller.appendToCodigoGerado(labelGenerator.generate() + ": LOADL 0\n");
                }
            }
        }
    }

    @Override
    public void visitIdentificador(Identificador identificador) {
        if(identificador != null) {
//            System.out.println("aqui ");
//            System.out.println(identificador.spelling);
            encodeFetch(identificador);
        }
    }

    @Override
    public void visitIntegerLiteral(IntegerLiteral integerLiteral) {
        if(integerLiteral != null) {
            controller.appendToCodigoGerado(labelGenerator.generate() + ": LOADL " + integerLiteral.spelling + "\n");
        }
    }

    @Override
    public void visitOperador(Operador operador) {
        if(operador != null){
            controller.appendToCodigoGerado(labelGenerator.generate() + ": CALL ");
            switch (operador.spelling){
                case "+" -> {
                    controller.appendToCodigoGerado("add");
                }
                case "-" -> {
                    controller.appendToCodigoGerado("sub");
                }
                case "*" -> {
                    controller.appendToCodigoGerado("mult");
                }
                case "/" -> {
                    controller.appendToCodigoGerado("div");
                }
                case ">" -> {
                    controller.appendToCodigoGerado("gt");
                }
                case "<" -> {
                    controller.appendToCodigoGerado("lt");
                }
                case "=" -> {
                    controller.appendToCodigoGerado("eq");
                }
                case "and" -> {
                    controller.appendToCodigoGerado("and");
                }
                case "or" -> {
                    controller.appendToCodigoGerado("or");
                }
            }
            controller.appendToCodigoGerado("\n");
        }
    }

    @Override
    public void visitTipo(Tipo tipo) {
    }

    private void encodeFetch(Identificador varId){
        if(varId != null && Checker.idTable.retrieve(varId.spelling) != null) {
            controller.appendToCodigoGerado(labelGenerator.generate() + ": LOAD " + Checker.idTable.getAddress(varId.spelling) + "[SB]\n");

        }
    }

    private void encodeAssign(Identificador varId){
        if(varId != null && Checker.idTable.retrieve(varId.spelling) != null) {
            controller.appendToCodigoGerado(labelGenerator.generate() + ": STORE " + Checker.idTable.getAddress(varId.spelling) + "[SB]\n");
        }
    }
}
