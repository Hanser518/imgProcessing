package Test;

import Controller.ImgProcessingController;
import Controller.StylizeController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;

import java.io.IOException;

public class imgGrilleTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static StylizeController styleCtrl = new StylizeController();
    public static void main(String[] args) throws IOException {
        String fileName = "rx78";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE grille;
        grille = styleCtrl.transGrilleStyle(px, styleCtrl.GRILLE_REGULAR, false);
        imgCtrl.saveByName(grille, fileName, "regular");

        grille = styleCtrl.transGrilleStyle(px, styleCtrl.GRILLE_MEDIUM, false);
        imgCtrl.saveByName(grille, fileName, "medium");

        grille = styleCtrl.transGrilleStyle(px, styleCtrl.GRILLE_BOLD, false);
        imgCtrl.saveByName(grille, fileName, "bold");
    }
}
