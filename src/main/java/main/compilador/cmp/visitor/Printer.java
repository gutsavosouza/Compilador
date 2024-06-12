package main.compilador.cmp.visitor;

// TODO: 29/04/2024  IMPLEMENTAR PRINTER COM UM CAMPO QUE SALVA O ESTADO ATUAL EM QUE ESTOU PERCORRENDO A ARVORE...

import javafx.scene.control.TreeItem;
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

public class Printer implements Visitor {
    private final TreeItem<String> ast;

    private TreeItem<String> corpoNode;
    private TreeItem<String> comandosNode;
    private TreeItem<String> declaracoesNode;

    private TreeItem<String> expressaoNode;
    private TreeItem<String> operadorNode;
    private TreeItem<String> fatorNode;

    private TreeItem<String> comandoNode;

    private TreeItem<String> auxCom;

    private boolean inCondBlock = false;
    private boolean inIteBlock = false;

    public Printer(MainController controller) {
        ast = controller.getTreeItem();
    }

    public void print(Programa programa) {
        programa.visit(this);
    }

    @Override
    public void visitPrograma(Programa programa) {
        if(programa != null){
            TreeItem<String> id = new TreeItem<>(programa.id.spelling);
            TreeItem<String> programaNode = new TreeItem<>("Programa");

            programaNode.getChildren().add(id);

            programa.corpo.visit(this);

            programaNode.getChildren().add(corpoNode);

            ast.getChildren().add(programaNode);
        }
    }

    @Override
    public void visitCorpo(Corpo corpo) {
        if(corpo != null) {
            corpoNode = new TreeItem<>("Corpo");

            declaracoesNode = new TreeItem<>("Declarações");
            comandosNode = new TreeItem<>("Comandos");

            corpoNode.getChildren().add(declaracoesNode);
            corpoNode.getChildren().add(comandosNode);

            corpo.declaracao.visit(this);
            corpo.comando.visit(this);

            if(comandosNode.getValue() != null) {
                comandosNode.getChildren().add(comandoNode);
            }
        }
    }

    @Override
    public void visitComandoAtribuicao(ComandoAtribuicao comandoAtribuicao) {
        if(comandoAtribuicao != null) {
            TreeItem<String> atribCom = new TreeItem<>("Comando de atribuição");
            expressaoNode = new TreeItem<>();

            TreeItem<String> id = new TreeItem<>(comandoAtribuicao.id.spelling);

            comandoAtribuicao.expressao.visit(this);

            atribCom.getChildren().add(id);
            if(expressaoNode.getValue() != null) {
                id.getChildren().add(expressaoNode);
            } else {
                id.getChildren().add(fatorNode);
            }

            comandoNode = cloneTree(atribCom);
        }
    }

    @Override
    public void visitComandoCondicional(ComandoCondicional comandoCondicional) {
        if(comandoCondicional != null) {
            TreeItem<String> expressao = new TreeItem<>("Expressao");
            TreeItem<String> comando = new TreeItem<>("Comando");

            TreeItem<String> condCom = new TreeItem<>("Comando condicional");
            expressaoNode = new TreeItem<>();

            this.inCondBlock = true;

            auxCom = comando;

            condCom.getChildren().add(expressao);
            condCom.getChildren().add(comando);

            comandoCondicional.expressao.visit(this);
            expressao.getChildren().add(expressaoNode);

            comandoCondicional.comando.visit(this);
            if(comando.getChildren().isEmpty()) comando.getChildren().add(comandoNode);

            comandoNode = cloneTree(condCom);

            this.inCondBlock = false;
        }
    }

    @Override
    public void visitComandoCondicionalCompleto(ComandoCondicionalCompleto comandoCondicionalCompleto) {
        TreeItem<String> expressao = new TreeItem<>("Expressao");
        TreeItem<String> comandoTrue = new TreeItem<>("Comando : True");
        TreeItem<String> comandoFalse = new TreeItem<>("Comando : False");

        TreeItem<String> condCCom = new TreeItem<>("Comando condicional");
        expressaoNode = new TreeItem<>();

        this.inCondBlock = true;


        condCCom.getChildren().add(expressao);
        condCCom.getChildren().add(comandoTrue);
        condCCom.getChildren().add(comandoFalse);

        comandoCondicionalCompleto.expressao.visit(this);
        expressao.getChildren().add(expressaoNode);

        // TRUE
        auxCom = comandoTrue;

        comandoCondicionalCompleto.comando1.visit(this);
        if(comandoTrue.getChildren().isEmpty()) comandoTrue.getChildren().add(comandoNode);


        // FALSE
        auxCom = comandoFalse;

        comandoCondicionalCompleto.comando2.visit(this);
        if(comandoFalse.getChildren().isEmpty()) comandoFalse.getChildren().add(comandoNode);

        comandoNode = cloneTree(condCCom);

        this.inCondBlock = false;
    }

    @Override
    public void visitComandoIterativo(ComandoIterativo comandoIterativo) {
        if(comandoIterativo != null) {
            TreeItem<String> expressao = new TreeItem<>("Expressão");
            TreeItem<String> comando = new TreeItem<>("Comando");

            TreeItem<String> iteCom = new TreeItem<>("Comando iterativo");
            expressaoNode = new TreeItem<>();

            this.inIteBlock = true;

            auxCom = comando;

            iteCom.getChildren().add(expressao);
            iteCom.getChildren().add(comando);

            comandoIterativo.expressao.visit(this);
            expressao.getChildren().add(expressaoNode);

            comandoIterativo.comando.visit(this);
            if(comando.getChildren().isEmpty()) comando.getChildren().add(comandoNode);

            comandoNode = cloneTree(iteCom);

            this.inIteBlock = false;
        }
    }

    @Override
    public void visitComandoSequencial(ComandoSequencial comandoSequencial) {
        if(comandoSequencial != null) {
            comandoSequencial.comando1.visit(this);
            if(inCondBlock || inIteBlock){
                auxCom.getChildren().add(comandoNode);
            } else if(comandoNode.getValue() != null){
                //System.out.println("aqui1 " + comandoNode);
                comandosNode.getChildren().add(comandoNode);
            }
            comandoSequencial.comando2.visit(this);
            if(inCondBlock || inIteBlock){
                auxCom.getChildren().add(comandoNode);
            } else if(comandoNode.getValue() != null){
                //System.out.println("aqui2 " + comandoNode);
                comandosNode.getChildren().add(comandoNode);
            }
            comandoNode = new TreeItem<>();
        }
    }

    @Override
    public void visitDeclaracaoVariavel(DeclaracaoVariavel declaracaoVariavel) {
        if(declaracaoVariavel != null) {
            TreeItem<String> declaracao = new TreeItem<>(declaracaoVariavel.id.spelling + " : " + declaracaoVariavel.tipo.spelling);

            declaracoesNode.getChildren().add(declaracao);
        }
    }

    @Override
    public void visitDeclaracaoSequencial(DeclaracaoSequencial declaracaoSequencial) {
        if (declaracaoSequencial != null) {
            declaracaoSequencial.declaracao1.visit(this);
            declaracaoSequencial.declaracao2.visit(this);
        }
    }

    @Override
    public void visitExpressaoBinaria(ExpressaoBinaria expressaoBinaria) {
        if(expressaoBinaria != null){
            if(expressaoNode.getValue() == null) {
                expressaoNode = new TreeItem<>(expressaoBinaria.operador.spelling);
                //System.out.println("2 " + expressaoNode + " " + operadorNode);
            } else {
                operadorNode = new TreeItem<>(expressaoBinaria.operador.spelling);
                expressaoNode.getChildren().add(operadorNode);
            }

            expressaoBinaria.expressao1.visit(this);
            expressaoBinaria.expressao2.visit(this);

            operadorNode = null;
        }
    }

    @Override
    public void visitExpressaoUnaria(ExpressaoUnaria expressaoUnaria) {
        if(expressaoUnaria != null) {
            expressaoUnaria.expressao.visit(this);
        }
    }

    @Override
    public void visitFator(Fator fator) {
        if(fator != null) {
            fatorNode = new TreeItem<>(fator.terminal.spelling);

            if(operadorNode != null) {
                operadorNode.getChildren().add(fatorNode);
            } else {
                if(expressaoNode.getValue() != null) expressaoNode.getChildren().add(fatorNode);
               // System.out.println("3 " + fat);
            }
        }
    }

    @Override
    public void visitBooleanLiteral(BooleanLiteral booleanLiteral) {
    }

    @Override
    public void visitIdentificador(Identificador identificador) {
    }

    @Override
    public void visitIntegerLiteral(IntegerLiteral integerLiteral) {
    }

    @Override
    public void visitOperador(Operador operador) {
    }

    @Override
    public void visitTipo(Tipo tipo) {
    }

    private TreeItem<String> cloneTree(TreeItem<String> tree) {
        TreeItem<String> clone = new TreeItem<>(tree.getValue());
        for(TreeItem<String> child : tree.getChildren()){
            clone.getChildren().add(cloneTree(child));
        }
        return clone;
    }

}
