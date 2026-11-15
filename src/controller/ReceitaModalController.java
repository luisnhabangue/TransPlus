package controller;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Carregamento;
import model.Veiculo;

public class ReceitaModalController {
    @FXML private Label lblModalMatricula;
    @FXML private Label lblModalTotalReceita;
    @FXML private Label lblModalModeloTipo;
    @FXML private Label lblModalProprietario;
    @FXML private Label lblModalQtdServicos;
    @FXML private ListView<String> listaServicosVeiculo;

    public void mostrar(Veiculo veiculo, List<Carregamento> servicos, Number total) {
        NumberFormat formato = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-MZ"));
        lblModalMatricula.setText("Total Arrecadado pelo Veículo " + veiculo.getMatricula());
        lblModalTotalReceita.setText(formato.format(total));
        lblModalModeloTipo.setText(veiculo.getMarca() + " " + veiculo.getModelo()
                + " · " + veiculo.getTipoVeiculo().getRotulo());
        lblModalProprietario.setText(veiculo.getProprietario().getNome());
        lblModalQtdServicos.setText(String.valueOf(servicos.size()));
        listaServicosVeiculo.setItems(FXCollections.observableArrayList(servicos.stream()
                .map(c -> c.getDataCarregamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        + " · " + c.getLocalOrigem() + " → " + c.getLocalDestino()
                        + " · " + formato.format(c.getCustoCarregamento()) + " MZN")
                .toList()));
    }

    @FXML
    private void fecharModal(javafx.event.ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
