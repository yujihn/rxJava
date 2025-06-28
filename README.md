# RxJavaSF — Реактивное программирование на Java

**Авторская реализация** основных идей реактивного программирования на Java, вдохновлённая RxJava, но построенная с нуля.

---

## Краткое описание

В этом проекте реализована система реактивных потоков с управлением потоками выполнения и обработкой событий по паттерну "Наблюдатель" (Observer). Включены базовые компоненты, операторы для трансформации данных, планировщики потоков и механизмы отмены подписки.

---

## Реализованные компоненты

- **Observable** — источники данных, фабрики `create()`, `just()`.
- **Observer** — интерфейс с методами `onNext()`, `onError()`, `onComplete()`.
- **Операторы** (пакет `com.mhide.operators`):

  - `Map` — преобразование элементов (`map`)
  - `Filter` — фильтрация элементов (`filter`)
  - `FlatMap` — развёртка вложенных потоков (`flatMap`)
  - `Merge` — слияние нескольких потоков (`merge`)
  - `Concatenation` — последовательное объединение потоков (`concat`)
  - `Reduce` — агрегация элементов (`reduce`)

- **Планировщики** (пакет `com.mhide.schedulers`):

  - `IOThreadScheduler` — кэшируемый пул потоков для I/O задач
  - `ComputationScheduler` — фиксированный пул для вычислений
  - `SingleThreadScheduler` — однопоточный исполнитель

- **Управление подписками**:

  - `Disposable` — отмена одной подписки
  - `CompositeDisposable` — групповое управление подписками

- **Логирование**: реализовано через SLF4J и Log4j 2

---

## Технологии

- Java 17+
- Maven
- SLF4J + Log4j 2
- JUnit 5

---

## Инструкция по запуску

1. Клонируйте репозиторий:

   ```bash
   git clone https://github.com/ВАШ_ПРОЕКТ/RxJavaSF.git
   cd RxJavaSF
   ```

2. Соберите проект и запустите тесты:

   ```bash
   mvn clean test
   ```

3. Запустите демонстрацию:

   ```bash
   mvn exec:java -Dexec.mainClass="com.mhide.Main"
   ```

---

## Структура проекта
Copy
plaintext
RxJavaSF/
├── .idea/ # Конфигурация IDE
├── logs/
│ └── rxjavasf.log # Файл логов
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/
│ │ │ └── mhide/
│ │ │ ├── core/
│ │ │ │ ├── CompositeDisposable.java
│ │ │ │ ├── Disposable.java
│ │ │ │ ├── Observable.java
│ │ │ │ ├── Observer.java
│ │ │ │ └── Subscribe.java
│ │ │ ├── operators/
│ │ │ │ ├── Concatenation.java
│ │ │ │ ├── Filter.java
│ │ │ │ ├── FlatMap.java
│ │ │ │ ├── Map.java
│ │ │ │ ├── Merge.java
│ │ │ │ └── Reduce.java
│ │ │ ├── schedulers/
│ │ │ │ ├── ComputationScheduler.java
│ │ │ │ ├── IOThreadScheduler.java
│ │ │ │ ├── Scheduler.java
│ │ │ │ └── SingleThreadScheduler.java
│ │ │ └── Main.java
│ │ └── resources/
│ │ └── log4j2.xml
│ └── test/
│ └── java/
│ └── com/
│ └── mhide/
│ ├── core/
│ │ └── ObservableTest.java
│ ├── operators/
│ │ └── OperatorTest.java
│ └── schedulers/
│ └── SchedulerTest.java
├── .gitignore
├── pom.xml
└── README.md

---

## Архитектура и принципы

- **Observer Pattern**:  
  `Observable` генерирует события через `Subscribe`. Подписчик реализует `Observer` или передаёт обработчики в `subscribe()`. Управление жизненным циклом подписки — через `Disposable` и `CompositeDisposable`.

- **Модульность**:  
  Код разделён на ядро (`core`), операторы (`operators`) и планировщики (`schedulers`) для удобства поддержки и расширения.

- **Потоковое управление**:  
  Все переключения потоков происходят через объекты `Scheduler`.

---

## Планировщики потоков

| Планировщик           | Реализация                 | Назначение                  |
|----------------------|----------------------------|-----------------------------|
| IOThreadScheduler    | CachedThreadPool            | Для I/O и сетевых операций   |
| ComputationScheduler | FixedThreadPool             | Для CPU-интенсивных задач    |
| SingleThreadScheduler| SingleThreadExecutor        | Последовательная обработка   |

---

## Тестирование

В проекте реализованы юнит-тесты, покрывающие:

- Создание и подписку на Observable
- Работа операторов (`Map`, `Filter`, `FlatMap`, `Merge`, `Concatenation`, `Reduce`)
- Проверку переключения потоков (`subscribeOn`, `observeOn`)
- Обработку ошибок и отмену подписок

Запуск тестов:
Copy
bash
mvn test

---

## Примеры использования
Copy
java
// map + filter с планировщиками
Map.apply(
Observable.just(1, 2, 3, 4, 5),
i -> i * 10
)
.subscribeOn(new IOThreadScheduler())
.observeOn(new SingleThreadScheduler())
.subscribe(
i -> System.out.println("Получено: " + i),
Throwable::printStackTrace,
() -> System.out.println("Завершено")
);

// flatMap пример
FlatMap.apply(
Observable.just("A", "B"),
s -> Observable.just(s + "1", s + "2")
).subscribe(System.out::println);