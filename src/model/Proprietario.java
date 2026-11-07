/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sagaz
 */
public class Proprietario implements Comparable<Proprietario>{
  //  private Long id;
    private String documentoId;
    private String nome;
    private String contacto;
    
    //Aqui iremos implementar a nossa TAD para guardar os veiculos do proprietario
    private List<Veiculo> veiculos;

    public Proprietario(String documentoId, String nome, String contacto) {
       // this.id =id;
        this.documentoId = documentoId;
        this.nome = nome;
        this.contacto = contacto;
        this.veiculos = new ArrayList<>();
    }
    

    public List<Veiculo> getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(List<Veiculo> veiculos) {
        this.veiculos = veiculos;
    }
    

    public String getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(String documentoId) {
        this.documentoId = documentoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    @Override
    public int compareTo(Proprietario o) {
        return this.documentoId.compareTo(o.documentoId);
        
        }
    
    
}
