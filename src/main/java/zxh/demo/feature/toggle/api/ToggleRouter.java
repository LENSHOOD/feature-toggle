package zxh.demo.feature.toggle.api;

/**
 * ToggleRouter:
 * @author zhangxuhai
 * @date 2020/7/6
*/
public interface ToggleRouter {
    boolean includeOrderCancellationInEmail();

    boolean canExperienceGiftVip(int userId);
}
