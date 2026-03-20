package ru.shilaev.tests;

import org.junit.jupiter.api.*;
import ru.shilaev.config.AppiumConfig;
import ru.shilaev.driver.DriverManager;
import ru.shilaev.pages.AlchemyGamePage;
import ru.shilaev.utils.Logger;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AlchemyGameTest {
    private static DriverManager driverManager;
    private static AppiumConfig config;
    private AlchemyGamePage alchemyGamePage;

    @BeforeAll
    public static void setUpSuite() {
        Logger.header("НАЧАЛО ТЕСТОВ АЛХИМИЯ");
        config = AppiumConfig.builder()
                .withDeviceName("Android Emulator")
                .withNoReset(false)
                .build();
        driverManager = DriverManager.getInstance();
    }

    @BeforeEach
    public void setUp() {
        Logger.step("ИНИЦИАЛИЗАЦИЯ ТЕСТА");
        driverManager.createDriver(config, 3600);
        alchemyGamePage = new AlchemyGamePage();

        Logger.info("Очистка данных приложения");
        alchemyGamePage.clearGameData();

        Logger.info("Запуск Алхимия...");
        driverManager.launchApp(alchemyGamePage.getPackageName());
        alchemyGamePage.waitForSeconds(10);
    }

    @Test
    @Order(1)
    @DisplayName("Тест: Запуск приложения")
    public void testAppLaunch() {
        Logger.step("ПРОВЕРКА ЗАПУСКА");

        boolean isRunning = driverManager.isAppRunningInForeground(alchemyGamePage.getPackageName());
        Assertions.assertTrue(isRunning, "Приложение должно быть запущено");
        Logger.success("Приложение работает в foreground");
    }

    @Test
    @Order(2)
    @DisplayName("Тест: Переход на вкладку подсказок")
    public void testStartGameAndOpenHint() {
        Logger.step("ПЕРЕХОД НА ВКЛАДКУ ПОДСКАЗОК");

        Logger.info("Нажатие Play");
        alchemyGamePage.playButtonClick();

        Logger.info("Нажатие Hints");
        alchemyGamePage.hintButtonClick();

        boolean loaded = alchemyGamePage.waitForHintsToLoad(15);
        Assertions.assertTrue(loaded, "Вкладка подсказок должна загрузиться");
    }

    @Test
    @Order(3)
    @DisplayName("Тест: Запуск рекламы")
    public void testWatchAddButton() {
        Logger.step("ЗАПУСК РЕКЛАМЫ");

        Logger.info("Нажатие Play");
        alchemyGamePage.playButtonClick();

        Logger.info("Нажатие Hints");
        alchemyGamePage.hintButtonClick();

        Assertions.assertTrue(alchemyGamePage.waitForHintsToLoad(10), "Вкладка подсказок не загрузилась");

        Logger.info("Нажатие Watch");
        alchemyGamePage.watchAddsButtonClick();

        boolean adsLoaded = alchemyGamePage.isAddsLoaded();
        Assertions.assertTrue(adsLoaded, "Реклама не открылась");
    }

    @Test
    @Order(4)
    @DisplayName("Тест: Получение подсказок за рекламу")
    public void testLootHintsForAdds() {
        Logger.step("ПОЛУЧЕНИЕ ПОДСКАЗОК ЗА РЕКЛАМУ");

        try {
            Logger.info("Нажатие Play");
            alchemyGamePage.playButtonClick();

            Logger.info("Нажатие Hints");
            alchemyGamePage.hintButtonClick();

            Assertions.assertTrue(alchemyGamePage.waitForHintsToLoad(30), "Вкладка подсказок не загрузилась");

            Logger.info("Нажатие Watch");
            alchemyGamePage.watchAddsButtonClick();

            Assertions.assertTrue(alchemyGamePage.isAddsLoaded(), "Реклама не открылась");

            boolean needRestart = alchemyGamePage.closeAddButton();
            if (needRestart) {
                Logger.warning("Перезапуск приложения...");
                driverManager.terminateApp(alchemyGamePage.getPackageName());
                alchemyGamePage.waitForSeconds(15);
                driverManager.launchApp(alchemyGamePage.getPackageName());
                alchemyGamePage.waitForSeconds(5);

                Logger.info("Нажатие Play");
                alchemyGamePage.playButtonClick();
            }

            boolean hintsReceived = alchemyGamePage.waitForHintsToBecomeFour(15);
            Assertions.assertTrue(hintsReceived, "Не получено 4 подсказки");

            int hintsCount = alchemyGamePage.getHintsCount();
            Logger.success("Итоговое количество подсказок: " + hintsCount);

        } catch (Exception e) {
            Logger.error("Тест упал с ошибкой: " + e.getMessage());
            throw e;
        }
    }

    @AfterEach
    public void tearDown() {
        Logger.step("ЗАВЕРШЕНИЕ ТЕСТА");

        driverManager.terminateApp(alchemyGamePage.getPackageName());

        if (!driverManager.isAppRunningInForeground(alchemyGamePage.getPackageName())) {
            Logger.success("Приложение закрыто");
        }

        driverManager.quitDriver();
    }

    @AfterAll
    public static void tearDownSuite() {
        Logger.step("ЗАВЕРШЕНИЕ ТЕСТОВ");
        Logger.success("Все тесты завершены");
    }
}