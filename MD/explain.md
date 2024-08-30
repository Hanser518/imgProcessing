# 缩写解释
* PMCT： picture matrix calculate thread ,图像矩阵计算线程
***

public Pixel[][] imgEnhance(Pixel[][] img, double theta) throws IOException {

int width = img.length;

int height = img[0].length;

Pixel[][] result = new Pixel[width][height];

if(theta < 0.5) theta = 0.5;

        int[] G_list = getGList(img);   // 获取图像灰度分布比例
        System.out.println("G_list:");
        for(int i = 0;i < G_list.length;i ++){
            System.out.println(G_list[i] + "\t\t"); // i + ":" +
            // if(i % 2 == 1) System.out.println();
        }
        int crest;  // 图像最高有效灰度级数
        int sub_sum = 0;
        for(crest = G_list.length - 1; crest > 0; crest --){
            if(G_list[crest] > width * height * 0.05 - sub_sum) break;
            else sub_sum += G_list[crest];
        }
        int trough; // 图像最低有效灰度级数
        sub_sum = 0;
        for(trough = 0; trough < crest; trough ++){
            if(G_list[trough] > width * height * 0.05 - sub_sum) break;
            else sub_sum += G_list[trough];
        }

        // 计算获取两个阈值
        double goldenRatio = 0.618;
        double threshold1 = (crest - trough) * (goldenRatio) + trough;
        double threshold2 = threshold1 * (goldenRatio);

        // 计算阈值所对应的灰度值
        double[] G_ratio = new double[G_list.length];
        G_ratio[0] = (double)G_list[0] / (width * height);
        for(int i = 1;i < G_list.length;i ++){
            G_ratio[i] = G_ratio[i - 1] + (double)G_list[i] / (width * height);
        }
        System.out.println("\nG_ratio:");
        for(int i = 0;i < G_list.length;i ++){
            System.out.println(G_ratio[i] + "\t\t"); //i + ":" +
            // if(i % 2 == 1) System.out.println();
        }
        int index1 = (int) (255 * (G_ratio[(int) threshold1]
                + (G_ratio[(int) threshold1 + 1] - G_ratio[(int) threshold1])
                * (threshold1 - (int) threshold1)));
        int index2 = (int) (255 * (G_ratio[(int) threshold2]
                + (G_ratio[(int) threshold2 + 1] - G_ratio[(int) threshold2])
                * (threshold2 - (int) threshold2)));
        System.out.println("\nCrest:" + crest + "\tTrough:" + trough);
        System.out.println("Threshold1:" + threshold1 + "\tThreshold2:" + threshold2);
        System.out.println("Index1:" + index1 + "\tIndex2:" + index2);

        // 对图像进行增幅
        double maxIllu = Math.min(theta * Math.pow(crest - trough, 2), 1024);
        System.out.println("maxIllumination:" + maxIllu);
        double a1 = maxIllu / (1 - 2 * index1 + index1 * index1);
        double b1 = -a1 * index1 * 2;
        double c1 = maxIllu - a1 - b1 + 1;
        double top = a1 * index2 * index2 + b1 * index2 + c1;
        double a2 = (maxIllu - top) / (1 - 2 * index2 + index2 * index2);
        double b2 = -a2 * index2 * 2;
        double c2 = maxIllu - a2 - b2 - top + 1;
        System.out.println("a1:" + a1 + "\tb1:" + b1 + "\tc1:" + c1);
        System.out.println("a2:" + a2 + "\tb2:" + b2 + "\tc2:" + c2);
        float count = 0;
        for(int i = 0;i < width;i ++){
            for(int j = 0;j < height;j ++){
                int r = img[i][j].r;
                int g = img[i][j].g;
                int b = img[i][j].b;
                int gray = img[i][j].gray;
                double num = 1;
                if(img[i][j].gray <= index1){
                    num = (a1 * gray * gray + b1 * gray + c1) / 255 + 1;        // 第一次增幅
                }
                if(img[i][j].gray <= index2){
                    double c = (a2 * gray * gray + b2 * gray + c2) / 255 + 1;   // 第二次增幅
                    num *= c;
                }
                double max = Math.max(r, Math.max(g, b));
                while(max * num > 254.0)
                    num *= (250.0 / (max * num));
                result[i][j] = new Pixel((int) (r * num), (int) (g * num), (int) (b * num));
                result[i][j].calcuG();
                // float percent = count ++ / (width * height);
                // System.out.printf("\r%.4f",percent);
            }
        }
        System.out.println("____OK");
        return result;
    }