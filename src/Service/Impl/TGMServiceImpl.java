package Service.Impl;

import Entity.PIXEL;
import Service.ICalculateService;

import java.util.ArrayList;
import java.util.List;

public class TGMServiceImpl implements Runnable {
    private ICalculateService calculateService = new ICalculateServiceImpl();
    int[][] map;
    int width, height;
    int base, top;

    public TGMServiceImpl(int width, int height, int base, int top){
        this.width = width;
        this.height = height;
        this.map = new int[width][height];
        this.base = base;
        this.top = top;
    }

    @Override
    public void run() {
        int x = (int) (Math.random() * width);  // 对应点位坐标
        int y = (int) (Math.random() * height);
        map[x][y] = base;        // 初始化
        List<PIXEL> stack = new ArrayList<>();// 存量栈
        stack.add(new PIXEL(x, y)); // 初始化
        while (!stack.isEmpty()){
            int dir = calculateService.getDirection(map, x, y);
            switch (dir) {
                case 0:
                    x -= 1; y -= 1; break;
                case 1:
                    x -= 1; break;
                case 2:
                    x -= 1; y += 1; break;
                case 3:
                    y -= 1; break;
                case 5:
                    y += 1; break;
                case 6:
                    x += 1; y -= 1; break;
                case 7:
                    x += 1; break;
                case 8:
                    x += 1; y += 1; break;
            }
            if (dir == -1) {
                stack.remove(stack.get(stack.size() - 1));// 移除栈顶
                if (stack.size() != 0) {
                    PIXEL tp = stack.get(stack.size() - 1);
                    x = tp.x;
                    y = tp.y;
                }
            } else {
                PIXEL tp = stack.get(stack.size() - 1);
                int bX = tp.x;
                int bY = tp.y;
                map[x][y] = (int) (map[bX][bY] + (Math.random() - 0.4) * 8);
                map[x][y] = map[x][y] > top ? top : map[x][y];
                map[x][y] = map[x][y] < base ? base : map[x][y];
                stack.add(new PIXEL(x, y));
            }
//            System.out.println("#" + x + " " + y);
        }
    }
}
