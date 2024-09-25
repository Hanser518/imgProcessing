package Main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class reflectTest {
    static Object arg = null;

    public void shut(int num){
        for(int i = 0;i < num;i ++){
            System.out.println("shut!");
        }
    }

    public static void main(String[] args) throws Exception {
        initRef(new reflectTest());
        System.out.println("name: " + arg.getClass().getName());
    }

    static void initRef(Object thread) throws Exception {
        Class<?> clz = Class.forName(thread.getClass().getName());
        Constructor<?> conv = clz.getConstructor();
        arg = conv.newInstance();
        Method method = clz.getMethod("shut", int.class);
        method.invoke(arg, 15);
    }
}
