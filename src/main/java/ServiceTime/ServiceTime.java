package ServiceTime;

import Entity.Map;
import Entity.Stock;
import Entity.WorkStation;

import java.util.List;

public class ServiceTime {
    public static ServiceTimeResponse runner(ServiceTimeRequest serviceTimeRequest) {

        /**
         * 输入
         */

        Map warehouseMap = serviceTimeRequest.getWarehouseMap();
        int stationNum = serviceTimeRequest.getStationNum();
        int workStationNum = warehouseMap.getWorkStationNum();
        int stockNum = warehouseMap.getStockNum();
        double v = serviceTimeRequest.getV();
        double liftingTime = serviceTimeRequest.getLiftingTime();
        double storingTime = serviceTimeRequest.getStoringTime();
        List<WorkStation> workStationDTOList = warehouseMap.getWorkStationDTOList();
        List<Stock> stockDTOList = warehouseMap.getStockDTOList();

        /**
         * 初始化
         */

        double[][] dsw = new double[stockNum][workStationNum];
        double[][] dws = new double[workStationNum][stockNum];
        double[][] dss = new double[stockNum][stockNum];

        double[][] tsw = new double[stockNum][workStationNum];
        double[][] tws = new double[workStationNum][stockNum];
        double[][] tss = new double[stockNum][stockNum];

        // note. 0 index 用于占位
        double[] ESs = new double[stationNum+1];
        double[] ESs2 = new double[stationNum+1];

        /**
         * 计算stock到workStation的距离
         */

        for (int s = 0; s < stockDTOList.size(); s++) {
            for (int w = 0; w < workStationDTOList.size(); w++) {
                dsw[s][w] = calStock2WorkStationDistance(stockDTOList.get(s), workStationDTOList.get(w));
            }
        }

        /**
         * 计算stock到stock的距离
         */

        for (int s1 = 0; s1 < stockDTOList.size(); s1++) {
            for (int s2 = 0; s2 < stockDTOList.size(); s2++) {
                dss[s1][s2] = calStock2StockDistance(stockDTOList.get(s1),stockDTOList.get(s2));
            }
        }

        /**
         * 计算workStation到stock的距离
         */

        for (int w = 0; w < workStationDTOList.size(); w++) {
            for (int s = 0; s < stockDTOList.size(); s++) {
                dws[w][s] = dsw[s][w];
            }
        }

        /**
         * 计算stock到workStation的时间
         */

        for (int s = 0; s < stockNum; s++) {
            for (int w = 0; w < workStationNum; w++) {
                tsw[s][w] = dsw[s][w]/v + liftingTime;
            }
        }

        /**
         * 计算stock到stock的时间
         */

        for (int s1 = 0; s1 < stockNum; s1++) {
            for (int s2 = 0; s2 < stockNum; s2++) {
                tss[s1][s2] = dss[s1][s2];
            }
        }

        /**
         * 计算workStation到stock的时间
         */

        for (int w = 0; w < workStationNum; w++) {
            for (int s = 0; s < stockNum; s++) {
                tws[w][s] = tsw[s][w] + storingTime;
            }
        }

        /**
         * 计算workStation到stock的一阶矩ESs[1]和二阶矩ESs2[1]
         */

        double sumTws = 0;
        double sumTws2 = 0;
        for (int w = 0; w < workStationNum; w++) {
            for (int s = 0; s < stockNum; s++) {
                sumTws += tws[w][s];
                sumTws2 += tws[w][s]*tws[w][s];
            }
        }
        ESs[1] = sumTws/(workStationNum*stockNum);
        ESs2[1] = sumTws2/(workStationNum*stockNum);

        /**
         * 计算stock到stock的一阶矩ESs[2]和二阶矩ESs2[2]
         */

        double sumTss = 0;
        double sumTss2 = 0;
        for (int s1 = 0; s1 < stockNum; s1++) {
            for (int s2 = 0; s2 < stockNum; s2++) {
                sumTss += tss[s1][s2];
                sumTss2 += tss[s1][s2]*tss[s1][s2];
            }
        }
        ESs[2] = sumTss/(stockNum*stockNum);
        ESs2[2] = sumTss2/(stockNum*stockNum);

        /**
         * 计算stock到workStation的一阶矩ESs[3]和二阶矩ESs2[3]
         */

        double sumTsw = 0;
        double sumTsw2 = 0;
        for (int s = 0; s < stockNum; s++) {
            for (int w = 0; w < workStationNum; w++) {
                sumTsw += tws[w][s];
                sumTsw2 += tws[w][s]*tws[w][s];
            }
        }
        ESs[3] = sumTsw/(workStationNum*stockNum);
        ESs2[3] = sumTsw2/(workStationNum*stockNum);

        /**
         * 输出
         */

        ServiceTimeResponse serviceTimeResponse = new ServiceTimeResponse();
        serviceTimeResponse.setESs(ESs);
        serviceTimeResponse.setESs2(ESs2);

        return serviceTimeResponse;
    }

    private static double calStock2WorkStationDistance(Stock stock, WorkStation workStation){
        double u = 1.0;
        double distance = 0.0;
        // ws的坐标
        int Xws = workStation.getPoint().getX();
        int Yws = workStation.getPoint().getY();
        // 计算detour
        double detour;
        if (workStation.getDetour().equals("0")) {
            detour = 0.0;
        }
        else {
            detour = 2 * u;
        }

        // case1: 如果workStation在west(east)且stock的entrance在west(east)
        if ((workStation.getDirection().equals("west") && stock.getEntranceAisleDirection().equals("west"))
                || (workStation.getDirection().equals("east") && stock.getEntranceAisleDirection().equals("east"))) {
            // TODO：确定case1距离的计算方式
            // le的坐标
            int Xle = 0;
            int Yle = 0;
            if (stock.getPoint().getY()%3 == 1) {
                // down stock
                Xle = stock.getPoint().getX();
                Yle = stock.getPoint().getY() - 1;
            }
            if (stock.getPoint().getY()%3 == 2) {
                // up stock
                Xle = stock.getPoint().getX();
                Yle = stock.getPoint().getY() + 1;
            }
            // 计算距离
            distance = u + Math.abs(Xle-Xws) + Math.abs(Yle-Yws) + detour;
        }

        // case2: 如果workStation在west(east)且stock的entrance在east(west)
        if ((workStation.getDirection().equals("west") && stock.getEntranceAisleDirection().equals("east"))
                || (workStation.getDirection().equals("east") && stock.getEntranceAisleDirection().equals("west"))) {
            // TODO：确定case2距离的计算方式
            // le的坐标
            int Xle = 0;
            int Yle = 0;
            if (stock.getPoint().getY()%3 == 1) {
                // down stock
                Xle = stock.getPoint().getX();
                Yle = stock.getPoint().getY() - 1;
            }
            if (stock.getPoint().getY()%3 == 2) {
                // up stock
                Xle = stock.getPoint().getX();
                Yle = stock.getPoint().getY() + 1;
            }
            // si的坐标以及le至si的距离
            int Xsi = 0;
            int Ysi = 0;
            double distanceLeSi = 0.0;
            // 横向通道的方向
            if (stock.getEntranceAisleDirection().equals("east")) {
                Xsi = stock.getPoint().getX() - stock.getPoint().getX()%6;
            }
            else {
                Xsi = stock.getPoint().getX() + stock.getPoint().getX()%6;
            }
            // 纵向通道的方向
            if (stock.getFirstCrossAisleEncountered().equals("north")) {
                Ysi = stock.getPoint().getY() + 3;
            }
            else {
                Ysi = stock.getPoint().getY() - 3;
            }
            distanceLeSi = Math.abs(Xsi-Xle) + 2*u + 2*u + 5*u;
            // 计算距离
            distance = u + distanceLeSi + Math.abs(Xsi-Xws) + Math.abs(Ysi-Yws) + detour;
        }

        // case3: 如果workStation在north(south)且stock的encountered在north(south)
        if ((workStation.getDirection().equals("north") && stock.getFirstCrossAisleEncountered().equals("north"))
                || (workStation.getDirection().equals("south") && stock.getFirstCrossAisleEncountered().equals("south"))) {
            // TODO：确定case3距离的计算方式
            // le的坐标
            int Xle = 0;
            int Yle = 0;
            if (stock.getPoint().getY()%3 == 1) {
                // down stock
                Xle = stock.getPoint().getX();
                Yle = stock.getPoint().getY() - 1;
            }
            if (stock.getPoint().getY()%3 == 2) {
                // up stock
                Xle = stock.getPoint().getX();
                Yle = stock.getPoint().getY() + 1;
            }
            // si的坐标以及le至si的距离
            int Xsi = 0;
            int Ysi = 0;
            double distanceLeSi = 0.0;
            // 横向通道的方向
            if (stock.getEntranceAisleDirection().equals("east")) {
                Xsi = stock.getPoint().getX() - stock.getPoint().getX()%6;
            }
            else {
                Xsi = stock.getPoint().getX() + stock.getPoint().getX()%6;
            }
            // 纵向通道的方向
            if (stock.getFirstCrossAisleEncountered().equals("north")) {
                Ysi = stock.getPoint().getY() + 3;
            }
            else {
                Ysi = stock.getPoint().getY() - 3;
            }
            distanceLeSi = Math.abs(Xsi-Xle);
            // 计算距离
            distance = u + distanceLeSi + Math.abs(Xsi-Xws) + Math.abs(Ysi-Yws) + detour;
        }

        // case4: 如果workStation在north(south)且stock的entrance在south(north)
        if ((workStation.getDirection().equals("north") && stock.getFirstCrossAisleEncountered().equals("south"))
                || (workStation.getDirection().equals("south") && stock.getFirstCrossAisleEncountered().equals("north"))) {
            // TODO：确定case4距离的计算方式
            double w = 2 * u;
            double l = 5 * u;
            // le的坐标
            int Xle = 0;
            int Yle = 0;
            if (stock.getPoint().getY()%3 == 1) {
                // down stock
                Xle = stock.getPoint().getX();
                Yle = stock.getPoint().getY() - 1;
            }
            if (stock.getPoint().getY()%3 == 2) {
                // up stock
                Xle = stock.getPoint().getX();
                Yle = stock.getPoint().getY() + 1;
            }
            // si的坐标以及le至ca的距离
            int Xsi = 0;
            int Ysi = 0;
            double distanceCaLe = 0.0;
            if (stock.getEntranceAisleDirection().equals("east")) {
                Xsi = stock.getPoint().getX() - stock.getPoint().getX()%6;
            }
            else {
                Xsi = stock.getPoint().getX() + stock.getPoint().getX()%6;
            }
            distanceCaLe = Math.abs(Xsi-Xle);

            double distance1 = 0.0;
            double distance2 = 0.0;
            distance1 = u + distanceCaLe + w + u + Math.abs(Xle-(int)(distanceCaLe)-w-u) + Math.abs(Yle-Yws) + detour;
            distance2 = u + distanceCaLe + 2 * l + w + 3 * u + Math.abs(Xle-(int)(distanceCaLe)+w+u) + Math.abs(Yle-Yws) + detour;
            distance = Math.min(distance1, distance2);
        }

        return distance;
    }

    private static double calStock2StockDistance(Stock stock1, Stock stock2){
        double distance = 0.0;
        int X1 = stock1.getPoint().getX();
        int X2 = stock2.getPoint().getX();
        int Y1 = stock1.getPoint().getY();
        int Y2 = stock2.getPoint().getY();
        distance = Math.abs(X1-X2)+Math.abs(Y1-Y2);

        return distance;
    }

}
