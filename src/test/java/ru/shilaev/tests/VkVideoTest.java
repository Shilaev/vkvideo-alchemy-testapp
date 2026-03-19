package ru.shilaev.tests;

import org.junit.jupiter.api.*;
import ru.shilaev.config.AppiumConfig;
import ru.shilaev.driver.DriverManager;
import ru.shilaev.pages.VkVideoPage;
import ru.shilaev.utils.Logger;

/**
 * Тест Vk Video - запуск приложения, закрытие попапов, запуск видео, закрытие плеера
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VkVideoTest {
    private static DriverManager driverManager;
    private static AppiumConfig config;
    private VkVideoPage vkVideoPage;

    @BeforeAll
    public static void setUpSuite() {
        Logger.header("НАЧАЛО ТЕСТОВ VK VIDEO");

        config = AppiumConfig.builder()
                .withDeviceName("Android Emulator")
                .withNoReset(false)
                .build();

        driverManager = DriverManager.getInstance();
    }

    @BeforeEach
    public void setUp() {
        Logger.step("ИНИЦИАЛИЗАЦИЯ ТЕСТА");

        driverManager.createDriver(config, 15);
        vkVideoPage = new VkVideoPage();

        Logger.info("Запуск VK Video...");
        driverManager.launchApp(vkVideoPage.getPackageName());

        // Ждем загрузки приложения (можно заменить на ожидание конкретного элемента)
        vkVideoPage.waitForSeconds(15);
    }

    @Test
    @Order(1)
    @DisplayName("Тест: Запуск приложения и проверка состояния")
    public void testAppLaunch() {
        Logger.step("ПРОВЕРКА ЗАПУСКА ПРИЛОЖЕНИЯ");

        boolean isRunning = driverManager.isAppRunningInForeground(vkVideoPage.getPackageName());

        Assertions.assertTrue(isRunning, "Приложение должно быть запущено в foreground");
        Logger.success("Приложение запущено и работает в foreground");
    }

    @Test
    @Order(2)
    @DisplayName("Тест: Закрытие всплывающих окон")
    public void testClosePopups() {
        Logger.step("ПРОВЕРКА ВСПЛЫВАЮЩИХ ОКОН");

        // Проверяем, что главная страница загрузилась
        Assertions.assertTrue(vkVideoPage.isMainPageLoaded(),
                "Главная страница должна загрузиться");

        // Закрываем все попапы
        vkVideoPage.closeAllPopups();

        // Даем время на применение закрытия
        vkVideoPage.waitForSeconds(2);
    }

    @Test
    @Order(3)
    @DisplayName("Тест: Поиск и воспроизведение видео")
    public void testFindAndPlayVideo() {
        Logger.step("ПОИСК И ВОСПРОИЗВЕДЕНИЕ ВИДЕО");

        // Убеждаемся, что главная страница загружена
        Assertions.assertTrue(vkVideoPage.isMainPageLoaded(),
                "Главная страница должна быть загружена");

        // Закрываем возможные попапы перед поиском видео
        vkVideoPage.closeAllPopups();

        // Запускаем первое видео
        boolean videoStarted = vkVideoPage.playFirstVideo();
        Assertions.assertTrue(videoStarted, "Видео должно запуститься");

        // Проверяем, что видео воспроизводится
        boolean isPlaying = vkVideoPage.isVideoPlaying();
        Assertions.assertTrue(isPlaying, "Видео должно воспроизводиться");

        // Смотрим видео 3 секунды
        Logger.info("Смотрим видео 3 секунды...");
        vkVideoPage.waitForSeconds(3);

        Logger.success("Тест воспроизведения видео успешно завершен");
    }

    @AfterEach
    public void tearDown() {
        Logger.step("ЗАВЕРШЕНИЕ ТЕСТА");

        try {
            // Закрываем приложение
            driverManager.terminateApp(vkVideoPage.getPackageName());

            // Проверяем, что приложение закрылось
            if (!driverManager.isAppRunningInForeground(vkVideoPage.getPackageName())) {
                Logger.success("Приложение закрыто");
            }
        } catch (Exception e) {
            Logger.error("Ошибка при закрытии приложения: " + e.getMessage());
        } finally {
            // Всегда завершаем драйвер
            driverManager.quitDriver();
            Logger.info("Драйвер завершен");
        }
    }

    @AfterAll
    public static void tearDownSuite() {
        Logger.step("ЗАВЕРШЕНИЕ НАБОРА ТЕСТОВ");
        Logger.success("Все тесты VK Video успешно завершены");
    }
}