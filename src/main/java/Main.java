import AMVA.AMVA;
import AMVA.AMVAResponse;
import AMVA.AMVARequest;
import Entity.*;
import ServiceTime.ServiceTime;
import ServiceTime.ServiceTimeRequest;
import ServiceTime.ServiceTimeResponse;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        /**
         * 构造warehouseMap
         */

        Map warehouseMap = new Map();
        int blockLengthNum = 10;
        int blockWidthNum = 5;
        int distanceWorkStation2Stock = 5;
        int robotNum = 10;
        double lambda = 0.05;
        // 构造每个point
        int warehouseLength = (1+distanceWorkStation2Stock)+(blockLengthNum+1+5*blockLengthNum);
        int warehouseWidth = (blockWidthNum+1+2*blockWidthNum);
        int pointNum = warehouseWidth*warehouseLength;
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
        int workStationNum = 5;
        List<WorkStation> workStationDTOList = new ArrayList<>();
        for (int w = 0; w < workStationNum; w++) {
            WorkStation workStation = new WorkStation();
            int workStationX = 0;
            int workStationY = 3*w + 1;
            for (Point point : pointDTOList) {
                if (point.getX() == workStationX && point.getY() == workStationY) {
                    workStation.setPoint(point);
                    workStation.setDetour("0");
                    break; // 找到匹配的点后，退出循环
                }
            }
            workStationDTOList.add(workStation);
        }
        // 构造每个stock和aisle
        int stockNum = blockLengthNum*blockWidthNum*2*5;
        List<Stock> stockDTOList = new ArrayList<>();
        for (Point point : pointDTOList) {
            // 判断哪些是stock
            int X = point.getX();
            int Y = point.getY();
            if ((X%6 != 0 && X/6 > 1) && (Y%2 != 0)){
                Stock stock = new Stock();
                stock.setPoint(point);
                // TODO:判断是哪种计算case的stock
                stockDTOList.add(stock);
                }
            else{
                Aisle aisle = new Aisle();
                aisle.setPoint(point);
                // TODO:判断aisle的方向
            }
        }
        warehouseMap.setPointNum(pointNum);
        warehouseMap.setPointDTOList(pointDTOList);
        warehouseMap.setWorkStationNum(workStationNum);
        warehouseMap.setWorkStationDTOList(workStationDTOList);
        warehouseMap.setStockNum(stockNum);
        warehouseMap.setStockDTOList(stockDTOList);
        warehouseMap.setDistanceWorkStation2Stock(distanceWorkStation2Stock);

        /**
         * 计算service time
         */

        ServiceTimeRequest serviceTimeRequest = new ServiceTimeRequest();
        serviceTimeRequest.setWarehouseMap(warehouseMap);
        serviceTimeRequest.setStationNum(3);
        serviceTimeRequest.setV(1.25);
        serviceTimeRequest.setLiftingTime(2);
        serviceTimeRequest.setStoringTime(2);

        ServiceTime serviceTimeAlgorithmTool = new ServiceTime();
        ServiceTimeResponse serviceTimeResponse = serviceTimeAlgorithmTool.runner(serviceTimeRequest);

        /**
         * 计算cqn1的吞吐量
         */

        AMVARequest cqn1AmvaRequest = new AMVARequest();
        // 求解类型
        cqn1AmvaRequest.setCqnType("cqn1");
        // 机器人的数量
        cqn1AmvaRequest.setRobotNum(robotNum+1);
        // 服务站的数量
        cqn1AmvaRequest.setStationNum(4);
        // 服务站s处的servers数量
        int[] cqn1Cs = new int[4];
        cqn1Cs[0] = 0;
        cqn1Cs[1] = 10;
        cqn1Cs[2] = 10;
        cqn1Cs[3] = 10;
        cqn1AmvaRequest.setCs(cqn1Cs);
        // 服务站s处的访问率
        double[] cqn1CsVs = new double[4];
        cqn1CsVs[0] = 0;
        cqn1CsVs[1] = 0.2;
        cqn1CsVs[2] = 0.3;
        cqn1CsVs[3] = 0.3;
        cqn1AmvaRequest.setVs(cqn1CsVs);
        // 服务站s处的一阶矩
        cqn1AmvaRequest.setESs(serviceTimeResponse.getESs());
        // 服务站s处的二阶矩
        cqn1AmvaRequest.setESs2(serviceTimeResponse.getESs2());

        AMVA amvaAlgorithmTool = new AMVA();
        AMVAResponse cqn1AmvaResponse = amvaAlgorithmTool.runner(cqn1AmvaRequest);

        // 获得cqn1的吞吐量
        double cqn1Throughput = cqn1AmvaResponse.getTr()[robotNum];

        /**
         * 计算cqn2的相关指标
         */

        AMVARequest cqn2AmvaRequest = new AMVARequest();
        // cqn1的吞吐量
        cqn2AmvaRequest.setCqn1Throughput(cqn1Throughput);
        // lambda
        cqn2AmvaRequest.setLambda(lambda);
        // 求解类型
        cqn2AmvaRequest.setCqnType("cqn2");
        // 机器人的数量
        cqn2AmvaRequest.setRobotNum(robotNum+1);
        // 服务站的数量
        cqn2AmvaRequest.setStationNum(5);
        // 服务站s处的servers数量
        int[] cqn2Cs = new int[5];
        // TODO：每个服务站服务人数的确定
        cqn2Cs[0] = 0;
        cqn2Cs[1] = 10;
        cqn2Cs[2] = 10;
        cqn2Cs[3] = 10;
        cqn2Cs[4] = 1;
        cqn2AmvaRequest.setCs(cqn2Cs);
        // 服务站s处的访问率
        double[] cqn2Vs = new double[5];
        // TODO：每个服务站访问概率的确定
        cqn2Vs[0] = 0;
        cqn2Vs[1] = 0.2;
        cqn2Vs[2] = 0.3;
        cqn2Vs[3] = 0.3;
        cqn2Vs[4] = 1;
        cqn2AmvaRequest.setVs(cqn2Vs);
        // 服务站s处的一阶矩和二阶矩
        double[] cqn2ESs = new double[5];
        double[] cqn2ESs2 = new double[5];
        for (int s = 0; s < serviceTimeResponse.getESs().length; s++) {
            cqn2ESs[s] = serviceTimeResponse.getESs()[s];
            cqn2ESs2[s] = serviceTimeResponse.getESs2()[s];
        }
        // 服务站s+1处的一阶矩和二阶矩
        cqn2ESs[4] = 0.0;
        cqn2ESs2[4] = 0.0;
        cqn2AmvaRequest.setESs(cqn2ESs);
        cqn2AmvaRequest.setESs2(cqn2ESs2);

        // 获得cqn2相关的指标
        AMVAResponse cqn2AmvaResponse = amvaAlgorithmTool.runner(cqn2AmvaRequest);


    }
}