/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package model.interfaces;

import java.util.ArrayList;
import model.ABP;

/**
 *
 * @author sagaz
 */
public interface DAOInterface {
   public <T> void guardar(String nomeFicheiro, ABP<T> arvore);
   public <T> ABP<T> carregar(String nomeFicheiro);
   public <T> void adicionar(String nomeFicheiro, T obj);
   public <T> void remover(String nomeFicheiro, Long id);
   public <T> void editar(String nomeFicheiro, Long id, T novoObj);
   
    
}
