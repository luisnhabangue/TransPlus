package service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Carregamento;
import model.Cliente;
import model.DadosTransCarga;
import model.Proprietario;
import model.Veiculo;
import model.enums.TipoVeiculo;
import persistence.ArquivoDadosTransCargaDAO;

public class TransCargaService {
    private final Path ficheiroDados;
    private final ArquivoDadosTransCargaDAO dao;
    private final DadosTransCarga dados;

    public TransCargaService(Path ficheiroDados) throws IOException, ClassNotFoundException {
        if (ficheiroDados == null) {
            throw new IllegalArgumentException("O caminho do ficheiro de dados é obrigatório.");
        }
        this.ficheiroDados = ficheiroDados;
        this.dao = new ArquivoDadosTransCargaDAO();
        this.dados = dao.carregar(ficheiroDados);
    }

    public Proprietario registarProprietario(String documentoId, String nome, String contacto)
            throws IOException {
        exigirTexto(documentoId, "Documento de identificação");
        exigirTexto(nome, "Nome");
        exigirTexto(contacto, "Contacto");
        Proprietario chave = chaveProprietario(documentoId);
        if (dados.getProprietarios().contains(chave)) {
            throw new IllegalArgumentException("Já existe um proprietário com esse documento.");
        }
        Proprietario proprietario = new Proprietario(documentoId.trim(), nome.trim(), contacto.trim());
        dados.getProprietarios().insert(proprietario);
        guardar();
        return proprietario;
    }

    public void actualizarProprietario(String documentoId, String nome, String contacto)
            throws IOException {
        exigirTexto(nome, "Nome");
        exigirTexto(contacto, "Contacto");
        Proprietario proprietario = obterProprietario(documentoId);
        proprietario.setNome(nome.trim());
        proprietario.setContacto(contacto.trim());
        guardar();
    }

    public Veiculo registarVeiculo(Veiculo veiculo) throws IOException {
        validarVeiculo(veiculo);
        if (obterVeiculoOpcional(veiculo.getMatricula()) != null) {
            throw new IllegalArgumentException("Já existe um veículo com essa matrícula.");
        }
        Proprietario proprietario = obterProprietario(veiculo.getProprietario().getDocumentoId());
        veiculo.setMatricula(veiculo.getMatricula().trim());
        veiculo.setProprietario(proprietario);
        proprietario.adicionarVeiculo(veiculo);
        dados.getVeiculos().insert(veiculo);
        guardar();
        return veiculo;
    }

    public void actualizarVeiculo(String matricula, Veiculo dadosActualizados) throws IOException {
        Veiculo veiculo = obterVeiculo(matricula);
        validarVeiculo(dadosActualizados);
        if (!veiculo.getMatricula().equalsIgnoreCase(dadosActualizados.getMatricula().trim())) {
            throw new IllegalArgumentException("A matrícula é a identificação do veículo e não pode ser alterada.");
        }
        Proprietario novoProprietario = obterProprietario(
                dadosActualizados.getProprietario().getDocumentoId());
        for (Carregamento carregamento : listarCarregamentos()) {
            if (carregamento.getVeiculoUsado().getMatricula().equalsIgnoreCase(veiculo.getMatricula())
                    && carregamento.getQtdMercadoria() > dadosActualizados.getCapacidadeCarga()) {
                throw new IllegalArgumentException(
                        "A nova capacidade é inferior à quantidade registada num transporte deste veículo.");
            }
        }

        Proprietario proprietarioAnterior = veiculo.getProprietario();
        if (proprietarioAnterior != novoProprietario) {
            proprietarioAnterior.removerVeiculo(veiculo);
            novoProprietario.adicionarVeiculo(veiculo);
        }
        dados.getVeiculos().remove(veiculo);
        veiculo.setMarca(dadosActualizados.getMarca().trim());
        veiculo.setModelo(dadosActualizados.getModelo().trim());
        veiculo.setCapacidadeCarga(dadosActualizados.getCapacidadeCarga());
        veiculo.setTipoVeiculo(dadosActualizados.getTipoVeiculo());
        veiculo.setProprietario(novoProprietario);
        dados.getVeiculos().insert(veiculo);
        guardar();
    }

    public Cliente registarCliente(String nome, String contacto, String localizacao) throws IOException {
        exigirTexto(nome, "Nome");
        exigirTexto(contacto, "Contacto");
        exigirTexto(localizacao, "Localização");
        Cliente cliente = new Cliente(dados.gerarIdCliente(), nome.trim(), contacto.trim(),
                localizacao.trim());
        dados.getClientes().insert(cliente);
        guardar();
        return cliente;
    }

    public void actualizarCliente(Long id, String nome, String contacto, String localizacao)
            throws IOException {
        exigirTexto(nome, "Nome");
        exigirTexto(contacto, "Contacto");
        exigirTexto(localizacao, "Localização");
        Cliente cliente = obterCliente(id);
        cliente.setNome(nome.trim());
        cliente.setContacto(contacto.trim());
        cliente.setLocalizacao(localizacao.trim());
        guardar();
    }

    public Carregamento registarCarregamento(Long idCliente, String matriculaVeiculo,
            double quantidade, String localOrigem, String localDestino, LocalDate data,
            double valorCobrado) throws IOException {
        validarQuantidade(quantidade);
        exigirTexto(localOrigem, "Local de origem");
        exigirTexto(localDestino, "Destino");
        if (data == null) {
            throw new IllegalArgumentException("A data do carregamento é obrigatória.");
        }
        validarValor(valorCobrado);

        Cliente cliente = obterCliente(idCliente);
        Veiculo veiculo = obterVeiculo(matriculaVeiculo);
        if (quantidade > veiculo.getCapacidadeCarga()) {
            throw new IllegalArgumentException("A quantidade excede a capacidade máxima do veículo.");
        }
        Carregamento carregamento = new Carregamento(dados.gerarIdCarregamento(), cliente, veiculo,
                quantidade, localOrigem.trim(), localDestino.trim(), data, valorCobrado);
        dados.getCarregamentos().insert(carregamento);
        guardar();
        return carregamento;
    }

    public List<Veiculo> listarVeiculos(TipoVeiculo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo de veículo é obrigatório.");
        }
        List<Veiculo> resultado = new ArrayList<>();
        for (Veiculo veiculo : dados.getVeiculos().toList()) {
            if (veiculo.getTipoVeiculo() == tipo) {
                resultado.add(veiculo);
            }
        }
        return resultado;
    }

    public List<Veiculo> listarVeiculos() {
        return dados.getVeiculos().toList();
    }

    public List<Carregamento> listarCarregamentos() {
        return dados.getCarregamentos().toList();
    }

    public List<Carregamento> listarCarregamentosDoProprietario(String documentoId) {
        Proprietario proprietario = obterProprietario(documentoId);
        List<Carregamento> resultado = new ArrayList<>();
        for (Carregamento carregamento : listarCarregamentos()) {
            if (carregamento.getVeiculoUsado().getProprietario().compareTo(proprietario) == 0) {
                resultado.add(carregamento);
            }
        }
        return resultado;
    }

    public BigDecimal totalArrecadadoPorVeiculo(String matricula) {
        Veiculo veiculo = obterVeiculo(matricula);
        BigDecimal total = BigDecimal.ZERO;
        for (Carregamento carregamento : listarCarregamentos()) {
            if (carregamento.getVeiculoUsado().getMatricula().equalsIgnoreCase(veiculo.getMatricula())) {
                total = total.add(BigDecimal.valueOf(carregamento.getCustoCarregamento()));
            }
        }
        return total;
    }

    public Proprietario obterProprietario(String documentoId) {
        exigirTexto(documentoId, "Documento de identificação");
        Proprietario proprietario = dados.getProprietarios().find(chaveProprietario(documentoId));
        if (proprietario == null) {
            throw new IllegalArgumentException("Proprietário não encontrado: " + documentoId);
        }
        return proprietario;
    }

    public Veiculo obterVeiculo(String matricula) {
        exigirTexto(matricula, "Matrícula");
        Veiculo veiculo = obterVeiculoOpcional(matricula);
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo não encontrado: " + matricula);
        }
        return veiculo;
    }

    private Veiculo obterVeiculoOpcional(String matricula) {
        if (matricula == null) {
            return null;
        }
        for (Veiculo veiculo : dados.getVeiculos().toList()) {
            if (veiculo.getMatricula().equalsIgnoreCase(matricula.trim())) {
                return veiculo;
            }
        }
        return null;
    }

    public Cliente obterCliente(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador do cliente é obrigatório.");
        }
        Cliente cliente = dados.getClientes().find(new Cliente(id, "", "", ""));
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado: " + id);
        }
        return cliente;
    }

    public List<Proprietario> listarProprietarios() {
        return dados.getProprietarios().toList();
    }

    public List<Cliente> listarClientes() {
        return dados.getClientes().toList();
    }

    private void guardar() throws IOException {
        dao.guardar(ficheiroDados, dados);
    }

    private Proprietario chaveProprietario(String documentoId) {
        return new Proprietario(documentoId.trim(), "", "");
    }

    private void validarVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new IllegalArgumentException("Os dados do veículo são obrigatórios.");
        }
        exigirTexto(veiculo.getMatricula(), "Matrícula");
        exigirTexto(veiculo.getMarca(), "Marca");
        exigirTexto(veiculo.getModelo(), "Modelo");
        if (veiculo.getTipoVeiculo() == null) {
            throw new IllegalArgumentException("O tipo de veículo é obrigatório.");
        }
        if (veiculo.getProprietario() == null) {
            throw new IllegalArgumentException("O proprietário do veículo é obrigatório.");
        }
        validarQuantidade(veiculo.getCapacidadeCarga());
    }

    private void validarQuantidade(double quantidade) {
        if (Double.isNaN(quantidade) || Double.isInfinite(quantidade) || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade/capacidade deve ser um número positivo.");
        }
    }

    private void validarValor(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0) {
            throw new IllegalArgumentException("O valor cobrado deve ser um número não negativo.");
        }
    }

    private void exigirTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
    }
}
