package controller;

import java.io.IOException;
import java.nio.file.Path;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import service.TransCargaService;

public class MainViewController {
    @FXML private StackPane contentArea;
    @FXML private Label lblBreadcrumb;
    @FXML private Button btnNavProprietarios;
    @FXML private Button btnNavVeiculos;
    @FXML private Button btnNavClientes;
    @FXML private Button btnNavCarregamentos;

    private TransCargaService service;

    @FXML
    private void initialize() {
        try {
            Path ficheiroDados = Path.of(System.getProperty("user.home"), ".transplus", "dados.ser");
            service = new TransCargaService(ficheiroDados);
            abrirProprietarios();
        } catch (IOException | ClassNotFoundException e) {
            contentArea.getChildren().setAll(new Label(
                    "Não foi possível carregar os dados guardados: " + e.getMessage()));
        }
    }

    @FXML private void abrirProprietarios() { abrirPagina("Proprietários", "ProprietariosView.fxml", btnNavProprietarios); }
    @FXML private void abrirVeiculos() { abrirPagina("Frota de Veículos", "VeiculosView.fxml", btnNavVeiculos); }
    @FXML private void abrirClientes() { abrirPagina("Clientes", "ClientesView.fxml", btnNavClientes); }
    @FXML private void abrirCarregamentos() { abrirPagina("Carregamentos & Transportes", "CarregamentosView.fxml", btnNavCarregamentos); }

    @FXML
    private void abrirAjuda() {
        Alert ajuda = new Alert(Alert.AlertType.INFORMATION);
        ajuda.setTitle("Centro de ajuda");
        ajuda.setHeaderText("Gestão da TransCarga Moçambique");
        ajuda.setContentText("Cadastre primeiro proprietários, depois associe veículos. "
                + "Registe clientes para criar carregamentos. A quantidade transportada "
                + "não pode exceder a capacidade do veículo.");
        ajuda.showAndWait();
    }

    private void abrirPagina(String titulo, String recurso, Button botaoActivo) {
        if (service == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/" + recurso));
            Parent pagina = loader.load();
            Object controller = loader.getController();
            if (controller instanceof ProprietariosController c) c.setService(service);
            else if (controller instanceof VeiculosViewController c) c.setService(service);
            else if (controller instanceof ClientesController c) c.setService(service);
            else if (controller instanceof CarregamentosController c) c.setService(service);

            contentArea.getChildren().setAll(pagina);
            lblBreadcrumb.setText(titulo);
            actualizarNavegacao(botaoActivo);
        } catch (IOException | RuntimeException e) {
            ControllerSupport.mostrarErro("Não foi possível abrir " + titulo + ": " + e.getMessage());
        }
    }

    private void actualizarNavegacao(Button activo) {
        for (Button botao : new Button[]{btnNavProprietarios, btnNavVeiculos,
                btnNavClientes, btnNavCarregamentos}) {
            botao.getStyleClass().remove("nav-button-active");
            if (!botao.getStyleClass().contains("nav-button")) botao.getStyleClass().add("nav-button");
        }
        activo.getStyleClass().remove("nav-button");
        if (!activo.getStyleClass().contains("nav-button-active")) {
            activo.getStyleClass().add("nav-button-active");
        }
    }
}
