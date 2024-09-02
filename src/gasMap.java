import Controller.ImgProcessingController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;

import java.io.IOException;

public class gasMap {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        IMAGE px = new IMAGE("index3.png");
        int[][] map = calculateServer.getGasMap(px, 30, 80);
        for(int i = 0;i < map.length;i ++){
            for(int j = 0;j < map[i].length; j++){
                System.out.printf("%2d ", map[i][j]);
            }
            System.out.printf("\n");
        }

    }
}
