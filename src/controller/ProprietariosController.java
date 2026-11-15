package controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import model.Proprietario;
import service.TransCargaService;

public class ProprietariosController {
    @FXML private Label lblTotalProprietarios;
    @FXML private Label lblTotalVeiculos;
    @FXML private Label lblTotalTransportes;
    @FXML private Label lblTituloForm;
    @FXML private Label lblBadgeProprietarios;
    @FXML private TextField txtNomeProprietario;
    @FXML private TextField txtContactoProprietario;
    @FXML private TextField txtDocumentoProprietario;
    @FXML private TextField txtPesquisarProprietario;
    @FXML private Button btnCancelarEdicao;
    @FXML private TableView<Proprietario> tabelaProprietarios;
    @FXML private TableColumn<Proprietario, String> colPropNome;
    @FXML private TableColumn<Proprietario, String> colPropContacto;
    @FXML private TableColumn<Proprietario, String> colPropDocumento;
    @FXML private TableColumn<Proprietario, String> colPropQtdVeiculos;
    @FXML private TableColumn<Proprietario, Void> colPropAcoes;

    private TransCargaService service;
    private Proprietario emEdicao;
    private final ObservableList<Proprietario> proprietarios = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        tabelaProprietarios.setItems(proprietarios);
        colPropNome.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNome()));
        colPropContacto.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getContacto()));
        colPropDocumento.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDocumentoId()));
        colPropQtdVeiculos.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getVeiculos().size())));
        colPropAcoes.setCellFactory(c -> new javafx.scene.control.TableCell<>() {
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
        txtPesquisarProprietario.textProperty().addListener((o, anterior, novo) -> actualizarTabela());
    }

    public void setService(TransCargaService service) {
        this.service = service;
        actualizarDados();
    }

    @FXML
    private void guardarProprietario() {
        try {
            if (emEdicao == null) {
                service.registarProprietario(txtDocumentoProprietario.getText(),
                        txtNomeProprietario.getText(), txtContactoProprietario.getText());
            } else {
                service.actualizarProprietario(emEdicao.getDocumentoId(),
                        txtNomeProprietario.getText(), txtContactoProprietario.getText());
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
        txtNomeProprietario.clear();
        txtContactoProprietario.clear();
        txtDocumentoProprietario.clear();
        txtDocumentoProprietario.setDisable(false);
        lblTituloForm.setText("Cadastrar proprietário");
        btnCancelarEdicao.setVisible(false);
        btnCancelarEdicao.setManaged(false);
    }

    @FXML
    private void exportarProprietarios() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar proprietários");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiro CSV", "*.csv"));
        java.io.File ficheiro = chooser.showSaveDialog(tabelaProprietarios.getScene().getWindow());
        if (ficheiro == null) {
            return;
        }
        try {
            List<String> linhas = new java.util.ArrayList<>();
            linhas.add("Nome,Contacto,Documento,Veiculos");
            for (Proprietario p : service.listarProprietarios()) {
                linhas.add(csv(p.getNome()) + "," + csv(p.getContacto()) + ","
                        + csv(p.getDocumentoId()) + "," + p.getVeiculos().size());
            }
            java.nio.file.Files.write(ficheiro.toPath(), linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            ControllerSupport.mostrarErro("Falha ao exportar os proprietários: " + e.getMessage());
        }
    }

    private String csv(String valor) {
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }

    private void iniciarEdicao(Proprietario proprietario) {
        emEdicao = proprietario;
        txtNomeProprietario.setText(proprietario.getNome());
        txtContactoProprietario.setText(proprietario.getContacto());
        txtDocumentoProprietario.setText(proprietario.getDocumentoId());
        txtDocumentoProprietario.setDisable(true);
        lblTituloForm.setText("Editar proprietário");
        btnCancelarEdicao.setVisible(true);
        btnCancelarEdicao.setManaged(true);
    }

    private void actualizarDados() {
        if (service == null) return;
        List<Proprietario> lista = service.listarProprietarios();
        lblTotalProprietarios.setText(String.valueOf(lista.size()));
        lblBadgeProprietarios.setText(String.valueOf(lista.size()));
        lblTotalVeiculos.setText(String.valueOf(service.listarVeiculos().size()));
        lblTotalTransportes.setText(String.valueOf(service.listarCarregamentos().size()));
        actualizarTabela();
    }

    private void actualizarTabela() {
        if (service == null) return;
        String termo = txtPesquisarProprietario.getText() == null ? ""
                : txtPesquisarProprietario.getText().trim().toLowerCase(Locale.ROOT);
        proprietarios.setAll(service.listarProprietarios().stream()
                .filter(p -> (p.getNome() + " " + p.getContacto() + " " + p.getDocumentoId())
                        .toLowerCase(Locale.ROOT).contains(termo))
                .toList());
    }
}
