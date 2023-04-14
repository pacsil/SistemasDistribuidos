/*
O código consiste em uma implementação de um servidor FTP simples em Java. O servidor utiliza sockets para se comunicar com os clientes e é capaz de receber arquivos enviados pelos clientes e enviar arquivos para os clientes.

A classe Server é responsável por iniciar o servidor e aceitar conexões de clientes. A classe mantém uma lista de threads de clientes (ClientThread) que se conectaram ao servidor. Quando o servidor é encerrado, ele também encerra todas as conexões com os clientes e fecha o socket do servidor.

A classe ClientThread é responsável por lidar com a comunicação com um cliente específico. Cada cliente é representado por uma thread. A thread do cliente recebe um fluxo de entrada de dados (DataInputStream) e um fluxo de saída de dados (DataOutputStream) do socket do cliente e usa esses fluxos para receber e enviar dados do e para o cliente.

A thread do cliente executa em um loop enquanto o cliente está conectado. Ela espera por mensagens do cliente e, em seguida, processa a mensagem e executa a ação apropriada. Se o cliente se desconectar, a thread do cliente é encerrada e removida da lista de threads de clientes na classe Server.

As funções receiveFile() e sendFile() são responsáveis por receber e enviar arquivos do e para o cliente, respectivamente. A função disconnect() é chamada quando o cliente se desconecta e é responsável por fechar o socket do cliente e parar a thread do cliente.

As exceções são tratadas de forma simples, apenas imprimindo o stack trace. Além disso, há algumas mensagens de log que são exibidas no console para ajudar na depuração e acompanhamento do fluxo do programa.*/

import java.io.*;
import java.net.*;
import java.util.*;

class Server {
    private ServerSocket servidor;
    private boolean executando = true;
    private List<ClientThread> clients = new ArrayList<>();

    public static void main(String[] arg) {
        Server s = new Server();
        s.start();
    }

    public void start() {
        try {
            servidor = new ServerSocket(8888);
            System.out.println("Servidor FTP iniciado na porta 8888.");
            while (executando) {
                Socket client = servidor.accept();
                System.out.println("Novo cliente conectado: " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
                ClientThread thread = new ClientThread(client, this);
                clients.add(thread);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        executando = false;
        for (ClientThread client : clients) {
            client.disconnect();
        }
        try {
            if (servidor != null) {
                servidor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void removeClient(ClientThread client) {
        clients.remove(client);
        if (clients.isEmpty()) {
            System.out.println("Todos os clientes desconectados. Encerrando o servidor.");
            stop();
        }
    }
}

class ClientThread extends Thread {
    private Socket client;
    private DataInputStream a;
    private DataOutputStream b;
    private FileOutputStream c;
    private File arquivo;
    private boolean executando = true;
    private Server servidor;

    public ClientThread(Socket client, Server servidor) {
        try {
            this.client = client;
            a = new DataInputStream(client.getInputStream());
            b = new DataOutputStream(client.getOutputStream());
            this.servidor = servidor;
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    public void run() {
        try {
            while (executando) {
                String input = a.readUTF();
                if (input.equals("ENVIO_DO_CLIENTE")) {
                    receiveFile();
                } else if (input.equals("BAIXA_ARQUIVO")) {
                    sendFile();
                } else {
                    System.out.println("Erro no servidor.");
                }
            }
        } catch (EOFException e) {
            // Client socket closed
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect();
            servidor.removeClient(this);
        }
    }

    private void receiveFile() throws IOException {
        String filename = a.readUTF();
        String filedata = a.readUTF();
        c = new FileOutputStream(filename);
        c.write(filedata.getBytes());
        c.close();
    }

    private void sendFile() throws IOException {
        String filename = a.readUTF();
        arquivo = new File(filename);
        if (arquivo.isFile()) {
            FileInputStream fis = new FileInputStream(arquivo);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            b.writeUTF(new String(data));
        } else {
            b.writeUTF(""); // Nenhum arquivo encontrado
        }
    }

    public void disconnect() {
        try {
            executando = false;
            if (client != null && !client.isClosed()) { // check if the client is not already closed
                System.out.println("Cliente " + client.getInetAddress().getHostAddress() + ":" + client.getPort() + " foi desconectado.");
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
