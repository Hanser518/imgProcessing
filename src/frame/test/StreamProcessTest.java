package frame.test;

import algorithm.cnn.service.ConvCalcService;
import controller.ImgController;
import entity.Image;
import frame.entity.base.AbstractEventBlur;
import frame.pipeLine.ThreadStreamProcess;
import service.ICalculateService;
import service.impl.ICalculateServiceImpl;

import static frame.constant.PipeLineParam.AVAILABLE_THREAD;
import static frame.constant.PipeLineParam.IMAGE_QUEUE;

public class StreamProcessTest {
    public static ImgController imgCtrl2 = new ImgController();
    public static ICalculateService calcService = new ICalculateServiceImpl();

    public static void main(String[] args) {
        ThreadStreamProcess TSP = new ThreadStreamProcess();

        TSP.start();
        Long time0 = System.currentTimeMillis();

        Image img = new Image("7820.jpg");
        Image img2 = new Image("bus.jpg");
        Image img3 = new Image("building.jpg");

        while (TSP.isAlive()) {
            Long time = System.currentTimeMillis();
            if ((time - time0) % 20 == 1) {
                double[][] kernel = calcService.getGasKernel((int) (Math.random() * 3) + 1);
                TSP.insertTask(AbstractEventBlur.class, img, kernel, (int) (Math.random() * 9) + 1);
            }
            if (!IMAGE_QUEUE.isEmpty()) {
                Image px = IMAGE_QUEUE.poll();
                // imgCtrl2.showImg(px, "1");
            }
        }
    }
}
