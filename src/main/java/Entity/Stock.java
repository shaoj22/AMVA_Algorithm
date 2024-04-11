package Entity;

import lombok.Data;

@Data
public class Stock {

    /**
     * 点类
     */
    private Point point;

    /**
     * 该stock的entranceAisleDirection：west or east
     */
    private String entranceAisleDirection;

    /**
     * 该stock用的firstCrossAisleEncountered：north or south
     */
    private String firstCrossAisleEncountered;

}
