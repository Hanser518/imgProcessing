package Entity;

public class PIXEL {
    public int[] argb;
    public final int x, y;

    public PIXEL(int[] argb, int x, int y){
        this.argb = argb;
        this.x = x;
        this.y = y;
    }

    public PIXEL(){
        this.argb = new int[]{255, 0, 0, 0};
        this.x = 0;
        this.y = 0;
    }

    public PIXEL(int x, int y){
        this.argb = new int[]{255, 0, 0, 0};
        this.x = x;
        this.y = y;
    }
}
