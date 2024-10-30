package service.impl;

import entity.IMAGE;
import service.IAdjustService;
import service.ICalculateService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class AdjustServiceImpl implements IAdjustService {
    private static final ICalculateService calcService = new ICalculateServiceImpl();

    @Override
    public int[][] compressDynamicRange(IMAGE px) {
        Map<Integer, Double> rate = calcService.getActiveAccumulateRate(px);

        // 1.判断方向
        double top = 1.518; // 1.05;
        double center = 1.0;
        double bottom = 0.874; // 1 - 0.318 / 2;
        if (rate.get(4) < 0.5) {
            top = 1.102;
            bottom = 0.692;
            System.out.println("acccccccc!!!");
        }

        // 2.计算关键点、计算起始/终止位点
        int starPoint = 0;
        int endPoint = 0;
        int base = 0;
        for (int i = 0; i < rate.size(); i++) {
            if (rate.get(i) >= 0.05 && starPoint == 0) {
                starPoint = (int) (base + (Math.pow(2, i) * (0.05 / rate.get(i))));
            }
            if (rate.get(i) >= 0.95 && endPoint == 0) {
                endPoint = (int) (base + (Math.pow(2, i) * ((0.95 - rate.get(i - 1)) / (rate.get(i) - rate.get(i - 1)))));
            }
            base += (int) Math.pow(2, i);
        }
        int centerPoint = (int) (starPoint + (endPoint - starPoint) * 0.378);
        System.out.println(starPoint + " " + centerPoint + " " + endPoint);

        // 3.计算曲线
        double a1 = (center - top) / Math.pow(centerPoint - starPoint, 2);
        double b1 = -2 * a1 * starPoint;
        double c1 = top - Math.pow(starPoint, 2) * a1 - starPoint * b1;
        double a2 = (center - bottom) / Math.pow(centerPoint - endPoint, 2);
        double b2 = -2 * a2 * endPoint;
        double c2 = bottom - Math.pow(endPoint, 2) * a2 - endPoint * b2;

        // 4.计算图像
        int[][] result = px.getPixelMatrix();
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                double enhance = 1;
                int r = ((result[i][j] >> 16) & 0xFF);
                int g = ((result[i][j] >> 8) & 0xFF);
                int b = (result[i][j] & 0xFF);
                int ac = px.getAcValue(result[i][j]);
                if (ac < centerPoint) {
                    enhance = a1 * Math.pow(ac, 2) + b1 * ac + c1;
                } else {
                    enhance = a2 * Math.pow(ac, 2) + b2 * ac + c2;
                }
                int r0 = r * enhance > 255 ? 254 : (int) (r * enhance);
                int g0 = g * enhance > 255 ? 254 : (int) (g * enhance);
                int b0 = b * enhance > 255 ? 254 : (int) (b * enhance);
                r = r0 == 0 ? 1 : r0;
                g = g0 == 0 ? 1 : g0;
                b = b0 == 0 ? 1 : b0;
                result[i][j] = (255 << 24) | (r << 16) | (g << 8) | b;
            }
        }
        return result;
    }

    @Override
    public int[][] AdjustSaturation(IMAGE px, int sat) {
        int s = Math.max(-100, Math.min(100, sat));
        double adRate = 1.0 + s * 0.005;    // 调整最大范围为0.5 ~ 1.5
        double[][][] hsv = px.RGB2HSV();
        for (int i = 0; i < px.getWidth(); i++) {
            for (int j = 0; j < px.getHeight(); j++) {
                hsv[i][j][1] = Math.min(hsv[i][j][1] * adRate, 1.0);
            }
        }
        return px.HSV2RGB(hsv);
    }

    @Override
    public int[][] AdjustValue(IMAGE px, int val) {
        int v = Math.max(-100, Math.min(100, val));
        double adRate = 1.0 + v * 0.005;    // 调整最大范围为0.5 ~ 1.5
        double[][][] hsv = px.RGB2HSV();
        for (int i = 0; i < px.getWidth(); i++) {
            for (int j = 0; j < px.getHeight(); j++) {
                hsv[i][j][2] = Math.min(hsv[i][j][2] * adRate, 1.0);
            }
        }
        return px.HSV2RGB(hsv);
    }

    @Override
    public IMAGE getReizedImage(IMAGE px, int wSize, int hSize) {
        BufferedImage raw = px.getImg();
        Image scaledImage = raw.getScaledInstance(wSize, hSize, Image.SCALE_AREA_AVERAGING);
        // 将调整尺寸后的图像转换为BufferedImage
        BufferedImage result = new BufferedImage(wSize, hSize, BufferedImage.TYPE_INT_ARGB);
        result.getGraphics().drawImage(scaledImage, 0, 0, null);
        return new IMAGE(result);
    }

    @Override
    public int[][] test(IMAGE px, int value) {
        double[][][] hsv = px.RGB2HSV();

        double p1x = 0.33 * Math.sin(Math.toRadians(0));
        double p1y = 0.33 * Math.cos(Math.toRadians(0));

        double p2x = 0.66 * Math.sin(Math.toRadians(120));
        double p2y = 0.66 * Math.cos(Math.toRadians(120));

        double p3x = 0.54 * Math.sin(Math.toRadians(240));
        double p3y = 0.54 * Math.cos(Math.toRadians(240));

        double pax = 1 * Math.sin(Math.toRadians(value));
        double pay = 1 * Math.cos(Math.toRadians(value));

        double p1d = Math.sqrt(Math.pow(pax - p1x, 2) + Math.pow(pay - p1y, 2));
        double p2d = Math.sqrt(Math.pow(pax - p2x, 2) + Math.pow(pay - p2y, 2));
        double p3d = Math.sqrt(Math.pow(pax - p3x, 2) + Math.pow(pay - p3y, 2));

        double rate1 = 1 / p1d;
        double rate2 = 1 / p2d;
        double rate3 = 1 / p3d;

        System.out.printf("%3d: %1.2f, %1.2f, %1.2f\n", value, rate1, rate2, rate3);

        for (int i = 0; i < px.getWidth(); i++) {
            for (int j = 0; j < px.getHeight(); j++) {
                double h = hsv[i][j][0];
                double s = hsv[i][j][1];
                if (h > 300 || h <= 60) {
                    s = Math.min(s * rate1, 1.0);
                }
                else if(h > 60 && h <= 180){
                    s = Math.min(s * rate2, 1.0);
                }
                else {
                    s = Math.min(s * rate3, 1.0);
                }
                hsv[i][j][1] = s;
            }
        }
        return px.HSV2RGB(hsv);
    }
}
