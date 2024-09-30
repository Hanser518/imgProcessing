package Main;

import Controller.EdgeController;
import Controller.ImgController;
import Discard.ImgProcessingController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;
import Service.Impl.imgServiceImpl;
import Service.imgService;

public class imgEdgeTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static ImgController imgCtrl2 = new ImgController();
    static EdgeController edgeCtrl = new EdgeController();
    static imgService service = new imgServiceImpl();

    public static void main(String[] args) throws Exception {
        String fileName = "otk";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE edge = new IMAGE();

        edge = edgeCtrl.getImgEdge(px, EdgeController.SOBEL);
        imgCtrl2.showImg(edge, "sobel");

        edge = new IMAGE(service.dilateImg(edge, 1));
        imgCtrl2.showImg(edge, "dilate");

//        edge = new IMAGE(service.paddingImg(edge, 1));
//        imgCtrl2.showImg(edge, "padding");
//
//        edge = edgeCtrl.getImgEdge(px, EdgeController.PREWITT);
//        imgCtrl2.showImg(edge, "prewitt");
//
//        edge = edgeCtrl.getImgEdge(px, EdgeController.MARR);
//        imgCtrl2.showImg(edge, "marr");



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
