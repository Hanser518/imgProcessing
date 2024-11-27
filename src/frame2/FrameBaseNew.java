package frame2;

import entity.Image;
import frame2.entity.Event;
import frame2.entity.base.AbstractEventBlur;
import frame2.pipeLine.AutoUpdateLine;
import frame2.pipeLine.StreamProcessLine;
import frame2.test.MaxThreadTest;

import javax.swing.*;
import java.awt.*;

import static frame2.constant.JFrameModuleAndParam.*;
import static frame2.test.StreamProcessTest.calcService;

public class FrameBaseNew {

    protected StreamProcessLine streamProcess = new StreamProcessLine();

    protected AutoUpdateLine updateLine = new AutoUpdateLine();


    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        new FrameBaseNew();
    }

    public FrameBaseNew() {
        new MaxThreadTest();
        streamProcess.start();
        updateLine.start();
        initModules();
        JButton test = buildFunctionButton("test");

        int rgbValue = (255 << 24) | (187 << 16) | (104 << 8) | 54;
        Image baseImage = new Image(4000, 4000, rgbValue);
        double[][] baseKernel = calcService.getGasKernel(11);

        test.addActionListener(ac -> {
            streamProcess.insertTask(AbstractEventBlur.class, baseImage, baseKernel, 4);
        });

        JButton test2 = buildFunctionButton("test");
        test2.addActionListener(ac -> {
            streamProcess.insertTask(AbstractEventBlur.class, baseImage, baseKernel, 4);
        });

        operationPanel.add(test);
        operationPanel.add(test2);
        window.add(operationPanel, BorderLayout.WEST);
        window.add(mainImageLabel, BorderLayout.CENTER);
        window.add(headPanel, BorderLayout.NORTH);


        window.revalidate();
        window.repaint();
    }

}
