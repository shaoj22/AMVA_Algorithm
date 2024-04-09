package AMVA;

import lombok.Data;

@Data
public class AMVAResponse {

    /**
     * The probability that there are i robots at station s when the system contains r robots
     */
    private double[][][] ps;

    /**
     * The probability that all servers are busy at station s when the system contains r robots
     */
    private double[][] Qs;

    /**
     * The expected robot queue length including robots in service at station s when the system contains r robots
     */
    private double[][] LsIn;

    /**
     * The expected queue length excluding robots in service at station s when the system contains r robots
     */
    private double[][] LsEx;

    /**
     * The lead time at station s when the system contains r robots
     */
    private double[][] ETs;

    /**
     * The expected time remaining until the first departure at station s
     */
    private double[] ESrem;

    /**
     * The throughput when the system contains r robots
     */
    private double[] Tr;

}
