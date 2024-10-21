package frame.service;

import java.io.File;
import java.util.ArrayList;

public interface IFileService {

    /**
     * 获取指定路径下的文件名称
     * @param path
     * @return
     */
    ArrayList<String> getFiles(String path);

    /**
     * 获取指定路径文件列表
     * @param path
     * @return
     */
    ArrayList<File> getFileList(String path);
}
