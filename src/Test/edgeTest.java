package Test;

import Controller.EdgeController;
import Controller.ImgController;
import Discard.ImgProcessingController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;

import java.io.IOException;

public class edgeTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static ImgController imgCtrl2 = new ImgController();
    static EdgeController edgeCtrl = new EdgeController();
    public static void main(String[] args) throws IOException {
        String fileName = "7820";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE edge = new IMAGE();
        edge = edgeCtrl.getImgEdge(px, EdgeController.SOBEL);
        imgCtrl2.showImg(edge, "sobel");

        edge = edgeCtrl.getImgEdge(px, EdgeController.PREWITT);
        imgCtrl2.showImg(edge, "prewitt");

        edge = edgeCtrl.getImgEdge(px, EdgeController.MARR);
        imgCtrl2.showImg(edge, "marr");

//        IMAGE edge = imgCtrl.getEdgeImage(px, true);
//        imgCtrl.saveByName(edge, "edge_erosion_" + fileName);
//
//        edge = imgCtrl.getEdgeImage(px, false);
//        imgCtrl.saveByName(edge, "edge_" + fileName);
//
//        imgCtrl.setPureEdge(true);
//        edge = imgCtrl.getEdgeImage(px, true);
//        imgCtrl.saveByName(edge, "edge_pure_erosion_" + fileName);
//
//        edge = imgCtrl.getEdgeImage(px, false);
//        imgCtrl.saveByName(edge, "edge_pure_" + fileName);
    }
}
