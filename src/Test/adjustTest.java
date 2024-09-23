package Test;

import Controller.AdjustController;
import Controller.EdgeController;
import Controller.ImgProcessingController;
import Entity.IMAGE;
import Service.IAdjustService;
import Service.ICalculateService;
import Service.Impl.AdjustServiceImpl;
import Service.Impl.ICalculateServiceImpl;

import java.io.IOException;

public class adjustTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static IAdjustService adService = new AdjustServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static AdjustController adCtrl = new AdjustController();
    static EdgeController edgeCtrl = new EdgeController();
    public static void main(String[] args) throws IOException {
        String fileName = "Red";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE saturation = new IMAGE(adService.AdjustSaturation(px, 72));
        imgCtrl.saveByName(saturation, fileName + "_saturation");

        IMAGE value = new IMAGE(adService.AdjustValue(px, 72));
        imgCtrl.saveByName(value, fileName + "_value");

        IMAGE both = adCtrl.AdjustSatAndVal(px, 72, 36);
        imgCtrl.saveByName(both, fileName + "_both");
    }
}
