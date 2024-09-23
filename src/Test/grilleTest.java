package Test;

import Controller.ImgProcessingController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;

import java.io.IOException;

public class grilleTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        String fileName = "rx78";
        IMAGE px = new IMAGE(fileName + ".jpg");
        imgCtrl.openGasBlur();
        imgCtrl.openMultiThreads();

        IMAGE grille = imgCtrl.getGrilleImage(px, imgCtrl.GRILLE_REGULAR, 0);
        imgCtrl.save(grille, "Grille_0" + fileName);

        grille = imgCtrl.getGrilleImage(px, imgCtrl.GRILLE_REGULAR, 1);
        imgCtrl.save(grille, "Grille_1" + fileName);

        grille = imgCtrl.getGrilleImage(px, imgCtrl.GRILLE_REGULAR, 2);
        imgCtrl.save(grille, "Grille_2" + fileName);
    }
}
