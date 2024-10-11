package algorithm;

public class NULL {
    public static void main(String[] args) {
        for(int num = 1;num < 64;num ++){
            String binary = Integer.toBinaryString(num);
            int bitLen = binary.length();
            int i;
            for(i = 0;i < bitLen / 2;i ++){
                int r = (num >> (bitLen - i - 1)) & 1;
                int l = (num >> i) & 1;
                if(r != l){
                    if (r > l){
                        System.out.println(num + " 原数\n");
                        break;
                    } else{
                        System.out.println(num + " 翻转\n");
                        break;
                    }
                }
            }
            if (i == bitLen / 2) {
                System.out.println(num + " 相同\n");
            }
        }

    }
}
