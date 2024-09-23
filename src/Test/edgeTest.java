package Test;

import Controller.EdgeController;
import Controller.ImgProcessingController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;

import java.io.IOException;

public class edgeTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static EdgeController edgeCtrl = new EdgeController();
    public static void main(String[] args) throws IOException {
        imgCtrl.openMultiThreads();
        imgCtrl.closeAccCalc();
        imgCtrl.setPureEdge(false);


        imgCtrl.setPureEdge(false);
        String fileName = "7820";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE edge1 = edgeCtrl.getImgEdge(px);
        imgCtrl.save(edge1, fileName + "_pool");

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
