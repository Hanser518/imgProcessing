package Controller;

import Entity.IMAGE;

public class EdgeController {
    static private final BlurController blurCtrl = new BlurController();
    public IMAGE getImgEdge(IMAGE img){
        IMAGE gas = blurCtrl.quickGasBlur(img);
        return gas;
    }
}
