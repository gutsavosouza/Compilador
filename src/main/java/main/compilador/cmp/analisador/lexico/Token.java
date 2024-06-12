package main.compilador.cmp.analisador.lexico;

public class Token {

    // respectivamente, campos de TIPO, VALOR, LINHA E COLUNA
    private byte KIND;
    private String SPELLING;
    private int LINE;
    private int COLUMN;

    public final static byte IDENTIFIER = 0, INTEGER_LITERAL = 1, OPERATOR_SUM = 2, OPERATOR_SUB = 3,
            OPERATOR_MULT = 4, OPERATOR_DIV = 5, OPERATOR_LESSTHAN = 6, OPERATOR_GREATERTHAN = 7, OPERATOR_EQUALS = 8,
            BEGIN = 9, PROGRAM = 10, OPERATOR_AND = 11, OPERATOR_OR = 12, DO = 13, ELSE = 14, END = 15, IF = 16,
            INTEGER_TYPE = 17, BOOLEAN_TYPE = 18, THEN = 19, VAR = 20, BOOLEAN_TRUE = 21, BOOLEAN_FALSE = 22,
            WHILE = 23, SEMICOLON = 24, COLON = 25, BECOMES = 26, LPAREN = 27, RPAREN = 28, ERROR = 29, EOT = 30;

    private final static String[] spellings = {
            "<identifier>", "<integer-literal>", "+", "-", "*", "/", "<", ">", "=", "begin", "program",
            "and", "or", "do", "else", "end", "if", "integer", "boolean", "then", "var", "true", "false", "while",
            ";", ":", ":=", "(", ")", "<erorr>", "<eot>"
    };

    // construtor do token, passar os valores da chamada para os respectivos campos e define o valor de SPELLING no caso
    // do token ser um identificador, baseando-se na lista de ortografias
    public Token(byte KIND, String SPELLING, int LINE, int COLUMN) {
        this.KIND = KIND;
        this.SPELLING = SPELLING;
        this.LINE = LINE;
        this.COLUMN = COLUMN;

        if (KIND == IDENTIFIER) {
            for (int k = BEGIN; k <= WHILE; k++) {
                if (SPELLING.equals(spellings[k])) {
                    this.KIND = (byte) k;
                    break;
                }
            }
        }
    }

    public byte getKIND() {
        return KIND;
    }

    public String getKindAsName() {
        return spellings[KIND];
    }

    public String getSPELLING() {
        return SPELLING;
    }

    public int getLINE() {
        return LINE;
    }

    public int getCOLUMN() {
        return COLUMN;
    }

    public String getLocalizacao() {
        return "Linha: " + this.LINE + " Coluna: " + this.COLUMN;
    }

    public static String getSpellings(byte Token) {
        return spellings[Token];
    }

    @Override
    public String toString() {
        return "T: " + this.KIND + " - " + spellings[this.KIND] + "\tV:" + this.SPELLING + "\tL: " + this.LINE + "\tC: " + this.COLUMN;
    }
}
