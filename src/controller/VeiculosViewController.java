package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Carregamento;
import model.Proprietario;
import model.Veiculo;
import model.enums.TipoVeiculo;
import service.TransCargaService;

public class VeiculosViewController {
    @FXML private Label lblFrotaTotal;
    @FXML private Label lblFrotaDetalhe;
    @FXML private Label lblCapacidadeTotal;
    @FXML private Label lblReceitaFrota;
    @FXML private Label lblTituloFormVeiculo;
    @FXML private Label lblBadgeVeiculos;
    @FXML private ComboBox<TipoVeiculo> cmbTipoVeiculo;
    @FXML private ComboBox<Proprietario> cmbProprietarioVeiculo;
    @FXML private TextField txtMatricula;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtCapacidade;
    @FXML private TextField txtPesquisarVeiculo;
    @FXML private Button btnCancelarVeiculo;
    @FXML private Button tabTodosVeiculos;
    @FXML private Button tabApenasCamioes;
    @FXML private Button tabApenasCarrinhas;
    @FXML private TableView<Veiculo> tabelaVeiculos;
    @FXML private TableColumn<Veiculo, String> colVeicMatricula;
    @FXML private TableColumn<Veiculo, String> colVeicTipo;
    @FXML private TableColumn<Veiculo, String> colVeicMarcaModelo;
    @FXML private TableColumn<Veiculo, String> colVeicCapacidade;
    @FXML private TableColumn<Veiculo, String> colVeicProprietario;
    @FXML private TableColumn<Veiculo, String> colVeicReceita;
    @FXML private TableColumn<Veiculo, Void> colVeicAcoes;

    private TransCargaService service;
    private Veiculo emEdicao;
    private TipoVeiculo filtroTipo;
    private final ObservableList<Veiculo> veiculos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        tabelaVeiculos.setItems(veiculos);
        cmbTipoVeiculo.setItems(FXCollections.observableArrayList(TipoVeiculo.values()));
        ControllerSupport.configurarCombo(cmbTipoVeiculo, TipoVeiculo::getRotulo);
        cmbTipoVeiculo.setValue(TipoVeiculo.Camiao);
        ControllerSupport.configurarCombo(cmbProprietarioVeiculo, Proprietario::getNome);
        colVeicMatricula.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMatricula()));
        colVeicTipo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTipoVeiculo().getRotulo()));
        colVeicMarcaModelo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getMarca() + " / " + c.getValue().getModelo()));
        colVeicCapacidade.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                ControllerSupport.formatar(c.getValue().getCapacidadeCarga())));
        colVeicProprietario.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getProprietario().getNome()));
        colVeicReceita.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                dinheiro(c.getValue())));
        colVeicAcoes.setCellFactory(c -> new javafx.scene.control.TableCell<>() {
            private final Button editar = new Button("Editar");
            private final Button receita = new Button("Receita");
            private final HBox botoes = new HBox(4, editar, receita);
            {
                editar.getStyleClass().add("btn-secondary");
                receita.getStyleClass().add("btn-secondary");
                editar.setOnAction(e -> iniciarEdicao(getTableView().getItems().get(getIndex())));
                receita.setOnAction(e -> abrirReceita(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : botoes);
            }
        });
        txtPesquisarVeiculo.textProperty().addListener((o, anterior, novo) -> actualizarTabela());
    }

    public void setService(TransCargaService service) {
        this.service = service;
        cmbProprietarioVeiculo.setItems(FXCollections.observableArrayList(service.listarProprietarios()));
        actualizarDados();
    }

    @FXML
    private void guardarVeiculo() {
        try {
            if (cmbProprietarioVeiculo.getValue() == null) {
                throw new IllegalArgumentException("Seleccione um proprietário.");
            }
            double capacidade = ControllerSupport.numero(txtCapacidade, "Capacidade");
            Veiculo dados = new Veiculo(txtMatricula.getText().trim().toUpperCase(Locale.ROOT),
                    txtMarca.getText().trim(), txtModelo.getText().trim(), capacidade,
                    cmbTipoVeiculo.getValue(), cmbProprietarioVeiculo.getValue());
            if (emEdicao == null) {
                service.registarVeiculo(dados);
            } else {
                service.actualizarVeiculo(emEdicao.getMatricula(), dados);
            }
            limparFormulario();
            cmbProprietarioVeiculo.setItems(FXCollections.observableArrayList(service.listarProprietarios()));
            actualizarDados();
        } catch (IOException | IllegalArgumentException e) {
            ControllerSupport.mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void limparFormulario() {
        emEdicao = null;
        txtMatricula.clear();
        txtMatricula.setDisable(false);
        txtMarca.clear();
        txtModelo.clear();
        txtCapacidade.clear();
        cmbTipoVeiculo.setValue(TipoVeiculo.Camiao);
        cmbProprietarioVeiculo.setValue(null);
        lblTituloFormVeiculo.setText("Registar veículo");
        btnCancelarVeiculo.setVisible(false);
        btnCancelarVeiculo.setManaged(false);
    }

    @FXML
    private void filtrarTodosVeiculos() {
        filtroTipo = null;
        marcarFiltro(tabTodosVeiculos);
        actualizarTabela();
    }

    @FXML
    private void filtrarCamioesCrescente() {
        filtroTipo = TipoVeiculo.Camiao;
        marcarFiltro(tabApenasCamioes);
        actualizarTabela();
    }

    @FXML
    private void filtrarCarrinhasCrescente() {
        filtroTipo = TipoVeiculo.Carrinha;
        marcarFiltro(tabApenasCarrinhas);
        actualizarTabela();
    }

    @FXML
    private void consultarReceitaSelecionado() {
        Veiculo selecionado = tabelaVeiculos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            ControllerSupport.mostrarErro("Seleccione um veículo na tabela para consultar a receita.");
            return;
        }
        abrirReceita(selecionado);
    }

    private void marcarFiltro(Button ativo) {
        for (Button botao : List.of(tabTodosVeiculos, tabApenasCamioes, tabApenasCarrinhas)) {
            botao.getStyleClass().remove("tab-button-selected");
            if (!botao.getStyleClass().contains("tab-button")) botao.getStyleClass().add("tab-button");
        }
        ativo.getStyleClass().remove("tab-button");
        if (!ativo.getStyleClass().contains("tab-button-selected")) {
            ativo.getStyleClass().add("tab-button-selected");
        }
    }

    private void iniciarEdicao(Veiculo veiculo) {
        emEdicao = veiculo;
        txtMatricula.setText(veiculo.getMatricula());
        txtMatricula.setDisable(true);
        txtMarca.setText(veiculo.getMarca());
        txtModelo.setText(veiculo.getModelo());
        txtCapacidade.setText(String.valueOf(veiculo.getCapacidadeCarga()));
        cmbTipoVeiculo.setValue(veiculo.getTipoVeiculo());
        cmbProprietarioVeiculo.setValue(veiculo.getProprietario());
        lblTituloFormVeiculo.setText("Editar veículo");
        btnCancelarVeiculo.setVisible(true);
        btnCancelarVeiculo.setManaged(true);
    }

    private void abrirReceita(Veiculo veiculo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/ReceitaModal.fxml"));
            javafx.scene.Parent conteudo = loader.load();
            ReceitaModalController controller = loader.getController();
            List<Carregamento> servicos = service.listarCarregamentos().stream()
                    .filter(c -> c.getVeiculoUsado().getMatricula().equalsIgnoreCase(veiculo.getMatricula()))
                    .toList();
            controller.mostrar(veiculo, servicos, service.totalArrecadadoPorVeiculo(veiculo.getMatricula()));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tabelaVeiculos.getScene().getWindow());
            stage.setTitle("Receita do veículo " + veiculo.getMatricula());
            stage.setScene(new Scene(conteudo));
            stage.showAndWait();
        } catch (IOException e) {
            ControllerSupport.mostrarErro("Não foi possível abrir a consulta de receita: " + e.getMessage());
        }
    }

    private String dinheiro(Veiculo veiculo) {
        return NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-MZ"))
                .format(service.totalArrecadadoPorVeiculo(veiculo.getMatricula()));
    }

    private void actualizarDados() {
        if (service == null) return;
        List<Veiculo> todos = service.listarVeiculos();
        long camioes = todos.stream().filter(v -> v.getTipoVeiculo() == TipoVeiculo.Camiao).count();
        BigDecimal receita = todos.stream()
                .map(v -> service.totalArrecadadoPorVeiculo(v.getMatricula()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblFrotaTotal.setText(String.valueOf(todos.size()));
        lblBadgeVeiculos.setText(String.valueOf(todos.size()));
        lblFrotaDetalhe.setText(camioes + " camiões e " + (todos.size() - camioes) + " carrinhas de carga");
        lblCapacidadeTotal.setText(ControllerSupport.formatar(
                todos.stream().mapToDouble(Veiculo::getCapacidadeCarga).sum()));
        lblReceitaFrota.setText(NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-MZ")).format(receita));
        actualizarTabela();
    }

    private void actualizarTabela() {
        if (service == null) return;
        String termo = txtPesquisarVeiculo.getText() == null ? ""
                : txtPesquisarVeiculo.getText().trim().toLowerCase(Locale.ROOT);
        List<Veiculo> ordenados;
        if (filtroTipo == TipoVeiculo.Camiao) {
            ordenados = obterCamioesCrescente();
        } else if (filtroTipo == TipoVeiculo.Carrinha) {
            ordenados = obterCarrinhasCrescente();
        } else {
            ordenados = service.listarVeiculos();
        }
        veiculos.setAll(ordenados.stream()
                .filter(v -> (v.getMatricula() + " " + v.getMarca() + " " + v.getModelo()
                        + " " + v.getProprietario().getNome()).toLowerCase(Locale.ROOT).contains(termo))
                .toList());
    }

    private List<Veiculo> obterCamioesCrescente() {
        List<Veiculo> resultado = new ArrayList<>();
        for (Veiculo veiculo : service.listarVeiculos()) {
            if (veiculo.getTipoVeiculo() == TipoVeiculo.Camiao) resultado.add(veiculo);
        }
        return resultado;
    }

    private List<Veiculo> obterCarrinhasCrescente() {
        List<Veiculo> resultado = new ArrayList<>();
        for (Veiculo veiculo : service.listarVeiculos()) {
            if (veiculo.getTipoVeiculo() == TipoVeiculo.Carrinha) resultado.add(veiculo);
        }
        return resultado;
    }
}
