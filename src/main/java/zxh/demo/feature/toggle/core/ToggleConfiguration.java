package zxh.demo.feature.toggle.core;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * ToggleConfiguration:
 * @author zhangxuhai
 * @date 2020/7/6
*/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ToggleConfiguration {
    private static Set<ToggleConfigExtractor> extractors = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static Map<FeatureKeyEnum, FeatureState> toggleConfigInventory = null;
    private static final Function<FeatureKeyEnum, FeatureState> DEFAULT_STATE_GENERATOR =
            key -> new FeatureState(key, false, ConfigPriority.P0);

    public static void init() {
        toggleConfigInventory = new ConcurrentHashMap<>(16);
        refreshConfig();
    }

    public static void refreshConfig() {
        extractors.forEach(extractor -> extractor.load().forEach(ToggleConfiguration::setFeature));
    }

    public static void register(ToggleConfigExtractor extractor) {
        extractors.add(extractor);
    }

    public static void setFeature(FeatureState currentState) {
        checkInit();

        FeatureState previousState = toggleConfigInventory.putIfAbsent(currentState.getKey(), currentState);

        boolean canOverride = nonNull(previousState)
                && previousState.getPriority().isLowerThan(currentState.getPriority());
        if (canOverride) {
            toggleConfigInventory.put(currentState.getKey(), currentState);
        }
    }

    public static boolean isEnabled(FeatureKeyEnum key) {
        checkInit();

        return toggleConfigInventory.getOrDefault(key, DEFAULT_STATE_GENERATOR.apply(key)).isStateEnabled();
    }

    private static void checkInit() {
        if (isNull(toggleConfigInventory)) {
            throw new IllegalStateException("Toggle Configuration not init yet!");
        }
    }
}
