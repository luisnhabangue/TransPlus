package persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import model.DadosTransCarga;
import model.interfaces.DAOInterface;

public class ArquivoDadosTransCargaDAO implements DAOInterface {
    @Override
    public DadosTransCarga carregar(Path ficheiro) throws IOException, ClassNotFoundException {
        if (!Files.exists(ficheiro)) {
            return new DadosTransCarga();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(ficheiro))) {
            Object dados = entrada.readObject();
            if (!(dados instanceof DadosTransCarga)) {
                throw new IOException("O ficheiro não contém dados da TransCarga.");
            }
            return (DadosTransCarga) dados;
        }
    }

    @Override
    public void guardar(Path ficheiro, DadosTransCarga dados) throws IOException {
        Path destino = ficheiro.toAbsolutePath();
        Path pasta = destino.getParent();
        if (pasta != null) {
            Files.createDirectories(pasta);
        }
        Path temporario = Files.createTempFile(pasta,
                "transcarga-" + destino.getFileName().toString(), ".tmp");
        try {
            try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
                saida.writeObject(dados);
                saida.flush();
            }
            try {
                Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            try {
                Files.deleteIfExists(temporario);
            } catch (IOException erroLimpeza) {
                e.addSuppressed(erroLimpeza);
            }
            throw e;
            //Files.deleteIfExists(temporario);
        }
    }
}
