package ru.shilaev.driver;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.appmanagement.ApplicationState;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.shilaev.config.AppiumConfig;

import java.time.Duration;

/*
    Инициализация и закрытие драйвера
 */
public class DriverManager {
    private static DriverManager instance;
    private AndroidDriver driver;
    private WebDriverWait wait;

    //region private constructor
    private DriverManager() {
    }
    //endregion

    //region getter | setter
    public static DriverManager getInstance() {
        if (instance == null) {
            instance = new DriverManager();
        }
        return instance;
    }

    public AndroidDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException("Драйвер не создан. Сначала нужно вызывать createDriver()");
        }
        return driver;
    }

    public WebDriverWait getWait() {
        if (wait == null) {
            throw new IllegalStateException("Wait не создан. Сначала нужно вызвать creatDriver()");
        }
        return wait;
    }
    //endregion

    //region application methods
    public void launchApp(String packageName) {
        driver.activateApp(packageName);
    }

    /**
     * Проверить, запущено ли приложение
     */
    public boolean isAppRunningInForeground(String packageName) {
        ApplicationState state = driver.queryAppState(packageName);
        return state == ApplicationState.RUNNING_IN_FOREGROUND;
    }

    /**
     * Закрыть приложение
     */
    public void terminateApp(String packageName) {
        driver.terminateApp(packageName);
    }

    /**
     * Завершить работу драйвера
     */
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            wait = null;
        }
    }
    //endregion

    //region driver creation
    //driver with WebDriverWait duration settings
    public void createDriver(AppiumConfig config, int webDriverTimeout) {
        if (driver == null) {
            driver = new AndroidDriver(
                    config.getAppiumServerUrl(),
                    config.getCapabilities()
            );
            wait = new WebDriverWait(driver, Duration.ofSeconds(webDriverTimeout));
        }
    }

    //default driver
    public void createDriver(AppiumConfig config) {
        createDriver(config, 10);
    }
    //endregion
}