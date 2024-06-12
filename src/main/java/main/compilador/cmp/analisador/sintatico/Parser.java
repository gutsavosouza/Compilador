package main.compilador.cmp.analisador.sintatico;

// anotcaoes:::
/*
    em parseIdentifier(); nos meotodos de parse trocar por accept(Token.IDDENTIFIER) pq na linguagem implementada
    IDENTIFIER é um simbolo terminal. trocar tambem parseTypeDenoter
    Em alguns pontos eh possivel trocar parseN(); por acceptIt(), visto que o simbolo terminal ja foi checado logo antes
    da chamada do parse.
    **OBSERVACAO:: a abortagem da execução é feita pelo analisador sintatico(Parser) o lexico vai passar um Token.ERROR
    para ele analisar e encerrar a execução
 */

// ** FALTA IMPLEMENTAR A REAÇÃO AOS ERROS COMPLETA: CASOS DE ERRO IMPLICARAO EM INTERRUPMENTO DO CODIGO OU NAO


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.compilador.MainController;
import main.compilador.cmp.analisador.lexico.Scanner;
import main.compilador.cmp.analisador.lexico.Token;
import main.compilador.cmp.ast.*;
import main.compilador.cmp.ast.comando.*;
import main.compilador.cmp.ast.declaracao.Declaracao;
import main.compilador.cmp.ast.declaracao.DeclaracaoSequencial;
import main.compilador.cmp.ast.declaracao.DeclaracaoVariavel;
import main.compilador.cmp.ast.expressao.Expressao;
import main.compilador.cmp.ast.expressao.ExpressaoBinaria;
import main.compilador.cmp.ast.expressao.Fator;
import main.compilador.cmp.ast.terminal.*;

public class Parser {
    MainController controller;

    private Token currentToken;
    private final Scanner scanner;
    private File sourceFile;

    private Programa progAST;

    public Parser(String sourcefile) {
        this.scanner = new Scanner(sourcefile);
    }

    public Parser(File file, MainController controller) {
        this.controller = controller;
        this.sourceFile = file;
        this.scanner = new Scanner(file);
    }

    public Programa parse() {
        currentToken = scanner.scan();
        progAST = parsePrograma();
        if(progAST == null) {
            logMessages("Impossível terminar a analise sintática.");
        } else {
            if (currentToken.getKIND() != Token.EOT) {
                handleErroTokenEsperado(Token.EOT);
                scanner.closeScanner();
            } else {
            logMessages("Analise sintática concluída com sucesso.");
            }
        }
        scanner.closeScanner();
        return progAST;
    }

    private Programa parsePrograma() {
        Programa progAST = null;
        if (currentToken.getKIND() == Token.PROGRAM) {
            acceptIt();
            Identificador idAST = parseId();
            if(!accept(Token.SEMICOLON)) idAST = null;
            Corpo corpoAST = parseCorpo();
            if(idAST != null && corpoAST != null) progAST = new Programa(idAST, corpoAST);
            return progAST;
        } else {
            handleErroTokenEsperado(Token.PROGRAM);
            return null;
        }
    }

    private Corpo parseCorpo() {
        Corpo corpoAST = null;
        Declaracao declaracao = parseDeclaracoes();
        Comando comando = parseComandoComposto();
        if(declaracao != null && comando != null) corpoAST = new Corpo(declaracao, comando);
        return corpoAST;
    }

    private Declaracao parseDeclaracoes() {
        Declaracao declaracao1 = parseDeclaracao();
        if(!accept(Token.SEMICOLON)) declaracao1 = null;
        while (currentToken.getKIND() == Token.VAR) {
            Declaracao declaracao2 = parseDeclaracao();
            if(!accept(Token.SEMICOLON)) declaracao1 = declaracao2 = null;
            if(declaracao1 != null && declaracao2 != null) {
                declaracao1 = new DeclaracaoSequencial(declaracao1, declaracao2);
            } else {
                declaracao1 = declaracao2 = null;
            }
        }
        return declaracao1;
    }

    private Declaracao parseDeclaracao() {
        Declaracao declaracao = null;
        if (currentToken.getKIND() == Token.VAR) {
            acceptIt();
            Identificador idAST = parseId();
            if(!accept(Token.COLON)) idAST = null;
            Tipo tipoAST = parseTipo();
            if(idAST != null && tipoAST != null) declaracao = new DeclaracaoVariavel(idAST, tipoAST);
            return declaracao;
        } else {
            handleErroTokenEsperado(Token.VAR);
            return null;
        }
    }

    private Tipo parseTipo() {
        Tipo tipoAST = null;
        switch (currentToken.getKIND()) {
            case Token.BOOLEAN_TYPE, Token.INTEGER_TYPE -> {
                tipoAST = new Tipo(currentToken.getSPELLING());
                acceptIt();
                return tipoAST;
            }
            default -> {
                logMessages("Erro sintático encontrado. Esperava encontrar identificador de tipo:   "
                        + Token.getSpellings(Token.BOOLEAN_TYPE) + " / "
                        + Token.getSpellings(Token.INTEGER_TYPE) + "   Encontrei:   " + currentToken.getSPELLING()
                        + "   \n" + currentToken.getLocalizacao() + "\n");
                acceptIt();
                this.progAST = null;
                return null;
            }
        }
    }

    private Comando parseComandoComposto() {
        Comando comAST = null;
        if (currentToken.getKIND() == Token.BEGIN) {
            acceptIt();
            comAST = parseListaComandos();
            //if(comAST == null) return null;
            if(!accept(Token.END)) comAST = null;
        } else {
            handleErroTokenEsperado(Token.BEGIN);
            this.progAST = null;
        }
        return comAST;
    }

    private Comando parseListaComandos() {
        Comando comAST = parseComando();
        if(!accept(Token.SEMICOLON)) comAST = null;
        while (currentToken.getKIND() == Token.IDENTIFIER || currentToken.getKIND() == Token.IF
                || currentToken.getKIND() == Token.WHILE || currentToken.getKIND() == Token.BEGIN) {
            Comando comAST2 = parseComando();
            if(!accept(Token.SEMICOLON)) comAST2 = comAST = null ;
            if(comAST != null && comAST2 != null) comAST = new ComandoSequencial(comAST, comAST2);
        }
        return comAST;
    }

    private Comando parseComando() {
        Comando comAST = null;
        switch (currentToken.getKIND()) {
            case Token.IDENTIFIER -> {
                Identificador idAST = parseId();
                if(!accept(Token.BECOMES)) idAST = null;
                Expressao expAST = parseExpressao();
                if(idAST != null && expAST != null) comAST = new ComandoAtribuicao(idAST, expAST);
                return comAST;
            }
            case Token.IF -> {
                acceptIt();
                Expressao expAST = parseExpressao();
                if(!accept(Token.THEN)) expAST = null;
                Comando comAST1 = parseComando();
                if(expAST != null && comAST1 != null) comAST = new ComandoCondicional(expAST, comAST1);
                if (currentToken.getKIND() == Token.ELSE) {
                    acceptIt();
                    Comando comAST2 = parseComando();
                    if(expAST != null && comAST1 != null && comAST2 != null) comAST = new ComandoCondicionalCompleto(expAST, comAST1, comAST2);
                    return comAST;
                }
            }
            case Token.WHILE -> {
                acceptIt();
                Expressao expAST = parseExpressao();
                if(!accept(Token.DO)) expAST = null;
                Comando comAST1 = parseComando();
                if(expAST != null && comAST1 != null) comAST = new ComandoIterativo(expAST, comAST1);
                return comAST;
            }
            case Token.BEGIN -> {
                comAST = parseComandoComposto();
            }
            default -> {
                logMessages("Erro sintático encontrado. Esperava encontrar token de comando:   "
                        + Token.getSpellings(Token.IDENTIFIER) + " / " + Token.getSpellings(Token.IF) + " / "
                        + Token.getSpellings(Token.WHILE) + " / " + Token.getSpellings(Token.BEGIN) + "   Encontrei:   "
                        + currentToken.getSPELLING() + "   \n" + currentToken.getLocalizacao() + "\n");
                acceptIt();
                return null;
            }
        }
        return comAST;
    }

    private Expressao parseExpressao() {
        Expressao expAST1 = parseExpressaoSimples();
        while (currentToken.getKIND() == Token.OPERATOR_LESSTHAN || currentToken.getKIND() == Token.OPERATOR_GREATERTHAN
                || currentToken.getKIND() == Token.OPERATOR_EQUALS) {
            Operador opAST = parseOpRel();
            Expressao expAST2 = parseExpressaoSimples();
            if(expAST1 != null && opAST != null && expAST2 != null) expAST1 = new ExpressaoBinaria(opAST, expAST1, expAST2);
        }
        return expAST1;
    }

    private Expressao parseExpressaoSimples() {
        Expressao expAST1 = parseTermo();
        while (currentToken.getKIND() == Token.OPERATOR_SUM || currentToken.getKIND() == Token.OPERATOR_SUB
                || currentToken.getKIND() == Token.OPERATOR_OR) {
            Operador opAST = parseOpAd();
            Expressao expAST2 = parseTermo();
            if(expAST1 != null && opAST != null && expAST2 != null) expAST1 = new ExpressaoBinaria(opAST, expAST1, expAST2);
        }
        return expAST1;
    }

    private Expressao parseTermo() {
        Expressao expAST1 = parseFator();
        while (currentToken.getKIND() == Token.OPERATOR_MULT || currentToken.getKIND() == Token.OPERATOR_DIV
                || currentToken.getKIND() == Token.OPERATOR_AND) {
            Operador opAST = parseOpMul();
            Expressao fator2AST = parseFator();
            if(expAST1 != null && opAST != null && fator2AST != null) expAST1 = new ExpressaoBinaria(opAST, expAST1, fator2AST);
        }
        return expAST1;
    }

    private Expressao parseFator() {
        Expressao expAST = null;
        switch (currentToken.getKIND()) {
            case Token.IDENTIFIER -> {
                expAST = new Fator(parseId());
            }
            case Token.INTEGER_LITERAL -> {
                expAST = new Fator(parseIntegerLiteral());
            }
            case Token.BOOLEAN_TRUE, Token.BOOLEAN_FALSE -> {
                expAST = new Fator(parseBooleanLiteral());
            }
            case Token.LPAREN -> {
                acceptIt();
                expAST = parseExpressao();
                if(!accept(Token.RPAREN)) expAST = null;
            }
            default -> {
                logMessages("Erro sintático encontrado. Esperava encontrar identificador:   "
                        + Token.getSpellings(Token.IDENTIFIER) + " / " + Token.getSpellings(Token.INTEGER_LITERAL)
                        + " / " + Token.getSpellings(Token.BOOLEAN_TRUE)
                        + " / " + Token.getSpellings(Token.BOOLEAN_FALSE) + " / " + Token.getSpellings(Token.LPAREN) +
                        "   Encontrei:   " + currentToken.getSPELLING() + "   \n" + currentToken.getLocalizacao() + "\n");
                acceptIt();
                return null;
            }
        }
        return expAST;
    }

    private Operador parseOpRel() {
        Operador opAST = null;
        switch (currentToken.getKIND()) {
            case Token.OPERATOR_LESSTHAN, Token.OPERATOR_GREATERTHAN, Token.OPERATOR_EQUALS -> {
                opAST = new Operador(currentToken.getSPELLING());
                acceptIt();
            }
            default -> {
                logMessages("Erro sintático encontrado. Esperava encontrador operador de comparação:   "
                        + Token.getSpellings(Token.OPERATOR_LESSTHAN) + " / " + Token.getSpellings(Token.OPERATOR_GREATERTHAN)
                        + " / " + Token.getSpellings(Token.OPERATOR_EQUALS) + "   Encontrei:   " + currentToken.getSPELLING()
                        + "   \n" + currentToken.getLocalizacao() + "\n");
                acceptIt();
                return null;
            }
        }
        return opAST;
    }

    private Operador parseOpAd() {
        Operador opAST = null;
        switch (currentToken.getKIND()) {
            case Token.OPERATOR_SUM, Token.OPERATOR_SUB, Token.OPERATOR_OR -> {
                opAST = new Operador(currentToken.getSPELLING());
                acceptIt();
            }
            default -> {
                logMessages("Erro sintático encontrado. Esperava encontrador operador de comparação:   "
                        + Token.getSpellings(Token.OPERATOR_SUM) + " / " + Token.getSpellings(Token.OPERATOR_SUB)
                        + " / " + Token.getSpellings(Token.OPERATOR_OR) + "   Encontrei:   " + currentToken.getSPELLING()
                        + "   \n" + currentToken.getLocalizacao() + "\n");
                acceptIt();
                return null;
            }
        }
        return opAST;
    }

    private Operador parseOpMul() {
        Operador opAST = null;
        switch (currentToken.getKIND()) {
            case Token.OPERATOR_MULT, Token.OPERATOR_DIV, Token.OPERATOR_AND -> {
                opAST = new Operador(currentToken.getSPELLING());
                acceptIt();
            }
            default -> {
                logMessages("Erro sintático encontrado. Esperava encontrador operador de comparação:   "
                        + Token.getSpellings(Token.OPERATOR_MULT) + " / " + Token.getSpellings(Token.OPERATOR_DIV)
                        + " / " + Token.getSpellings(Token.OPERATOR_AND) + "   Encontrei:   " + currentToken.getSPELLING()
                        + "   \n" + currentToken.getLocalizacao() + "\n");
                acceptIt();
                return null;
            }
        }
        return opAST;
    }

    private Identificador parseId() {
        Identificador idAST;
        if (currentToken.getKIND() == Token.IDENTIFIER) {
            idAST = new Identificador(currentToken.getSPELLING());
            currentToken = scanner.scan();
            return idAST;
        } else {
            handleErroTokenEsperado(Token.IDENTIFIER);
            this.progAST = null;
            return null;
        }
    }

    private IntegerLiteral parseIntegerLiteral() {
        IntegerLiteral intAST;
        if (currentToken.getKIND() == Token.INTEGER_LITERAL) {
            intAST = new IntegerLiteral(currentToken.getSPELLING());
            acceptIt();
            return intAST;
        } else {
            handleErroTokenEsperado(Token.INTEGER_LITERAL);
            return null;
        }
    }

//    private FloatLiteral parseFloatLiteral() {
//        FloatLiteral floatAST = null;
//        if (currentToken.getKIND() == Token.FLOAT_LITERAL) {
//            floatAST = new FloatLiteral(currentToken.getSPELLING());
//            acceptIt();
//            return floatAST;
//        } else {
//            handleErroTokenEsperado(Token.FLOAT_LITERAL);
//            return null;
//        }
//    }

    private BooleanLiteral parseBooleanLiteral() {
        BooleanLiteral boolAST = null;
        if (currentToken.getKIND() == Token.BOOLEAN_FALSE || currentToken.getKIND() == Token.BOOLEAN_TRUE) {
            boolAST = new BooleanLiteral(currentToken.getSPELLING());
            acceptIt();
            return boolAST;
        } else {
            logMessages("Erro sintático encontrado. Esperava encontrar identificador de booleano:   "
                    + Token.getSpellings(Token.BOOLEAN_FALSE) + " / " + Token.getSpellings(Token.BOOLEAN_TRUE) +
                    "   Encontrei:   " + currentToken.getSPELLING() + "   \n"
                    + currentToken.getLocalizacao() + "\n");
            return null;
        }
    }

    private boolean accept(byte expectedKind) {
        if (currentToken.getKIND() == expectedKind) {
            currentToken = scanner.scan();
            return true;
        } else {
            handleErroTokenEsperado(expectedKind);
            return false;
        }
    }

    private void acceptIt() {
        currentToken = scanner.scan();
    }

    private void handleErroTokenEsperado(byte expectedToken) {
        logMessages("Erro sintático encontrado. Esperava encontrar:   " + Token.getSpellings(expectedToken) +
                "   Encontrei:   " + currentToken.getSPELLING() + "   \n" + currentToken.getLocalizacao() + "\n");
        this.progAST = null;
        //scanner.scan();
    }

    public List<String> getFileString() {
        List<String> lines = new ArrayList<String>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(sourceFile));
            while((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private void logMessages(String message) {
        controller.appendToLogCompilador(message);
    }
}
