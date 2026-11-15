/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package model.interfaces;

import java.io.IOException;
import java.nio.file.Path;
import model.DadosTransCarga;

/**
 *
 * @author sagaz
 */
public interface DAOInterface {
    DadosTransCarga carregar(Path ficheiro) throws IOException, ClassNotFoundException;
    void guardar(Path ficheiro, DadosTransCarga dados) throws IOException;
}
