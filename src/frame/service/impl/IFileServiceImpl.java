package frame.service.impl;

import frame.service.IFileService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IFileServiceImpl implements IFileService {

    @Override
    public ArrayList<String> getFiles(String path) {
        ArrayList<String> files = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        System.out.println("FilePath: " + path + "\t>>>");
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i].toString());
                System.out.println(tempList[i].toString() + "\tFile");
            }
            if (tempList[i].isDirectory()) {
                files.add(tempList[i].toString());
                System.out.println(tempList[i].toString() + "\tDir");
            }
        }
        return files;
    }

    @Override
    public ArrayList<File> getFileList(String path) {
        File file = new File(path);
        File[] tempList = file.listFiles();
        return new ArrayList<>(List.of(tempList));
    }

    @Override
    public String getFileType(File file) {
        String filePath = file.getName();
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1).toUpperCase();
        return suffix;
    }
}
