package algorithm.cnnDiscard.main;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String trainDataPath = "database/train";
        String testDataPath = "database/test";
        FileLoad.setTrainFilePath(trainDataPath);
        FileLoad.setTestFilePath(testDataPath);

        List<String> trainLabels = new ArrayList<>(FileLoad.getTrainDirectory().keySet());

    }
}
