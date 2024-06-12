package main.compilador.cmp.codegen;

public class LabelGenerator {
    private int labelCount;

    public LabelGenerator() {
        this.labelCount = 0;
    }

    public String generate(){
        labelCount++;
        return "L" + String.format("%04d", labelCount);
    }
}
