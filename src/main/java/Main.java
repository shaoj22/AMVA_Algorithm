import AMVA.AMVA;
import AMVA.AMVAResponse;
import AMVA.AMVARequest;
import Entity.*;
import ServiceTime.ServiceTime;
import ServiceTime.ServiceTimeRequest;
import ServiceTime.ServiceTimeResponse;

public class Main {
    public static void main(String[] args) {

        /**
         * 构造instance
         */

        Instance instance = new Instance();

        int blockLengthNum = 16;
        int blockWidthNum = 8;
        int robotNum = 10;
        int workStationNum = 5; // 拣选站的数量不能设置太多，太多会超出地图上限
        double lambda = 0.15;
        int distanceWorkStation2Stock = 5;
        int stationNum = 3;
        double v = 1.25;
        double liftingTime = 2;
        double storingTime = 2;
        double u = 1.0;

        instance.setBlockLengthNum(blockLengthNum);
        instance.setBlockWidthNum(blockWidthNum);
        instance.setRobotNum(robotNum);
        instance.setWorkStationNum(workStationNum);
        instance.setLambda(lambda);
        instance.setDistanceWorkStation2Stock(distanceWorkStation2Stock);
        instance.setStationNum(stationNum);
        instance.setV(v);
        instance.setLiftingTime(liftingTime);
        instance.setStoringTime(storingTime);
        instance.setU(u);

        System.out.println("------- " + "step1：Instance的初始参数配置成功" + " -------" + "\n");

        /**
         * 构造warehouseMap
         */

        instance = instance.generateInstance(instance);
        Map warehouseMap = instance.getWarehouseMap();

        System.out.println("------- " + "step2：Instance的Map构造成功" + " -------" + "\n");

        /**
         * 计算service time
         */

        ServiceTimeRequest serviceTimeRequest = new ServiceTimeRequest();
        serviceTimeRequest.setWarehouseMap(warehouseMap);
        serviceTimeRequest.setStationNum(stationNum);
        serviceTimeRequest.setV(v);
        serviceTimeRequest.setLiftingTime(liftingTime);
        serviceTimeRequest.setStoringTime(storingTime);
        serviceTimeRequest.setU(u);

        ServiceTime serviceTimeAlgorithmTool = new ServiceTime();
        ServiceTimeResponse serviceTimeResponse = serviceTimeAlgorithmTool.runner(serviceTimeRequest);

        System.out.println("------- " + "step3：ServiceTime计算成功" + " -------");
        System.out.println("服务时间的一阶矩：\n" +
                "Es[1]:" + serviceTimeResponse.getESs()[1] + "  " +
                "Es[2]:" + serviceTimeResponse.getESs()[2] + "  " +
                "Es[3]:" + serviceTimeResponse.getESs()[3] + "\n" +
                "服务时间的二阶矩：\n" +
                "Es2[1]:" + serviceTimeResponse.getESs2()[1] + "  " +
                "Es2[2]:" + serviceTimeResponse.getESs2()[2] + "  " +
                "Es2[3]:" + serviceTimeResponse.getESs2()[3] + "\n"
        );

        /**
         * 计算cqn1的吞吐量
         */

        AMVARequest cqn1AmvaRequest = new AMVARequest();
        // 求解类型
        cqn1AmvaRequest.setCqnType("cqn1");
        // 机器人的数量
        cqn1AmvaRequest.setRobotNum(robotNum);
        // 服务站的数量
        cqn1AmvaRequest.setStationNum(stationNum);
        // 服务站s处的servers数量
        int[] cqn1Cs = new int[5];
        // TODO：每个服务站服务人数的确定
        cqn1Cs[0] = 0;
        cqn1Cs[1] = instance.getWorkStationNum()*instance.getWarehouseMap().getStockNum();
        cqn1Cs[2] = instance.getWarehouseMap().getStockNum()*instance.getWarehouseMap().getStockNum();
        cqn1Cs[3] = instance.getWorkStationNum()*instance.getWarehouseMap().getStockNum();
        cqn1Cs[4] = instance.getWorkStationNum();
        cqn1AmvaRequest.setCs(cqn1Cs);
        // 服务站s处的访问率
        double[] cqn1CsVs = new double[5];
        // TODO：每个服务站访问概率的确定
        cqn1CsVs[0] = 0;
        cqn1CsVs[1] = 1;
        cqn1CsVs[2] = 1;
        cqn1CsVs[3] = 1;
        cqn1CsVs[4] = 1;
        cqn1AmvaRequest.setVs(cqn1CsVs);
        // 服务站s处的一阶矩
        double[] cqn1ESs = new double[5];
        double[] cqn1ESs2 = new double[5];
        for (int s = 0; s < serviceTimeResponse.getESs().length; s++) {
            cqn1ESs[s] = serviceTimeResponse.getESs()[s];
            cqn1ESs2[s] = serviceTimeResponse.getESs2()[s];
        }
        cqn1ESs[4] = 18.0;
        cqn1ESs2[4] = 50.0;
        cqn1AmvaRequest.setESs(cqn1ESs);
        // 服务站s处的二阶矩
        cqn1AmvaRequest.setESs2(cqn1ESs2);

        AMVA amvaAlgorithmTool = new AMVA();
        AMVAResponse cqn1AmvaResponse = amvaAlgorithmTool.runner(cqn1AmvaRequest);

        // 获得cqn1的吞吐量
        double cqn1Throughput = cqn1AmvaResponse.getTr()[robotNum];

        System.out.println("------- " + "step4：Cqn1的吞吐量计算成功" + " -------");
        System.out.println("Cqn1的吞吐量为：" + cqn1Throughput*3600 + " 单/小时" + "\n");

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
        cqn2AmvaRequest.setRobotNum(robotNum);
        // 服务站的数量
        cqn2AmvaRequest.setStationNum(stationNum+1);
        // 服务站s处的servers数量
        int[] cqn2Cs = new int[6];
        // TODO：每个服务站服务人数的确定
        cqn2Cs[0] = 0;
        cqn2Cs[1] = instance.getWorkStationNum()*instance.getWarehouseMap().getStockNum();
        cqn2Cs[2] = instance.getWarehouseMap().getStockNum()*instance.getWarehouseMap().getStockNum();
        cqn2Cs[3] = instance.getWorkStationNum()*instance.getWarehouseMap().getStockNum();
        cqn2Cs[4] = instance.getWorkStationNum();
        cqn2Cs[5] = 1;
        cqn2AmvaRequest.setCs(cqn2Cs);
        // 服务站s处的访问率
        double[] cqn2Vs = new double[6];
        // TODO：每个服务站访问概率的确定
        cqn2Vs[0] = 0;
        cqn2Vs[1] = 1;
        cqn2Vs[2] = 1;
        cqn2Vs[3] = 1;
        cqn2Vs[4] = 1;
        cqn2Vs[5] = 1;
        cqn2AmvaRequest.setVs(cqn2Vs);
        // 服务站s处的一阶矩和二阶矩
        double[] cqn2ESs = new double[6];
        double[] cqn2ESs2 = new double[6];
        for (int s = 0; s < serviceTimeResponse.getESs().length; s++) {
            cqn2ESs[s] = serviceTimeResponse.getESs()[s];
            cqn2ESs2[s] = serviceTimeResponse.getESs2()[s];
        }
        // 服务站s+1处的一阶矩和二阶矩
        cqn2ESs[4] = 18.0;
        cqn2ESs2[4] = 50.0;
        cqn2ESs[5] = 5;
        cqn2ESs2[5] = 250.0;
        cqn2AmvaRequest.setESs(cqn2ESs);
        cqn2AmvaRequest.setESs2(cqn2ESs2);

        // 获得cqn2相关的指标
        // TODO: 获得cqn2相关的指标
        AMVAResponse cqn2AmvaResponse = amvaAlgorithmTool.runner(cqn2AmvaRequest);
        // 机器人的利用率
        double Lr = cqn2AmvaResponse.getLsIn()[stationNum+1][robotNum];
        double pr = 1 - Lr/robotNum;
        // 订单周转时间
        double Li = 0.0;
        double Lo = cqn2AmvaResponse.getLsIn()[stationNum+1][robotNum];
        for (int s = 1; s <= stationNum; s++) {
            Li += cqn2AmvaResponse.getLsIn()[s][robotNum];
        }
        double toc = (Lo+Li) / instance.getLambda();
        // 系统的吞吐量
        double cqn2Throughput = cqn2AmvaResponse.getTr()[robotNum];
        // 工作站的利用率
        double pws = cqn2Throughput*(1.0/instance.getWorkStationNum())*10;
        System.out.println("------- " + "step5：Cqn2的相关指标计算成功" + " -------");
        System.out.println("机器人的利用率为：" + pr);
        System.out.println("订单周转时间为：" + toc + " 秒");
        System.out.println("系统的吞吐量为：" + cqn2Throughput*3600 + " 单/小时");
        System.out.println("工作站的利用率为：" + pws);

    }
}