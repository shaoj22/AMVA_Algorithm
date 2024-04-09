package Entity;

import lombok.Data;

@Data
public class Aisle {

    /**
     * 点类
     */
    private Point point;

    /**
     * 左："+X"；右："-X"；上："+Y"；下："-Y"；
     */
    private String aisleDirection;

}
