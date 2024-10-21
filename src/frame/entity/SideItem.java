package frame.entity;

import java.lang.reflect.Method;

public class SideItem {
    private String itemName;
    private Method method;

    public SideItem(String name, Method method){
        this.itemName = name;
        this.method = method;
    }


    public String getItemName() {
        return itemName;
    }

    public Method getMethod() {
        return method;
    }
}
