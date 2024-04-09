package ServiceTime;

import lombok.Data;

@Data
public class ServiceTimeResponse {

    /**
     * 服务站的一阶矩
     */
    private double[] ESs;

    /**
     * 服务站的二阶矩
     */
    private double[] ESs2;

}
