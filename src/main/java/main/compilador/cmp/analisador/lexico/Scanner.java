package main.compilador.cmp.analisador.lexico;

import main.compilador.cmp.analisador.lexico.Token;

import java.io.*;

public class Scanner {
    // reader que vai ler o arquivo caracter por caracter, inicilizado em openFileAndReadData()
    private BufferedReader reader;

    private String sourceFile;
    // boolean que diz se o arquivo chegou ao fim
    private boolean isEndOfFile = false;

    // campos que ditam, respectivamente, o caracter atual que estamos analisando no arquivo
    // o tipo atual desse mesmo caracter, que é retornado no metodo scan
    // grafia do token atual
    private char currentChar;
    private byte currentKind;
    private StringBuffer currentSpelling;

    // campos que dizem a linha e a coluna do caracter atual
    private int fileLine;
    private int fileColumn;

    // o caracter que representa o começo de um comentario na linguagem analisada
    private final char commentChar = '#';

    // construtor do scanner, coloca os valores de base na linha e coluna para começar contagem, é inciado o stringbuffer
    // pra nao encontrar valor nulo
    // entao é aberto o arquivo fonte
    // por fim é colocado o primeiro caractere do arquivo fonte na variavel currentChar
    public Scanner(String sourceFile) {
        this.fileLine = 1;
        this.fileColumn = 0;
        this.currentSpelling = new StringBuffer();
        this.sourceFile = sourceFile;
        openFileAndReadData();
        nextCharTreatment();
    }

    public Scanner(File file) {
        this.fileLine = 1;
        this.fileColumn = 0;
        this.currentSpelling = new StringBuffer();
        try{
        this.reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        nextCharTreatment();
    }

    // o analisador em si esta aqui, rodando as funcoes e retornando um token com as informacao de onde esse token foi encontrado
    public Token scan() {
        Token scannedToken;
        while (isSeparator(currentChar)) {
            scanSeparator();
        }
//        currentSpelling = new StringBuffer();
        currentSpelling.delete(0, currentSpelling.length());
//        currentSpelling.setLength(0);
        currentKind = scanToken();
        scannedToken = new Token(currentKind, currentSpelling.toString(), fileLine, fileColumn);
        return scannedToken;
    }

    // analago ao metodo de scan(), porem ao inves de retornar um token esse metodo retorna uma lista com todos os tokens
    // escaneados no arquivo fonte
    // METODO DE DEBUG
//    public List<Token> scanToList(){
//        List<Token> readTokens = new ArrayList<>();
//        while(!this.isEndOfFile){
//            while (isSeparator(currentChar)) {
//                scanSeparator();
//            }
//            currentSpelling = new StringBuffer();
//            currentKind = scanToken();
//            readTokens.add(new Token(currentKind, currentSpelling.toString(), fileLine, fileColumn));
//        }
//        return readTokens;
//    }

    // metodo que abre o arquivo e instancia o BufferedReader
    private void openFileAndReadData() {
        try {
            this.reader = new BufferedReader(new FileReader(sourceFile));
            //System.out.println("Arquivo: " + sourceFile + " aberto com sucesso.");
        } catch (IOException e) {
            //System.out.println("Erro ao iniciar reader de arquivo");
            e.printStackTrace();
        }
    }

    // metodo que "engole" um caractere do arquivo fonte baseando num caractere esperado
    private void take(char expectedChar) {
        if (currentChar == expectedChar) {
            newLineTreatment(expectedChar);
            currentSpelling.append(currentChar);
            nextCharTreatment();
        } else {
            //System.out.println("Erro léxico.");
        }
    }

    // metodo que "engole" um caractere do arquivo fonte indepedente de outro caractere
    private void takeIt() {
        newLineTreatment(currentChar);
        currentSpelling.append(currentChar);
        // pulando a contagem de caracters \n e \r que estao no final de cada linha
        if (currentChar != '\n' && currentChar != '\r') this.fileColumn++;
        nextCharTreatment();
    }

    // tratamento para novas linhas>> detecta uma nova linha, incrementa o contador, reseta o contador de colunas
    // checa se a proxima linha é nula
    private void newLineTreatment(char c) {
        if (c == '\n' || c == '\r') {
            this.fileLine++;
            this.fileColumn = 0;
            try {
                if (reader.readLine() == null) {
                    this.currentKind = Token.EOT;
                    this.isEndOfFile = true;
                }
                // colocando o contador em -1 pois a contagem estava contando com a o caracter de nova linha, o que nao era ideal
                // porém essa atribuição serve para zerar o contador
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // tratamento para um novo caractere, checa se o mesmo é o final do arquivo
    private void nextCharTreatment() {
        try {
            currentChar = (char) reader.read();
            // detectando final do arquivo (aparentemente esse numero aqui diz isso)
            if (currentChar == 65535) {
                this.isEndOfFile = true;
            }
        } catch (IOException e) {
            //System.out.println("Erro na leitura de dados.");
            e.printStackTrace();
        }
    }

    // scaneia o caractere atual e define seu valor para o compiladorr, ou seja, se o mesmo é um identificador, palavra-chave
    // etc
    private byte scanToken() {
        switch (currentChar) {
            case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
                    'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                    'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' -> {
                takeIt();
                while (isLetter(currentChar) || isDigit(currentChar)) {
                    takeIt();
                }
                return Token.IDENTIFIER;
            }
            // CONSERTAR ASSIM QUE POSSIVEL !!!!!!!
            // POSSIVEL BUG: A ENTRADA >> "123k" É LIDA COMO INTEGER_LITERAL. !!
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                while (isDigit(currentChar)) {
                    takeIt();
                }
                return Token.INTEGER_LITERAL;
            }
            case '+' -> {
                takeIt();
                return Token.OPERATOR_SUM;
            }
            case '-' -> {
                takeIt();
                return Token.OPERATOR_SUB;
            }
            case '*' -> {
                takeIt();
                return Token.OPERATOR_MULT;
            }
            case '/' -> {
                takeIt();
                return Token.OPERATOR_DIV;
            }
            case '<' -> {
                takeIt();
                return Token.OPERATOR_LESSTHAN;
            }
            case '>' -> {
                takeIt();
                return Token.OPERATOR_GREATERTHAN;
            }
            case '=' -> {
                takeIt();
                return Token.OPERATOR_EQUALS;
            }
            case ';' -> {
                takeIt();
                return Token.SEMICOLON;
            }
            case ':' -> {
                takeIt();
                if (currentChar == '=') {
                    takeIt();
                    return Token.BECOMES;
                } else {
                    return Token.COLON;
                }
            }
            case '(' -> {
                takeIt();
                return Token.LPAREN;
            }
            case ')' -> {
                takeIt();
                return Token.RPAREN;
            }
            case 65535 -> {
                takeIt();
                return Token.EOT;
            }
            default -> {
                takeIt();
                return Token.ERROR;
            }
        }
    }

    // scaneia o caractere atual para checar se o mesmo é um separador e faz seu tratament: ignorar ele e continuar lendo o arquivo
    private void scanSeparator() {
        switch (currentChar) {
            case commentChar -> {
                takeIt();
                while (isGraphic(currentChar)) {
                    takeIt();
                }
                take('\r');
//                take('\n');
            }
            case ' ', '\r' -> takeIt();
        }
    }

    // metodo para dizer se um char é letra ou não
    private boolean isLetter(char c) {
        String cString = String.valueOf(c);
        return (cString.matches("[A-Za-z]"));
    }

    // metodo para dizer se um char é digito ou não
    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    // funcao para dizer se um char é um caractere grafico ou nao
    private boolean isGraphic(char c) {
        String cString = String.valueOf(c);
        return (cString.matches("."));
    }

    // metodo para dizer se um char é separator ou nao
    // separator é um conceito do compilador, presente no livro
    private boolean isSeparator(char c) {
        return c == commentChar || c == ' ' || c == '\r' || c == '\n';
    }

    // getter simples para ter informacao sobre o final do arquivo
    public boolean isEndOfFile() {
        return isEndOfFile;
    }

    public void closeScanner() {
        try {
            this.reader.close();
            //System.out.println("Reader fechado com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        this.currentSpelling = null;
        //System.out.println("Analisador léxico encerrado com sucesso.");
    }
}
