package Banco;

import java.io.*;
import java.net.Socket;

public class ThreadServinte implements Runnable {
    BufferedReader in;
    BufferedWriter out;
    Socket socket;
    Banco banco;

    public ThreadServinte(Socket s, Banco b){
        this.socket = s;
        this.banco = b;
    }
/*
* Este exemplo não temo os try/catch para ser legível como um exemplo
* Se os try/catch forem colocados o programa funciona*/
    @Override
    public void run() {
        //abrir as conexões para ler/escrever
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Receber um comando
        String linhainput = null, arg1 = null, arg2 = null, arg3 = null;
        int i=-1, j=-1; double d=-1;
        try {
            linhainput = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //processar um comando
        while(linhainput!=null){
            switch (linhainput){
                case "criarConta":
                    try {
                        arg1 = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    d = Double.parseDouble(arg1);
                    i = banco.criarConta(d);
                    try {
                        out.write("criarConta");//resposta ao cliente
                        out.newLine();
                        out.write(String.valueOf(i));
                        out.newLine();
                        out.flush();
                        System.out.println("criarConta feito");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("criarConta feito");
                    break;
                case "fecharConta":
                    try {
                        arg1 = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        banco.fecharConta(Integer.parseInt(arg1));
                    } catch (ContaInvalida contaInvalida) {
                            contaInvalida.printStackTrace();
                    }
                    try {
                        out.write("fecharConta");//resposta ao cliente
                        out.newLine();
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("fecharConta feito");
                    break;
                case "consultar":
                    try {
                        arg1 = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    i = Integer.parseInt(arg1);
                    d = -1;
                    try {

                        d=banco.consultar(i);
                    } catch (ContaInvalida contaInvalida) {
                        contaInvalida.printStackTrace();
                    }
                    try {
                        out.write("consultar");
                        out.newLine();
                        out.write(String.valueOf(i));//resposta ao cliente
                        out.newLine();
                        out.write(String.valueOf(d));
                        out.newLine();
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("consultar feito");
                    break;
                case "consultarTotal":
                    try {
                        arg1 = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int nargs = Integer.parseInt(arg1);
                    int [] arglist = new int [nargs];
                    for(i = 0; i<nargs; i++){
                        try {
                            arglist[i] = Integer.parseInt(in.readLine());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    d = 0;
                    try {
                        d = banco.consultarTotal(arglist);
                    } catch (ContaInvalida contaInvalida) {
                        contaInvalida.printStackTrace();
                    }
                    try {
                        out.write("consultarTotal");//resposta ao cliente
                        out.newLine();
                        out.write(String.valueOf(d));
                        out.newLine();
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("consultarTotal feito");
                    break;
                case "transferir":
                    try {
                        arg1 = in.readLine();
                        arg2 = in.readLine();
                        arg3 = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    d = Double.parseDouble(arg3) ;
                    i = Integer.parseInt(arg1);
                    j = Integer.parseInt(arg2);
                    try {
                        banco.transferir(i,j,d);
                    } catch (ContaInvalida contaInvalida) {
                        contaInvalida.printStackTrace();
                    } catch (SaldoInsuficiente saldoInsuficiente) {
                        saldoInsuficiente.printStackTrace();
                    }
                    try {
                        out.write( "transferir");//resposta ao cliente
                        out.newLine();
                        out.write(String.valueOf(d));
                        out.newLine();
                        out.write(String.valueOf(i));
                        out.newLine();
                        out.write(String.valueOf(j));
                        out.newLine();
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("transferir feito");
                    break;
                case "levantar":
                    try {
                        arg1 = in.readLine();
                        arg2 = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    d=Double.parseDouble(arg2);
                    i =Integer.parseInt(arg1);
                    try {
                        banco.levantar(i, d);
                    } catch (ContaInvalida contaInvalida) {
                        contaInvalida.printStackTrace();
                    } catch (SaldoInsuficiente saldoInsuficiente) {
                        saldoInsuficiente.printStackTrace();
                    }
                    try {
                        out.write("levantar");//resposta ao cliente
                        out.newLine();
                        out.write(String.valueOf(i));
                        out.newLine();
                        out.write(String.valueOf(d));
                        out.newLine();
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("levantar feito");
                    break;
                case "depositar":
                    try {
                        arg1 = in.readLine();
                        arg2 = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    d = Double.parseDouble(arg2);
                    i = Integer.parseInt(arg1);
                    try {
                        banco.depositar(i,d);
                    } catch (ContaInvalida contaInvalida) {
                        contaInvalida.printStackTrace();
                    }
                    try {
                        out.write("depositar");//resposta ao cliente
                        out.newLine();
                        out.write(String.valueOf(i));
                        out.newLine();
                        out.write(String.valueOf(d));
                        out.newLine();
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("depositar feito");
                    break;
                default:
                    break;
            }
        }
        //terminar a conexão
        try {
            socket.shutdownInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*
string values = "1,2,3,4,5,6,7,8,9,10";
string[] tokens = values.Split(',');

int[] convertedItems = Array.ConvertAll<string, int>(tokens, int.Parse);
 */