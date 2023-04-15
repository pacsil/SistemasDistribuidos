# SistemasDistribuidos
Aqui constam os 3 EP requisitados para serem avaliados para o curso durante o Estudo Dirigido.

EP1

Para O EP1 foi criado um Cliente/Servidor FTP utilizando sockets. Nao apenas é possível enviar e receber arquivo como também excluir localmente o arquivo transferido, foi implementando de forma que é possivel mais de um cliente e o servidor só se encerra quando todos clientes estiverem desconectados. Como sugestão, recomendo colocar o Server e o Client em diretórios diferentes para perceber que acontece sim tanto a transferência como os tratamentos e exclusões.

EP2 - Escreva um exemplo de aplicação que utilize REST e SpringBoot.

Para executar o código, você precisará criar um projeto Spring Boot em sua IDE preferida (por exemplo, IntelliJ, Eclipse, VS Code) e adicionar a dependência spring-boot-starter-web. Em seguida, crie uma classe com um método main que chama o método run da classe SpringApplication, passando a classe SimpsonRuleController e os argumentos de linha de comando (se houver). Por exemplo:

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpsonRuleController.class, args);
    }
}

Em seguida, você pode iniciar o aplicativo e testar o endpoint /integral usando um navegador ou ferramenta de teste de API, como o Postman. Certifique-se de passar os parâmetros corretos, como a, b, n, function e method. O valor de retorno será uma string formatada que contém o valor da integral.
