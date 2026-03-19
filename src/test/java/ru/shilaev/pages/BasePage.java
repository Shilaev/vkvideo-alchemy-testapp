package ru.shilaev.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.shilaev.driver.DriverManager;
import ru.shilaev.utils.Logger;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Общие методы для всех страниц (wait, click, find и т.д.)
 */
public class BasePage {
    protected AndroidDriver driver;
    protected WebDriverWait wait;
    protected WebDriverWait longWait;

    public BasePage() {
        this.driver = DriverManager.getInstance().getDriver();
        this.wait = DriverManager.getInstance().getWait();
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    /**
     * Исполнить скрипт
     */
    protected void executeScript(String script, Map<String, String> command) {
        driver.executeScript(script, command);
    }

    /**
     * Ожидать появление элемента
     */
    protected WebElement findElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Ожидать, что элемент станет кликабельным и кликнуть
     */
    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Проверить, есть ли элемент на странице (мгновенно)
     */
    protected boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /**
     * Проверить, есть ли элемент на странице с таймаутом
     */
    protected boolean isElementPresent(By locator, int timeoutSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            customWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ожидать, когда элемент исчезнет
     */
    public boolean waitForElementToDisappear(By locator) {
        Logger.step("Ожидание исчезновения элемента");
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            Logger.success("Элемент исчез");
            return true;
        } catch (Exception e) {
            Logger.error("Элемент не исчез за отведенное время");
            return false;
        }
    }

    /**
     * Ожидать, когда элемент исчезнет (с кастомным таймаутом)
     */
    public boolean waitForElementToDisappear(By locator, int timeoutSeconds) {
        Logger.step("Ожидание исчезновения элемента (макс. " + timeoutSeconds + " сек)");
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            customWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            Logger.success("Элемент исчез");
            return true;
        } catch (Exception e) {
            Logger.error("Элемент не исчез за " + timeoutSeconds + " секунд");
            return false;
        }
    }

    /**
     * Получить текст из элемента
     */
    protected String getText(By locator) {
        return findElement(locator).getText();
    }

    /**
     * Получить текст из элемента с проверкой наличия
     */
    protected String getTextIfPresent(By locator) {
        try {
            return findElement(locator).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Найти все элементы по локатору
     */
    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    /**
     * Найти все элементы внутри другого элемента
     */
    protected List<WebElement> findElements(WebElement parent, By locator) {
        return parent.findElements(locator);
    }

    /**
     * Кликнуть по элементу с именем (для логирования)
     */
    protected void clickIfPresent(By locator, String elementName) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            Logger.warning("Найден элемент: " + elementName);
            element.click();
            wait.until(ExpectedConditions.invisibilityOf(element));
            Logger.success("Элемент закрыт: " + elementName);
        } catch (Exception e) {
            Logger.info("Элемент не найден: " + elementName);
        }
    }

    /**
     * Кликнуть по элементу, если он появился
     */
    protected boolean clickIfPresent(By locator) {
        try {
            if (isElementPresent(locator, 3)) {
                Logger.info("Элемент найден: " + locator);
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                Logger.success("Элемент успешно нажат");
                return true;
            } else {
                Logger.debug("Элемент отсутствует на странице: " + locator);
                return false;
            }
        } catch (TimeoutException e) {
            Logger.warning("Элемент найден, но не стал кликабельным: " + locator);
            return false;
        } catch (Exception e) {
            Logger.error("Ошибка при работе с элементом " + locator + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Подождать указанное количество секунд
     */
    public void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}