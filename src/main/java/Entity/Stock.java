package Entity;

import lombok.Data;

@Data
public class Stock {

    /**
     * 点类
     */
    private Point point;

    /**
     * 计算该stock用的case
     */
    private String distanceFundamentalCase;

}
