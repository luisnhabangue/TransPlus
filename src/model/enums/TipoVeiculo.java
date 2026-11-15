/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package model.enums;

/**
 *
 * @author sagaz
 */
public enum TipoVeiculo {
    Camiao("Camião"),
    Carrinha("Carrinha de Carga");
    
    private final String rotulo;

    private TipoVeiculo(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
