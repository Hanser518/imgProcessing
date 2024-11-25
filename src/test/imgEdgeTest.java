package test;

import controller.EdgeController;
import controller.ImgController;
import discard.ImgProcessingController;
import entity.Image;
import service.ICalculateService;
import service.impl.ICalculateServiceImpl;
import service.impl.ImgServiceImpl;
import service.ImgService;

public class imgEdgeTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static ImgController imgCtrl2 = new ImgController();
    static EdgeController edgeCtrl = new EdgeController();
    static ImgService service = new ImgServiceImpl();

    public static void main(String[] args) throws Exception {
        String fileName = "bus";
        Image px = new Image(fileName + ".jpg");

        Image edge = new Image();

        edge = edgeCtrl.getImgEdge(px, EdgeController.MARR);
        imgCtrl2.showImg(edge, "sobel");

        edge = new Image(service.dilateImg(edge, 1));
        imgCtrl2.showImg(edge, "dilate");

//        edge = new Image(service.paddingImg(edge, 1));
//        imgCtrl2.showImg(edge, "padding");
//
//        edge = edgeCtrl.getImgEdge(px, EdgeController.PREWITT);
//        imgCtrl2.showImg(edge, "prewitt");
//
//        edge = edgeCtrl.getImgEdge(px, EdgeController.MARR);
//        imgCtrl2.showImg(edge, "marr");



//        Image edge = imgCtrl.getEdgeImage(px, true);
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
