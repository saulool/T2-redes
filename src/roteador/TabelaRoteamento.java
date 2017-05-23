package roteador;

import java.net.InetAddress;
import java.util.ArrayList;

public class TabelaRoteamento {
    /*Implemente uma estrutura de dados para manter a tabela de roteamento. 
     * A tabela deve possuir: IP Destino, Métrica e IP de Saída.
    */
    
    ArrayList<Tabela> tabelaRoteamento;
    String LOCALHOST;
    ArrayList<String> NEIGHBORS = new ArrayList<>();
    
    public TabelaRoteamento(String LOCALHOST, ArrayList<String> neighbors){
        tabelaRoteamento = new ArrayList<Tabela>();
        this.LOCALHOST = LOCALHOST;
        this.NEIGHBORS = neighbors;
    }
    
    public void update_tabela(String tabela_s,  InetAddress IPAddress){
        /* Atualize a tabela de rotamento a partir da string recebida. */
        
        ArrayList<Tabela> decodedTable = new ArrayList<Tabela>();
        
        //Limpa bits vazios
        tabela_s = tabela_s.split("")[0];
        
        System.out.println("Recebendo mensagem: " + tabela_s);
        
        //Se for mensagem de novo host, cria uma nova linha na tabela
        if(tabela_s.equals("!")){
            Tabela newLine = new Tabela(IPAddress.getHostAddress(), 1, IPAddress.getHostAddress());
            decodedTable.add(newLine); 
        }else{
            decodedTable = decodeTable(tabela_s, IPAddress);
            
            resetTimeToDie(IPAddress.getHostAddress());
            
            if(!addressExistsInTable(IPAddress.getHostAddress())){
                Tabela newLine = new Tabela(IPAddress.getHostAddress(), 1, IPAddress.getHostAddress());
                decodedTable.add(newLine); 
            }
        }
        
        for (Tabela decodedLine : decodedTable) {            
            if(addressExistsInTable(decodedLine.getIp_destino())){
                if(metricIsLower(decodedLine)){
                    updateTableLine(decodedLine);
                }
            }else{
                if(!itsMyAddress(decodedLine)){
                    if(!isMyNeighbor(decodedLine.getIp_saida())){
                        decodedLine.setMetrica(decodedLine.getMetrica()+1);
                    }
                    
                    tabelaRoteamento.add(decodedLine);
                }
            }
        }
        
        System.out.println("Minha tabela:\n");
        System.out.println(printTable(tabelaRoteamento));
    }
    
    private Boolean isMyNeighbor(String ip){
        for (String neighbor : NEIGHBORS) {
            if(neighbor.equals(ip)) return true;
        }
        
        return false;
    }
    
    private Boolean itsMyAddress(Tabela newLine){
        return newLine.getIp_destino().equals(LOCALHOST);
    }
    
    private void updateTableLine(Tabela newLine){
        for (Tabela tabelaRot : tabelaRoteamento) {
            if(tabelaRot.getIp_destino().equals(newLine.getIp_destino())){
                tabelaRot.setMetrica(newLine.getMetrica());
            }
        }
    }
    
    private Boolean metricIsLower(Tabela newLine){
        for (Tabela tabelaRot : tabelaRoteamento) {
            if(tabelaRot.getIp_destino().equals(newLine.getIp_destino()) && tabelaRot.getMetrica() > newLine.getMetrica()){
                return true;
            }
        }
        
        return false;
    }
    
    private Boolean addressExistsInTable(String ip){
        for (Tabela tabelaRot : tabelaRoteamento) {
            if(tabelaRot.getIp_destino().equals(ip)){
                return true;
            }
        }
        
        return false;
    }
    
    public String get_tabela_string(){
        String tabela_string = encodeTable(tabelaRoteamento); /* Tabela de roteamento vazia conforme especificado no protocolo *        
        /* Converta a tabela de rotamento para string, conforme formato definido no protocolo . */
        
        System.out.println("Enviando mensagem: " + tabela_string);
        
        return tabela_string;
    }
    
    private ArrayList<Tabela> decodeTable(String table, InetAddress IPAddress){
        String[] linhas = table.split("\\*");
        ArrayList<Tabela> decodedTable = new ArrayList<Tabela>();
        
        for(int i=1; i < linhas.length; i++){
            String ip_destino = linhas[i].split(";")[0];
            Integer metrica = Integer.parseInt(linhas[i].split(";")[1]);
            
            Tabela newLine = new Tabela(ip_destino, metrica, IPAddress.getHostAddress());
            
            decodedTable.add(newLine);
        }
        
        return decodedTable;
    }
    
    private String encodeTable(ArrayList<Tabela> tabelaRoteamento){
        String output = "";
        
        if(tabelaRoteamento.isEmpty())
            return "!";
        
        for (Tabela tabelaRot : tabelaRoteamento) {
            output += "*" + tabelaRot.getIp_destino() + ";" + tabelaRot.getMetrica();
        }
        
        return output;
    }
    
    private StringBuilder printTable(ArrayList<Tabela> tabelaRoteamento){
        StringBuilder output = new StringBuilder();
        
        output.append("+--------------+---------+--------------+\n");
        output.append("|  IP Destino  | Métrica |    Origem    |\n");
        output.append("+--------------+---------+--------------+\n");                
                
        for (Tabela tabela : tabelaRoteamento) {
            output.append("| ");   
            output.append(tabela.getIp_destino());
            output.append(" |    ");
            output.append(tabela.getMetrica());
            output.append("    | "); 
            output.append(tabela.getIp_saida());
            output.append(" |\n");
        }
        
        output.append("+--------------+---------+--------------+\n");
        
        return output;
    }
    
    public void addTimeToDie(){
        for (Tabela line : tabelaRoteamento) {
            line.setTimeToDie(line.getTimeToDie()+1);
        }
    }
    
    private void resetTimeToDie(String ip){
        for (Tabela line : tabelaRoteamento) {
            if(line.getIp_saida().equals(ip))
                line.setTimeToDie(0);
        }
    }
    
    public void removeDeadLines(){
        ArrayList<Tabela> linesToDie = new ArrayList<Tabela>();
        
        for (Tabela line : tabelaRoteamento) {
            if(line.getTimeToDie() == 3)
                linesToDie.add(line);
        }
        
        for (Tabela line : linesToDie) {
            tabelaRoteamento.remove(line);
            System.out.println("Removendo linha: " + line.getIp_destino() + " " + line.getMetrica() + " " + line.getIp_saida() + "\n");
        }
        
    }
}
