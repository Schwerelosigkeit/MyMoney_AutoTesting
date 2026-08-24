# MyMoney AutoTesting

Автотесты (UI + API) для веб-приложения [MyMoney](https://mymoney-webapp.onrender.com) — простого трекера доходов и расходов.

> **Ветки репозитория**  
> - `main` — базовая версия **без** Allure  
> - `with-allure` — версия **с** Allure Report (рекомендуется для просмотра отчётов)
> - `gh-pages` — ветка для публикации Allure Report на GitHub Pages (в ней лежат сгенерированные файлы отчёта) 

## Содержание:

> ➠ [Покрытый функционал](#-покрытый-функционал)  
> ➠ [Технологический стек](#-технологический-стек)  
> ➠ [Запуск тестов](#-запуск-тестов)  
> ➠ [Allure Report](#-allure-report)  
> ➠ [Структура проекта](#-структура-проекта)

---


## Покрытый функционал:

Разработаны автотесты на **UI** и **API**.

### UI
- [x] Отображение главной страницы (баланс, список транзакций, статистика по категориям)
- [x] Модальное окно добавления дохода (видимость, позитивные и негативные сценарии)
- [x] Модальное окно добавления расхода (видимость, позитивные и негативные сценарии)
- [x] Удаление транзакции с проверкой обновления баланса и статистики
- [x] Работа кнопки переключения статистики на узком экране
- [x] Валидация суммы (отрицательные, > 1 000 000, лишние знаки после запятой)
- [x] Валидация комментария (недопустимые символы, длина > 500)

### API
- [x] `GET /` — главная страница
- [x] `GET /api/categories` — список категорий
- [x] `GET /api/balance` — текущий баланс
- [x] `GET /api/monthly-transactions` и `/api/transactions/month`
- [x] `POST /api/income` и `POST /api/expense` (позитивные и негативные)
- [x] `DELETE /api/transactions/{id}`
- [x] Проверки неверных HTTP-методов и валидации данных

---


## Технологический стек

В проекте используются:
- **Java 24** + **Maven**
- **Selenide** — UI-автотесты
- **TestNG** — фреймворк тестирования и группировки
- **Rest-Assured** — API-тесты
- **DataFaker** — генерация тестовых данных
- **GitHub Actions** — CI (запуск тестов)
- **Allure Report** — интерактивные отчёты (ветка `with-allure`)

---


## Запуск тестов

### Локально
> Все тесты (по testng.xml)  
(bash)  
mvn clean test  
Для варианта с отчётом (Allure):  
mvn allure:report  
mvn allure:serve

### Headless-режим (CI)
> mvn clean test -Dheadless=true  
Через GitHub Actions  
Workflow .github/workflows/tests.yml запускается вручную (workflow_dispatch).  
После прогона отчёты Surefire загружаются как артефакты.

---


## Allure Report
Полный отчёт доступен по ссылке:  
[Открыть Allure Report](https://schwerelosigkeit.github.io/MyMoney_AutoTesting/#behaviors)  
Рекомендуется смотреть отчёт онлайн — там есть подробности по каждому тесту, шагам и группировке.

---


## Структура проекта:
textMyMoney_AutoTesting/  
├── .github/workflows/     # CI (GitHub Actions)  
├── src/test/java/  
│   ├── api/               # API-тесты (Rest-Assured)  
│   ├── data/              # Генераторы данных (DataFaker)  
│   ├── ui/pages/         # Page Objects (Selenide)  
│   └── ui/tests/         # UI-тесты  
├── pom.xml  
└── testng.xml             # Сьюты и группы тестов  

### 🔗 Полезные ссылки

> Приложение: [mymoney-webapp.onrender.com ](https://mymoney-webapp.onrender.com/)  
> Исходники приложения: [Schwerelosigkeit/MyMoney](https://github.com/Schwerelosigkeit/MyMoney)  
> Allure-отчёт: ссылка выше  
