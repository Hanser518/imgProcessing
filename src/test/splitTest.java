package test;

import discard.ImgProcessingController;
import entity.Image;
import service.ICalculateService;
import service.IPictureService;
import service.impl.ICalculateServiceImpl;
import service.impl.IPictureServiceImpl;

import java.io.IOException;
import java.util.List;

public class splitTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    private final IPictureService picServer = new IPictureServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        Image testPx = new Image("rx78.jpg");
        imgCtrl.openMultiThreads();

        // 异步切分
        List<Image> splitList = imgCtrl.asyncSplit(testPx, 17, true);
        for(int i = 0;i < splitList.size();i ++){
            imgCtrl.save(splitList.get(i), "asyncImage" + i);
        }

        // 组合图像
        Image comImg = imgCtrl.combineImages(splitList, 2, true);
        imgCtrl.save(comImg, "combineImage");

        // 等值切分
        splitList = imgCtrl.equalSplit(testPx, 5, true);
        for(int i = 0;i < splitList.size();i ++){
            imgCtrl.save(splitList.get(i), "equalImage" + i);
        }

        // 固定步长切分
        splitList = imgCtrl.valueSplit(testPx, 200, true);
        for(int i = 0;i < splitList.size();i ++){
            imgCtrl.save(splitList.get(i), "valueImage" + i);
        }
    }
}
