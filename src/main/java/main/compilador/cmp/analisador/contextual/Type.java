package main.compilador.cmp.analisador.contextual;

public class Type {
    private byte kind; // bool, integer, float

    private static final byte BOOL = 0, INTEGER = 1, FLOAT = 2, ERROR = -1;

    private Type(byte kind) {
        this.kind = kind;
    }

    public boolean equals(Type otherType) {
        return (this.kind == otherType.kind);
    }

    public static Type type_bool = new Type(BOOL);
    public static Type type_integer = new Type(INTEGER);
    public static Type type_float = new Type(FLOAT);
    public static Type type_error = new Type(ERROR);

    @Override
    public String toString() {
        String s = "ERROR";

        switch(this.kind) {
            case 0 -> s = "BOOL";
            case 1 -> s = "INTEGER";
            case 2 -> s = "FLOAT";
        }

        return s;
    }
}
