package ServiceTime;

import Entity.Map;
import lombok.Data;

@Data
public class ServiceTimeRequest {

    /**
     * 服务站的数量
     */
    private int stationNum;

    /**
     * 地图类
     */
    private Map warehouseMap;

    /**
     * 机器人的速度
     */
    private double v;

    /**
     * 机器人抬起货架的时间
     */
    private double liftingTime;

    /**
     * 机器人存储货架的时间
     */
    private double storingTime;

    /**
     * 货架的宽度
     */
    private double u;

}
