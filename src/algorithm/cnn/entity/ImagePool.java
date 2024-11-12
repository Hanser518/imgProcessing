package algorithm.cnn.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePool {

    protected List<ImageCNN> imageList = new ArrayList<>();

    /**
     * 载入输入路径下的所有图像
     * @param path  路径
     */
    public ImagePool(String path){
        File[] tempList = new File(path).listFiles();
        if (tempList != null) {
            for(int i = 0;i < tempList.length;i ++){
                if(tempList[i].isFile()){
                    String fileName = tempList[i].getName();
                    String suffix = fileName.substring(fileName.lastIndexOf('.'));
                    if(suffix.equals(".jpg") || suffix.equals(".png")){
                        imageList.add(new ImageCNN(tempList[i].getPath()));
                    }
                }
            }
        }
    }

    public ImagePool(List<ImageCNN> imgList){
        this.imageList = imgList;
    }

    public void setImageList(List<ImageCNN> imgList){
        this.imageList = imgList;
    }

    public Integer getPoolSize() {
        return imageList.size();
    }

    public List<ImageCNN> getImageList(){
        return imageList;
    }
}
