package Cliente;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;

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
        StringTokenizer stt = new StringTokenizer(comandoTotal," ");
        while(stt.hasMoreTokens()) {
            String token = stt.nextToken();
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
        String comando = null;
        comando = in.readLine();
        System.out.println("Confirmação recebida: "+comando);
        String arg1 = null, arg2 = null, arg3 = null, arg4 = null;
        int i = -1, j = -1; double d = -1;

        /*
        * TODAS AS OPÇÕES ABAIXO SÃO RESPOSTAS AOS COMANDOS INVOCADOS
        * por favor ninguém se ponha aqui a ler coisas, todas as leituras são feitas por escreverComando()
        * as coisas lidas aqui são as respostas que chegam do socket
        * */
        switch(comando){
            //comandos disponíveis e respectivas respstas possíveis
            case "criarConta":
                //arg1 representa um booleano
                arg1 = in.readLine();
                if (arg1.equals("sim")) System.out.println("Conta criada com sucesso\n");
                else{
                    //lê o username
                    arg2 = in.readLine();
                    System.out.println("Já existe um jogador com o nome "+arg2 +"\n");
                }
                break;
            case "login":
                arg1 = in.readLine();
                if (arg1.equals("sim")) System.out.println("Login com sucesso\n");
                else System.out.println("Login falhou\n");
                break;
            case "consultarRank":
                arg1 = in.readLine();
                i = Integer.parseInt(arg1);
                System.out.println("LinkCliente tem rank "+i+"\n");
                break;
            case "consultarPontos":
                arg1 = in.readLine();
                i = Integer.parseInt(arg1);
                System.out.println("LinkCliente tem "+i+" pontos no rank atual\n");
                break;
            case "logout":
                /*não ocorre o caso de falhar o fecho de sessão, porque se não estiver aberta
                não consegue invocar o método e não recebe resposta.*/
                System.out.println("Sessão terminada\n");
                break;
            case "jogar":
                arg1 = in.readLine();
                if (arg1.equals("sim")) System.out.println("A procurar uma equipa\n");
                else System.out.println("Não pode estar em mais que uma partida em simultâneo\n");
                break;
            case "sairQueue":
                arg1 = in.readLine();
                if (arg1.equals("sim")) System.out.println("Procura de equipa cancelada\n");
                else System.out.println("Não pode cancelar sem estar na queue\n");
                break;
            case "escolher":
                //int escolha = ThreadLocalRandom.current().nextInt(0, 30 + 1);
                System.out.println("Escolha um championindicando o número entre 0 e 30\n");
                break;
            case "outro":
                System.out.println("Esse já foi escolhido, escolha outro\n"); //indica outro número
                break;
            case"escolhido":
                System.out.println("Escolhido com sucesso, aguarde...\n");
                /*a Partida está a esperar que o alarme toque e vai verificar se todos escolheram.*/
                break;
            case"ingame":
                System.out.println("Já está num jogo, não pode participar noutro até o jogo atual terminar\n");
                break;
            case "inqueue":
                System.out.println("Já está em queue, aguarde por mais jogadores\n");
                break;
            case "ganhou":
                System.out.println("Ganhou a partida! PARABÉNS!\n");
                break;
            case "perdeu":
                System.out.println("Perdeu a partida! PARABÉNS!\n"); //lol
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
