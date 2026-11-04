/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import model.enums.TipoVeiculo;

/**
 *
 * @author sagaz
 */
public class Veiculo {
    private String matricula;
    private String marca;
    private String modelo;
    private Double capacidadeCarga;
    private TipoVeiculo tipoVeiculo;
    private Proprietario proprietario;

    public Veiculo(String matricula, String marca, String modelo, Double capacidadeCarga, TipoVeiculo tipoVeiculo, Proprietario proprietario) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.capacidadeCarga = capacidadeCarga;
        this.tipoVeiculo = tipoVeiculo;
        this.proprietario = proprietario;
    }


    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Double getCapacidadeCarga() {
        return capacidadeCarga;
    }

    public void setCapacidadeCarga(Double capacidadeCarga) {
        this.capacidadeCarga = capacidadeCarga;
    }

    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(TipoVeiculo tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }
    
    
    
}
