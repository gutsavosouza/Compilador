module main.compilador {
    requires javafx.controls;
    requires javafx.fxml;


    opens main.compilador to javafx.fxml;
    exports main.compilador;
    exports main.compilador.cmp.analisador.sintatico;
    opens main.compilador.cmp.analisador.sintatico to javafx.fxml;
    exports main.compilador.cmp.analisador.lexico;
    opens main.compilador.cmp.analisador.lexico to javafx.fxml;
    exports main.compilador.cmp.analisador.contextual;
    opens main.compilador.cmp.analisador.contextual to javafx.fxml;
}