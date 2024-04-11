package Entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class Instance {

    /**
     * 地图类
     */
    private Map warehouseMap;

    /**
     * block长方向的数量
     */
    private int blockLengthNum;

    /**
     * block宽方向的数量
     */
    private int blockWidthNum;

    /**
     * 机器人的数量
     */
    private int robotNum;

    /**
     * 拣选站的数量
     */
    private int workStationNum;

    /**
     * 订单的到达率
     */
    private double lambda;

    /**
     * 拣选站到货架之间的距离
     */
    private int distanceWorkStation2Stock;

    /**
     * 服务站的数量
     */
    private int stationNum;

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

    public Instance generateInstance(Instance instance){

        /**
         * 构造warehouseMap
         */

        Map warehouseMap = new Map();

        // 构造每个point
        int warehouseLength = (instance.getBlockLengthNum()+2) * (5+1) + 1;
        int warehouseWidth = (instance.getBlockWidthNum()+2) * (2+1) + 1;
        int pointNum = warehouseWidth * warehouseLength;
        List<Point> pointDTOList = new ArrayList<>();
        int index = 0;
        for (int x = 0; x < warehouseLength; x++) {
            for (int y = 0; y < warehouseWidth; y++) {
                Point point = new Point();
                point.setX(x);
                point.setY(y);
                point.setIndex(index);
                index += 1;
                pointDTOList.add(point);
            }
        }

        // 构造每个workStation
        int workStationNum = instance.getWorkStationNum();
        List<WorkStation> workStationDTOList = new ArrayList<>();
        for (int w = 0; w < workStationNum; w++) {
            WorkStation workStation = new WorkStation();
            // 构造workStation的X和Y坐标
            int workStationX = 0;
            int workStationY = 3 * (w+1);
            for (Point point : pointDTOList) {
                if (point.getX() == workStationX && point.getY() == workStationY) {
                    // 设置point
                    workStation.setPoint(point);
                    // 设置detour
                    workStation.setDetour("0");
                    // 设置direction
                    workStation.setDirection("east");
                    break; // 找到匹配的点后，退出循环
                }
            }
            workStationDTOList.add(workStation);
        }

        // 构造每个aisle和stock
        List<Aisle> aisleDTOList = new ArrayList<>();
        // 先构造aisle
        for (Point point : pointDTOList) {
            // 判断哪些是aisle
            int X = point.getX();
            int Y = point.getY();
            if (!((X%6 != 0 && X/6 > 1) && (Y%3 != 0 && Y/3 > 1))){
                // 设置point
                Aisle aisle = new Aisle();
                aisle.setPoint(point);
                // 设置directions
                HashMap<String, Integer> directions = new HashMap<>();
                directions.put("上", 0);
                directions.put("下", 0);
                directions.put("左", 0);
                directions.put("右", 0);
                if ((Y%3)%2 == 0){
                    directions.put("左", 1);
                }
                else{
                    directions.put("右", 1);
                }
                if ((X%6)%2 == 0){
                    directions.put("上", 1);
                }
                else{
                    directions.put("下", 1);
                }
                aisle.setDirections(directions);
                aisleDTOList.add(aisle);
            }
        }

        // 再构造stock
        int stockNum = instance.getBlockLengthNum() * instance.getBlockWidthNum() * 2 * 5;
        List<Stock> stockDTOList = new ArrayList<>();
        for (Point point : pointDTOList) {
            // 判断哪些是stock
            int X = point.getX();
            int Y = point.getY();
            if ((X%6 != 0 && X/6 > 1) && (Y%3 != 0 && Y/3 > 1)){
                // 设置point
                Stock stock = new Stock();
                stock.setPoint(point);
                // 设置entranceAisleDirection
                if (Y%3 == 1) {
                    // down stock
                    int entranceAisleX = X;
                    int entranceAisleY = Y - 1;
                    // 判断aisle的方向
                    for (Aisle aisle : aisleDTOList) {
                        int aisleX = aisle.getPoint().getX();
                        int aisleY = aisle.getPoint().getY();
                        if (aisleX == entranceAisleX && aisleY == entranceAisleY) {
                            // 设置west or east
                            if (aisle.getDirections().get("左") == 1) {
                                stock.setEntranceAisleDirection("west");
                            }
                            if (aisle.getDirections().get("右") == 1) {
                                stock.setEntranceAisleDirection("east");
                            }
                            // 沿aisle方向找到第一个纵向aisle
                            if (stock.getEntranceAisleDirection().equals("west")) {
                                int newAisleX = aisleX - (aisleX%6);
                                int newAisleY = aisleY;
                                Aisle newAisle = aisleDTOList.stream()
                                        .filter(aisle1 -> aisle1.getPoint().getX() == newAisleX && aisle1.getPoint().getY() == newAisleY)
                                        .findFirst()
                                        .orElse(null);
                                // 设置north or south
                                if (newAisle.getDirections().get("上") == 1) {
                                    stock.setFirstCrossAisleEncountered("north");
                                }
                                if (newAisle.getDirections().get("下") == 1) {
                                    stock.setFirstCrossAisleEncountered("south");
                                }
                            }
                            else {
                                int newAisleX = aisleX + (aisleX%6);
                                int newAisleY = aisleY;
                                Aisle newAisle = aisleDTOList.stream()
                                        .filter(aisle1 -> aisle1.getPoint().getX() == newAisleX && aisle1.getPoint().getY() == newAisleY)
                                        .findFirst()
                                        .orElse(null);
                                // 设置north or south
                                if (newAisle.getDirections().get("上") == 1) {
                                    stock.setFirstCrossAisleEncountered("north");
                                }
                                if (newAisle.getDirections().get("下") == 1) {
                                    stock.setFirstCrossAisleEncountered("south");
                                }
                            }
                        }
                    }
                }
                if (Y%3 == 2) {
                    // up stock
                    int entranceAisleX = X;
                    int entranceAisleY = Y + 1;
                    // 判断aisle的方向
                    for (Aisle aisle : aisleDTOList) {
                        int aisleX = aisle.getPoint().getX();
                        int aisleY = aisle.getPoint().getY();
                        if (aisleX == entranceAisleX && aisleY == entranceAisleY) {
                            // 设置west or east
                            if (aisle.getDirections().get("左") == 1) {
                                stock.setEntranceAisleDirection("west");
                            }
                            if (aisle.getDirections().get("右") == 1) {
                                stock.setEntranceAisleDirection("east");
                            }
                            // 沿aisle方向找到第一个纵向aisle
                            if (stock.getEntranceAisleDirection().equals("west")) {
                                int newAisleX = aisleX - (aisleX%6);
                                int newAisleY = aisleY;
                                Aisle newAisle = aisleDTOList.stream()
                                        .filter(aisle1 -> aisle1.getPoint().getX() == newAisleX && aisle1.getPoint().getY() == newAisleY)
                                        .findFirst()
                                        .orElse(null);
                                // 设置north or south
                                if (newAisle.getDirections().get("上") == 1) {
                                    stock.setFirstCrossAisleEncountered("north");
                                }
                                if (newAisle.getDirections().get("下") == 1) {
                                    stock.setFirstCrossAisleEncountered("south");
                                }
                            }
                            else {
                                int newAisleX = aisleX + (aisleX%6);
                                int newAisleY = aisleY;
                                Aisle newAisle = aisleDTOList.stream()
                                        .filter(aisle1 -> aisle1.getPoint().getX() == newAisleX && aisle1.getPoint().getY() == newAisleY)
                                        .findFirst()
                                        .orElse(null);
                                // 设置north or south
                                if (newAisle.getDirections().get("上") == 1) {
                                    stock.setFirstCrossAisleEncountered("north");
                                }
                                if (newAisle.getDirections().get("下") == 1) {
                                    stock.setFirstCrossAisleEncountered("south");
                                }
                            }
                        }
                    }
                }
                stockDTOList.add(stock);
            }
        }
        warehouseMap.setPointNum(pointNum);
        warehouseMap.setPointDTOList(pointDTOList);
        warehouseMap.setWorkStationNum(workStationNum);
        warehouseMap.setWorkStationDTOList(workStationDTOList);
        warehouseMap.setStockNum(stockNum);
        warehouseMap.setStockDTOList(stockDTOList);
        warehouseMap.setDistanceWorkStation2Stock(instance.getDistanceWorkStation2Stock());

        instance.setWarehouseMap(warehouseMap);

        return instance;
    }

}
