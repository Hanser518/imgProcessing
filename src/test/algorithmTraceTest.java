package test;

import controller.EdgeController;
import controller.ImgController;
import controller.ProcessingController;
import controller.StylizeController;
import entity.IMAGE;
import service.ICalculateService;
import service.imgService;
import service.impl.ICalculateServiceImpl;
import service.impl.imgServiceImpl;

public class algorithmTraceTest {
    static ImgController imgCtrl2 = new ImgController();
    static EdgeController edgeCtrl = new EdgeController();
    static StylizeController styleCtrl = new StylizeController();
    static ProcessingController proCtrl = new ProcessingController();
    static imgService service = new imgServiceImpl();
    static ICalculateService calcServ = new ICalculateServiceImpl();

    public static void main(String[] args) throws Exception {

        String fileName = "7820";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE his = new IMAGE(calcServ.getHistogram(px));
        imgCtrl2.showImg(his, "his");

//        IMAGE oil = styleCtrl.transOilPaintingStyle(px);
//        imgCtrl2.showImg(oil, "oil");

//        EdgeTrace edgeTrace = new EdgeTrace(edgeCtrl.getImgEdge(px));
//        edgeTrace.start(0);
//        IMAGE et = new IMAGE(edgeTrace.getData());
//        imgCtrl2.showImg(et, "et");

//        List<IMAGE> imgList = new ArrayList<>();
//        imgList.add(px);
//        imgList.add(oil);
//        imgList.add(et);
//        IMAGE combine = proCtrl.combineImageList(imgList);
//        imgCtrl2.showImg(combine, "combine");

//        edge = new IMAGE(service.dilateImg(etImg, 1));
//        imgCtrl2.showImg(edge, "dilate");
    }
}
