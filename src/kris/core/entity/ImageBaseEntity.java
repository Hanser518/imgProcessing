package kris.core.entity;

import java.awt.image.BufferedImage;

public abstract class ImageBaseEntity {

    protected final BufferedImage image;
    protected final Integer width, height;

    protected ImageBaseEntity(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    protected ImageBaseEntity(ImageBaseEntity object){
        this.image = object.image;
        this.width = object.width;
        this.height = object.height;
    }


}
