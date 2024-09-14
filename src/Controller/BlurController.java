package Controller;

import Entity.IMAGE;
import Service.ThreadPoolConVService;
import Service.ThreadPoolPaperService;

import static Controller.StylizeController.calcService;

public class BlurController {
    public IMAGE quickGasBlur(IMAGE img){
        ThreadPoolConVService conv = new ThreadPoolConVService(img.getPixelMatrix(), calcService.getGasKernel(1), 16);
        conv.start();
        return new IMAGE(conv.getData());
    }

    public IMAGE getGasBlur(IMAGE img, int size, int maxThreadCount){
        double[][] kernel = calcService.getGasKernel(size);
        ThreadPoolConVService conv = new ThreadPoolConVService(img.getPixelMatrix(), kernel, maxThreadCount);
        conv.start();
        return new IMAGE(conv.getData());
    }


}
