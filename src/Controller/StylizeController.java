package Controller;

import Discard.ImgProcessingController;
import Entity.IMAGE;
import Service.Extends.ThreadPoolPaper;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;
import Service.CORE.ThreadPoolCore;

import java.util.List;

public class StylizeController {
    // 格栅宽度预设
    public final Integer GRILLE_REGULAR = 2;
    public final Integer GRILLE_MEDIUM = 0;
    public final Integer GRILLE_BOLD = 1;

    static ICalculateService calcService = new ICalculateServiceImpl();
    static BlurController BlurCtrl = new BlurController();
    static AdjustController AdCtrl = new AdjustController();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static ThreadPoolCore conV;

    public IMAGE transPaperStyle(IMAGE img, int maxTreadCount, int... kernelSize){
        int size = kernelSize.length > 0 ? kernelSize[0] : 131;
        conV = new ThreadPoolPaper(img.getPixelMatrix(), calcService.getGasKernel(size), maxTreadCount);
        conV.start();
        return new IMAGE(conV.getData());
    }

    public IMAGE transGrilleStyle(IMAGE img, int grilleType, boolean multiple) {
        double radio = 1;
        int kernelSize = 117;
        switch (grilleType){
            case 0:
                radio = 0.01024;
                System.out.println(1 / radio);
                kernelSize = 81;
                break;
            case 1:
                radio = 0.0425;
                break;
            case 2:
                radio = 0.02125;
                break;
            default:
                radio = 0.5;
        }
        IMAGE ad = AdCtrl.adjustSatAndVal(img, 72, 36);
        if(multiple){
            IMAGE gas = transPaperStyle(ad, -1, kernelSize); // kernelSize
            List<IMAGE> imgList = imgCtrl.asyncSplit(gas, (int) (1 / radio), true);
            IMAGE ver = imgCtrl.combineImages(imgList, grilleType, true);
            imgList = imgCtrl.asyncSplit(ver, (int) (1 / (radio * 1.73)), false);
            IMAGE res = imgCtrl.combineImages(imgList, grilleType, false);
            return res;
        }else{
            IMAGE gas = transPaperStyle(ad, -1, kernelSize); // kernelSize
            List<IMAGE> imgList = imgCtrl.asyncSplit(gas, (int) (1 / radio), true);
            return imgCtrl.combineImages(imgList, grilleType, true);
        }
    }


}
