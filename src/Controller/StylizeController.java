package Controller;

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

    public IMAGE transPaperStyle(IMAGE img, int maxTreadCount){
        IMAGE cdr = AdCtrl.CDR(img);
        conV = new ThreadPoolPaper(cdr.getPixelMatrix(), calcService.getGasKernel(67), maxTreadCount);
        conV.start();
        // return new IMAGE(conV.getData());
        return cdr;
    }

    public IMAGE transGrilleStyle(IMAGE img, int grilleType){
        IMAGE cdr = AdCtrl.CDR(img);
        IMAGE gas = BlurCtrl.getGasBlur(cdr, 67, -1);
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
