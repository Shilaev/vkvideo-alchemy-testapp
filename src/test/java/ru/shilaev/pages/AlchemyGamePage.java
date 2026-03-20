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
    public static final By REWARD_BUTTON = By.xpath("//android.widget.TextView[@text='Claim!' or @text='Забрать' or @text='Получить' or @text='Get' or @text='Take' or contains(@text, 'claim') or contains(@text, 'получ')]");
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
     * Ожидание появления кнопки Claim! (до 10 минут)
     *
     * @return true если кнопка появилась и нажата, false если нет
     */
    public boolean waitForClaimButtonAndClick() {
        Logger.step("ОЖИДАНИЕ КНОПКИ ПОЛУЧИТЬ НАГРАДУ");

        int maxWaitMinutes = 10;
        long startTime = System.currentTimeMillis();
        long maxWaitMillis = maxWaitMinutes * 60 * 1000L;

        Logger.info("Ожидаем появления кнопки получить награду");

        while (System.currentTimeMillis() - startTime < maxWaitMillis) {
            if (isElementPresent(REWARD_BUTTON, 2)) {
                Logger.success("Кнопка 'Claim!' появилась! Нажимаем.");
                click(REWARD_BUTTON);
                waitForSeconds(3);
                return true;
            }

            waitForSeconds(5);
        }

        Logger.error("Кнопка получить награду не появилась.");
        return false;
    }

    /**
     * Нажимаем на кнопку закрыть рекламу
     */
    public boolean closeAddButton() {
        Logger.step("ЗАКРЫТИЕ РЕКЛАМЫ / ОЖИДАНИЕ НАГРАДЫ");

        try {
            // Проверяем особый случай: реклама с текстом (ADDS_TEXT_VIEW)
            if (safeIsElementPresent(ADDS_TEXT_VIEW, 3)) {
                Logger.warning("Обнаружена реклама с текстовыми кнопками (ADDS_TEXT_VIEW). Ждем 2 минуты...");
                waitForSeconds(120); // Ждем 2 минуты
                Logger.info("2 минуты прошли, проверяем счетчик подсказок");

                if (safeIsElementPresent(HINTS_COUNTER_TEXT, 5)) {
                    Logger.success("Счетчик подсказок появился после ожидания!");
                    return false;
                } else {
                    Logger.warning("Счетчик не появился, потребуется перезапуск");
                    return true;
                }
            }

            // Если есть кнопка закрытия
            else if (safeIsElementPresent(CLOSE_ADD_BUTTON, 5)) {
                Logger.info("Обнаружена кнопка закрытия рекламы");

                int attempts = 0;
                while (attempts < 20) {
                    attempts++;

                    if (clickIfPresent(CLOSE_ADD_BUTTON)) {
                        Logger.info("Кнопка нажата, ожидаем исчезновения рекламы...");
                        safeWaitForElementToDisappear(ADDS_IMAGE_VIEW, 10);
                        waitForSeconds(2);
                    }

                    // Проверяем появление счетчика подсказок
                    if (safeIsElementPresent(HINTS_COUNTER_TEXT, 3)) {
                        Logger.success("Счетчик подсказок появился! Реклама закрыта");
                        return false;
                    }

                    Logger.info("Попытка #" + attempts + ": счетчик еще не появился");
                }

                Logger.error("Счетчик подсказок не появился после 20 попыток");
                return true;
            }

            // Если есть реклама без кнопки закрытия
            else if (safeIsElementPresent(ADDS_IMAGE_VIEW, 5)) {
                Logger.info("Реклама без кнопки закрытия, ожидаем окончания...");

                boolean disappeared = safeWaitForElementToDisappear(ADDS_IMAGE_VIEW, 60);

                if (disappeared) {
                    waitForSeconds(3);

                    // Проверяем счетчик
                    if (safeIsElementPresent(HINTS_COUNTER_TEXT, 5)) {
                        Logger.success("Реклама закончилась, счетчик подсказок появился");
                        return false;
                    }
                }

                Logger.info("Счетчик не появился, пробуем ждать кнопку получения награды (до 10 минут)");
                return !waitForClaimButtonAndClick();
            }

            // Если ничего не нашли, просто ждем кнопку получения награды
            else {
                Logger.info("Ни рекламы, ни кнопки закрытия не найдено. Ждем кнопку получения награды (до 10 минут)");
                return !waitForClaimButtonAndClick();
            }

        } catch (Exception e) {
            Logger.error("Ошибка при закрытии рекламы: " + e.getMessage());
            if (e.getMessage().contains("instrumentation process is not running")) {
                Logger.warning("Обнаружен сбой UiAutomator2, потребуется перезапуск");
                return true;
            }
        }

        Logger.warning("Не удалось штатно закрыть рекламу");
        return true;
    }

    /**
     * Безопасная проверка наличия элемента с обработкой ошибок сессии
     */
    private boolean safeIsElementPresent(By locator, int timeoutSeconds) {
        try {
            return isElementPresent(locator, timeoutSeconds);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("instrumentation process is not running")) {
                Logger.error("Сессия UiAutomator2 потеряна при проверке элемента");
                throw e; // Пробрасываем дальше для обработки в тесте
            }
            Logger.debug("Элемент не найден (ожидаемо): " + locator);
            return false;
        }
    }

    /**
     * Безопасное ожидание исчезновения элемента
     */
    private boolean safeWaitForElementToDisappear(By locator, int timeoutSeconds) {
        try {
            return waitForElementToDisappear(locator, timeoutSeconds);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("instrumentation process is not running")) {
                Logger.error("Сессия UiAutomator2 потеряна при ожидании исчезновения");
                throw e;
            }
            return false;
        }
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