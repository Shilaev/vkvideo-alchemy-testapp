### Для запуска используется следующая команда:
```bash
appium --allow-insecure="*:adb_shell"
```

### Настройка deviceName
В файлах тестов (AlchemyGameTest.java и VkVideoTest.java) укажите ваш deviceName:
```java
config = AppiumConfig.builder()
        .withDeviceName("R5CT31XS90T")  // ваше устройство
        .withNoReset(false)
        .build();
```

### Структура проекта
```
src/
├── main/
│   └── java/ru/shilaev/
│       ├── config/          # Конфигурация Appium
│       ├── driver/          # Управление драйвером
│       └── utils/           # Логирование
└── test/
    └── java/ru/shilaev/
        ├── pages/           # Page Objects
        │   ├── BasePage.java
        │   ├── AlchemyGamePage.java
        │   └── VkVideoPage.java
        └── tests/           # Тесты
            ├── AlchemyGameTest.java
            └── VkVideoTest.java
```

### Что тестируется
**AlchemyGameTest**
  - Запуск приложения
  - Переход на вкладку подсказок
  - Запуск рекламы
  - Получение подсказок за просмотр рекламы

**VkVideoTest**
  - Запуск приложения
  - Закрытие всплывающих окон
  - Поиск и воспроизведение видео
