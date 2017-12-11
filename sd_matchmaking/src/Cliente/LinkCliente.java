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
    ...
     */
    Socket socket;
    BufferedReader in;
    BufferedWriter out;
    public int idConta;

    public LinkCliente() throws IOException{
        //criar socket no cliente
        socket = new Socket("127.0.0.1", 12345);
        //criar linha de input
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //criar linha para output
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        idConta=-1;
    }
    public void escreverComando(String comandoTotal) throws IOException {
        /*
        Cada comando é enviado para a ThreadServinte com orientação à linha.
        Cada palavra (métodos ou argumentos) é colocada numa linha, seguida com \newline

        Exemplo:
        comandoTotal - "login ChicoMarico 12345"
        comandoInvocado - "login"

         */
        String comandoInvocado = null;
        StringTokenizer stt = new StringTokenizer(comandoTotal," ");
        for (int i = 0; stt.hasMoreTokens(); i++) {
            String token = stt.nextToken();
            if (i == 0) comandoInvocado = token;
            out.write(token);
            out.newLine();
        }
        out.flush();
        System.out.println("Comando enviado: "+comandoTotal);
        /*
        Por simplicidade de código, todos os métodos devem ter uma resposta que vai ser interpretada no switch.
        A resposta pode ser apenas uma confirmação de que métodos void funcionaram normalmente
        Se não fizer sentido receber ou não estiver definida uma resposta da ThreadServinte,
        null será enviado e interpretado como default.
         */
        String resposta = null;
        resposta = in.readLine();
        System.out.println("Confirmação recebida: "+resposta);

        String arg1 = null, arg2 = null, arg3 = null, arg4 = null;
        int i = -1, j = -1; double d = -1;
        switch(resposta){
            //Métodos disponíveis:
            case "criarConta":
                //arg1 representa um booleano
                arg1 = in.readLine();
                if (arg1.equals("sim")) System.out.println("Conta criada com sucesso");
                else{
                    //lê o username
                    arg2 = in.readLine();
                    System.out.println("Já existe um jogador com o nome "+arg2);
                }
                break;
            case "login":
                arg1 = in.readLine();
                if (arg1.equals("sim")) System.out.println("Login com sucesso");
                else System.out.println("Login falhou");
                break;
            case "consultarRank":
                arg1 = in.readLine();
                i = Integer.parseInt(arg1);
                System.out.println("LinkCliente tem rank "+i);
                break;
            case "consultarPontos":
                arg1 = in.readLine();
                i = Integer.parseInt(arg1);
                System.out.println("LinkCliente tem "+i+" pontos no rank atual");
                break;
            case "logout":
                /*
                não ocorre o caso de falhar o fecho de sessão,
                porque se não estiver aberta não consegue invocar o método
                e não recebe resposta.
                 */
                System.out.println("Sessão terminada");
                break;
            case "jogar":
                arg1 = in.readLine();
                if (arg1.equals("sim")) System.out.println("A procurar uma equipa");
                else System.out.println("Não pode estar em mais que uma partida em simultâneo");
                break;
            case "sairQueue":
                arg1 = in.readLine();
                if (arg1.equals("sim")) System.out.println("Procura de equipa cancelada");
                else System.out.println("Não pode cancelar sem estar na queue");
                break;
            default:
                break;

        }
    }
    public void fecharSocket() throws IOException {
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
    }
}
