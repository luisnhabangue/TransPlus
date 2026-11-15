package controller;

import java.io.IOException;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.Cliente;
import service.TransCargaService;

public class ClientesController {
    @FXML private Label lblTotalClientes;
    @FXML private Label lblTotalLocalidades;
    @FXML private Label lblServicosPrestados;
    @FXML private Label lblTituloFormCliente;
    @FXML private Label lblBadgeClientes;
    @FXML private TextField txtNomeCliente;
    @FXML private TextField txtContactoCliente;
    @FXML private TextField txtLocalizacaoCliente;
    @FXML private TextField txtPesquisarCliente;
    @FXML private Button btnCancelarCliente;
    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, String> colCliNome;
    @FXML private TableColumn<Cliente, String> colCliContacto;
    @FXML private TableColumn<Cliente, String> colCliLocalizacao;
    @FXML private TableColumn<Cliente, Void> colCliAcoes;

    private TransCargaService service;
    private Cliente emEdicao;
    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        tabelaClientes.setItems(clientes);
        colCliNome.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNome()));
        colCliContacto.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getContacto()));
        colCliLocalizacao.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLocalizacao()));
        colCliAcoes.setCellFactory(c -> new javafx.scene.control.TableCell<>() {
            private final Button editar = new Button("Editar");
            {
                editar.getStyleClass().add("btn-secondary");
                editar.setOnAction(event -> iniciarEdicao(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editar);
            }
        });
        txtPesquisarCliente.textProperty().addListener((o, anterior, novo) -> actualizarTabela());
    }

    public void setService(TransCargaService service) {
        this.service = service;
        actualizarDados();
    }

    @FXML
    private void guardarCliente() {
        try {
            if (emEdicao == null) {
                service.registarCliente(txtNomeCliente.getText(), txtContactoCliente.getText(),
                        txtLocalizacaoCliente.getText());
            } else {
                service.actualizarCliente(emEdicao.getId(), txtNomeCliente.getText(),
                        txtContactoCliente.getText(), txtLocalizacaoCliente.getText());
            }
            limparFormulario();
            actualizarDados();
        } catch (IOException | IllegalArgumentException e) {
            ControllerSupport.mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void limparFormulario() {
        emEdicao = null;
        txtNomeCliente.clear();
        txtContactoCliente.clear();
        txtLocalizacaoCliente.clear();
        lblTituloFormCliente.setText("Cadastrar cliente");
        btnCancelarCliente.setVisible(false);
        btnCancelarCliente.setManaged(false);
    }

    private void iniciarEdicao(Cliente cliente) {
        emEdicao = cliente;
        txtNomeCliente.setText(cliente.getNome());
        txtContactoCliente.setText(cliente.getContacto());
        txtLocalizacaoCliente.setText(cliente.getLocalizacao());
        lblTituloFormCliente.setText("Editar cliente");
        btnCancelarCliente.setVisible(true);
        btnCancelarCliente.setManaged(true);
    }

    private void actualizarDados() {
        if (service == null) return;
        var lista = service.listarClientes();
        long localidades = lista.stream().map(c -> c.getLocalizacao().trim().toLowerCase(Locale.ROOT))
                .distinct().count();
        lblTotalClientes.setText(String.valueOf(lista.size()));
        lblBadgeClientes.setText(String.valueOf(lista.size()));
        lblTotalLocalidades.setText(String.valueOf(localidades));
        lblServicosPrestados.setText(String.valueOf(service.listarCarregamentos().size()));
        actualizarTabela();
    }

    private void actualizarTabela() {
        if (service == null) return;
        String termo = txtPesquisarCliente.getText() == null ? ""
                : txtPesquisarCliente.getText().trim().toLowerCase(Locale.ROOT);
        clientes.setAll(service.listarClientes().stream()
                .filter(c -> (c.getNome() + " " + c.getContacto() + " " + c.getLocalizacao())
                        .toLowerCase(Locale.ROOT).contains(termo))
                .toList());
    }
}
