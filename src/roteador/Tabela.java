/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roteador;

/**
 *
 * @author 10201796
 */
public class Tabela {
    public String ip_destino;
    public String ip_saida;
    public Integer metrica;
    
    public Tabela(String ip_destino, Integer metrica, String ip_saida){
        this.ip_destino = ip_destino;
        this.ip_saida = ip_saida;
        this.metrica = metrica;
    }

    public String getIp_destino() {
        return ip_destino;
    }

    public String getIp_saida() {
        return ip_saida;
    }

    public Integer getMetrica() {
        return metrica;
    }

    public void setIp_destino(String ip_destino) {
        this.ip_destino = ip_destino;
    }

    public void setIp_saida(String ip_saida) {
        this.ip_saida = ip_saida;
    }

    public void setMetrica(Integer metrica) {
        this.metrica = metrica;
    }
}
