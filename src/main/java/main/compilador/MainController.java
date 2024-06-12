package main.compilador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import main.compilador.cmp.analisador.sintatico.Parser;
import main.compilador.cmp.ast.Programa;
import main.compilador.cmp.visitor.Checker;
import main.compilador.cmp.visitor.Coder;
import main.compilador.cmp.visitor.Printer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainController {
    @FXML
    private TreeView<String> treeView;
    @FXML
    private TreeItem<String> ast;

    @FXML
    private Tab tabVerArquivoFonte;
    @FXML
    private Tab tabVerAST;
    @FXML
    private Tab tabGerarCodigo;

    @FXML
    private Button butaoAnaliseSintatica;
    @FXML
    private Button butaoGerarAST;
    @FXML
    private Button butaoAnaliseContextual;
    @FXML
    private Button butaoAbrirArquivo;
    @FXML
    private Button butaoGerarCodigo;
    @FXML
    private MenuItem butaoSalvarArquivo;

    @FXML
    private TextArea textoCodigoFonte;
    @FXML
    private TextArea logDoCompilador;
    @FXML
    private TextArea textoCodigoGerado;

    private FileChooser fileChooser;

    Parser parser;
    Programa programa;

    Printer printer;
    Checker checker;
    Coder coder;

    File openedFile;

    public void initialize() {
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TXT files: (*.txt)", "*.txt");
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
        fileChooser.getExtensionFilters().add(extensionFilter);

        printer = new Printer(this);
        checker = new Checker(this);
        coder = new Coder(this);

        tabGerarCodigo.setDisable(true);
        tabVerAST.setDisable(true);
        tabVerArquivoFonte.setDisable(true);
        butaoAnaliseSintatica.setDisable(true);
        butaoAnaliseContextual.setDisable(true);
        butaoGerarAST.setDisable(true);
        butaoGerarCodigo.setDisable(true);

    }

    public void handleButaoAbrirArquivo() {
        ast.setExpanded(false);
        fileChooser.setTitle("Escolher arquivo");
        openedFile = fileChooser.showOpenDialog(butaoAbrirArquivo.getScene().getWindow());
        if(openedFile != null) {
            parser = new Parser(openedFile, this);

            tabVerArquivoFonte.setDisable(false);
            butaoAnaliseSintatica.setDisable(false);
            tabGerarCodigo.setDisable(true);
            tabVerAST.setDisable(true);
            butaoGerarAST.setDisable(true);
            butaoAnaliseContextual.setDisable(true);
            butaoGerarCodigo.setDisable(true);
            butaoSalvarArquivo.setDisable(true);

            textoCodigoFonte.visibleProperty().set(true);
            textoCodigoFonte.clear();
            for(String line : parser.getFileString()) {
                textoCodigoFonte.appendText(line + "\n");
            }

            logDoCompilador.clear();
            //ast = new TreeItem<>();
        }
    }

    public void handleButaoAnaliseSintatica() {
        programa = parser.parse();

        //System.out.println(programa);
        if(programa != null) {
            butaoAnaliseContextual.setDisable(false);
            butaoGerarAST.setDisable(false);
        }
        butaoAnaliseSintatica.setDisable(true);
    }

    public void handleButaoGerarAST(){
        if(programa != null) {
            if(!ast.getChildren().isEmpty()) ast.getChildren().remove(0);
            printer.print(programa);
            tabVerAST.setDisable(false);
        }
        butaoGerarAST.setDisable(true);
    }

    public void handleButaoAnaliseContextual(){
        if(programa != null) {
            checker.check(programa);
            if(!checker.hasContextError()) {
                //System.out.println(checker.hasContextError());
                butaoGerarCodigo.setDisable(false);
            }
        }
        butaoAnaliseContextual.setDisable(true);
    }

    public void handleButaoGerarCodigo(){
        if(programa != null) {
            coder.code(programa);
            tabGerarCodigo.setDisable(false);
            butaoSalvarArquivo.setDisable(false);
        }
        butaoGerarCodigo.setDisable(true);
    }

    public void handleButaoSalvarArquivo(){
        fileChooser.setTitle("Salvar arquivo");
        File file = fileChooser.showSaveDialog(butaoAbrirArquivo.getScene().getWindow());
//        System.out.println(file);
        try{
            FileWriter fw = new FileWriter(file);
            fw.write(textoCodigoGerado.getText());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public TreeItem<String> getTreeItem() {
        return ast;
    }

    public void appendToCodigoGerado(String string) {
        textoCodigoGerado.appendText(string);
    }

    public void setCodigoGeradoToEmptyText() {
        this.textoCodigoGerado.clear();
    }

    public void appendToLogCompilador(String string) {
        logDoCompilador.appendText(string);
    }

    public TextArea getLogCompilador(){
        return logDoCompilador;
    }

    public TextArea getTextoCodigoGerado() {
        return textoCodigoGerado;
    }
}
