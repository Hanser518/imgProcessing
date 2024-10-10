package test;

import entity.IMAGE;
import entity.test.TestEntity;

import java.io.IOException;

public class newEntityTest {
    public static void main(String[] args) throws IOException {
        String fileName = "bus";
        IMAGE px = new IMAGE(fileName + ".jpg");

        TestEntity te = new TestEntity(px.getPixelMatrix());
    }
}
