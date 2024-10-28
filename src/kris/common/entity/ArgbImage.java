package kris.common.entity;

import kris.core.entity.ImageBaseEntity;

import java.awt.image.BufferedImage;

public class ArgbImage extends ImageBaseEntity {



    protected ArgbImage(BufferedImage image) {
        super(image);
    }

    protected ArgbImage(ImageBaseEntity object){
        super(object);
    }
}
