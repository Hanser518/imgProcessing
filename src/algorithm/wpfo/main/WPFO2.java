package algorithm.wpfo.main;

import algorithm.edgeTrace.entity.Node;
import algorithm.edgeTrace.main.EdgeTrace;
import entity.Image;

public class WPFO2 implements Runnable{

    private static EdgeTrace edgeTraceService;
    private static Integer focusCount = 0;

    public WPFO2(Image px){
        edgeTraceService = new EdgeTrace(px);


    }

    @Override
    public void run() {
        // 获取
        edgeTraceService.start(EdgeTrace.PATTERN_ONE);
        for (Node node : edgeTraceService.getPathList()){
            if(node.getNodeSize() > 8){

            }
        }
    }
}
