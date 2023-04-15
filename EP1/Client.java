/*
    As importações de pacotes necessárias são realizadas no início do arquivo.

    As constantes SERVER_HOST e SERVER_PORT são definidas para indicar o endereço e a porta do servidor.

    A classe Client é definida, que contém a lógica para se conectar ao servidor, estabelecer as conexões de entrada e saída e implementar as operações de envio, recebimento e exclusão de arquivos.

    O método main() cria uma instância de Client e chama o método inicia() para iniciar a execução do cliente.

    O método inicia() é responsável por conectar ao servidor, estabelecer as conexões de entrada e saída e exibir o menu de opções para o usuário.

    O método conectaAoServidor() cria um objeto Socket para se conectar ao servidor e exibe uma mensagem na tela informando que a conexão foi estabelecida.

    O método iniciaStreams() cria objetos DataInputStream e DataOutputStream para enviar e receber dados através do socket e um objeto BufferedReader para ler dados do usuário a partir do console.

    O método Menu() exibe o menu de opções para o usuário e processa a entrada do usuário de acordo com a escolha selecionada. As opções incluem enviar um arquivo, receber um arquivo, excluir um arquivo e sair do cliente.

    O método deleteFile() pede ao usuário o nome do arquivo a ser excluído e verifica se o arquivo existe. Se o arquivo não existir, uma mensagem de erro é exibida. Caso contrário, o arquivo é excluído e uma mensagem de sucesso ou erro é exibida na tela.

    O método sendFile() pede ao usuário o nome do arquivo a ser enviado e verifica se o arquivo existe. Se o arquivo não existir, uma mensagem de erro é exibida. Caso contrário, o conteúdo do arquivo é lido e enviado ao servidor por meio dos objetos DataOutputStream. Uma mensagem de sucesso ou erro é exibida na tela.

    O método receiveFile() pede ao usuário o nome do arquivo a ser recebido e verifica se o arquivo já existe no diretório local. Se o arquivo já existir, uma mensagem de erro é exibida. Caso contrário, uma mensagem é enviada ao servidor para solicitar o arquivo. O conteúdo do arquivo é recebido do servidor por meio do objeto DataInputStream e é salvo no diretório local. Uma mensagem de sucesso ou erro é exibida na tela.

    O método closeStreams() é responsável por fechar as conexões de entrada e saída e o socket. Se ocorrer um erro durante o fechamento das conexões, uma mensagem de erro é exibida.*/

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8888;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private BufferedReader reader;

    public static void main(String[] args) {
        Client client = new Client();
        client.inicia();
    }

    public void inicia() {
        try {
            conectaAoServidor();
            iniciaStreams();
            Menu();
        } catch (IOException e) {
            System.err.println("Nao consegui conectar ao servidor: " + e.getMessage());
        } finally {
            closeStreams();
        }
    }

    private void conectaAoServidor() throws IOException {
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        System.out.println("Conectado ao servidor.");
    }

    private void iniciaStreams() throws IOException {
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    private void Menu() throws IOException {
        while (true) {
            System.out.println("Escolha uma opcao:");
            System.out.println("1. Enviar");
            System.out.println("2. Receber");
            System.out.println("3. Excluir arquivo local");
            System.out.println("4. Sair");
            System.out.print("Sua Escolha: ");

            String inputFromUser = reader.readLine().trim();

            switch (inputFromUser) {
                case "1":
                    sendFile();
                    break;
                case "2":
                    receiveFile();
                    break;
                case "3":
                    deleteFile();
                    break;
                case "4":
                    System.out.println("Encerrando o cliente...");
                    return;
                default:
                    System.out.println("Opcao invalida!");
                    break;
            }
        }
    }

    private void deleteFile() throws IOException {
        System.out.print("Digite o nome do arquivo a ser excluido: ");
        String filename = reader.readLine().trim();
        File file = new File(filename);

        if (!file.isFile()) {
            System.out.println("Arquivo nao encontrado!");
        return;
    }

        if (file.delete()) {
            System.out.println("Arquivo excluido com sucesso!");
        } else {
            System.out.println("Nao foi possivel excluir o arquivo.");
    }
}

    private void sendFile() throws IOException {
        System.out.print("Digite o nome do arquivo: ");
        String filename = reader.readLine().trim();
        File file = new File(filename);

        if (!file.isFile()) {
            System.out.println("Arquivo nao encontrado!");
            return;
        }

        if (new File(filename).exists()) {
            System.out.println("Arquivo solicitado ja existe!");
            return;
        }

        byte[] conteudo = Files.readAllBytes(file.toPath());
        String conteudoString = new String(conteudo);

        outputStream.writeUTF("ENVIO_DO_CLIENTE");
        outputStream.writeUTF(filename);
        outputStream.writeUTF(conteudoString);

        System.out.println("Arquivo enviado com sucesso!");
    }

    private void receiveFile() throws IOException {
        System.out.print("Digite o nome do arquivo: ");
        String filename = reader.readLine().trim();

        if (new File(filename).exists()) {
            System.out.println("Arquivo solicitado ja existe!");
            return;
        }

        outputStream.writeUTF("BAIXA_ARQUIVO");
        outputStream.writeUTF(filename);

        String conteudo = inputStream.readUTF();

        if (conteudo.isEmpty()) {
            System.out.println("Nao ha tal arquivo.");
            return;
        }

        Files.write(new File(filename).toPath(), conteudo.getBytes());

        System.out.println("Arquivo recebido com sucesso!");
    }

    private void closeStreams() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            System.err.println("Erro enquanto encerrava as streams: " + e.getMessage());
        }
    }
}