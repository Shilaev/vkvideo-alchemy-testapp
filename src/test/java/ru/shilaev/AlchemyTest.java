package ru.shilaev;

import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.fail;

public class AlchemyTest {

    private AndroidDriver driver;
    private WebDriverWait wait;

    @Test
    public void launchApp() throws Exception {
        System.out.println("\n🚀 ЗАПУСК ТЕСТА АЛХИМИЯ");
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
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        // Загрузка библиотеки OpenCV
        nu.pattern.OpenCV.loadLocally();

        // Очистка данных перед тестом
        System.out.println("\n🧹 Очистка данных игры...");
        driver.executeScript("mobile: shell", Map.of("command", "pm clear com.ilyin.alchemy"));
        System.out.println("✅ Данные очищены");

        // Запуск приложения
        System.out.println("\n📱 Запуск Alchemy...");
        driver.activateApp("com.ilyin.alchemy");
        System.out.println("✅ Алхимия запущена");

        // Шаг 1: Нажимаем Play
        System.out.println("\n1️⃣ Нажимаем Play...");
        performProtectedAction(() -> {
            WebElement playButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//x2.f1/android.view.View/android.view.View/android.view.View/android.view.View[5]/android.widget.Button"))
            );
            playButton.click();
            System.out.println("   ✅ Play нажата");
            return null;
        }, "Нажатие Play");

        Thread.sleep(3000);

        // Шаг 2: Нажимаем кнопку подсказок
        System.out.println("\n2️⃣ Нажимаем кнопку подсказок...");
        performProtectedAction(() -> {
            WebElement hintButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath(
                            "//x2.f1/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]/android.widget.Button"
                    ))
            );
            hintButton.click();
            System.out.println("   ✅ Кнопка подсказок нажата");
            return null;
        }, "Нажатие кнопки подсказок");

        // Шаг 3: Получаем подсказку за рекламу
        System.out.println("\n3️⃣ Получаем подсказку за рекламу...");
        performProtectedAction(() -> {
            // Сначала пробуем найти по тексту
            try {
                WebElement watchButton = wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath("//android.widget.TextView[@text='Watch' or @text='Смотреть']")
                        )
                );
                watchButton.click();
                System.out.println("   ✅ Кнопка Watch/Смотреть нажата");
                return null;
            } catch (Exception e) {
                // Если не нашли по тексту — ищем контейнер
                System.out.println("   ⚠️ Не найдено по тексту, ищем контейнер");
                WebElement container = wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//x2.f1/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[2]/android.view.View")
                        )
                );
                WebElement button = container.findElement(By.xpath(".//android.widget.Button"));
                wait.until(ExpectedConditions.elementToBeClickable(button));
                button.click();
                System.out.println("   ✅ Кнопка нажата через контейнер");
                return null;
            }
        }, "Поиск и нажатие Watch");

        // Шаг 4: Закрываем всю рекламу с помощью распознавания изображений
        System.out.println("\n📢 Закрываем всю рекламу...");
        handleAllAds();

        // Шаг 5: Перезапускаем приложение, чтобы награда засчиталась
        System.out.println("\n🔄 Перезапуск приложения для фиксации награды...");
        driver.terminateApp("com.ilyin.alchemy");
        Thread.sleep(2000);
        driver.activateApp("com.ilyin.alchemy");
        System.out.println("✅ Приложение перезапущено");

        // Шаг 6: Снова нажимаем Play
        System.out.println("\n🎯 Повторный вход в игру...");
        performProtectedAction(() -> {
            WebElement playButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//x2.f1/android.view.View/android.view.View/android.view.View/android.view.View[5]/android.widget.Button"))
            );
            playButton.click();
            System.out.println("   ✅ Play нажата");
            return null;
        }, "Повторное нажатие Play");

        Thread.sleep(3000);

        // Шаг 7: Проверяем количество подсказок
        System.out.println("\n🔍 Проверка количества подсказок...");
        performProtectedAction(() -> {
            WebElement hintElement = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//x2.f1/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]//android.widget.TextView")
                    )
            );

            String hintText = hintElement.getText().trim();
            System.out.println("📊 Найден текст: '" + hintText + "'");

            try {
                int hintCount = Integer.parseInt(hintText);
                System.out.println("📊 Количество подсказок: " + hintCount);

                if (hintCount == 4) {
                    System.out.println("\n✅ УСПЕХ! Количество подсказок равно 4");
                    System.out.println("═══════════════════════════");
                    System.out.println("🎉 ТЕСТ ПРОЙДЕН");
                } else {
                    fail("\n❌ ОШИБКА: Ожидалось 4. В результате " + hintCount);
                }
            } catch (NumberFormatException e) {
                fail("❌ ОШИБКА: Текст '" + hintText + "' не является числом");
            }
            return null;
        }, "Проверка количества подсказок");

        // Завершение
        System.out.println("\n🧹 Завершение теста...");
        driver.terminateApp("com.ilyin.alchemy");
        driver.quit();
        System.out.println("\n✅ ТЕСТ ЗАВЕРШЕН");
    }

    /**
     * Выполняет действие с защитой от рекламы.
     * Если действие падает с ошибкой, пробует закрыть рекламу и повторяет действие.
     */
    private <T> T performProtectedAction(Supplier<T> action, String actionName) {
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("   🔄 Попытка " + attempt + "/" + maxRetries + ": " + actionName);
                return action.get();
            } catch (Exception e) {
                System.out.println("   ⚠️ Ошибка при выполнении: " + e.getMessage());

                // Пробуем закрыть рекламу
                if (closeAnyAdWithImageRecognition()) {
                    System.out.println("   ✅ Реклама закрыта, пробуем снова...");
                    try { Thread.sleep(2000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                } else {
                    // Если рекламы нет — значит реальная ошибка
                    if (attempt == maxRetries) {
                        throw new RuntimeException("Не удалось выполнить '" + actionName + "' после " + maxRetries + " попыток", e);
                    }
                    System.out.println("   ⚪ Рекламы нет, пробуем ещё раз...");
                }
            }
        }
        throw new RuntimeException("Не удалось выполнить '" + actionName + "'");
    }

    //Пытаемся найти хоть какой-то шаблон рекламы
    private void handleAllAds() {
        int maxAttempts = 30; // максимум попыток закрытия
        int attempts = 0;
        boolean adClosed;

        // Ждём появления рекламы
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        do {
            adClosed = false;

            // Пробуем распознавание с несколькими проходами (разные области поиска)
            if (closeAnyAdWithImageRecognition()) {
                adClosed = true;
            }

            if (adClosed) {
                attempts++;
                System.out.println("   ✅ Закрыто реклам: " + attempts);
                try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            } else {
                // Если ничего не нашли, проверяем, есть ли ещё реклама
                List<WebElement> adContainers = driver.findElements(
                        By.xpath("//android.widget.RelativeLayout[contains(@content-desc, 'pageIndex:')]")
                );
                if (adContainers.isEmpty()) {
                    break;
                } else {
                    System.out.println("   ⚪ Реклама есть, но не нашли кнопку, пробуем тап в угол...");
                    tapRightTopCorner();
                    try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    adClosed = true; // продолжаем цикл
                }
            }
        } while (attempts < maxAttempts);

        if (attempts > 0) {
            System.out.println("✅ Всего закрыто реклам: " + attempts);
        } else {
            System.out.println("⚪ Рекламы не было.");
        }
    }

    //Кликнуть в угол, если ничего не помогло
    private void tapRightTopCorner() {
        try {
            int width = driver.manage().window().getSize().getWidth();
            int x = width - 50;
            int y = 100;

            driver.executeScript("mobile: clickGesture", Map.of(
                    "x", x,
                    "y", y
            ));
            System.out.println("         ✅ Тапнули в правый верхний угол");
        } catch (Exception e) {
            System.out.println("         ⚠️ Ошибка тапа: " + e.getMessage());
        }
    }


    //Пытаемся закрыть рекламу...хоть как-то найти способ
    private boolean closeAnyAdWithImageRecognition() {
        System.out.println("      🔍 Ищем способ закрыть рекламу (OpenCV)...");

        // 📁 Папка с шаблонами
        String templatesDir = "templates/";

        // 🎯 РАСШИРЕННЫЙ СПИСОК ШАБЛОНОВ
        String[] templateFiles = {
                // Крестики (разные вариации)
                "close_x.png",
                "close_x2.png",
                "skip_button.png",
                "skip_button2.png"
        };

        File screenshot = null;
        try {
            screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Mat source = Imgcodecs.imread(screenshot.getAbsolutePath());

            if (source.empty()) {
                System.out.println("         ⚠️ Не удалось загрузить скриншот");
                return false;
            }

            int screenWidth = source.cols();
            int screenHeight = source.rows();

            // ЭТАП 1: Поиск в правом верхнем углу (самое вероятное место)
            int searchStartX = screenWidth * 2 / 3;      // начинаем с 2/3 экрана
            int searchEndX = screenWidth;
            int searchStartY = 0;
            int searchEndY = screenHeight / 3;           // только верхняя треть

            System.out.println("         📍 Ищем в правом верхнем углу...");

            for (String templateFile : templateFiles) {
                String templatePath = templatesDir + templateFile;
                File templateFileObj = new File(templatePath);

                if (!templateFileObj.exists()) {
                    continue;
                }

                Mat template = Imgcodecs.imread(templatePath);
                if (template.empty()) continue;

                // Проверяем, что шаблон помещается в область поиска
                if (template.cols() > (searchEndX - searchStartX) ||
                        template.rows() > (searchEndY - searchStartY)) {
                    continue;
                }

                // Вырезаем область поиска
                int roiWidth = searchEndX - searchStartX - template.cols() + 1;
                int roiHeight = searchEndY - searchStartY - template.rows() + 1;

                if (roiWidth <= 0 || roiHeight <= 0) continue;

                Rect searchRect = new Rect(searchStartX, searchStartY, roiWidth, roiHeight);
                Mat sourceRoi = new Mat(source, searchRect);
                Mat result = new Mat();

                Imgproc.matchTemplate(sourceRoi, template, result, Imgproc.TM_CCOEFF_NORMED);
                Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

                // Разные пороги для разных типов шаблонов
                double threshold = 0.7;
                if (templateFile.contains("arrow")) threshold = 0.6;
                if (templateFile.contains("skip")) threshold = 0.65;
                if (templateFile.contains("x")) threshold = 0.75;

                if (mmr.maxVal > threshold) {
                    Point matchLoc = mmr.maxLoc;
                    int centerX = searchStartX + (int) (matchLoc.x + template.cols() / 2);
                    int centerY = searchStartY + (int) (matchLoc.y + template.rows() / 2);

                    System.out.println("         ✅ НАЙДЕН в правом углу: " + templateFile +
                            " (совпадение " + String.format("%.2f", mmr.maxVal) + ")");
                    System.out.println("            Координаты: (" + centerX + ", " + centerY + ")");

                    // Пробуем кликнуть
                    if (performClick(centerX, centerY, templateFile)) {
                        return true;
                    }
                }
            }

            // ЭТАП 2: Поиск по всему экрану
            System.out.println("         🔄 Ничего в правом углу, ищем по всему экрану...");

            for (String templateFile : templateFiles) {
                String templatePath = templatesDir + templateFile;
                if (!new File(templatePath).exists()) continue;

                Mat template = Imgcodecs.imread(templatePath);
                if (template.empty()) continue;

                int resultCols = source.cols() - template.cols() + 1;
                int resultRows = source.rows() - template.rows() + 1;

                if (resultCols <= 0 || resultRows <= 0) continue;

                Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);
                Imgproc.matchTemplate(source, template, result, Imgproc.TM_CCOEFF_NORMED);
                Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

                double threshold = 0.65;
                if (templateFile.contains("arrow")) threshold = 0.55;
                if (templateFile.contains("skip")) threshold = 0.6;

                if (mmr.maxVal > threshold) {
                    Point matchLoc = mmr.maxLoc;
                    int centerX = (int) (matchLoc.x + template.cols() / 2);
                    int centerY = (int) (matchLoc.y + template.rows() / 2);

                    System.out.println("         ✅ НАЙДЕН (везде): " + templateFile +
                            " с коэф. " + String.format("%.2f", mmr.maxVal));
                    System.out.println("            Координаты: (" + centerX + ", " + centerY + ")");

                    if (performClick(centerX, centerY, templateFile)) {
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("         ⚠️ Ошибка распознавания: " + e.getMessage());
        } finally {
            if (screenshot != null && screenshot.exists()) {
                screenshot.delete();
            }
        }
        return false;
    }

    //Клик по координатам...
    private boolean performClick(int x, int y, String templateName) {
        try {
            // Кликаем
            driver.executeScript("mobile: clickGesture", Map.of(
                    "x", x,
                    "y", y
            ));

            Thread.sleep(1500);

            // Проверяем, не ушли ли мы из игры
            String currentPackage = driver.getCurrentPackage();
            if (!currentPackage.equals("com.ilyin.alchemy")) {
                System.out.println("         ⚠️ Ушли в другое приложение (" + currentPackage + ")! Возвращаемся...");

                // Пробуем вернуться
                try {
                    driver.navigate().back();
                    Thread.sleep(1000);
                } catch (Exception ignored) {}

                driver.activateApp("com.ilyin.alchemy");
                Thread.sleep(2000);

                // Если ушли, считаем что клик не помог
                return false;
            }

            // Проверяем, исчезла ли реклама
            List<WebElement> adContainers = driver.findElements(
                    By.xpath("//android.widget.RelativeLayout[contains(@content-desc, 'pageIndex:')]")
            );

            if (adContainers.isEmpty()) {
                System.out.println("         ✅ Реклама успешно закрыта!");
                return true;
            } else {
                System.out.println("         ⚠️ Реклама осталась, пробуем дальше...");
                return false;
            }

        } catch (Exception e) {
            System.out.println("         ⚠️ Ошибка клика: " + e.getMessage());
            return false;
        }
    }
}