package algorithm;

public class NULL {
    public static void main(String[] args) {
        long set = System.currentTimeMillis();
        for(int num = 1;num <= 10000;num ++){
            int bitLen = getLen(num);
            int i;
            for(i = 0;i < bitLen / 2;i ++){
                int r = (num >> (bitLen - i - 1)) & 1;
                int l = (num >> i) & 1;
                if(r != l){
                    if (r > l){
                        System.out.println(num + " 原数");
                    } else{
                        System.out.println(num + " 翻转");
                    }
                    break;
                }
            }
            if (i == bitLen / 2) {
                System.out.println(num + " 相同");
            }
        }
        System.out.println("take: " + (System.currentTimeMillis() - set));
    }

    public static int getLen(int num){
        int len = 0;
        while(num > 0){
            num >>= 1;
            len ++;
        }
        return len;
    }
}
