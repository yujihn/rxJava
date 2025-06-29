# RxJavaSF — Реактивное программирование на Java

**Авторская реализация** основных идей реактивного программирования на Java, вдохновлённая RxJava, но построенная с нуля.

---

## Краткое описание

Проект реализует систему реактивных потоков с управлением потоками выполнения и обработкой событий по паттерну "Наблюдатель" (Observer). Включает базовые компоненты, операторы для трансформации данных, планировщики потоков и механизмы отмены подписки.

---

## Реализованные компоненты

- **Observable** — источники данных, фабрики `create()`, `just()`.
- **Observer** — интерфейс с методами `onNext()`, `onError()`, `onComplete()`.
- **Операторы** (пакет `com.mhide.operators`):
  - `Map` — преобразование элементов (`map`)
  - `Filter` — фильтрация элементов (`filter`)
  - `FlatMap` — развёртка вложенных потоков (`flatMap`)
  - `Merge` — слияние нескольких потоков (`merge`)
  - `Concatenation` — последовательное объединение потоков (`concatenation`)
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

- Java 23
- Maven
- SLF4J + Log4j 2
- JUnit 5

---

## Инструкция по запуску

1. Клонируйте репозиторий:

   ```
   git clone https://github.com/yujihn/rxJava.git
   cd RxJavaSF
   ```

2. Соберите проект и запустите тесты:

   ```
   mvn clean test
   ```

3. Запустите демонстрацию:

   ```bash
   mvn exec:java -Dexec.mainClass="com.mhide.Main"
   ```

---

## Структура проекта
```
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
```
---

## Архитектура и принципы работы

### Паттерн Observer

- `Observable` — источник данных, который эмитирует события через вызов `Subscribe`.
- `Observer` — подписчик, реализующий методы:
- `onNext(T item)` — получение очередного элемента
- `onError(Throwable error)` — обработка ошибки
- `onComplete()` — уведомление о завершении потока
- Для управления жизненным циклом подписки применяются интерфейсы `Disposable` и `CompositeDisposable`, которые позволяют отменять подписку и освобождать ресурсы.

### Модульность

- Код разделён на три основных пакета:
  - `core` — базовые компоненты и интерфейсы
  - `operators` — реализации операторов трансформации и комбинирования потоков
  - `schedulers` — планировщики, управляющие потоками исполнения

### Потоковое управление

- Все переключения потоков осуществляются через объекты `Scheduler`.
- Методы `subscribeOn()` и `observeOn()` позволяют управлять, в каких потоках будут выполняться подписка и обработка событий соответственно.

---

## Принципы работы Schedulers
```
| Планировщик               | Реализация                 | Назначение                        |
|---------------------------|----------------------------|-----------------------------------|
| IOThreadScheduler         | Кэшируемый пул потоков     | Для I/O операций, сетевых запросов|
| ComputationScheduler      | Фиксированный пул потоков  | Для CPU-интенсивных вычислений    |
| SingleThreadScheduler     | Однопоточный исполнитель   | Для последовательной обработки    |
```
### Как работают `subscribeOn` и `observeOn`

- `subscribeOn(Scheduler scheduler)` — определяет поток, в котором происходит подписка и генерация данных.
- `observeOn(Scheduler scheduler)` — переключает поток обработки событий дальше по цепочке операторов.

Пример:
```
Observable.just(1,2,3)
.subscribeOn(new IOThreadScheduler()) // Источник работает в IO потоке
.observeOn(new ComputationScheduler()) // Обработка в вычислительном потоке
.subscribe(System.out::println);
```
---

## Тестирование

В проекте реализован набор Unit-тестов с использованием JUnit 5, покрывающих ключевые сценарии:

- Проверка создания и подписки на `Observable`
- Тестирование операторов: `Map`, `Filter`, `FlatMap`, `Merge`, `Concatenation`, `Reduce`
- Проверка корректности переключения потоков с помощью `subscribeOn` и `observeOn`
- Обработка ошибок и корректное завершение потоков
- Отмена подписок с помощью `Disposable` и `CompositeDisposable`

Для запуска тестов используйте команду:
```
mvn test
```
---

## Примеры использования
```
// Пример: map + filter с планировщиками
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

// Пример: flatMap
FlatMap.apply(
Observable.just("A", "B"),
s -> Observable.just(s + "1", s + "2")
).subscribe(System.out::println);

// Пример: merge
Merge.apply(
Observable.just("X", "Y"),
Observable.just("1", "2")
).subscribe(s -> System.out.println("merge: " + s));

// Пример: concatenation
Concatenation.apply(
Observable.just("M", "N"),
Observable.just("O")
).subscribe(s -> System.out.println("concat: " + s));
```
## Пример логирования
```
2025-06-28T22:02:34,757 [main] INFO  c.m.Main - Пример map и filter с планировщиками:
2025-06-28T22:02:34,772 [pool-3-thread-4] INFO  c.m.Main - Completed

2025-06-28T22:02:34,773 [pool-3-thread-3] INFO  c.m.Main - Received: 50
2025-06-28T22:02:34,773 [pool-3-thread-1] INFO  c.m.Main - Received: 30
2025-06-28T22:02:34,773 [pool-3-thread-2] INFO  c.m.Main - Received: 40
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - Пример flatMap:
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - flatMap: 1
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - flatMap: 1
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - flatMap: 2
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - flatMap: 4
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - flatMap: 3
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - flatMap: 9
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - flatMap: 4
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - flatMap: 16
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - flatMap: 5
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - flatMap: 25
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - 
Пример merge:
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - merge: A
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - merge: B
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - merge: 1
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - merge: 2
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - 
Пример concatenation:
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - concatenation: X
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - concatenation: Y
2025-06-28T22:02:35,274 [main] INFO  c.m.Main - concatenation: Z
```
