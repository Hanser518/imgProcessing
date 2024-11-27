package frame2.test;

import controller.ImgController;
import entity.Image;
import frame2.entity.Event;
import frame2.entity.base.AbstractEventBlur;
import frame2.pipeLine.StreamProcessLine;
import service.ICalculateService;
import service.impl.ICalculateServiceImpl;

import static frame2.constant.PipeLineParam.IMAGE_QUEUE;

public class StreamProcessTest {
    public static ImgController imgCtrl2 = new ImgController();
    public static ICalculateService calcService = new ICalculateServiceImpl();

    public static void main(String[] args) {
        StreamProcessLine TSP = new StreamProcessLine();

        TSP.start();
        Long time0 = System.currentTimeMillis();

        Image img = new Image("7820.jpg");
        double[][] kernel = calcService.getGasKernel((int) (Math.random() * 33) + 11);
        Event e = new AbstractEventBlur(img, kernel, 6);

        while (TSP.isAlive()) {
            Long time = System.currentTimeMillis();
            if ((time - time0) % 20 == 1) {
                TSP.insertEvent(e);
            }
            if (!IMAGE_QUEUE.isEmpty()) {
                Image px = IMAGE_QUEUE.poll();
                // imgCtrl2.showImg(px, "1");
            }
        }
    }
}
