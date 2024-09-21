package Test;

import Controller.ImgProcessingController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;

import java.io.IOException;

public class edgeTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        imgCtrl.openMultiThreads();
        imgCtrl.closeAccCalc();
        imgCtrl.setPureEdge(false);


        imgCtrl.setPureEdge(false);
        String fileName = "index1";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE edge = imgCtrl.getEdgeImage(px, true);
        imgCtrl.saveByName(edge, "edge_erosion_" + fileName);

        edge = imgCtrl.getEdgeImage(px, false);
        imgCtrl.saveByName(edge, "edge_" + fileName);

        imgCtrl.setPureEdge(true);
        edge = imgCtrl.getEdgeImage(px, true);
        imgCtrl.saveByName(edge, "edge_pure_erosion_" + fileName);

        edge = imgCtrl.getEdgeImage(px, false);
        imgCtrl.saveByName(edge, "edge_pure_" + fileName);
    }
}
