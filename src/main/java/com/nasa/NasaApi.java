package com.nasa;

import com.nasa.controller.NasaController;
import com.nasa.model.NasaModel;
import com.nasa.view.NasaView;

public class NasaApi {
    public static void main(String[] args) {
        NasaModel model = new NasaModel();
        NasaView view = new NasaView();
        NasaController controller = new NasaController(model, view);
        controller.start();
    }
}