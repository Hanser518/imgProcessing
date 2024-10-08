package main;

import algorithm.edgeTrace.main.EdgeTrace;
import controller.EdgeController;
import controller.ImgController;
import entity.IMAGE;
import service.imgService;
import service.impl.imgServiceImpl;

import java.io.IOException;

public class algorithmTraceTest {
    static ImgController imgCtrl2 = new ImgController();
    static EdgeController edgeCtrl = new EdgeController();
    static imgService service = new imgServiceImpl();

    public static void main(String[] args) throws Exception {

        String fileName = "7820";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE edge = edgeCtrl.getImgEdge(px, EdgeController.SOBEL);
        imgCtrl2.showImg(edge, "sobel");

        edge = new IMAGE(service.dilateImg(edge, 1));
        imgCtrl2.showImg(edge, "dilate");

        EdgeTrace et = new EdgeTrace(edge);
        et.start(EdgeTrace.PATTERN_ALL);
        IMAGE etImg = new IMAGE(et.getData());
        imgCtrl2.showImg(etImg, "et");
    }
}
