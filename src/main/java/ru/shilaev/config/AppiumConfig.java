package ru.shilaev.config;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

/*
    Настройки подключения к Appium
 */
public class AppiumConfig {
    //region default values
    private static final String DEFAULT_URL = "http://localhost:4723";
    private static final String DEFAULT_PLATFORM = "Android";
    private static final String DEFAULT_DEVICE = "Android Emulator";
    private static final String DEFAULT_AUTOMATION = "UiAutomator2";
    //endregion

    private final String appiumServerUrl;
    private final DesiredCapabilities capabilities;

    //region getters | setters
    public URL getAppiumServerUrl() {
        try {
            return new URL(this.appiumServerUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Неверный URL Appium сервера: " + this.appiumServerUrl, e);
        }
    }

    public DesiredCapabilities getCapabilities() {
        return this.capabilities;
    }
    //endregion

    //region private constructor
    private AppiumConfig(String appiumServerUrl, DesiredCapabilities capabilities) {
        this.appiumServerUrl = appiumServerUrl;
        this.capabilities = capabilities;
    }
    //endregion

    //region builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String appiumUrl = DEFAULT_URL;
        private String platformName = DEFAULT_PLATFORM;
        private String deviceName = DEFAULT_DEVICE;
        private String automationName = DEFAULT_AUTOMATION;
        private boolean noReset = false;

        public Builder withAppiumUrl(String appiumUrl) {
            this.appiumUrl = appiumUrl;
            return this;
        }

        public Builder withDeviceName(String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        public Builder withNoReset(boolean noReset) {
            this.noReset = noReset;
            return this;
        }

        public AppiumConfig build() {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("platformName", platformName);
            capabilities.setCapability("appium:deviceName", deviceName);
            capabilities.setCapability("appium:automationName", automationName);
            capabilities.setCapability("appium:noReset", noReset);
            capabilities.setCapability("appium:newCommandTimeout", 1800);

            return new AppiumConfig(appiumUrl, capabilities);
        }
    }
    //endregion
}
