package algorithm.cnn.main;

import controller.ImgController;
import entity.IMAGE;
import entity.test.TestEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static ImgController imgCtrl2 = new ImgController();

    public static void main(String[] args) throws IOException {
        String dataPath = "output/";
        List<String> file = getFiles(dataPath + "QuickSave");
//        int count = 0;
//        TestEntity entity;
//        for (String path : file) {
//            entity = new TestEntity(path);
//            // imgCtrl2.showImg(new IMAGE(entity.getImg()), count + " ");
//            count++;
//            System.out.print("\r" + count);
//        }
        Map<String, List<String>> db = getLabelAndFilePath(dataPath);
        db.forEach((key, value) -> {
            System.out.println(key);
            value.forEach(str -> {
                System.out.println("\t" + str);
            });
        });
    }

    public static Map<String, List<String>> getLabelAndFilePath(String path) {
        Map<String, List<String>> result = new HashMap<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList != null) {
            for (File value : tempList) {
                if (value.isDirectory()) {
                    String label = value.getName();
                    List<String> filePaths = getFiles(path + "/" + label);
                    result.put(label, filePaths);
                }
            }
        }
        return result;
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
        }
        return files;
    }
}
