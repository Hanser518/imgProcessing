import frame.FrameBase;
import frame2.test.MaxThreadTest;

import javax.swing.*;

public class RunApplication {
    public static void main(String[] args) {
        new MaxThreadTest();
        SwingUtilities.invokeLater(FrameBase::new);
    }
}
