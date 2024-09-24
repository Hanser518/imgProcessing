package Controller;

import Entity.IMAGE;
import Service.IAdjustService;
import Service.Impl.AdjustServiceImpl;

public class processingController {
    // 缩放类型预设
    public final Integer RESIZE_ENTIRETY = 0;
    public final Integer RESIZE_LANDSCAPE = 1;
    public final Integer RESIZE_VERTICAL = 2;

    private static final IAdjustService adService = new AdjustServiceImpl();

    /**
     * 改变图像大小
     */
    public IMAGE resizeImage(IMAGE img, double radio, int type){
        if(type == 0){
            return adService.getReizedImage(img, (int) (img.getWidth() * radio), (int) (img.getHeight() * radio));
        }else if(type == 1){
            return adService.getReizedImage(img, (int) (img.getWidth() * radio), img.getHeight());
        }else if(type == 2){
            return adService.getReizedImage(img, img.getWidth(), (int) (img.getHeight() * radio));
        }
        return img;
    }
}
