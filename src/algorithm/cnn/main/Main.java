package algorithm.cnn.main;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String dataPath = "output/QuickSave";
        getFiles(dataPath);
    }

    public static ArrayList<String> getFiles(String path) {
        ArrayList<String> files = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                System.out.println("* " + tempList[i].getName()); // .toString().substring(path.length())
                files.add(tempList[i].toString());
            }
            if (tempList[i].isDirectory()) {
                System.out.println("文件夹：" + tempList[i]);
            }
        }
        return files;
    }
}
