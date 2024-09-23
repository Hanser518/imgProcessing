package Controller;

import Entity.IMAGE;
import Service.IAdjustService;
import Service.ICalculateService;
import Service.Impl.AdjustServiceImpl;
import Service.Impl.ICalculateServiceImpl;

import java.util.Map;

public class AdjustController {
    private static final ICalculateService calcService = new ICalculateServiceImpl();
    public static final IAdjustService adService = new AdjustServiceImpl();

    /**
     * 压缩图像的动态范围
     * @param px    输入图像
     * @return  输出图像
     */
    public IMAGE compressDynamicRange(IMAGE px) {
        int[][] result = adService.compressDynamicRange(px);
        return new IMAGE(result);
    }

    /**
     * 快速调用compressDynamicRange函数
     * @param px    输入图像
     * @return  输出图像
     */
    public IMAGE CDR(IMAGE px) {
        return compressDynamicRange(px);
    }

    /**
     * 调整图像饱和度
     * @param px    输入图像
     * @param value 饱和度调整值，范围为-100~100，超出范围的值将被压缩至-100~100以内
     * @return  输出图像
     */
    public IMAGE AdjustSaturation(IMAGE px, int value){
        return new IMAGE(adService.AdjustSaturation(px, value));
    }

    public IMAGE AdjustSatAndVal(IMAGE px, int saturation, int value){
        int sat = Math.max(-100, Math.min(100, saturation));
        int val = Math.max(-100, Math.min(100, value));
        int[][] pxSaturation = adService.AdjustSaturation(px, sat);
        int[][] pxValue = adService.AdjustValue(new IMAGE(pxSaturation), val);
        return new IMAGE(pxValue);
    }

}
