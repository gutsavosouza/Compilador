# Compilador
Compilador simples contruídos didaticamente. As especificações da linguagem da linguagem de entrada e da linguagem
de saída estão presentes no [documento PDF](./documentação%20-%20compilador.pdf) que acompanha esse repositório. Esse projeto foi fruto da disciplina
de Compiladores.

## Rodando a aplicação
### Pre-requisitos
- [Java](https://www.oracle.com/java/technologies/downloads/) (v17.0.12)
- [JavaFX](https://gluonhq.com/products/javafx/) (v21.0.4)
- [Apache Maven](https://maven.apache.org/) (v3.8.7 ou maior)

### Instalação
#### Compilando e Rodando
Para compilar a aplicação basta utilizar o Maven. Lembrando que em sistema Windows ao invés de chamar o Maven com "mvn"
chame com o arquivo presente nesse diretório, "./mvnw.cmd"
1. Para instalar todas as dependências execute o comando:
    ```
    mvn clean install
    ```
2. Para apenas executar a aplicação execute o comando:
    ```
    mvn javafx:run
    ```

3. Para compilar a aplicação em um executável, rode o comando:
    ```
    mvn package
    ```
   Esse comando irá criar no diretório "./target" da aplicação um arquivo .jar que representa o executável da aplicação.
   Com isso, basta executar o comando:
    ```
    java -jar ./target/<arquivo-gerado>.jar
    ```
#### Configurando ambiente de desenvolvimento
Ao clonar o repositório todas as dependências de bibliotecas externas devem ser automaticamente instaladas seguindo o arquivo
"./pom.xml". Caso isso não acontece, configure a SDK do JavaFX na versão especificada na sua IDE de preferência.