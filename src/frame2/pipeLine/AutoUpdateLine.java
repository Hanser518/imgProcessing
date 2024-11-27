package frame2.pipeLine;

import entity.Image;

import javax.swing.*;

import static frame2.constant.PipeLineParam.*;
import static frame2.constant.JFrameModuleAndParam.*;

public class AutoUpdateLine extends Thread {

    private static int record = IMAGE_QUEUE.size();

    public static void checkImageQueue() {
        if(record != IMAGE_QUEUE.size()) {
            System.out.println("CHECK IMAGE QUEUE: " + IMAGE_QUEUE.size());
            record = IMAGE_QUEUE.size();
        }
        if (!IMAGE_QUEUE.isEmpty()) {
            Image img = IMAGE_QUEUE.poll();
            mainImageLabel.setIcon(new ImageIcon(img.getRawFile()));
            window.revalidate(); // 重新验证布局
            window.repaint(); // 重新绘制组件
        }
    }

    @Override
    public void run() {
        while (PIPELINE_ALIVE) {
            checkImageQueue();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
