package controller;

import algorithm.edgeTrace.entity.Node;
import algorithm.edgeTrace.entity.Point;
import algorithm.edgeTrace.main.EdgeTrace;
import discard.ImgProcessingController;
import entity.IMAGE;
import service.imgService;
import service.impl.imgServiceImpl;
import service.threadPool.ThreadPoolPaper;
import service.ICalculateService;
import service.impl.ICalculateServiceImpl;
import service.threadPool.core.ThreadPoolCore;
import service.threadPool.core.ThreadPoolReflectCore;
import service.threadPool.thread.PaperBlur;

import java.util.List;

public class StylizeController {
    // 格栅宽度预设
    public final Integer GRILLE_REGULAR = 2;
    public final Integer GRILLE_MEDIUM = 0;
    public final Integer GRILLE_BOLD = 1;

    public static ICalculateService calcService = new ICalculateServiceImpl();
    static BlurController BlurCtrl = new BlurController();
    static AdjustController AdCtrl = new AdjustController();
    static EdgeController edgeCtrl = new EdgeController();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static imgService imgServ = new imgServiceImpl();
    static ThreadPoolCore conV;

    public IMAGE transPaperStyle(IMAGE img, int maxTreadCount, int... kernelSize) {
        int size = kernelSize.length > 0 ? kernelSize[0] : 131;
        try{
            ThreadPoolReflectCore tprc = new ThreadPoolReflectCore(img.getPixelMatrix(), calcService.getGasKernel(size), maxTreadCount, new PaperBlur());
            tprc.start();
            return new IMAGE(tprc.getData());
        }catch (Exception e){
            conV = new ThreadPoolPaper(img.getPixelMatrix(), calcService.getGasKernel(size), maxTreadCount);
            conV.start();
        }
        return new IMAGE(conV.getData());
    }

    public IMAGE transGrilleStyle(IMAGE img, int grilleType, boolean multiple) {
        double radio = 1;
        int kernelSize = 117;
        switch (grilleType) {
            case 0 -> {
                radio = 0.01024;
                System.out.println(1 / radio);
                kernelSize = 81;
            }
            case 1 -> {
                radio = 0.0425;
                kernelSize = 153;
            }
            case 2 -> radio = 0.04125;
            default -> radio = 0.5;
        }
        System.out.println("adjust...");
        IMAGE ad = AdCtrl.adjustSatAndVal(img, 36, 24);
        System.out.println("adjust over");
        if (multiple) {
            IMAGE gas = transPaperStyle(ad, 24, kernelSize); // kernelSize
            List<IMAGE> imgList = imgCtrl.asyncSplit(gas, (int) (1 / radio), true);
            IMAGE ver = imgCtrl.combineImages(imgList, grilleType, true);
            imgList = imgCtrl.asyncSplit(ver, (int) (1 / (radio * 1.73)), false);
            IMAGE res = imgCtrl.combineImages(imgList, grilleType, false);
            return res;
        } else {
            IMAGE gas = transPaperStyle(ad, 24, kernelSize); // kernelSize
            List<IMAGE> imgList = imgCtrl.asyncSplit(gas, (int) (1 / radio), true);
            return imgCtrl.combineImages(imgList, grilleType, true);
        }
    }

    public IMAGE transOilPaintingStyle(IMAGE img) throws Exception {
        return new IMAGE(imgServ.traceImg(img));
    }
}
