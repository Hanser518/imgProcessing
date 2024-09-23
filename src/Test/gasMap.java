package Test;

import Controller.BlurController;
import Controller.ImgProcessingController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.IPictureService;
import Service.Impl.ICalculateServiceImpl;
import Service.Impl.IPictureServiceImpl;

import java.io.IOException;

public class gasMap {
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static BlurController blurCtrl = new BlurController();

    public static void main(String[] args) throws IOException {
        String fileName = "building";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE gas = blurCtrl.getStrangeBlur(px, 67);
        imgCtrl.showImg(gas, "gas");
        imgCtrl.saveByName(gas, fileName, "strange");


    }
}
