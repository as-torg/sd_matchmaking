package Banco;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class Cliente {

    Socket socket;
    BufferedReader in;
    BufferedWriter out;
    public int idConta;

    public Cliente() throws IOException {
        //criar socket no cliente
        socket = new Socket("127.0.0.1", 12345);
        //criar linha de input
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //criar linha para output
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        idConta=-1;

    }

    public void escreverComando(String linha) throws IOException {
        StringTokenizer stt = new StringTokenizer(linha," ");
        while (stt.hasMoreTokens()) {
            String token = stt.nextToken();
            out.write(token);
            out.newLine();
        }
        out.flush();
        System.out.println("Comando enviado: "+linha);
        linha = in.readLine();
        System.out.println("Resposta recebida: "+linha);

        String arg1 = null, arg2 = null, arg3 = null, arg4 = null;
        int i = -1, j = -1; double d = -1;
        switch(linha){
            case "criarConta":
                arg1 = in.readLine();
                idConta = Integer.parseInt(arg1);
                System.out.println("Conta criada, id "+idConta);
                break;
            case "fecharConta":
                System.out.println("Conta fechada, id "+idConta);
                break;
            case "consultar":
                arg1 = in.readLine();
                arg2 = in.readLine();
                i = Integer.parseInt(arg1);
                d = Double.parseDouble(arg2);
                System.out.println("Conta "+i+" tem saldo "+d);
                break;
            case "consultarTotal":
                arg1 = in.readLine();
                d = Double.parseDouble(arg1);
                System.out.println("Saldo total "+d);
                break;
            case "levantar":
                arg1 = in.readLine();
                arg2 = in.readLine();
                i = Integer.parseInt(arg1);
                d = Double.parseDouble(arg2);
                System.out.println("Conta "+i+" levantou "+d);
                break;
            case "depositar":
                arg1 = in.readLine();
                arg2 = in.readLine();
                i = Integer.parseInt(arg1);
                d = Double.parseDouble(arg2);
                System.out.println("Conta "+i+" depositou "+d);
                break;
            case "transferir":
                arg1 = in.readLine();
                arg2 = in.readLine();
                arg3 = in.readLine();
                d = Double.parseDouble(arg1);
                i = Integer.parseInt(arg2);
                j = Integer.parseInt(arg3);
                System.out.println("Conta "+i+" transferiu "+d + " para "+j);
                break;
            default:
                break;

        }
    }
    public void fecharSocket() throws IOException {
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();    }
}
