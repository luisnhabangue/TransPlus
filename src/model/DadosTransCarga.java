package model;

import java.io.Serializable;

public class DadosTransCarga implements Serializable {
    private static final long serialVersionUID = 1L;

    private final BinarySearchTree<Proprietario> proprietarios = new BinarySearchTree<>();
    private final BinarySearchTree<Veiculo> veiculos = new BinarySearchTree<>();
    private final BinarySearchTree<Cliente> clientes = new BinarySearchTree<>();
    private final BinarySearchTree<Carregamento> carregamentos = new BinarySearchTree<>();
    private long proximoIdCliente = 1;
    private long proximoIdCarregamento = 1;

    public BinarySearchTree<Proprietario> getProprietarios() {
        return proprietarios;
    }

    public BinarySearchTree<Veiculo> getVeiculos() {
        return veiculos;
    }

    public BinarySearchTree<Cliente> getClientes() {
        return clientes;
    }

    public BinarySearchTree<Carregamento> getCarregamentos() {
        return carregamentos;
    }

    public long gerarIdCliente() {
        return proximoIdCliente++;
    }

    public long gerarIdCarregamento() {
        return proximoIdCarregamento++;
    }
}
