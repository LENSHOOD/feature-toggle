package zxh.demo.feature.toggle.api.internal;

import zxh.demo.feature.toggle.api.ToggleRouter;
import zxh.demo.feature.toggle.core.FeatureKeyEnum;
import zxh.demo.feature.toggle.core.ToggleConfiguration;

/**
 * DefaultToggleRouter:
 * @author zhangxuhai
 * @date 2020/7/7
*/
public class DefaultToggleRouter implements ToggleRouter {
    @Override
    public boolean includeOrderCancellationInEmail() {
        return ToggleConfiguration.isEnabled(FeatureKeyEnum.NEXT_GEN_ECOMM);
    }
}
