package Entity;

import lombok.Data;

import java.util.HashMap;

@Data
public class Aisle {

    /**
     * 点类
     */
    private Point point;

    /**
     * 左："0/1"；右："0/1"；上："0/1"；下："0/1"；
     */
    private HashMap<String, Integer> directions;

}
