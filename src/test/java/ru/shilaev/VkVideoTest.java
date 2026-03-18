package ru.shilaev;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.appmanagement.ApplicationState;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

//appium --allow-insecure="*:adb_shell"

public class VkVideoTest {

    private AndroidDriver driver;
    private WebDriverWait wait;

    @Test
    public void launchApp() throws Exception {
        System.out.println("\n🚀 ЗАПУСК ТЕСТА VK ВИДЕО");
        System.out.println("═══════════════════════════");

        // Настройки подключения
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appium:deviceName", "Android Emulator");
        capabilities.setCapability("appium:automationName", "UiAutomator2");
        capabilities.setCapability("appium:noReset", false);

        // Подключаемся к Appium
        URL appiumServerUrl = new URL("http://localhost:4723");
        driver = new AndroidDriver(appiumServerUrl, capabilities);

        // ✅ ВАЖНО: wait создаём ПОСЛЕ driver
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("📱 Запуск VK Video...");
        driver.activateApp("com.vk.vkvideo");
        Thread.sleep(15000); //Приложение грузится очень долго, даем подождать, пока прогрузится

        // Проверка состояния приложения
        ApplicationState state = driver.queryAppState("com.vk.vkvideo");

        if (state == ApplicationState.RUNNING_IN_FOREGROUND) {
            System.out.println("✅ Приложение запущено");
            System.out.println("✅ Приложение стабильно");
        } else {
            String errorMsg = (state == ApplicationState.RUNNING_IN_BACKGROUND)
                    ? "⚠️ Приложение ушло в фон..."
                    : "⚠️ Приложение не запустилось...";

            System.out.println(errorMsg);
            System.out.println("❌ Тест провален.");
            cleanupAndExit();
            return;
        }

        // Закрываем всплывающие окна
        System.out.println("\n🧹 Проверка всплывающих окон...");
        closePopups();

        // Поиск видео
        System.out.println("\n🔍 Поиск видео...");
        if (!findAndPlayVideo()) {
            cleanupAndExit();
            return;
        }

        // Завершение теста
        cleanupAndExit();
        System.out.println("\n═══════════════════════════");
        System.out.println("✅ ТЕСТ УСПЕШНО ЗАВЕРШЕН\n");
    }

    private void closePopups() throws InterruptedException {
        // Проверяем рекламу
        try {
            WebElement closeButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("com.vk.vkvideo:id/close_btn_left"))
            );
            System.out.println("  🟡 Найдена реклама → закрываем");
            closeButton.click();
            wait.until(ExpectedConditions.invisibilityOf(closeButton));
            System.out.println("  ✅ Реклама закрыта");
        } catch (Exception e) {
            System.out.println("  ⚪ Реклама не найдена");
        }

        // Проверяем кнопку Skip
        try {
            WebElement skipButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("com.vk.vkvideo:id/fast_login_tertiary_btn"))
            );
            System.out.println("  🟡 Найден Skip → закрываем");
            skipButton.click();
            wait.until(ExpectedConditions.invisibilityOf(skipButton));
            System.out.println("  ✅ Skip закрыт");
        } catch (Exception e) {
            System.out.println("  ⚪ Skip не найден");
        }
    }

    private boolean findAndPlayVideo() throws InterruptedException {
        try {
            // Сначала находим GridView (он есть всегда)
            WebElement gridView = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("com.vk.vkvideo:id/recycler"))
            );
            System.out.println("  ✅ GridView найден");

            // Пробуем разные варианты поиска видео
            List<WebElement> videos = new ArrayList<>();

            // Вариант 1: content (обычно для незалогиненных)
            videos = gridView.findElements(By.id("com.vk.vkvideo:id/content"));
            if (!videos.isEmpty()) {
                System.out.println("  ✅ Видео найдены через content");
            }

            // Вариант 2: video_display (для залогиненных)
            if (videos.isEmpty()) {
                videos = gridView.findElements(By.id("com.vk.vkvideo:id/video_display"));
                if (!videos.isEmpty()) {
                    System.out.println("  ✅ Видео найдены через video_display");
                }
            }

            // Вариант 3: частичное совпадение (на всякий случай)
            if (videos.isEmpty()) {
                videos = gridView.findElements(
                        By.xpath(".//*[contains(@resource-id, 'video') or contains(@resource-id, 'content')]")
                );
                if (!videos.isEmpty()) {
                    System.out.println("  ✅ Видео найдены по частичному совпадению");
                }
            }

            if (videos.isEmpty()) {
                System.out.println("  ❌ Видео не найдены");
                return false;
            }

            System.out.println("  ✅ Найдено видео: " + videos.size() + " шт");
            System.out.println("\n🎬 Запуск первого видео...");
            videos.get(0).click();

            // Ждем появления элементов плеера (максимум 15 секунд)
            System.out.println("\n🎯 Проверка воспроизведения...");

            boolean playerReady = false;

            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.id("com.vk.vkvideo:id/player_control")
                ));
                System.out.println("  ✅ playerControl найден");
                playerReady = true;
            } catch (Exception e) {
                System.out.println("  ⚪ playerControl не найден");
            }

            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.id("com.vk.vkvideo:id/video_display")
                ));
                System.out.println("  ✅ videoDisplay найден");
                playerReady = true;
            } catch (Exception e) {
                System.out.println("  ⚪ videoDisplay не найден");
            }

            // Если хотя бы один элемент плеера найден — считаем успехом
            if (playerReady) {
                System.out.println("\n✅ ВИДЕО УСПЕШНО ВОСПРОИЗВОДИТСЯ");
                Thread.sleep(3000);
                return true;
            } else {
                System.out.println("\n❌ ВИДЕО НЕ ВОСПРОИЗВОДИТСЯ");
                return false;
            }

        } catch (Exception e) {
            System.out.println("  ❌ Ошибка при поиске видео: " + e.getMessage());
            return false;
        }
    }

    private void cleanupAndExit() throws Exception {
        System.out.println("\n🧹 Завершение теста...");

        driver.terminateApp("com.vk.vkvideo");

        ApplicationState state = driver.queryAppState("com.vk.vkvideo");
        if (state == ApplicationState.NOT_RUNNING) {
            System.out.println("  ✅ Приложение закрыто");
        }
        driver.quit();
    }
}