package controller;

import entity.IMAGE;
import service.impl.ImgServiceImpl;
import service.ImgService;

public class EdgeController {
    public static final int SOBEL = 0;
    public static final int PREWITT = 1;
    public static final int MARR = 2;

    static private final BlurController blurCtrl = new BlurController();
    static private final ImgService imgServ = new ImgServiceImpl();
    public IMAGE getImgEdge(IMAGE img) throws Exception {
        IMAGE gas = blurCtrl.quickGasBlur(img);
        IMAGE raw = new IMAGE(imgServ.getSobelEdge(gas));
        return raw;
        // return new IMAGE(imgServ.dilateImg(raw));
    }

    public IMAGE getImgEdge(IMAGE img, int edgeType) throws Exception {
        long set = System.currentTimeMillis();
        IMAGE gas = blurCtrl.quickGasBlur(img);
        IMAGE result = new IMAGE();
        if(edgeType == SOBEL) {
            result = new IMAGE(imgServ.getSobelEdge(gas));
        } else if(edgeType == PREWITT) {
            result = new IMAGE(imgServ.getPrewittEdge(gas));
        } else if(edgeType == MARR) {
            result = new IMAGE(imgServ.getMarrEdge(img));
        } else {
            result = gas;
        }
        System.out.println(System.currentTimeMillis() - set);
        return result;
    }
}
