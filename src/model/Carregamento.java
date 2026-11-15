/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.io.Serializable;

/**
 *
 * @author sagaz
 */
public class Carregamento implements Comparable<Carregamento>, Serializable {
    private static final long serialVersionUID = 1L;
    private Long idCarregamento;
    private Cliente clienteSolicitador;
    private Veiculo veiculoUsado;
    private Double qtdMercadoria;
    private String localOrigem;
    private String localDestino;
    private LocalDate dataCarregamento;
    private Double custoCarregamento;

    public Carregamento(Long idCarregamento, Cliente clienteSolicitador, Veiculo veiculoUsado, Double qtdMercadoria, String localOrigem, String localDestino, LocalDate dataCarregamento, Double custoCarregamento) {
        this.idCarregamento = idCarregamento;
        this.clienteSolicitador = clienteSolicitador;
        this.veiculoUsado = veiculoUsado;
        this.qtdMercadoria = qtdMercadoria;
        this.localOrigem = localOrigem;
        this.localDestino = localDestino;
        this.dataCarregamento = dataCarregamento;
        this.custoCarregamento = custoCarregamento;
    }

    public Long getIdCarregamento() {
        return idCarregamento;
    }

    public void setIdCarregamento(Long idCarregamento) {
        this.idCarregamento = idCarregamento;
    }

    public Cliente getClienteSolicitador() {
        return clienteSolicitador;
    }

    public void setClienteSolicitador(Cliente clienteSolicitador) {
        this.clienteSolicitador = clienteSolicitador;
    }

    public Veiculo getVeiculoUsado() {
        return veiculoUsado;
    }

    public void setVeiculoUsado(Veiculo veiculoUsado) {
        this.veiculoUsado = veiculoUsado;
    }

    public Double getQtdMercadoria() {
        return qtdMercadoria;
    }

    public void setQtdMercadoria(Double qtdMercadoria) {
        this.qtdMercadoria = qtdMercadoria;
    }

    public String getLocalOrigem() {
        return localOrigem;
    }

    public void setLocalOrigem(String localOrigem) {
        this.localOrigem = localOrigem;
    }

    public String getLocalDestino() {
        return localDestino;
    }

    public void setLocalDestino(String localDestino) {
        this.localDestino = localDestino;
    }

    public LocalDate getDataCarregamento() {
        return dataCarregamento;
    }

    public void setDataCarregamento(LocalDate dataCarregamento) {
        this.dataCarregamento = dataCarregamento;
    }

    public Double getCustoCarregamento() {
        return custoCarregamento;
    }

    public void setCustoCarregamento(Double custoCarregamento) {
        this.custoCarregamento = custoCarregamento;
    }

    public Proprietario getProprietarioVeiculo() {
        return veiculoUsado == null ? null : veiculoUsado.getProprietario();
    }

    @Override
    public int compareTo(Carregamento other) {
        return Long.compare(this.idCarregamento, other.idCarregamento);
    }
    
    
    

}
