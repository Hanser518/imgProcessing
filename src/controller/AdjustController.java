package controller;

import entity.Image;
import service.IAdjustService;
import service.ICalculateService;
import service.impl.AdjustServiceImpl;
import service.impl.ICalculateServiceImpl;

public class AdjustController {
    private static final ICalculateService calcService = new ICalculateServiceImpl();
    public static final IAdjustService adService = new AdjustServiceImpl();

    /**
     * 压缩图像的动态范围
     * @param px    输入图像
     * @return  输出图像
     */
    public Image compressDynamicRange(Image px) {
        int[][] result = adService.compressDynamicRange(px);
        return new Image(result);
    }

    /**
     * 快速调用compressDynamicRange函数
     * @param px    输入图像
     * @return  输出图像
     */
    public Image CDR(Image px) {
        return compressDynamicRange(px);
    }

    /**
     * 调整图像饱和度
     * @param px    输入图像
     * @param value 饱和度调整值，范围为-100~100，超出范围的值将被压缩至-100~100以内
     * @return  输出图像
     */
    public Image adjustSaturation(Image px, int value){
        return new Image(adService.AdjustSaturation(px, value));
    }

    /**
     * 调整图像饱和度和亮度
     * @param px    输入图像
     * @param saturation    饱和度调整值，范围为-100~100，超出范围的值将被压缩至-100~100以内
     * @param value 亮度调整值，范围为-100~100，超出范围的值将被压缩至-100~100以内
     * @return  返回值
     */
    public Image adjustSatAndVal(Image px, int saturation, int value){
        int sat = Math.max(-100, Math.min(100, saturation));
        int val = Math.max(-100, Math.min(100, value));
        int[][] pxSV = adService.AdjustSaturationAndValue(px, sat, val);
        return new Image(pxSV);
    }

    public Image test(Image px, int value) {
        return new Image(adService.test(px, value));
    }

}
