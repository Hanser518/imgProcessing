package Service.Extends.Thread;

import Entity.EventPool;
import Service.CORE.ThreadCore;

public class ConfActive extends ThreadCore {
    int threshold;

    public ConfActive() {
        super();
    }

    public ConfActive(EventPool ep, int threshold) {
        super(ep);
        this.threshold = threshold;
    }

    @Override
    public int matrixCalc(int x, int y) {
        int r, g, b;
        int rate = 0;
        int active = 0;
        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel.length; j++) {
                if (kernel[i][j] == 1) {
                    r = ((data[x + i][y + j] >> 16) & 0xFF) > threshold ? 1 : 0;
                    g = ((data[x + i][y + j] >> 8) & 0xFF) > threshold ? 1 : 0;
                    b = (data[x + i][y + j] & 0xFF) > threshold ? 1 : 0;
                    if (i == kernel.length / 2 && j == kernel.length / 2) {
                        if ((r + g + b) >= 1)
                            active += kernel.length * kernel.length;
                    } else {
                        active += ((r + g + b) >= 1 ? 1 : 0);
                    }
                    rate++;
                }
            }
        }
        if (active > 2) return 1;
        else return 0;
    }
}
