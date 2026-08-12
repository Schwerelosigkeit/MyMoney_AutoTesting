# MyMoney AutoTesting

Автотесты (UI + API) для веб-приложения [MyMoney](https://mymoney-webapp.onrender.com) — простого трекера доходов и расходов.

> **Ветки репозитория**  
> - `main` — базовая версия **без** Allure  
> - `with-allure` — версия **с** Allure Report (рекомендуется для просмотра отчётов)
> - `gh-pages` — отдельная ветка для публикации Allure Report на GitHub Pages (в ней лежат сгенерированные файлы отчёта HTML, JS, CSS, результаты тестов и т.д.) 

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

<p align="center">
  <img src="https://img.shields.io/badge/Java-24-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/Selenide-7.16-41B883?style=for-the-badge&logo=selenium&logoColor=white" alt="Selenide"/>
  <img src="https://img.shields.io/badge/TestNG-7.12-FF6A00?style=for-the-badge&logo=testng&logoColor=white" alt="TestNG"/>
  <img src="https://img.shields.io/badge/Rest--Assured-6.0-5B9BD5?style=for-the-badge&logo=apache&logoColor=white" alt="Rest-Assured"/>
  <img src="https://img.shields.io/badge/Allure-FF6A00?style=for-the-badge&logo=allure&logoColor=white" alt="Allure"/>
  <img src="https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white" alt="GitHub Actions"/>
  <img src="https://img.shields.io/badge/DataFaker-2.5-6DB33F?style=for-the-badge&logo=data&logoColor=white" alt="DataFaker"/>
</p>

В проекте используются:
- **Java 24** + **Maven**
- **Selenide** — UI-автотесты
- **TestNG** — фреймворк тестирования и группировки
- **Rest-Assured** — API-тесты
- **DataFaker** — генерация тестовых данных
- **GitHub Actions** — CI (запуск тестов)
- **Allure Report** — красивые отчёты (ветка `with-allure`)

---


## Запуск тестов

### Локально

``bash
# Все тесты (по testng.xml)
mvn clean test
для варианта с отчётом (Allure):
mvn allure:report
mvn allure:serve

# Headless-режим (CI)
mvn clean test -Dheadless=true
Через GitHub Actions
Workflow .github/workflows/tests.yml запускается вручную (workflow_dispatch).
После прогона отчёты Surefire загружаются как артефакты.

---


## Allure Report
Полный отчёт доступен по ссылке:
[Открыть Allure Report](https://schwerelosigkeit.github.io/MyMoney_AutoTesting/#suites/9e17cc8d04634948e1e41037d3b0b6ce/31531310850ef409/)
Рекомендуется смотреть отчёт онлайн — там есть подробности по каждому тесту, шагам и группировке.

---


## Структура проекта:
textMyMoney_AutoTesting/
├── .github/workflows/     # CI (GitHub Actions)
├── src/test/java/
│   ├── api/               # API-тесты (Rest-Assured)
│   ├── data/              # Генераторы данных (DataFaker)
│   └── ui/
│       ├── pages/         # Page Objects (Selenide)
│       └── tests/         # UI-тесты
├── pom.xml
└── testng.xml             # Сьюты и группы тестов

🔗 Полезные ссылки

Приложение: mymoney-webapp.onrender.com
Исходники приложения: Schwerelosigkeit/MyMoney
Allure-отчёт: ссылка выше
