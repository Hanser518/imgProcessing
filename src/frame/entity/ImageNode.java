package frame.entity;

import entity.Image;

/**
 * 节点链表
 */
public class ImageNode {

    public Image image;

    public ImageNode prev;

    public ImageNode next;

    public String nodeName;

    public ImageNode(Image image){
        this.image = image;
    }

}
