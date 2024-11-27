package frame.service.impl;

import controller.ProcessingController;
import frame.FrameBase;
import frame.entity.Param;
import frame.service.IFileService;
import frame.service.InitializeService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InitializeServiceImpl implements InitializeService {

    private static final ProcessingController pcsCtrl = new ProcessingController();

    private static final IFileService fileService = new IFileServiceImpl();

    @Override
    public JFrame initializeMainFrame() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        JFrame frame = new JFrame("Visible");

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Param.screenWidth = screenSize.width;
        Param.screenHeight = screenSize.height;
        Param.frameWidth = (int) (Param.screenWidth * Param.rate);
        Param.frameHeight = (int) (Param.screenHeight * Param.rate);
        // 窗口宽高
        frame.setBounds(
                (int) (Param.screenWidth * (1 - Param.rate) / 2),
                (int) (Param.screenHeight * (1 - Param.rate) / 2),
                Param.frameWidth,
                Param.frameHeight + 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        return frame;
    }

    @Override
    public JLabel initializeCenterLabel() {
        JLabel centerLabel = new JLabel(new ImageIcon(pcsCtrl.resizeImage(Param.image, Param.rate, pcsCtrl.RESIZE_ENTIRETY).getRawFile()));
        centerLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
        centerLabel.addMouseListener(new MouseAdapter() {
            boolean press = false;
            @Override
            public void mousePressed(MouseEvent e) {
                press = true;
                System.out.println(press);
                int x = e.getX();
                int y = e.getY();
                System.out.println(x + " " + y);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                System.out.println(x + " " + y);
            }
        });
        return centerLabel;
    }

    @Override
    public void initializeFileList() {
        Param.fileList.clear();
        String path_head = "./";
        Param.fileList = fileService.getFileList(path_head);
        FrameBase.updateSidePanel();
    }
}
