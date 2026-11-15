import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class TransCargaApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        UserAgentBuilder.builder().themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();

        FXMLLoader fxmlLoader = new FXMLLoader(
                TransCargaApplication.class.getResource("/view/fxml/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        var stylesheet = TransCargaApplication.class.getResource("/view/css/styles.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
        stage.setTitle("TransCarga Moçambique");
        stage.setScene(scene);
        stage.setWidth(1440);
        stage.setHeight(820);
        stage.setMinWidth(1060);
        stage.setMinHeight(680);
        stage.centerOnScreen();
        stage.show();
    }
}
