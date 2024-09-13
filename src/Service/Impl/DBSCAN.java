package Service.Impl;

public class DBSCAN {
    int[][] dataMap;
    int distance;
    int w, h;

    DBSCAN(int[][] rawData, int distance, int acThreshold) {
        this.w = rawData.length;
        this.h = rawData[0].length;
        this.distance = distance;
        this.dataMap = new int[w][h];
        for(int i = 0;i < w;i ++){
            for(int j = 0;j < h;j ++){
                this.dataMap[i][j] = rawData[i][j] > acThreshold ? 1 : 0;
            }
        }
    }

    public int[][] getData(){
        return null;
    }
}
