package zxh.demo.feature.toggle.core;

/**
 * ConfigPriority:
 * @author zhangxuhai
 * @date 2020/7/6
*/
public enum ConfigPriority {
    /**
     * The priority of configurations, from lowest P0 to highest P4
     */
    P0, P1, P2, P3, P4;

    public boolean isHigherThan(ConfigPriority priorityToBeCompared) {
        return compareTo(priorityToBeCompared) >= 0;
    }

    public boolean isLowerThan(ConfigPriority priorityToBeCompared) {
        return compareTo(priorityToBeCompared) <= 0;
    }
}
