package zxh.demo.feature.toggle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zxh.demo.feature.toggle.api.ToggleRouter;
import zxh.demo.feature.toggle.api.ToggleRouterFactory;
import zxh.demo.feature.toggle.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FeatureToggleTest {
    class FakeInvoiceEmailer {
        private final ToggleRouter router;

        FakeInvoiceEmailer(ToggleRouter router) {
            this.router = router;
        }

        public String generateInvoiceEmail() {
            String basicEmail = "fake-basic-email";

            if (router.includeOrderCancellationInEmail()) {
                return addOrderCancellationContentToEmail(basicEmail);
            } else {
                return basicEmail;
            }
        }

        private String addOrderCancellationContentToEmail(String originalEmail) {
            return originalEmail + " can cancel.";
        }
    }

    class FakeVipSender {
        private final ToggleRouter router;

        FakeVipSender(ToggleRouter router) {
            this.router = router;
        }

        public String tryLucky(int userId) {
            if (router.canExperienceGiftVip(userId)) {
                return "Win a VIP!";
            }

            return "Not so lucky, pity...";
        }
    }

    class TestResourceConfigExtractor implements ToggleConfigExtractor {

        @Override
        public Set<FeatureState> load() {
            try ( InputStream resourceStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("toggle-config.properties")){
                if (Objects.isNull(resourceStream)) {
                    throw new IllegalArgumentException("No such properties.");
                }

                Properties properties = new Properties();
                properties.load(resourceStream);
                return Stream.of(FeatureKeyEnum.values()).map(key -> new FeatureState(
                        key,
                        Boolean.parseBoolean(properties.getProperty(key.name())),
                        getPriority())).collect(Collectors.toSet());
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        @Override
        public ConfigPriority getPriority() {
            return ConfigPriority.P0;
        }
    }

    @BeforeEach
    void init() {
        ToggleConfiguration.register(new TestResourceConfigExtractor());
        ToggleConfiguration.init();
    }

    @Test
    void should_include_order_cancellation_when_send_email() {
        // given
        FakeInvoiceEmailer emailer = new FakeInvoiceEmailer(ToggleRouterFactory.create());

        // when
        String email = emailer.generateInvoiceEmail();

        // then
        assertThat(email, is("fake-basic-email can cancel."));
    }

    @Test
    void should_exclude_order_cancellation_when_send_email() {
        // given
        ToggleConfiguration.setFeature(new FeatureState(FeatureKeyEnum.NEXT_GEN_ECOMM, false, ConfigPriority.P1));
        FakeInvoiceEmailer emailer = new FakeInvoiceEmailer(ToggleRouterFactory.create());

        // when
        String email = emailer.generateInvoiceEmail();

        // then
        assertThat(email, is("fake-basic-email"));
    }

    @Test
    void should_choose_current_user_to_get_gift_vip() {
        // given
        FakeVipSender sender = new FakeVipSender(ToggleRouterFactory.create());

        // when
        String result = sender.tryLucky(100);

        // then
        assertThat(result, is("Win a VIP!"));
    }

    @Test
    void should_not_choose_current_user_to_get_gift_vip() {
        // given
        FakeVipSender sender = new FakeVipSender(ToggleRouterFactory.create());

        // when
        String result = sender.tryLucky(20);

        // then
        assertThat(result, is("Not so lucky, pity..."));
    }
}