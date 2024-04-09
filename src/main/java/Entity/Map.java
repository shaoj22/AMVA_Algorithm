package Entity;

import lombok.Data;

import java.util.List;
@Data
public class Map {

    /**
     * 拣选站的数量
     */
    private int workStationNum;

    /**
     * 拣选站类的List
     */
    private List<WorkStation> workStationDTOList;

    /**
     * 点的数量
     */
    private int pointNum;

    /**
     * 点类的List
     */
    private List<Point> pointDTOList;

    /**
     * 拣选站到货架之间的距离
     */
    private int distanceWorkStation2Stock;

    /**
     * 货架的数量
     */
    private int stockNum;

    /**
     * 货架类的List
     */
    private List<Stock> stockDTOList;

}
