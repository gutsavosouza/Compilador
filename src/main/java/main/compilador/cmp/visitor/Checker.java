package main.compilador.cmp.visitor;

import javafx.scene.control.TextArea;
import main.compilador.MainController;
import main.compilador.cmp.analisador.contextual.IdentificationTable;
import main.compilador.cmp.analisador.contextual.Type;
import main.compilador.cmp.ast.Corpo;
import main.compilador.cmp.ast.Programa;
import main.compilador.cmp.ast.comando.*;
import main.compilador.cmp.ast.declaracao.DeclaracaoSequencial;
import main.compilador.cmp.ast.declaracao.DeclaracaoVariavel;
import main.compilador.cmp.ast.expressao.ExpressaoBinaria;
import main.compilador.cmp.ast.expressao.ExpressaoUnaria;
import main.compilador.cmp.ast.expressao.Fator;
import main.compilador.cmp.ast.terminal.*;

import java.util.*;

public class Checker implements Visitor {
    private MainController controller;

    public static IdentificationTable idTable;
    private boolean contextError = false;

    private List<Type> typesList;

    private short varAddr;

    public Checker(MainController controller) {
        this.controller = controller;
    }

    public void check(Programa programa){
        idTable = new IdentificationTable();
        typesList = new ArrayList<>();
        varAddr = 0;

        contextError = false;
        programa.visit(this);

        if(!contextError){
            controller.appendToLogCompilador("\nAnálise contextual terminada com sucesso.");
            //System.out.println("Análise contextual terminada com sucesso.");
        } else {
            controller.appendToLogCompilador("\nA análise contextual não pôde terminar pois foi encontrado um erro de contexto.");
            //System.out.println("Erro de contexto encontrado durante a análise.");
        }

    }

    @Override
    public void visitPrograma(Programa programa) {
        if(programa != null && !contextError){
            programa.id.visit(this);
            programa.corpo.visit(this);
        }
    }

    @Override
    public void visitCorpo(Corpo corpo) {
        if(corpo != null && !contextError){
            corpo.declaracao.visit(this);
            corpo.comando.visit(this);
        }
    }

    @Override
    public void visitComandoAtribuicao(ComandoAtribuicao comandoAtribuicao) {
        if(comandoAtribuicao != null && !contextError){
            Type varType, expType;

            comandoAtribuicao.id.visit(this);
            varType = idTable.retrieve(comandoAtribuicao.id.spelling);

            typesList = new ArrayList<>();
            comandoAtribuicao.expressao.visit(this);

            //System.out.println("aqui " + typesList);
            if(varType == null) {
                this.contextError = true;
                controller.appendToLogCompilador("\nVariável \"" + comandoAtribuicao.id.spelling + "\" não declarada anteriormente.");
                //System.out.println("Variável \"" + comandoAtribuicao.id.spelling + "\" não declarada anteriormente.");
                return;
            }

            if (!typesList.stream().allMatch(typesList.get(0)::equals)) {
                contextError = true;
                //expType = Type.type_error;
                controller.appendToLogCompilador("\nErro ao analisar expressão: Tipos incorretos");
                //System.out.println("Erro ao analisar expressão: Tipos incorretos");
                return;
            } else {
                expType = typesList.get(0);
            }

            //System.out.println(varType + " " + expType);
            if(varType != expType) {
                contextError = true;
                controller.appendToLogCompilador("\nErro ao atribuir valor à variável: Tipagem incorreta");
                //System.out.println("Erro ao atribuir valor à variável: Tipagem incorreta");
            }
        }
    }

    @Override
    public void visitComandoCondicional(ComandoCondicional comandoCondicional) {
        if(comandoCondicional != null && !contextError){
            Type expType;

            typesList = new ArrayList<>();
            comandoCondicional.expressao.visit(this);

            if(Collections.frequency(typesList, Type.type_bool) != 1){
                contextError = true;
                controller.appendToLogCompilador("\nErro ao analisar expressão do comando condicional: Tipagem incorreta");
//                System.out.println("Erro ao analisar expressão do comando condicional: Tipagem incorreta");
            }

            comandoCondicional.comando.visit(this);
        }
    }

    @Override
    public void visitComandoCondicionalCompleto(ComandoCondicionalCompleto comandoCondicionalCompleto) {
        if(comandoCondicionalCompleto != null && !contextError) {
            Type expType;

            typesList = new ArrayList<>();
            comandoCondicionalCompleto.expressao.visit(this);

            if(Collections.frequency(typesList, Type.type_bool) != 1){
                contextError = true;
                controller.appendToLogCompilador("\nErro ao analisar expressão do comando condicional completo: Tipagem incorreta");
//                System.out.println("Erro ao analisar expressão do comando condicional completo: Tipagem incorreta");
            }

            comandoCondicionalCompleto.comando1.visit(this);
            comandoCondicionalCompleto.comando2.visit(this);
        }
    }

    @Override
    public void visitComandoIterativo(ComandoIterativo comandoIterativo) {
        if(comandoIterativo != null & !contextError) {
            Type expType;

            typesList = new ArrayList<>();
            comandoIterativo.expressao.visit(this);

            if(Collections.frequency(typesList, Type.type_bool) != 1){
                contextError = true;
                controller.appendToLogCompilador("\nErro ao analisar expressão do comando iterativo: Tipagem incorreta");
//                System.out.println("Erro ao analisar expressão do comando iterativo: Tipagem incorreta");
            }

            comandoIterativo.comando.visit(this);
        }
    }

    @Override
    public void visitComandoSequencial(ComandoSequencial comandoSequencial) {
        if(comandoSequencial != null  && !contextError) {
            comandoSequencial.comando1.visit(this);
            comandoSequencial.comando2.visit(this);
        }
    }

    @Override
    public void visitDeclaracaoVariavel(DeclaracaoVariavel declaracaoVariavel) {
        if(declaracaoVariavel != null && !contextError){
            declaracaoVariavel.id.visit(this);
            declaracaoVariavel.tipo.visit(this);

            if(idTable.retrieve(declaracaoVariavel.id.spelling) == null) {
                idTable.enter(declaracaoVariavel.id.spelling, declaracaoVariavel.tipo.spelling, varAddr);
                varAddr++; // indo para o proximo endereço
            } else {
                this.contextError = true;
                controller.appendToLogCompilador("\nVariável \"" + declaracaoVariavel.id.spelling + "\" declarada mais de uma vez.");
//                System.out.println("Variável \"" + declaracaoVariavel.id.spelling + "\" declarada mais de uma vez.");
            }
        }
    }

    @Override
    public void visitDeclaracaoSequencial(DeclaracaoSequencial declaracaoSequencial) {
        if(declaracaoSequencial != null && !contextError){
            declaracaoSequencial.declaracao1.visit(this);
            declaracaoSequencial.declaracao2.visit(this);
        }
    }

    @Override
    public void visitExpressaoBinaria(ExpressaoBinaria expressaoBinaria) {
        if(expressaoBinaria != null && !contextError) {
            expressaoBinaria.expressao1.visit(this);
            expressaoBinaria.operador.visit(this);
            expressaoBinaria.expressao2.visit(this);
        }
    }

    @Override
    public void visitExpressaoUnaria(ExpressaoUnaria expressaoUnaria) {
        if(expressaoUnaria != null && !contextError) {
            expressaoUnaria.expressao.visit(this);
        }
    }

    @Override
    public void visitFator(Fator fator) {
        if(fator != null && !contextError) {
            fator.terminal.visit(this);
        }
    }

    @Override
    public void visitBooleanLiteral(BooleanLiteral booleanLiteral) {
        if(booleanLiteral != null && !contextError){
            typesList.add(Type.type_bool);
        }
    }

    @Override
    public void visitIdentificador(Identificador identificador) {
        if(identificador != null && !contextError) {
            typesList.add(idTable.retrieve(identificador.spelling));
        }
    }

    @Override
    public void visitIntegerLiteral(IntegerLiteral integerLiteral) {
        if(integerLiteral != null && !contextError){
            typesList.add(Type.type_integer);
        }
    }

    @Override
    public void visitOperador(Operador operador) {
        if(operador != null && !contextError){
            switch (operador.spelling){
                case ">", "<", "=" -> {
                    typesList.add(Type.type_bool);
                }
            }
        }
    }

    @Override
    public void visitTipo(Tipo tipo) {

    }

    public boolean hasContextError() {
        return contextError;
    }
}
