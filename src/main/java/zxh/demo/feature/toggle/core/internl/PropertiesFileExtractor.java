package zxh.demo.feature.toggle.core.internl;

import zxh.demo.feature.toggle.core.ConfigPriority;
import zxh.demo.feature.toggle.core.FeatureState;
import zxh.demo.feature.toggle.core.ToggleConfigExtractor;
import java.util.Set;

/**
 * PropertiesFileExtractor:
 * @author zhangxuhai
 * @date 2020/7/7
*/
public class PropertiesFileExtractor implements ToggleConfigExtractor {
    @Override
    public Set<FeatureState> load() {
        return null;
    }

    @Override
    public ConfigPriority getPriority() {
        return ConfigPriority.P0;
    }
}
