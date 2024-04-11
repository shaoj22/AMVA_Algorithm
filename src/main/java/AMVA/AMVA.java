package AMVA;


public class AMVA {

    public static AMVAResponse runner (AMVARequest amvaRequest) {

        /**
         * 输入
         */

        int R = amvaRequest.getRobotNum();
        int S = amvaRequest.getStationNum();
        int[] Cs = amvaRequest.getCs();
        double[] Vs = amvaRequest.getVs();
        double[] ESs = amvaRequest.getESs();
        double[] ESs2 = amvaRequest.getESs2();

        double[][][] ps = new double[S+1][R+1][R+1];
        double[][] Qs = new double[S+1][R+1];
        double[][] LsIn = new double[S+1][R+1];
        double[][] LsEx = new double[S+1][R+1];
        double[][] ETs = new double[S+1][R+1];
        double[] ESrem = new double[S+1];
        double[] Tr = new double[R+1];

        /**
         * 初始化
         */

        // TODO：初始化
        for (int s = 1; s <= S; s++) {
            ps[s][0][0] = 1;
            Qs[s][0] = 0;
            LsIn[s][0] = 0;
            LsEx[s][0] = 0;
        }

        /**
         * 预处理
         */

        // TODO：预处理
        for (int s = 1; s <= S; s++) {
            ESrem[s] = ((Cs[s]-1)/(Cs[s]+1))*(ESs[s]/Cs[s]) + (2/(Cs[s]+1))*(1/Cs[s])*(ESs2[s]/(2*ESs[s]));
        }

        /**
         * 迭代
         */

        for (int r = 1; r <= R; r++) {

            // 如果是cqn2的话需要初始化不同机器人数量r情况下的一阶矩和二阶矩
            if (amvaRequest.getCqnType().equals("cqn2")) {
                if (r == 1) {
                    // TODO：r=1情况下的一阶矩和二阶矩
                    ESs[S] = amvaRequest.getCqn1Throughput()/((1- amvaRequest.getLambda()) * amvaRequest.getLambda());
                }
                else{
                    // TODO：r>1情况下的一阶矩和二阶矩
                    ESs[S] = 1 / amvaRequest.getLambda();
                }
            }

            // (a) 服务站s服务完当前所有顾客的时间期望
            // TODO：(a) 服务站s服务完当前所有顾客的时间期望
            for (int s = 1; s <= S; s++) {
                ETs[s][r] = Qs[s][r-1]*ESrem[s] + LsEx[s][r-1]*(ESs[s]/Cs[s]) + ESs[s];
            }

            // (b) 系统吞吐量
            // TODO：(b) 系统吞吐量
            double sumVsETs = 0;
            for (int s = 1; s <= S; s++) {
                sumVsETs += Vs[s]*ETs[s][r];
            }
            Tr[r] = r/sumVsETs;

            // (c) 条件概率
            // TODO：(c) 条件概率
            for (int s = 1; s <= S; s++) {
                int minCsr = Math.min(Cs[s]-1, r);
                for (int b = 1; b <= minCsr; b++) {
                    ps[s][b][r] = (ESs[s]/b)*Vs[s]*Tr[r]*ps[s][b-1][r-1];
                }
            }

            // (d) 服务站全忙概率
            // TODO：(d) 服务站全忙概率
            for (int s = 1; s <= S; s++) {
                if (r < Cs[s]){
                    Qs[s][r] = 0;
                }
                else{
                    Qs[s][r] = (ESs[s]/Cs[s])*Vs[s]*Tr[r]*(Qs[s][r-1] + ps[s][Cs[s]-1][r-1]);
                }
            }

            // (e) 服务站没有顾客的概率
            // TODO：(e) 服务站没有顾客的概率
            for (int s = 1; s <= S; s++) {
                int minCsr = Math.min(Cs[s]-1, r);
                double sumPsQs = 0;
                for (int b = 1; b <= minCsr; b++) {
                    sumPsQs += ps[s][b][r] - Qs[s][r];
                }
                ps[s][0][r] = 1 - sumPsQs;
            }

            // (f) 服务站处的Ex队长
            // TODO：(f) 服务站处的Ex队长
            for (int s = 1; s <= S; s++) {
                if (r < Cs[s]){
                    LsEx[s][r] = 0;
                }
                else{
                    LsEx[s][r] = (ESs[s]/Cs[s])*Vs[s]*Tr[r]*(LsEx[s][r-1] + Qs[s][r-1]);
                }
            }

            // (g) 服务站处的In队长
            // TODO：(g) 服务站处的In队长
            for (int s = 1; s <= S; s++) {
                LsIn[s][r] = Tr[r]*Vs[s]*ETs[s][r];
            }
        }

        /**
         * 输出
         */

        AMVAResponse amvaResponse = new AMVAResponse();
        amvaResponse.setPs(ps);
        amvaResponse.setQs(Qs);
        amvaResponse.setLsIn(LsIn);
        amvaResponse.setLsEx(LsEx);
        amvaResponse.setETs(ETs);
        amvaResponse.setESrem(ESrem);
        amvaResponse.setTr(Tr);

        return amvaResponse;

    }
}
