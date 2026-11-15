package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BinarySearchTree<T extends Comparable<T>> implements Serializable {
    private static final long serialVersionUID = 1L;

    public No<T> raiz;
    private int tamanho;

    public BinarySearchTree() {
        this.raiz = null;
        this.tamanho = 0;
    }

    public BinarySearchTree(No<T> raiz) {
        this.raiz = raiz;
        this.tamanho = contarNos(raiz);
    }

    public void insert(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Não é possível inserir um valor nulo.");
        }
        raiz = inserir(raiz, obj);
        tamanho++;
    }

    private No<T> inserir(No<T> no, T obj) {
        if (no == null) {
            return new No<>(obj);
        }
        if (obj.compareTo(no.getNo()) < 0) {
            no.setNoEsquerdo(inserir(no.getNoEsquerdo(), obj));
        } else {
            no.setNoDireito(inserir(no.getNoDireito(), obj));
        }
        return no;
    }

    public T find(T chave) {
        if (chave == null) {
            return null;
        }
        No<T> atual = raiz;
        while (atual != null) {
            int comparacao = chave.compareTo(atual.getNo());
            if (comparacao == 0) {
                return atual.getNo();
            }
            atual = comparacao < 0 ? atual.getNoEsquerdo() : atual.getNoDireito();
        }
        return null;
    }

    public boolean contains(T chave) {
        return find(chave) != null;
    }

    public boolean remove(T chave) {
        if (chave == null || !contains(chave)) {
            return false;
        }
        raiz = remover(raiz, chave);
        tamanho--;
        return true;
    }

    private No<T> remover(No<T> no, T chave) {
        if (no == null) {
            return null;
        }
        int comparacao = chave.compareTo(no.getNo());
        if (comparacao < 0) {
            no.setNoEsquerdo(remover(no.getNoEsquerdo(), chave));
        } else if (comparacao > 0) {
            no.setNoDireito(remover(no.getNoDireito(), chave));
        } else if (no.getNoEsquerdo() == null) {
            return no.getNoDireito();
        } else if (no.getNoDireito() == null) {
            return no.getNoEsquerdo();
        } else {
            No<T> sucessor = menorNo(no.getNoDireito());
            no.setNo(sucessor.getNo());
            no.setNoDireito(remover(no.getNoDireito(), sucessor.getNo()));
        }
        return no;
    }

    private No<T> menorNo(No<T> no) {
        while (no.getNoEsquerdo() != null) {
            no = no.getNoEsquerdo();
        }
        return no;
    }

    public List<T> toList() {
        List<T> elementos = new ArrayList<>(tamanho);
        percorrerEmOrdem(raiz, elementos);
        return elementos;
    }

    private void percorrerEmOrdem(No<T> no, List<T> elementos) {
        if (no != null) {
            percorrerEmOrdem(no.getNoEsquerdo(), elementos);
            elementos.add(no.getNo());
            percorrerEmOrdem(no.getNoDireito(), elementos);
        }
    }

    public int size() {
        return tamanho;
    }

    public boolean isEmpty() {
        return raiz == null;
    }

    private int contarNos(No<T> no) {
        return no == null ? 0 : 1 + contarNos(no.getNoEsquerdo()) + contarNos(no.getNoDireito());
    }
}
