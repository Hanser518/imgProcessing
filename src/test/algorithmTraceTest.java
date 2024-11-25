package test;

import algorithm.edgeTrace.main.EdgeTrace;
import controller.EdgeController;
import controller.ImgController;
import controller.ProcessingController;
import controller.StylizeController;
import entity.Image;
import service.ICalculateService;
import service.ImgService;
import service.impl.ICalculateServiceImpl;
import service.impl.ImgServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class algorithmTraceTest {
    static ImgController imgCtrl2 = new ImgController();
    static EdgeController edgeCtrl = new EdgeController();
    static StylizeController styleCtrl = new StylizeController();
    static ProcessingController proCtrl = new ProcessingController();
    static ImgService service = new ImgServiceImpl();
    static ICalculateService calcServ = new ICalculateServiceImpl();

    public static void main(String[] args) throws Exception {

        String fileName = "paste";
        Image px = new Image(fileName + ".jpg");

        Image his = new Image(calcServ.getHistogram(px));
        imgCtrl2.showImg(his, "his");

        Image eg = new Image(service.getSobelEdge(px));
        imgCtrl2.showImg(eg, "eg");

        Image oil = styleCtrl.transOilPaintingStyle(px);
        imgCtrl2.showImg(oil, "oil");

        EdgeTrace edgeTrace = new EdgeTrace(edgeCtrl.getImgEdge(px));
        edgeTrace.start(0);
        Image et = new Image(edgeTrace.getData());
        imgCtrl2.showImg(et, "et");

        List<Image> imgList = new ArrayList<>();
        imgList.add(eg);
        imgList.add(oil);
        imgList.add(et);
        Image combine = proCtrl.combineImageList(imgList);
        imgCtrl2.showImg(combine, "combine");
    }
}
