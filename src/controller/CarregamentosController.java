package controller;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import model.Carregamento;
import model.Cliente;
import model.Proprietario;
import model.Veiculo;
import service.TransCargaService;

public class CarregamentosController {
    @FXML private Label lblQtdTransportes;
    @FXML private Label lblTotalMercadoria;
    @FXML private Label lblTotalArrecadado;
    @FXML private Label lblBadgeCarregamentos;
    @FXML private ComboBox<Cliente> cmbClienteCarregamento;
    @FXML private ComboBox<Veiculo> cmbVeiculoCarregamento;
    @FXML private TextField txtProprietarioAuto;
    @FXML private TextField txtQuantidadeCarga;
    @FXML private TextField txtOrigem;
    @FXML private TextField txtDestino;
    @FXML private HBox boxAlertaCapacidade;
    @FXML private Label lblMensagemAlerta;
    @FXML private DatePicker dpDataCarregamento;
    @FXML private TextField txtValorCobrado;
    @FXML private Button btnRegistarCarregamento;
    @FXML private TextField txtPesquisarCarregamento;
    @FXML private ComboBox<Proprietario> cmbFiltroProprietario;
    @FXML private TableView<Carregamento> tabelaCarregamentos;
    @FXML private TableColumn<Carregamento, String> colCarrData;
    @FXML private TableColumn<Carregamento, String> colCarrCliente;
    @FXML private TableColumn<Carregamento, String> colCarrVeiculo;
    @FXML private TableColumn<Carregamento, String> colCarrProprietario;
    @FXML private TableColumn<Carregamento, String> colCarrCarga;
    @FXML private TableColumn<Carregamento, String> colCarrRota;
    @FXML private TableColumn<Carregamento, String> colCarrValor;

    private TransCargaService service;
    private final ObservableList<Carregamento> carregamentos = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        tabelaCarregamentos.setItems(carregamentos);
        dpDataCarregamento.setValue(LocalDate.now());
        ControllerSupport.configurarCombo(cmbClienteCarregamento, Cliente::getNome);
        ControllerSupport.configurarCombo(cmbVeiculoCarregamento, v -> v.getMatricula() + " · "
                + v.getTipoVeiculo().getRotulo() + " · " + ControllerSupport.formatar(v.getCapacidadeCarga()) + " t");
        ControllerSupport.configurarCombo(cmbFiltroProprietario, Proprietario::getNome);
        colCarrData.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDataCarregamento().format(DATA)));
        colCarrCliente.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getClienteSolicitador().getNome()));
        colCarrVeiculo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getVeiculoUsado().getMatricula()));
        colCarrProprietario.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getProprietarioVeiculo().getNome()));
        colCarrCarga.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                ControllerSupport.formatar(c.getValue().getQtdMercadoria())));
        colCarrRota.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getLocalOrigem() + " → " + c.getValue().getLocalDestino()));
        colCarrValor.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                dinheiro(c.getValue().getCustoCarregamento())));
        txtPesquisarCarregamento.textProperty().addListener((o, anterior, novo) -> actualizarTabela());
        txtQuantidadeCarga.textProperty().addListener((o, anterior, novo) -> validarCapacidadeEmTempoReal());
        cmbVeiculoCarregamento.valueProperty().addListener((o, anterior, novo) -> {
            txtProprietarioAuto.setText(novo == null ? "" : novo.getProprietario().getNome());
            validarCapacidadeEmTempoReal();
        });
    }

    public void setService(TransCargaService service) {
        this.service = service;
        cmbClienteCarregamento.setItems(FXCollections.observableArrayList(service.listarClientes()));
        cmbVeiculoCarregamento.setItems(FXCollections.observableArrayList(service.listarVeiculos()));
        cmbFiltroProprietario.setItems(FXCollections.observableArrayList(service.listarProprietarios()));
        actualizarDados();
    }

    @FXML
    private void aoSelecionarVeiculo() {
        Veiculo selecionado = cmbVeiculoCarregamento.getValue();
        txtProprietarioAuto.setText(selecionado == null ? "" : selecionado.getProprietario().getNome());
        validarCapacidadeEmTempoReal();
    }

    @FXML
    private void registarCarregamento() {
        try {
            if (cmbClienteCarregamento.getValue() == null) {
                throw new IllegalArgumentException("Seleccione um cliente.");
            }
            if (cmbVeiculoCarregamento.getValue() == null) {
                throw new IllegalArgumentException("Seleccione um veículo.");
            }
            double quantidade = ControllerSupport.numero(txtQuantidadeCarga, "Quantidade");
            double valor = ControllerSupport.numero(txtValorCobrado, "Valor cobrado");
            Veiculo veiculo = cmbVeiculoCarregamento.getValue();
            service.registarCarregamento(cmbClienteCarregamento.getValue().getId(),
                    veiculo.getMatricula(), quantidade, txtOrigem.getText(), txtDestino.getText(),
                    dpDataCarregamento.getValue(), valor);
            limparFormulario();
            actualizarDados();
        } catch (IOException | IllegalArgumentException e) {
            ControllerSupport.mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void filtrarPorProprietario() {
        actualizarTabela();
    }

    @FXML
    private void limparFiltroProprietario() {
        cmbFiltroProprietario.setValue(null);
        actualizarTabela();
    }

    @FXML
    private void limparFormulario() {
        cmbClienteCarregamento.setValue(null);
        cmbVeiculoCarregamento.setValue(null);
        txtProprietarioAuto.clear();
        txtQuantidadeCarga.clear();
        txtOrigem.clear();
        txtDestino.clear();
        txtValorCobrado.clear();
        dpDataCarregamento.setValue(LocalDate.now());
        esconderAlerta();
    }

    private void validarCapacidadeEmTempoReal() {
        Veiculo selecionado = cmbVeiculoCarregamento.getValue();
        boolean excede = false;
        double quantidade = 0;
        if (selecionado != null && txtQuantidadeCarga.getText() != null
                && !txtQuantidadeCarga.getText().isBlank()) {
            try {
                quantidade = Double.parseDouble(txtQuantidadeCarga.getText().trim().replace(',', '.'));
                excede = Double.isFinite(quantidade) && quantidade > selecionado.getCapacidadeCarga();
            } catch (NumberFormatException ignored) {
                excede = false;
            }
        }
        if (excede) {
            lblMensagemAlerta.setText("Aviso: A carga (" + ControllerSupport.formatar(quantidade)
                    + " t) ultrapassa a capacidade máxima do veículo selecionado ("
                    + ControllerSupport.formatar(selecionado.getCapacidadeCarga()) + " t).");
            boxAlertaCapacidade.setVisible(true);
            boxAlertaCapacidade.setManaged(true);
            btnRegistarCarregamento.setDisable(true);
        } else {
            esconderAlerta();
        }
    }

    private void esconderAlerta() {
        boxAlertaCapacidade.setVisible(false);
        boxAlertaCapacidade.setManaged(false);
        btnRegistarCarregamento.setDisable(false);
    }

    private String dinheiro(double valor) {
        return NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-MZ")).format(valor);
    }

    private void actualizarDados() {
        if (service == null) return;
        List<Carregamento> lista = service.listarCarregamentos();
        double carga = lista.stream().mapToDouble(Carregamento::getQtdMercadoria).sum();
        double receita = lista.stream().mapToDouble(Carregamento::getCustoCarregamento).sum();
        lblQtdTransportes.setText(String.valueOf(lista.size()));
        lblBadgeCarregamentos.setText(String.valueOf(lista.size()));
        lblTotalMercadoria.setText(ControllerSupport.formatar(carga));
        lblTotalArrecadado.setText(dinheiro(receita));
        actualizarTabela();
    }

    private void actualizarTabela() {
        if (service == null) return;
        List<Carregamento> lista;
        Proprietario proprietario = cmbFiltroProprietario.getValue();
        if (proprietario == null) {
            lista = service.listarCarregamentos();
        } else {
            lista = service.listarCarregamentosDoProprietario(proprietario.getDocumentoId());
        }
        String termo = txtPesquisarCarregamento.getText() == null ? ""
                : txtPesquisarCarregamento.getText().trim().toLowerCase(Locale.ROOT);
        carregamentos.setAll(lista.stream().filter(c ->
                (c.getClienteSolicitador().getNome() + " " + c.getVeiculoUsado().getMatricula()
                        + " " + c.getProprietarioVeiculo().getNome() + " " + c.getLocalOrigem()
                        + " " + c.getLocalDestino()).toLowerCase(Locale.ROOT).contains(termo))
                .toList());
    }
}
