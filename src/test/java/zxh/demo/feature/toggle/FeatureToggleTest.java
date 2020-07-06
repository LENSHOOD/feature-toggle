package zxh.demo.feature.toggle;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zxh.demo.feature.toggle.api.ToggleRouter;
import zxh.demo.feature.toggle.api.ToggleRouterFactory;
import zxh.demo.feature.toggle.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

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
                String isNextGenEcomm = properties.getProperty(FeatureKeyEnum.NEXT_GEN_ECOMM.name());
                return Collections.singleton(new FeatureState(
                        FeatureKeyEnum.NEXT_GEN_ECOMM,
                        Boolean.parseBoolean(isNextGenEcomm),
                        getPriority()));
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
        MatcherAssert.assertThat(email, Matchers.is("fake-basic-email can cancel."));
    }

    @Test
    void should_exclude_order_cancellation_when_send_email() {
        // given
        ToggleConfiguration.setFeature(new FeatureState(FeatureKeyEnum.NEXT_GEN_ECOMM, false, ConfigPriority.P1));
        FakeInvoiceEmailer emailer = new FakeInvoiceEmailer(ToggleRouterFactory.create());

        // when
        String email = emailer.generateInvoiceEmail();

        // then
        MatcherAssert.assertThat(email, Matchers.is("fake-basic-email"));
    }
}