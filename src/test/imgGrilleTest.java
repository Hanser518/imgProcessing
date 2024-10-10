package test;

import controller.ImgController;
import discard.ImgProcessingController;
import controller.StylizeController;
import entity.IMAGE;
import service.ICalculateService;
import service.impl.ICalculateServiceImpl;

import java.io.IOException;

public class imgGrilleTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static ImgController imgCtrl2 = new ImgController();
    static StylizeController styleCtrl = new StylizeController();
    public static void main(String[] args) throws IOException {
        String fileName = "building";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE grille;
        grille = styleCtrl.transGrilleStyle(px, styleCtrl.GRILLE_REGULAR, false);
        imgCtrl2.showImg(grille, "regular");

        grille = styleCtrl.transGrilleStyle(px, styleCtrl.GRILLE_MEDIUM, false);
        imgCtrl2.showImg(grille, "medium");

        grille = styleCtrl.transGrilleStyle(px, styleCtrl.GRILLE_BOLD, false);
        imgCtrl2.showImg(grille, "bold");

        grille = styleCtrl.transPaperStyle(px, 24, 118);
        imgCtrl2.showImg(grille, "paper");
    }
}
