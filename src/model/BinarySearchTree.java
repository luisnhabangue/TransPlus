/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author sagaz
 */
public class BinarySearchTree<T extends Comparable<T>> {

    public No<T> raiz;

    public BinarySearchTree() {
            this.raiz = null;

    }
    

    public BinarySearchTree(No<T> raiz) {
        this.raiz = raiz;
    }

    public void insert(T obj) {

        if (obj == null) {
            return;
        }

        this.raiz = recursiveInsertion(this.raiz, obj);

    }

    private No<T> recursiveInsertion(No<T> no, T obj) {
//Este e o caso base, se o no estiver vazio, o objecto sera inserido la
        if (no == null) {
            return new No<T>(obj);
        }

        int comparacaoNos = no.getNo().compareTo(obj);
        
        if(comparacaoNos<0) {
            return recursiveInsertion(no.getNoEsquerdo(),obj);
        }
        
          if(comparacaoNos>=0) {
            return recursiveInsertion(no.getNoDireito(),obj);
        }
   
          return no;
        

    }

    private void remove(T obj){

        //o metodo para quando os filhos, direto e esquerdo forem nulos
        if(obj==null) return;
        
    
 
 
    }
    public boolean isEmpty() {
        return this.raiz == null;
    }

}
