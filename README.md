## 001. Интерфейс DataSource. Способы создания.


Интерфейс **DataSource** обеспечивает подключение к БД. Основной метод:
```java
Connection getConnection() throws SQLException;
```

Для Spring нужно создать бин любым способом.

Разные варианты:
1. Получение **DS** для встроенной БД (H2, Derby) (в памяти) через **EmbeddedDatabaseBuilder**:
```kt
@Configuration
class AppConfig {

    @Bean("dataSource")
    fun getDataSource(): DataSource {
        val dataSource: DataSource = EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .generateUniqueName(false)
            .setName("my-db")
            .setScriptEncoding("UTF-8")
            // схемы в папке /resources
            .addScript("db-schema.sql")
            .addScripts("db-test-data.sql")
            .build()
        return dataSource
    }
}
```
Зависимость не забыть подключить на саму СУБД и `spring-jdbc`)

2. Создание пула соединений с помощью **Apache Database Connection Pooling** 

Зависимость `org.apache.commons:commons-dbcp2`
```kt
@Bean("dataSource")
fun getDataSource(): DataSource {
    val dataSource: BasicDataSource = BasicDataSource()
    dataSource.driverClassName = "org.h2.Driver"
    dataSource.url = "jdbc:h2:~/test"

    dataSource.initialSize = 5
    val initSize = dataSource.initialSize
    println("pool size: $initSize")
    return dataSource
}
```
3. и другие способы:
* **DriverManagerDataSource** - чисто для JDBC-драйвера
* **SmartDataSource**
* **SingleConnectionDataSource**


[id](003.004.001)


## 002. Особенности настройки DataSource в Spring Boot. Настройка пула соединений.


Подключаем зависимости `spring-boot-starter-jdbc` и встроенную базу, например `com.h2database:h2`

При запуске создается подключение по умолчанию. Можно настраивать.

Основные настройки:
```
spring.datasource.url=jdbc:mysql://localhost/test
spring.datasource.username=dbuser
spring.datasource.password=dbpass
```
Имя драйвера умеет определять из URL, можно явно указать
```
spring.datasource.driver-class-name
```

Полный список [docs](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.data)

Для пула по умолчанию используется **HikariCP** (включена в `spring-boot-starter-jdbc`).

Вообще алгоритм определения пула такой (по зависимостям):
1. **HikariCP**
2. **Tomcat pooling DataSource**
3. **Commons DBCP2**
4. **Oracle UCP**

Для пула есть свои группы настроек: например `spring.datasource.hikari.*`

Можно создать свой бин **DataSource**, тогда автоконфигурация не выполняется и надо настраивать вручную. Например через **DataSourceBuilder**:
```kt
@Configuration
class MyDataSourceConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "app.datasource")
    fun dataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }
}
```
Свойства `application.properties` например:
```
app.datasource.url=jdbc:mysql://localhost/test
app.datasource.username=dbuser
app.datasource.password=dbpass
app.datasource.pool-size=30
```
Здесь возможно нужно указывать, как пул огранизован. У разных - разные свойства могут быть. Подробнее [Spring How-to](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-access)


[id](003.004.002)


## 003. Инициализация БД в Spring Boot через файлы со схемами


Spring Boot: если бин **DataSource** конфигурируется автоматически, будет выполнять скрипты:
* `/resources/schema.sql`
* `/resources/data.sql`

По умолчанию это работает только для встроенных БД. Для остальных надо явно включать:
```
spring.sql.init.mode=always
spring.sql.init.mode=never
spring.sql.init.mode=embedded
```
[Подробнее](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.using-basic-sql-scripts)


[id](003.004.003)


## 004. Инициализация БД в Spring Boot через DataSourceInitializer


Класс **DataSourceInitializer** из пакета `org.springframework.jdbc`

Общая схема:
* регистрируем как бин
* при создании передаем бин DataSource (если такой бин есть должно сработать `@Autowired`) и устанавливаем `setDataSource()`
* также при создании задаем **DatabasePopulator** `setDatabasePopulator()`, где указываем sql-скрипты.
* можно указать **DatabasePopulator** для очистки БД `setDatabaseCleaner()`

Здесь **DatabasePopulator** - интерфейс. Реализация - **ResourceDatabasePopulator** (устанавливает скрипты)

Можно на разные профили разные инициализаторы создавать.

Пример:
```kt
@Configuration
class DbInitializerConfig(
    @Value("classpath:/db-schema.sql") private val schemaScript: Resource,
    @Value("classpath:/db-data.sql") private val dataScript: Resource
) {
    @Bean
    fun dataSourceInitializer(dataSource: DataSource): DataSourceInitializer {
        val initializer: DataSourceInitializer = DataSourceInitializer()
        initializer.setDataSource(dataSource)
        initializer.setDatabasePopulator(getPopulator())
        return initializer
    }

    private fun getPopulator(): DatabasePopulator {
        val populator: ResourceDatabasePopulator = ResourceDatabasePopulator()
        populator.addScript(schemaScript)
        return populator
    }
}
```


[id](003.004.004)


## 005. JdbcTemplate: назначение, пример создания


**JdbcTemplate** - реализует паттерн **Шаблон** для выполнения sql-запросов. 

Выполняет задачи:
* открытие / получение соединения
* выполнение SQL-команд
* обработка результата
* обработке исключений и т. п.

Расположен: `org.springframework.jdbc.core`

[docs](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#jdbc-core)

Создание: внедрить бин типа **JdbcTemplate**. При наличии бина **DataSource** будет сконфигурирован:
```kt
@SpringBootApplication
class App004: CommandLineRunner {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    override fun run(vararg args: String?) {
        val rowCount = jdbcTemplate.queryForObject<Int>("select count(*) from countries")
        println("table size: $rowCount")
    }
}
```


[id](003.004.005)


## 006. Интерфейс ResultSet


Итнерфейс из `javax.sql`.

Представляет выборку из результата запроса.

Есть методы для позиционирования на выборке:
* **next()** - переход на следующую строку и возврат истина, если был переход
* **first()**, **last()** - на первую или последнюю строку
* **absolute(int row)** - на конкретную строки
* и др.

Методы для получения данных из выборки по индексу или имени колонки:
* **getString(int index)**, **getString(String name)** (индекс с 1 начинается) * и подобные для других типов Int, Long и др.

Используется в **JdbcTemplate** (закрывается например автоматически)


[id](003.004.006)


## 007. Метод JdbcTemplate query(): варианты, используемые коллбеки, передача параметров в sql-выражение.


Несколько методов, сигнатуры типа `query(String sql, Callback)`.

В качестве коллбека интерфейсы:
* **RowMapper**:
    - обход автоматический по строкам
    - для каждой строки создается объект (интерфейс возвращает объект)
    - `query()` возвращает список созданных объектов
* **RowCallbackHandler**:
    - также автоматически обходит строки
    - реализация интерфейса ничего не возвращает
    - `query()` тоже ничего не возвращает 
    - обычно заполняется внутренняя переменная / состояние
* **ResultSetExtractor**
    - обход нужно выполнять вручную
    - интерфейс возвращает какой-то объект
    - `query()` возвращает то, что вернула реализация интерфейса

Сигнатуры:
```java
public <T> List<T> query(String sql, RowMapper<T> mapper)
public void query(String sql, RowCallbackHandler rch)
<T> T query(String sql, ResultSetExtractor<T> rse)
```

В sql-выражение параметры передаются через `?` и varargs:
```kt
jdbcTemplate.query(
    "select * from countries where id = ?"
    , mapper
    , 3)
```


[id](003.004.007)


## 008. Интерфейс RowMapper и его использование с JdbcTemplate


Сигнатура метода **query()** класса **JdbcTemplate**:
```java
public <T> List<T> query(String sql, RowMapper<T> mapper)
```

Сигнатура метода интерфейса:
```java
T mapRow(ResultSet rs, int rowNum)
```

Суть:
* автоматический обход **ResultSet** и преобразование каждой строки в объект типа `T`.
* сам объект **RowMapper** обычно stateless. 
* реализация только вызывает `ResultSet::getXXX()`, на основе полученных значений формируется объект и возвращает его на каждой итерации
* реализация не должна вызывать `ResultSet::next()` и подобные.
* результат - список объектов типа `T`
```kt
 val countries: List<Coutnry> = jdbcTemplate.query(
    "select id, name from countries") { rs, _ ->
        val id = rs.getLong("id")
        val name = rs.getString("name")
        Country(id, name)
    }
```


[id](003.004.008)


## 009. Интерфейс RowCallbackHandler и его использование с JdbcTemplate


Сигнатура метода **query()** класса **JdbcTemplate**:
```java
public void query(String sql, RowCallbackHandler rch)
```
Сигнатура метода интерфейса:
```java
void processRow(ResultSet rs)
```
Суть:
* обходит каждую строку результата запроса. Ничего не возвращает 
* поэтому обычно имеет состояние (не stateless)
* реализация может вызывать `ResultSet::getXXX()`, но не должна вызывать `ResultSet::next()` и подобные
* `proccessRow()` обычно извлекает данные и сохраняет / накапливает их в полях класса-реализации
```kt
var sumOfId = 0
jdbcTemplate.query("select id, name from countries") {
    val id = it.getInt("id")
    sumOfId += id
}
```


[id](003.004.009)


## 010. Интерфейс ResultSetExtractor и его использование с JdbcTemplate


Сигнатура метода **query()** класса **JdbcTemplate**:
```java
<T> T query(String sql, ResultSetExtractor<T> rse)
```
Сигнатура метода интерфейса:
```java
T extractData(ResultSet rs)
```
Суть:
* сама реализация интерфейса выполняет обход результат запроса (метод `ResultSet::next()`)
* например сформировать какое-то значение и вернуть его
* обычно stateless
* закрывать `ResultSet` не нужно
```kt
 val extractor = { resultSet: ResultSet ->
    var sum = 0
    while (resultSet.next()) {
        val id = resultSet.getInt("id")
        sum += id
        if (id > 3) break
    }
    sum
}
val sum = jdbcTemplate.query("select id, name from countries", extractor)
```


[id](003.004.010)


## 011. Разные методы JdbcTemplate для выполнения запросов


**queryForObject()** получает единственный экземпляр объекта при выполнении запроса. Если в выборке 0 или больше одного элемента - исключение.

Два варианта:
* с единственной колонкой в результате, без дополнительного преобразования:
```kt
val sql = "select name from countries where id = 3"
val name = jdbcTemplate.queryForObject<String>(sql)
```
```kt
val sql = "select name from countries where id = ?"
val id = 4
val name = jdbcTemplate.queryForObject(sql, String::class.java, id)
```
* с более сложным преобразованием через **RowMapper**:
```kt
val sql = "select id, name from countries where id = 2"
val mapper = {rs: ResultSet, _: Int ->
    val name: String = rs.getString("name")
    val id: Long = rs.getLong("id")
    Country(id, name)
}
val country = jdbcTemplate.queryForObject(sql, mapper)
```

**queryForList()** - возвращает список из результата запроса. Предполагается, что в запросе несколько строк.

Если тип не задан возвращает список из **Map<String, Any>** имя колонки - значение:
```kt
val listOfMap = jdbcTemplate
    .queryForList("select name, id from countries")
```

Если указать единственную колонку и ее тип, тогда результат - список из значений в этой колонке:
```kt
val listOfNames = jdbcTemplate
    .queryForList("select name from countries"
        , String::class.java)
```

**queryForMap()** - возвращает **Map** из имя колонки - значение. Ожидает единственную строку в результате. Если не так - исключение:
```kt
val map = jdbcTemplate
    .queryForMap("select name, id from countries where id = 2")
```


[id](003.004.011)


## 012. Методы для выполнения DDL и DML запросов


Методы **UPDATE**, **INSERT**, **DELETE** выполняются через метод **update()**:
```kt
jdbcTemplate.update(
        "insert into t_actor (first_name, last_name) values (?, ?)",
        "Leonor", "Watling")
```
```kt
jdbcTemplate.update(
    "delete from t_actor where id = ?", actorId)
```

Есть универсальный метод **execute()**, можно в принципе любые запросы выполнять:
```kt
jdbcTemplate.execute("create table mytable (id integer, name varchar(100))")
```


[id](003.004.012)


## 013. Способы внедрения JdbcTemplate в конфигурацию (best practice)


Создавать один **JdbcTemplate** на все приложение или нет?

**DataSource** однозначно один на приложение. **JdbcTemplate** лучше отдельный на каждый DAO (он легкий, проблем не будет, скрываем реализацию как-бы):
```kt
@Repository 
class JdbcCorporateEventDao(dataSource: DataSource) : CorporateEventDao { 
    private val jdbcTemplate = JdbcTemplate(dataSource) 
}
```

Хотя ничего не мешает сделать один бин **JdbcTemplate** для всех DAO.


[id](003.004.013)


## 014. Получение ключа записи после вставки в базу данных


Если ключ генерируется в БД после вставки, может понадобится получить его.

Используем **GeneratedKeyHolder** и реализацию интерфейса**PreparedStatementCreator**. Через **PreparedStatement** задаем команду и колонку, в которой находится ключ. 

После выполнения запроса, получаем ключ через **GeneratedKeyHolder**:
```kt
val keyHolder = GeneratedKeyHolder()
val INSERT_SQL = "insert into countries (name) values(?)"
val name = "Poland"
jdbcTemplate.update({
    // it это Connection
    it.prepareStatement (INSERT_SQL, arrayOf("id"))
        .apply { setString(1, name) }
}, keyHolder)
val newId = keyHolder.key
```


[id](003.004.014)


### 015. Что такое транзакция. ACID. Глобальные и локальные транзакции.


Транзакция - операция, состоящая из набора задач, в которой или все эти задачи выполняются, или не выполняется ни одна.

ACID-принципы транзакции:

* **Атомарность** - все изменения принимаются или ни одно не должно произойти
* **Консистентность** - система должна переходить из одного допустимого состояния в другое допустимое состояние. Никакие промежуточные состояния не должны быть видны для пользователей системы.
* **Изолированность** - ни одна транзакция не должна влиять на другую. Конкурентное выполнение транзакций должно вести к состоянию, как если бы транзакции выполнялись последовательно.
* **Прочность** (durability) - если транзакция была завершена, данные будут сохранены, несмотря на любые падения системы, питания и т. п.

**Глобальная транзакция** - транзакция, применяемая сразу к нескольким ресурсам (разные БД и т. п.). Решение - например JTA со своим собственным сервером приложения

**Локальная транзакция** - транзакция, применяемая к единственному ресурсу. Много проще реализуется, чем глобальные, обычно СУБД обеспечивает реализацию.


[id](003.004.015)


## 016. Что такое уровень изолированности транзакции. Виды аномалий.


Уровень изолированности определяет как изменения в одной транзакции отражаются на других паралелльных транзакциях. Чем выше уровень, тем более согласованны данные (изменения в незавершенной транзакции не видны другим транзакциям). При низких уровнях изменения в незавершенных транзакциях могут быть видны другим транзакциям. Высокие уровни дают более согласованный доступ к данным, но снижают производительность системы.

При параллельном выполнении транзакций могут возникать следующие проблемы:

* **грязное чтение** (dirty read)
    - транзакция № 1 добавляет/изменяет данные, а потом откатывается
    - до отката транзакции № 1, транзакция № 2 читает данные и видит изменения, сделанные транзакцией № 1
    - нужен запрет на незакоммиченные изменения (uncommited locks)
* **фантомное чтение** (phantom read)
    - транзакция № 1 несколько раз одинаковым запросом читает (не конкретную строку, а какой-то диапазон) какие-либо данные
    - между чтениями из транзакции № 1, транзакция № 2 (пока транзакция № 1 не закрыта) вставляет данные в таблицу, которую читает транзакция № 1
    - в результате транзакция № 1 получит разные данные в одинаковых запросах
    - чтобы избежать - нужна блокировка на диапазон (range locks)
* **неповторяющееся чтение** (non-repeatable read)
    - почти то же, что и фантомное чтение, но транзакция № 2 не вставляет, а изменяет данные
    - нужна блокировка на чтение-запись (read-write locks)
* **потерянное обновление** (lost update)
    - две транзакции пытаются изменить одно и то же значение, одно из изменений теряется
    - например `UPDATE tbl1 SET f2=f2+20 WHERE f1=1` и `UPDATE tbl1 SET f2=f2+55 WHERE f1=1`
    - если здесь транзакции не блокируются, может возникнуть ситуация, когда обе прочитают одинаковое исходное значение, а потом только последняя запись сохранится. БД не допускают такого на любом уровне изоляции.

**Уровни изолированности**:

* **Serializable**
    - самый высокий, параллельно транзанкции не выполняются
    - защищает от всех проблем 
    - но нужно, чтобы **все транзакции были на этом уровне**
* **Repeatable read**
    - ставятся блокировки на чтение-запись на читаемые строки до завершения транзакции
    - защищает от всего, кроме фантомного чтения (блокировки на новые строки не действуют)
* **Read commited** (чтение фиксированных данных)
    - ставится блокировка на чтение до конца выполнения SELECT
    - ставится блокировка на запись до конца транзакции
    - защищает только от грязного чтения и потерянного обновления
    - режим работы по умолчанию для большинства СУБД
* **Read uncommited** 
    - ставится блокировка только на запись
    - защищает только от потерянного обновления

В Spring уровень изолированности задается через свойство аннотации **@Transactional(isolation = Isolation.Serializable)**


[id](003.004.016)


## 017. Как определить транзакцию в Spring.


Определение транзакции в Spring:
* Разрешить транзакции через аннотацию **@EnableTransactionManagment** над конфигурационным классом
* Создать бин, реализующий интерфейс **PlatformTransactionManager**
    - **DataSourceTransactionManager**
    - **JtaTransactionManager**
    - **JpaTransactionManager**
    - и другие
* Указать **@Transactional** над методом или классом

```kt
@Configuration
@EnableTransactionManagement
class AppConfig {
    @Bean("dataSource")
    fun getDataSource(): DataSource { ... }

    @Bean
    fun transactionManager(dataSource: DataSource):
        PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean
    fun countriesDao(dataSource: DataSource) 
        = CountriesDao(dataSource)
```
**Внимание**: класс с **@Transactional** для проксирования и **ВСЕ** его публичные методы должны быть открытыми. Для этого варианты:
* явно через ключевое **open**
* с помощью плагина **all-open**, но класс должен быть с аннотацией **@Component** или **@Transactional**
```kt
open class CountriesDao(private val dataSource: DataSource) {
    @Transactional
    open fun insert(id: Int, name: String) { ... }
```


[id](003.004.017)


## 018. Аннотация @Transactional. Что такое PlatformTransactionManager.


Аннотация **@Transactional** может ставиться:
* над методом: метод выполняется в транзакции
* над классом: все методы класса в транзакции

Вызовы таких методов проксируются через **TransactionInterceptor** и **TransactionAspectSupport**, которые передают управление **PlatformTransactionManager**.

Свойства аннотации позволяют настраивать/задавать:

* менеджер транзакций (вместо заданного бином по умолчанию)
* тип распространения (propagation) транзакций
* уровень изоляции
* таймаут для транзакции
* флаг только для чтения
* определять какие исключения вызывают откат транзакции
* какие не вызывают

**Интерфейс PlatformTransactionManager**

Управляет транзакциями. Методы:
* **getTransaction()** - возвращает активную транзакцию или создает новую
* **commit()** - коммитит транзакцию или откатывает, если была помечена на откат
* **rollback()** - откатывает транзакцию


[id](003.004.018)


## 019. Аннотация @EnableTransactionManagment


**@EnableTransactionManagment** - аннотация, применяемая к конфигурационному классу (классу с аннотацией **@Configuration**). При наличии бина **@PlatformTransactionManager**, обеспечивает работу аннотации **@Transactional**

Для этого классы **TransactionInterceptor** и **TransactionAspectSupport** проксируют вызовы методов с аннотацией **@Transactional** и используют **PlatformTransactionManager** для управления транзакциями

Параметры аннотации **@EnableTransactionManagment**:

* `mode` - как перехватываются вызовы методов:
    - `PROXY` - по умолчанию через проксирование
    - `ASPECTJ` - через АОП
* `order` - если в режиме АОП и несколько советов присутствует
* `proxyTargetClass` - каким способом проксировать в режиме прокси: CGLIB или JDK Proxy (по умолчанию)

Итого по умолчанию:
* через проксирование
* JDK Proxy


[id](003.004.019)


## 020. Особенности применения @Transactional к методам. Self-invocation.


По умолчанию работает только для публичных методов. Для приватных поддерживается в тестах (Spring TestContext Framework).

Можно настроить для не публичных методов, но только для прокси через наследование и с дополнительной настройкой.

Аннотировать можно:
* определение классов
* методы классов
* определение интерфейсов (не рекомендуется, будет работать только с прокси на интерфейсах)
* методы интерфейсов (аналогично)

Бин и соотвественно его прокси должны быть польностью инициализированны, поэтому нельзя применять к методам жизненного цикла (**@PostConstruct** и т. п.)

Если класс с аннотациями унаследован, нужно явно переопределять методы и помечать аннотацией. Вызов метода родителя не будет работать в транзакции.

Настройки **@Transactional**, применненной к методу имеют более высокий приоритет, чем **@Transactional**, примененная к классу.

**Self-invocation** - вызов внутри метода класса другого метода этого же класса. Транзакции через проксирование (CGLIB и JDK Dynamic Proxy) работать не будут. Только через АОП / aspectj.
```java
public class A {
    @Transactional
    void methodA() {
        ...
    }
    void methodB() {
        // транзакция не будет установлена
        methodA();
    }
}
```


[id](003.004.020)


## 021. Что такое распространение транзакций (propagation)


[dzone](https://dzone.com/articles/spring-boot-transactions-tutorial-understanding-tr)

Определяет как существующая транзакция переиспользуется, когда вызывается **@Transactional** метод внутри другого метода с уже запущенной транзакцией или в методе без транзакции.

За это отвечает параметр аннотации **@Transactional** `propagation`:
* **REQUIRED** - используется существующая транзакция, или создается новая, если нет транзакции. **По умолчанию.**
* **SUPPORTS** - если метод вызывается внутри транзакции, он ее использует. Если вызывается самостоятельно, из метода вне транзакции, транзакция не создается и метод выполняется как без аннотации `@Transactional`
* **MANDATORY** - обязан вызываеться внутри метода с транзакцией. Если вызывается самостоятельно или внутри не транзакционного метода - выбрасывается исключение
* **REQUIRES_NEW** - всегда создает новую транзакции: когда вызывается без транзакции и когда вызывается внутри существующей транзакции
*  **NOT_SUPPORTED** - не создает транзакции и не использует существующие
*  **NEWER** - не создает транзакции, но если запускается внутри существующей - вызывает исключение
*  **NESTED** - так же как и **REQUIRED**, но создаются точки сохранения, что позволяет откатывать внутреннюю транзакции независимо от внешней.

|      Тип      | Вне транзакции |      Внутри транзакции    | Как выполняется|
| ------------- | -------------- | --------------------------|----------------|
| REQUIRED      |новая           |переиспользуется           | всегда в       |
| SUPPORTS      |без транзакции  |переиспользуется           | зависит от     |
| MANDATORY     |исключение      |переиспользуется           | всегда в       |
| REQUIRES_NEW  |новая           |новая                      | всегда в       |
| NOT_SUPPORTED |без транзакции  |без транзакции             | всегда без     |
| NEWER         |без транзакции  |исключение                 | всегда без     |
| NESTED        |новая           |переиспользуется с точками | всегда в       |


[id](003.004.021)


## 022. Что произойдет, если один @Transactional метод вызывает другой @Transactional метод того же самого класса


Это **self-invocation**. Проксирование через **CGLIB** и **JDK Dynamic Proxy** не поддерживают такой механизм, поэтому **TransactionInterceptor** и **TransactionAspectSupport** не смогут перехватить вызов и транзакция не будет обработана.

Например при вызове метода Б не будет создана транзакция для метода А:
```java
public class A {
    @Transactional
    void methodA() {
        ...
    }
    void methodB() {
        methodA();
    }
}
```

Чтобы разрешить поддержку **self-invocation** нужно отказаться от проксирования и перейти на АОП:
* добавить зависимость `spring-aspects`
* включить плагин `aspectj-maven-plugin`
* переключить режим в АОП: `@EnableTransactionManagment(mode = AdviceMode.ASPECTJ)`


[id](003.004.022)


## 023. Настройка режима отката транзакций (rollback policy)


По умолчанию откатываются транзакции, внутри которых произошли непроверяемые исключения (а это например исключения Spring Data).

Проверяемые исключения не являются по умолчанию причиной для отката транзакции

Можно настроить через параметры аннотации **@Transactional**:

* `rollbackFor` / `noRollbackFor` - через указание класса
* `rollbackForClassName` / `noRollbackForClassName` - через строковое имя класса


[id](003.004.023)

