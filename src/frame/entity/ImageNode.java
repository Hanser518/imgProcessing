package frame.entity;

import entity.IMAGE;

/**
 * 节点链表
 */
public class ImageNode {

    public IMAGE image;

    public ImageNode prev;

    public ImageNode next;

    public String nodeName;

    public ImageNode(IMAGE image){
        this.image = image;
    }

}
