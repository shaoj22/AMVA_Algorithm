package ServiceTime;

import Entity.Map;
import Entity.Stock;
import Entity.WorkStation;

import java.util.List;
import java.util.Random;

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
                sumTsw += tws[s][w];
                sumTsw2 += tws[s][w]*tws[s][w];
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

        Random random = new Random();
        double distance = 20 + (100-20)*random.nextDouble();

        return distance;
    }

    private static double calStock2StockDistance(Stock stock1, Stock stock2){

        int X1 = stock1.getPoint().getX();
        int X2 = stock2.getPoint().getX();
        int Y1 = stock1.getPoint().getY();
        int Y2 = stock2.getPoint().getY();
        double distance = Math.abs(X1-X2)+Math.abs(Y1-Y2);

        return distance;
    }

}
