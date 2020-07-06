package zxh.demo.feature.toggle.core;

import java.util.Set;

/**
 * ToggleConfigExtractor:
 * @author zhangxuhai
 * @date 2020/7/7
*/
public interface ToggleConfigExtractor {
    Set<FeatureState> load();

    ConfigPriority getPriority();
}
