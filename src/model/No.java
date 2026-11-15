package model;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.util.Objects;
import java.io.Serializable;

/**
 *
 * @author sagaz
 * @param <T>
 */
public class No<T extends Comparable<T>> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private T no;
    private No<T> noEsquerdo;
    private No<T> noDireito;

    public No(T no) {
        this.no = no;
        this.noEsquerdo = null;
        this.noDireito = null;
    }

    public No() {
    }
    

    public T getNo() {return no;}

    public void setNo(T no) {this.no = no;}

    public No<T> getNoEsquerdo() {return noEsquerdo;}

    public void setNoEsquerdo(No<T> noEsquerdo) {this.noEsquerdo = noEsquerdo;}

    public No<T> getNoDireito() {return noDireito;}

    public void setNoDireito(No<T> noDireito) {this.noDireito = noDireito;}


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.no);
        hash = 89 * hash + Objects.hashCode(this.noEsquerdo);
        hash = 89 * hash + Objects.hashCode(this.noDireito);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final No<?> other = (No<?>) obj;
        if (!Objects.equals(this.no, other.no)) {
            return false;
        }
        if (!Objects.equals(this.noEsquerdo, other.noEsquerdo)) {
            return false;
        }
        return Objects.equals(this.noDireito, other.noDireito);
    }
    
    
    
}
