package test;

import controller.AdjustController;
import controller.EdgeController;
import controller.ImgController;
import discard.ImgProcessingController;
import entity.IMAGE;
import service.IAdjustService;
import service.ICalculateService;
import service.impl.AdjustServiceImpl;
import service.impl.ICalculateServiceImpl;

import java.io.IOException;

public class imgAdjustTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static IAdjustService adService = new AdjustServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static ImgController imgCtrl2 = new ImgController();
    static AdjustController adCtrl = new AdjustController();
    static EdgeController edgeCtrl = new EdgeController();
    public static void main(String[] args) throws IOException {
        String fileName = "bus";
        IMAGE px = new IMAGE(fileName + ".jpg");

//         IMAGE cdr = adCtrl.CDR(px);
//         imgCtrl.saveByName(cdr, fileName, "cdr");
//
//         IMAGE saturation = adCtrl.adjustSaturation(px, -72);
//         imgCtrl.saveByName(saturation, fileName, "saturation");
//
//         IMAGE both = adCtrl.adjustSatAndVal(px, 0, 0);
//         imgCtrl.saveByName(both, fileName, "both");
//
//
//         imgCtrl2.showImg(cdr, "cdr");
//         imgCtrl2.showImg(saturation, "saturation");
//         imgCtrl2.showImg(both, "both");
        for(int i = 0;i <= 360;i += 10){
            IMAGE test = adCtrl.test(px, i);
            imgCtrl2.showImg(test, "test" + i);
        }

    }
}