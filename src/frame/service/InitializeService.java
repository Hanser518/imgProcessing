package frame.service;

import javax.swing.*;

public interface InitializeService {

    JFrame initializeMainFrame();

    JLabel initializeCenterLabel();

    void initializeFileList();
}
