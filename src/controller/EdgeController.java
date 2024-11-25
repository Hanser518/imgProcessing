package controller;

import entity.Image;
import service.impl.ImgServiceImpl;
import service.ImgService;

public class EdgeController {
    public static final int SOBEL = 0;
    public static final int PREWITT = 1;
    public static final int MARR = 2;

    static private final BlurController blurCtrl = new BlurController();
    static private final ImgService imgServ = new ImgServiceImpl();
    public Image getImgEdge(Image img) throws Exception {
        Image gas = blurCtrl.quickGasBlur(img);
        Image raw = new Image(imgServ.getSobelEdge(gas));
        return raw;
        // return new Image(imgServ.dilateImg(raw));
    }

    public Image getImgEdge(Image img, int edgeType) throws Exception {
        long set = System.currentTimeMillis();
        Image gas = blurCtrl.quickGasBlur(img);
        Image result = new Image();
        if(edgeType == SOBEL) {
            result = new Image(imgServ.getSobelEdge(gas));
        } else if(edgeType == PREWITT) {
            result = new Image(imgServ.getPrewittEdge(gas));
        } else if(edgeType == MARR) {
            result = new Image(imgServ.getMarrEdge(img));
        } else {
            result = gas;
        }
        System.out.println(System.currentTimeMillis() - set);
        return result;
    }
}
