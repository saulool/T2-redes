package roteador;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Roteador {

    public static void main(String[] args) throws IOException {
        /* Lista de endereço IPs dos vizinhos */
        ArrayList<String> ip_list = new ArrayList<>();
        
        System.out.println("Digite seu ip:");
        Scanner scanner = new Scanner(System.in);
        String LOCALHOST = scanner.nextLine();
        
        /* Le arquivo de entrada com lista de IPs dos roteadores vizinhos. */
        try ( BufferedReader inputFile = new BufferedReader(new FileReader("IPVizinhos.txt"))) {
            String ip;
            
            while( (ip = inputFile.readLine()) != null){
                ip_list.add(ip);
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Roteador.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        /* Cria instâncias da tabela de roteamento e das threads de envio e recebimento de mensagens. */
        TabelaRoteamento tabela = new TabelaRoteamento(LOCALHOST, ip_list);
        Thread sender = new Thread(new MessageReceiver(tabela));
        Thread receiver = new Thread(new MessageSender(tabela, ip_list));
        
        sender.start();
        receiver.start();
        
        while(true){
            tabela.addTimeToDie();
            tabela.removeDeadLines();
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Roteador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
