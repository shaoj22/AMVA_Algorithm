package AMVA;


import lombok.Data;

@Data
public class AMVARequest {

    /**
     * 机器人的数量R
     */
    private int robotNum;

    /**
     * 服务站的数量S
     */
    private int stationNum;

    /**
     * 服务站s处的servers数量
     */
    private int[] Cs;

    /**
     * 服务站s处的访问率
     */
    private double[] Vs;

    /**
     * 服务站s的一阶矩
     */
    private double[] ESs;

    /**
     * 服务站s的二阶矩
     */
    private double[] ESs2;

    /**
     * 求解类型
     */
    private String cqnType;

    /**
     * cqn1的吞吐量
     */
    private double cqn1Throughput;

    /**
     * lambda
     */
    private double lambda;

}
