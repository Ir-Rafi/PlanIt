## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

<!-- Compile :
    javac --module-path "C:/Java/javafx-sdk-25/lib" --add-modules javafx.controls,javafx.fxml -d bin -cp "bin;lib/mysql-connector-j-9.5.0.jar" src\App.java src\Controller.java src\DatabaseUtility.java

Run cmd : 
java --module-path "C:/Java/javafx-sdk-25/lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp "bin;lib/mysql-connector-j-9.5.0.jar" App -->

# Compile
javac --module-path "C:/Java/javafx-sdk-25/lib" --add-modules javafx.controls,javafx.fxml -cp "lib/mysql-connector-j-9.5.0.jar;lib/jbcrypt-0.4.jar;src" -d bin src\App.java src\Controller.java src\DatabaseUtility.java src\RememberMeUtility.java

# Run
java --module-path "C:/Java/javafx-sdk-25/lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp "bin;lib/mysql-connector-j-9.5.0.jar;lib/jbcrypt-0.4.jar" App



# Event Management

## Authors
1. Md. Irfan Iqbal  
2. Shadman Zaman Sajid  
3. Maheru Tafannum  
4. Rubaiya Srishti  

## Language
- Java
