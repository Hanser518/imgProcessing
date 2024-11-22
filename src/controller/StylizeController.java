package controller;

import discard.ImgProcessingController;
import entity.IMAGE;
import frame.entity.Param;
import service.ImgService;
import service.impl.ImgServiceImpl;
import threadPool.pool.ThreadPoolPaper;
import service.ICalculateService;
import service.impl.ICalculateServiceImpl;
import threadPool.core.ThreadPoolCore;
import threadPool.core.ThreadPoolReflectCore;
import threadPool.thread.PaperBlur;

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
    static ImgController imgCtrl2 = new ImgController();
    static ImgService imgServ = new ImgServiceImpl();
    static ThreadPoolCore conV;

    public IMAGE transPaperStyle(IMAGE img, int maxTreadCount, int... kernelSize) {
        int size = kernelSize.length > 0 ? kernelSize[0] : 131;
        try{
            ThreadPoolReflectCore tprc = new ThreadPoolReflectCore(img.getArgbMatrix(), calcService.getGasKernel(size), maxTreadCount, new PaperBlur());
            tprc.start();
            return new IMAGE(tprc.getData());
        }catch (Exception e){
            conV = new ThreadPoolPaper(img.getArgbMatrix(), calcService.getGasKernel(size), maxTreadCount);
            conV.start();
        }
        return new IMAGE(conV.getData());
    }

    public IMAGE transGrilleStyle(IMAGE img, int grilleType, boolean multiple) {
        double radio = 1;
        int kernelSize = 67;
        switch (grilleType) {
            case 0 -> {
                radio = 0.01024;
                System.out.println(1 / radio);
                kernelSize = 81;
            }
            case 1 -> {
                radio = 0.0425;
                kernelSize = 81;
            }
            case 2 -> radio = 0.04125;
            default -> radio = 0.5;
        }
        System.out.println("adjust...");
        IMAGE ad = AdCtrl.adjustSatAndVal(img, 36, 24);
        System.out.println("adjust over");
        if (multiple) {
            IMAGE gas = BlurCtrl.getQuickGasBlur(ad, kernelSize, 16); // kernelSize
            List<IMAGE> imgList = imgCtrl2.asyncSplit(gas, (int) (1 / radio), true);
            IMAGE ver = imgCtrl.combineImages(imgList, grilleType, true);
            imgList = imgCtrl2.asyncSplit(ver, (int) (1 / (radio * 1.73)), false);
            return imgCtrl.combineImages(imgList, grilleType, false);
        } else {
            IMAGE gas = BlurCtrl.getQuickGasBlur(ad, kernelSize, 16); // kernelSize
            List<IMAGE> imgList = imgCtrl2.asyncSplit(gas, (int) (1 / radio), true);
            return imgCtrl.combineImages(imgList, grilleType, true);
        }
    }

    public IMAGE buildGrille(IMAGE img, double rate, int grilleType){
        rate = Math.min(Math.max(rate, 0), 1);
        IMAGE res = new IMAGE();
        switch (grilleType){
            case Param.GRILLE_MULTIPLE -> {
                List<IMAGE> imgList = imgCtrl2.asyncSplit(img, (int) (1 / rate), true);
                IMAGE ver = imgCtrl.combineImages(imgList, grilleType, true);
                imgList = imgCtrl2.asyncSplit(ver, (int) (1 / (rate * 1.73)), false);
                res = imgCtrl.combineImages(imgList, grilleType, false);
            }
            case Param.GRILLE_VERTICAL -> {
                List<IMAGE> imgList = imgCtrl2.asyncSplit(img, (int) (1 / (rate * 1.73)), true);
                res = imgCtrl.combineImages(imgList, grilleType, true);
            }
            case Param.GRILLE_HORIZONTAL -> {
                List<IMAGE> imgList = imgCtrl2.asyncSplit(img, (int) (1 / rate), false);
                res = imgCtrl.combineImages(imgList, 1, false);
            }
        }
        return res;
    }

    public IMAGE transOilPaintingStyle(IMAGE img) throws Exception {
        return new IMAGE(imgServ.traceImg(img));
    }
}
