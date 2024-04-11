package Entity;

import lombok.Data;

@Data
public class WorkStation {

    /**
     * 点类
     */
    private Point point;

    /**
     * detour的类型
     */
    private String detour;

    /**
     * 拣选站在仓库的方向
     */
    private String direction;

}
