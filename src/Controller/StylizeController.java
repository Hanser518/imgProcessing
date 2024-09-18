package Controller;

import Entity.IMAGE;
import Service.Extends.ThreadPoolPaper;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;
import Service.ThreadPoolService;

import java.util.List;

public class StylizeController {
    // 格栅宽度预设
    public final Integer GRILLE_REGULAR = 2;
    public final Integer GRILLE_MEDIUM = 0;
    public final Integer GRILLE_BOLD = 1;

    static ICalculateService calcService = new ICalculateServiceImpl();
    static BlurController BlurCtrl = new BlurController();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static ThreadPoolService conV;

    public IMAGE transPaperStyle(IMAGE img, int maxTreadCount){
        conV = new ThreadPoolPaper(img.getPixelMatrix(), calcService.getGasKernel(100), maxTreadCount);
        conV.start();
        return new IMAGE(conV.getData());
    }

    public IMAGE transGrilleStyle(IMAGE img, int grilleType){
        IMAGE gas = BlurCtrl.getGasBlur(img, 67, -1);
        double radio = 1;
        switch (grilleType){
            case 0:
                radio = 0.125;
                break;
            case 1:
                radio = 0.25;
                break;
            case 2:
                radio = 0.0625;
                break;
            default:
                radio = 0.5;
        }
        List<IMAGE> imgList = imgCtrl.asyncSplit(gas, (int) (1 / radio), true);
        return imgCtrl.combineImages(imgList, grilleType);
    }


}
