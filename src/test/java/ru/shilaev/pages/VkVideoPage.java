package ru.shilaev.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.shilaev.utils.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Методы для работы с приложением VK Video
 */
public class VkVideoPage extends BasePage {
    //region locators
    // Локаторы для попапов
    private static final By AD_CLOSE_BUTTON = By.id("com.vk.vkvideo:id/close_btn_left");
    private static final By SKIP_BUTTON = By.id("com.vk.vkvideo:id/fast_login_tertiary_btn");

    // Локаторы для главной страницы
    private static final By GRID_VIEW = By.id("com.vk.vkvideo:id/recycler");

    // Локаторы для видео (разные варианты)
    private static final By VIDEO_CONTENT = By.id("com.vk.vkvideo:id/content");
    private static final By VIDEO_DISPLAY = By.id("com.vk.vkvideo:id/video_display");
    private static final By VIDEO_XPATH = By.xpath(".//*[contains(@resource-id, 'video') or contains(@resource-id, 'content')]");

    // Локаторы для плеера
    private static final By PLAYER_CONTROL = By.id("com.vk.vkvideo:id/player_control");
    private static final By VIDEO_PLAYER_DISPLAY = By.id("com.vk.vkvideo:id/video_display");
    //endregion

    //region utils methods

    /**
     * Дождаться загрузки главной страницы
     */
    public boolean isMainPageLoaded() {
        try {
            Logger.info("Ожидание загрузки главной страницы...");
            findElement(GRID_VIEW);
            Logger.success("Главная страница загружена");
            return true;
        } catch (Exception e) {
            Logger.error("Главная страница не загрузилась");
            return false;
        }
    }

    /**
     * Закрыть все всплывающие окна
     */
    public void closeAllPopups() {
        Logger.step("Закрытие всплывающих окон");
        clickIfPresent(AD_CLOSE_BUTTON, "Реклама");
        clickIfPresent(SKIP_BUTTON, "Skip");
    }

    /**
     * Найти все видео на главной
     */
    public List<WebElement> findAllVideos() {
        Logger.step("Поиск видео на главной странице");

        WebElement gridView = findElement(GRID_VIEW);
        List<WebElement> videos = new ArrayList<>();

        // Пробуем разные варианты поиска
        videos = findElements(gridView, VIDEO_CONTENT);
        if (!videos.isEmpty()) {
            Logger.success("Видео найдены через content: " + videos.size() + " шт");
            return videos;
        }

        videos = findElements(gridView, VIDEO_DISPLAY);
        if (!videos.isEmpty()) {
            Logger.success("Видео найдены через video_display: " + videos.size() + " шт");
            return videos;
        }

        videos = findElements(gridView, VIDEO_XPATH);
        if (!videos.isEmpty()) {
            Logger.success("Видео найдены по частичному совпадению: " + videos.size() + " шт");
            return videos;
        }

        Logger.error("Видео не найдены");
        return videos;
    }

    /**
     * Запустить первое видео
     */
    public boolean playFirstVideo() {
        List<WebElement> videos = findAllVideos();

        if (videos.isEmpty()) {
            Logger.error("Нет видео для запуска");
            return false;
        }

        Logger.step("Запуск первого видео");
        videos.get(0).click();
        return true;
    }

    /**
     * Проверить, запустилось ли видео
     */
    public boolean isVideoPlaying() {
        Logger.step("Проверка воспроизведения");

        try {
            // Ждем появления любого элемента плеера (макс 15 секунд)
            WebDriverWait playerWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            playerWait.until(driver ->
                    isElementPresent(PLAYER_CONTROL) || isElementPresent(VIDEO_PLAYER_DISPLAY)
            );

            Logger.success("ВИДЕО УСПЕШНО ВОСПРОИЗВОДИТСЯ");
            return true;

        } catch (Exception e) {
            Logger.error("ВИДЕО НЕ ВОСПРОИЗВОДИТСЯ");
            return false;
        }
    }

    /**
     * Полное название пакета приложения
     */
    public String getPackageName() {
        return "com.vk.vkvideo";
    }
    //endregion
}