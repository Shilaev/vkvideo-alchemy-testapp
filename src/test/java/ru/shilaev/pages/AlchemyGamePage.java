package ru.shilaev.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.shilaev.utils.Logger;

import java.time.Duration;
import java.util.Map;

public class AlchemyGamePage extends BasePage {
    //region locators
    // Локаторы для начального экрана
    public static final By START_GAME_BUTTON = By.xpath("//x2.f1/android.view.View/android.view.View/android.view.View/android.view.View[5]/android.widget.Button");

    // Локаторы внутри игры
    public static final By SHOW_HINTS_BUTTON = By.xpath("//x2.f1/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]/android.widget.Button");
    public static final By SHOW_ADDS_LOADING_STATUS = By.xpath("//android.widget.TextView[@text='Watch' or @text='Смотреть']");
    public static final By WATCH_ADDS_BUTTON = By.xpath("//x2.f1/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[2]/android.view.View[@clickable='true']");
    public static final By HINTS_COUNTER_EQUALS_FOUR = By.xpath("//x2.f1/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]//android.widget.TextView[@text='4']");
    public static final By HINTS_COUNTER_TEXT = By.xpath("//x2.f1/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]//android.widget.TextView");

    // Локаторы для рекламы
    public static final By ADDS_TEXT_VIEW = By.xpath("//android.widget.TextView[@text='Скачать' or @text='Играть' or @text='Play' or @text='Установить' or @text='Открыть' or @text='Смотреть' or @text='Watch' or @text='Download' or @text='Install']");
    public static final By ADDS_IMAGE_VIEW = By.id("com.ilyin.alchemy:id/inter_native_ad_view");
    public static final By ADDS_COUNTDOWN_TIMER = By.id("com.ilyin.alchemy:id/inter_text_countdown");
    public static final By CLOSE_ADD_BUTTON = By.id("com.ilyin.alchemy:id/bigo_ad_btn_close");
    //endregion

    //region utils methods

    /**
     * Очистить данные игры
     */
    public void clearGameData() {
        executeScript("mobile: shell", Map.of("command", "pm clear com.ilyin.alchemy"));
    }

    /**
     * Полное название пакета приложения
     */
    public String getPackageName() {
        return "com.ilyin.alchemy";
    }

    /**
     * Нажатие кнопки Play
     */
    public void playButtonClick() {
        click(START_GAME_BUTTON);
    }

    /**
     * Нажатие на кнопку Hints
     */
    public void hintButtonClick() {
        click(SHOW_HINTS_BUTTON);
    }

    /**
     * Нажатие на кнопку Watch Adds
     */
    public void watchAddsButtonClick() {
        if (waitForHintsToLoad(10)) {
            click(WATCH_ADDS_BUTTON);
        } else {
            Logger.error("Не удалось дождаться загрузки подсказок");
        }
    }

    /**
     * Ожидание загрузки вкладки подсказок
     */
    public boolean waitForHintsToLoad(int timeoutSeconds) {
        Logger.info("Ожидание загрузки вкладки подсказок...");
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            customWait.until(ExpectedConditions.presenceOfElementLocated(SHOW_ADDS_LOADING_STATUS));
            Logger.success("Вкладка подсказок загружена");
            return true;
        } catch (Exception e) {
            Logger.error("Вкладка подсказок не загрузилась за " + timeoutSeconds + " сек");
            return false;
        }
    }

    /**
     * Нажимаем на кнопку закрыть рекламу
     */
    public boolean closeAddButton() {
        Logger.step("ЗАКРЫТИЕ РЕКЛАМЫ");

        try {
            // Если есть кнопка закрытия
            if (isElementPresent(CLOSE_ADD_BUTTON, 5)) {
                Logger.info("Обнаружена кнопка закрытия рекламы");

                int attempts = 0;
                while (attempts < 20) {
                    attempts++;

                    if (clickIfPresent(CLOSE_ADD_BUTTON)) {
                        Logger.info("Кнопка нажата, ожидаем исчезновения рекламы...");
                        waitForElementToDisappear(ADDS_IMAGE_VIEW, 10);
                        waitForSeconds(2);
                    }

                    // Проверяем появление счетчика подсказок
                    if (isElementPresent(HINTS_COUNTER_TEXT, 3)) {
                        Logger.success("Счетчик подсказок появился! Реклама закрыта");
                        return false;
                    }

                    Logger.info("Попытка #" + attempts + ": счетчик еще не появился");
                }

                Logger.error("Счетчик подсказок не появился после 20 попыток");
                return true;
            }

            // Если нет кнопки закрытия, возможно реклама с таймером
            else if (isElementPresent(ADDS_IMAGE_VIEW) || isElementPresent(ADDS_TEXT_VIEW)) {
                Logger.info("Реклама без кнопки закрытия, ожидаем окончания...");

                boolean disappeared = waitForElementToDisappear(ADDS_IMAGE_VIEW, 60);

                if (disappeared) {
                    waitForSeconds(3);

                    if (isElementPresent(HINTS_COUNTER_TEXT, 5)) {
                        Logger.success("Реклама закончилась, счетчик подсказок появился");
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            Logger.error("Ошибка при закрытии рекламы: " + e.getMessage());
        }

        Logger.warning("Не удалось штатно закрыть рекламу");
        return true;
    }

    /**
     * Проверка, что Hints открылись и загрузились
     */
    public boolean isHintsLoaded() {
        Logger.info("Проверка загрузки вкладки подсказок...");

        if (isElementPresent(SHOW_ADDS_LOADING_STATUS, 5)) {
            Logger.success("Вкладка подсказок загружена");
            return true;
        } else {
            Logger.debug("Вкладка подсказок еще не загрузилась");
            return false;
        }
    }

    /**
     * Проверка, что реклама открылась и загрузилась
     */
    public boolean isAddsLoaded() {
        Logger.info("Проверка загрузки рекламы...");

        if (isElementPresent(ADDS_IMAGE_VIEW, 5)) {
            Logger.success("Реклама загружена (найден ImageView)");
            return true;
        }

        if (isElementPresent(ADDS_TEXT_VIEW, 3)) {
            Logger.success("Реклама загружена (найден TextView)");
            return true;
        }

        Logger.warning("Реклама не открылась или не распознана");
        return false;
    }

    /**
     * Получить текущее количество подсказок
     */
    public int getHintsCount() {
        try {
            String text = getTextIfPresent(HINTS_COUNTER_TEXT);
            if (!text.isEmpty()) {
                return Integer.parseInt(text);
            }
        } catch (NumberFormatException e) {
            Logger.error("Не удалось преобразовать текст в число: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Проверка, что значение hints стало равным 4
     */
    public boolean isHintsEqualsFour() {
        return isElementPresent(HINTS_COUNTER_EQUALS_FOUR, 3);
    }

    /**
     * Ожидание, пока hints не станут равны 4
     */
    public boolean waitForHintsToBecomeFour(int timeoutSeconds) {
        Logger.info("Ожидание получения 4 подсказок...");
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            customWait.until(driver -> isHintsEqualsFour());
            Logger.success("Получено 4 подсказки");
            return true;
        } catch (Exception e) {
            Logger.error("Не удалось получить 4 подсказки за " + timeoutSeconds + " сек");
            return false;
        }
    }
    //endregion
}