package Controller;

import Entity.IMAGE;
import Service.Impl.imgServiceImpl;
import Service.imgService;

public class EdgeController {
    static private final BlurController blurCtrl = new BlurController();
    static private final imgService imgServ = new imgServiceImpl();
    public IMAGE getImgEdge(IMAGE img){
        IMAGE gas = blurCtrl.quickGasBlur(img);
        IMAGE raw = new IMAGE(imgServ.getSobelEdge(gas));
        return raw;
        // return new IMAGE(imgServ.dilateImg(raw));
    }
}
