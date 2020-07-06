package zxh.demo.feature.toggle.api;

import zxh.demo.feature.toggle.api.internal.DefaultToggleRouter;

/**
 * ToggleRouterFactory:
 * @author zhangxuhai
 * @date 2020/7/6
*/
public class ToggleRouterFactory {
    public static ToggleRouter create() {
        return new DefaultToggleRouter();
    }
}
