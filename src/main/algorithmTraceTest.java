package main;

import algorithm.edgeTrace.main.EdgeTrace;
import controller.EdgeController;
import controller.ImgController;
import controller.StylizeController;
import entity.IMAGE;
import service.imgService;
import service.impl.imgServiceImpl;

import java.io.IOException;

public class algorithmTraceTest {
    static ImgController imgCtrl2 = new ImgController();
    static EdgeController edgeCtrl = new EdgeController();
    static StylizeController styleCtrl = new StylizeController();
    static imgService service = new imgServiceImpl();

    public static void main(String[] args) throws Exception {

        String fileName = "rx78";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE etImg = styleCtrl.transOilPaintingStyle(px);
        imgCtrl2.showImg(etImg, "et");

//        edge = new IMAGE(service.dilateImg(etImg, 1));
//        imgCtrl2.showImg(edge, "dilate");
    }
}
