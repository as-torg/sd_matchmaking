package Cliente;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class LinkCliente {
    /*
    Métodos disponíveis no cliente.
    São enviados para o servidor
    -pedir login
    -pedir logout
    -procurar partida (queue)
    -ver rank
    -jogar
    ...
     */

    public static void main(String [] args) throws IOException {
        Socket socket;
        BufferedReader in, stdin;
        BufferedWriter out;
        //criar socket no cliente
        socket = new Socket("127.0.0.1", 12345);
        //criar linha de input
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //criar linha para output
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        //abrir o stdinput
        stdin = new BufferedReader(new InputStreamReader(System.in));
        boolean sair = false;



        ParserCliente parser = new ParserCliente(in, socket, sair);
        Thread t = new Thread(parser);
        t.start();
        String comandoTotal;

        /*
        Cada comando é enviado para a ThreadServinte com orientação à linha.
        Cada palavra (métodos ou argumentos) é colocada numa só linha, seguida com \newline (ao fazer enter)

        Exemplo:
        comandoTotal - "login ChicoMarico 12345"
        comandoInvocado - "login"

         */
        System.out.println("Bem vindo. Faça login para começar...\n");
        while (!sair){
            comandoTotal = stdin.readLine();
            //envia o comando ao servidor, partido por palavras e separados com \n
            StringTokenizer stt = new StringTokenizer(comandoTotal," ");
            while(stt.hasMoreTokens()) {
                String token = stt.nextToken();
                out.write(token);
                out.newLine();
                break;
            }
            out.flush();
            System.out.println("Comando enviado: "+comandoTotal+"\nA aguardar resposta...\n");
            /*
            Por simplicidade de código, todos os métodos devem ter uma resposta que vai ser interpretada no switch.
            A resposta pode ser apenas uma confirmação de que métodos void funcionaram normalmente
            Se não fizer sentido receber ou não estiver definida uma resposta da ThreadServinte,
            null será enviado e interpretado como default.
            */
            /*
            String comando = null; //resposta do servidor, recebida via socket.
            comando = in.readLine();
            System.out.println("Confirmação recebida: "+comando);
            System.out.println("Introduza outro comando...\n");
            */
        }
        System.out.println("Adeus!");
    }
}
