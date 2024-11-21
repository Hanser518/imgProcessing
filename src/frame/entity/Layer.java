package frame.entity;

import entity.IMAGE;

import java.util.ArrayList;
import java.util.List;

public class Layer {

    /**
     * 图层数据列表
     */
    private List<ImageNode> layerList = new ArrayList<>();

    /**
     * 图层指向参数
     */
    private Integer index = null;

    public Layer() {
        IMAGE image = new IMAGE();
        ImageNode node = new ImageNode(image);
        layerList.add(node);
        index = 0;
    }

    public void addLayer(IMAGE img) {
        ImageNode node = new ImageNode(img);
        layerList.add(node);
    }

    public void deleteLayer(Integer index) {
        if (index == null) {
            layerList.clear();
            this.index = null;
            return;
        }
        if (index > layerList.size() || index < 0) {
            return;
        }
        layerList.remove((int) index);
    }

    public void updateLayer(ImageNode node, Integer index) {
        if (index == null || index > layerList.size() || index < 0) {
            return;
        }
        layerList.set(index, node);
    }

    public ImageNode getNode(Integer index) {
        if (index == null || index > layerList.size() || index < 0) {
            return null;
        }
        return layerList.get(index);
    }

    public int[] getRange(){
        if(layerList != null){
            return new int[]{0, layerList.size()};
        }
        return new int[]{0, 0};
    }

    public void setIndex(Integer index){
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }
}
