package controller;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Function;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

final class ControllerSupport {
    private ControllerSupport() {
    }

    static void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("TransCarga Moçambique");
        alert.setHeaderText("Não foi possível concluir a operação");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    static void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("TransCarga Moçambique");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    static double numero(TextField campo, String nome) {
        String valor = campo.getText() == null ? "" : campo.getText().trim();
        if (valor.isEmpty()) {
            throw new IllegalArgumentException(nome + " é obrigatório.");
        }
        try {
            return Double.parseDouble(valor.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nome + " deve ser um número válido.");
        }
    }

    static String formatar(double valor) {
        return NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-MZ"))
                .format(valor);
    }

    static <T> void configurarCombo(javafx.scene.control.ComboBox<T> combo,
            Function<T, String> texto) {
        combo.setConverter(new StringConverter<>() {
            @Override
            public String toString(T valor) {
                return valor == null ? "" : texto.apply(valor);
            }

            @Override
            public T fromString(String valor) {
                return combo.getItems().stream()
                        .filter(item -> texto.apply(item).equals(valor))
                        .findFirst().orElse(null);
            }
        });
    }
}
