package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

/*
Esta classe tem por objetivo filtrar as mensagens vindas do servidor em duas: notificações ou pedidos de input
Se for uma notificação, será feito print; se for um pedidode input do cliente será passado para cima (LinkCliente),
onde o switch vai ter os casos necessários para lidar com essas mensagens.

A necessidade desta thread surge ao querermos notificações assíncronas.
Por exemplo, enquanto não é encontrada uma partida, o cliente pode fazer outras coisas, ou até mesmo pedir para cancelar
a procura. Assim conseguimos estar a ler de dois sítios em simultâneo (o socket aqui, e o stdin em LinkCliente), e
podemos imprimir mensagens enquanto o cliente está a escrever comandos.

Na prática, o LinkCliente serve para ler do teclado, e o ParserCliente serve para imprimir coisas no terminal
 */

public class ParserCliente implements Runnable {
    private BufferedReader in;
    private Socket socket;
    private boolean sair;

    public ParserCliente(BufferedReader fromServidor, Socket socket, boolean sair){
        this.in=fromServidor;
        this.socket = socket;
        this.sair = sair;
    }

    @Override
    public void run() {

        String mensagem;
        while(!sair){
            try {
                mensagem = in.readLine();
                String arg1, arg2;
                int i;
            /*
            * TODAS AS OPÇÕES ABAIXO SÃO RESPOSTAS AOS COMANDOS INVOCADOS
            * por favor ninguém se ponha aqui a ler coisas, todas as leituras são feitas no link e enviadas para o servidor
            * as coisas lidas aqui são as respostas que chegam do socket (servidor)
            * */
                switch(mensagem){
                    //comandos disponíveis e respectivas respstas possíveis
                    case "criarConta":
                        //arg1 representa um booleano
                        arg1 = in.readLine();
                        if (arg1.equals("sim")) System.out.println("Conta criada com sucesso");
                        else{
                            //lê o username
                            arg2 = in.readLine();
                            System.out.println("Já existe um jogador com o nome "+arg2 +"");
                        }
                        break;
                    case "login":
                        arg1 = in.readLine();
                        if (arg1.equals("sim")) System.out.println("Login com sucesso\nIntroduza um comando...");
                        else System.out.println("Login falhou, tente novamente");
                        break;
                    case "consultarRank":
                        arg1 = in.readLine();
                        i = Integer.parseInt(arg1);
                        if(i>=0)System.out.println("O jogador tem rank "+i+"");
                        else System.out.println("O jogador não existe");
                        break;
                    case "consultarPontos":
                        arg1 = in.readLine();
                        i = Integer.parseInt(arg1);
                        if(i>=-100)System.out.println("O jogador tem "+i+" pontos");
                        else System.out.println("O jogador não existe");
                        break;
                    case "logout":
                          /*não ocorre o caso de falhar o fecho de sessão, porque se não estiver aberta
                            não consegue invocar o método e não recebe resposta.*/
                        socket.shutdownOutput();
                        socket.shutdownInput();
                        socket.close();
                        sair = true;
                        System.out.println("Sessão terminada");
                        break;
                    case "jogar":
                        System.out.println("Foi colocado em queue, aguarde por mais jogadores...");
                        break;
                    case "sairQueue":
                        arg1 = in.readLine();
                        if (arg1.equals("sim")) System.out.println("Procura de equipa cancelada");
                        else System.out.println("Não pode cancelar sem estar na queue");
                        break;
                    case "escolher":
                        //int escolha = ThreadLocalRandom.current().nextInt(0, 30 + 1);
                        System.out.println("Jogo encontrado. Escolha um champion indicando o número entre 0 e 30");
                        break;
                    case "outro":
                        System.out.println("Esse já foi escolhido, escolha outro"); //indica outro número
                        break;
                    case "escolhido":
                        System.out.println("Escolhido com sucesso");
                        /*a Partida está a esperar que o alarme toque e vai verificar se todos escolheram.*/
                        break;
                    case "ingame":
                        System.out.println("Já está num jogo, não pode participar noutro até o jogo atual terminar");
                        break;
                    case "inqueue":
                        System.out.println("Já está em queue, aguarde enquanto procuramos por mais jogadores");
                        break;
                    case "ganhou":
                        System.out.println("Ganhou a partida! PARABÉNS!");
                        break;
                    case "perdeu":
                        System.out.println("Perdeu a partida!");
                        break;
                    case "timeout":
                        System.out.println("Não escolheu nenhum champion. Foram-lhe retirados 20 pontos");
                        break;
                    case "nosessao":
                        System.out.println("Tem que fazer login");
                        break;
                    case "updateEquipa":
                        /*for(i=0;i<5;i++){
                            championsEquipa[i]=Integer.parseInt(in.readLine());
                        }
                        */
                        System.out.println("---Champion Select---");
                        System.out.println("   Jogador 1: "+Integer.parseInt(in.readLine()));
                        System.out.println("   Jogador 2: "+Integer.parseInt(in.readLine()));
                        System.out.println("   Jogador 3: "+Integer.parseInt(in.readLine()));
                        System.out.println("   Jogador 4: "+Integer.parseInt(in.readLine()));
                        System.out.println("   Jogador 5: "+Integer.parseInt(in.readLine()));
                        System.out.println("---------------------\n");
                        break;
                    case "start":
                        System.out.println("---Champion Select---");
                        System.out.println("Equipa 1: escolhas");
                        System.out.println("1:   "+in.readLine()+": "+Integer.parseInt(in.readLine()));
                        System.out.println("2:   "+in.readLine()+": "+Integer.parseInt(in.readLine()));
                        System.out.println("3:   "+in.readLine()+": "+Integer.parseInt(in.readLine()));
                        System.out.println("4:   "+in.readLine()+": "+Integer.parseInt(in.readLine()));
                        System.out.println("5:   "+in.readLine()+": "+Integer.parseInt(in.readLine()));
                        System.out.println("Equipa 2: escolhas");
                        System.out.println("1:   "+in.readLine()+": "+Integer.parseInt(in.readLine()));
                        System.out.println("2:   "+in.readLine()+": "+Integer.parseInt(in.readLine()));
                        System.out.println("3:   "+in.readLine()+": "+Integer.parseInt(in.readLine()));
                        System.out.println("4:   "+in.readLine()+": "+Integer.parseInt(in.readLine()));
                        System.out.println("5:   "+in.readLine()+": "+Integer.parseInt(in.readLine()));
                        System.out.println("---------------------\n");

                        break;
                    default:
                        System.out.println("Mensagem desconhecida: "+mensagem);
                        break;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
