package zxh.demo.feature.toggle.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FeatureState:
 * @author zhangxuhai
 * @date 2020/7/6
*/
@AllArgsConstructor
@Getter
public class FeatureState {
    private final FeatureKeyEnum key;
    private final boolean stateEnabled;
    private final ConfigPriority priority;
}
