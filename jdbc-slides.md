class: inverse, center, middle


# Perzisztencia, adatbázis programozás JDBC technológiával

---

class: inverse, center, middle



## Tematika

---

## Tematika

* JDBC
* Tranzakciókezelés
* Integrációs tesztelés
* Sémainicializálás, Flyway és Liquibase
* Spring JdbcTemplate

---

## Források

* [Jeanne Boyarsky, Scott Selikoff: OCP: Oracle Certified Professional Java SE 8 Programmer II Study Guide: Exam 1Z0-809 1st Edition](https://www.amazon.com/OCP-Certified-Professional-Programmer-1Z0-809/dp/1119067901/ref=sr_1_1?ie=UTF8&qid=1484581288&sr=8-1)
* https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html

---

class: inverse, center, middle



##  Egyszerű JDBC adatmódosítás

---

## JDBC elvárások

* Adatok perzisztenciájának biztosítása relációs adatbázis kezelő rendszereken
* Tetszőleges adatbázist kezelni tudjon, de az adatbázis specifikus kód elkészítése az adatbázis gyártó (harmadik partner) feladata legyen
* Az SQL szabványos, de vannak gyártófüggő kiegészítései

---

## JDBC

* Java Database Connectivity, standard programozói interfész
* Elrejti az alkalmazás elől az adatbázis specifikus részleteket
* Java SE része, interfészeket definiál
  * `java.sql` csomag
  * Pl. `DriverManager`, `Connection`, `Statement`, `ResultSet`
  * `javax.sql` csomag
  * Pl. `DataSource`, `RowSet`

---

## JDBC Driver

* JDBC Driver implementálja az API-t
  * Tipikusan egy JAR
  * Tisztán Java, vagy lehet natív része is

---

## JDBC kapcsolat

![JDBC](images/jdbc-driver.png)

---

## JDBC url

* Adatbázis szerver elérésének megadására, Java specifikus
* `jdbc:[gyártó]:[adatbázis specifikus elérési út]`  
  * `jdbc:mysql://localhost:3306/employees?useUnicode=true`
  * `jdbc:oracle:thin:@localhost:1521:employees`
  * `jdbc:sqlserver://localhost\emplyoees`
  * `jdbc:h2:mem:db;DB_CLOSE_DELAY=-1`

---

## Használat lépései adatmódosítás esetén

* Driver elhelyezése a CLASSPATH-on
* `Connection` példány létrehozása (`DriverManager` vagy `DataSource`)
* `Statement` példány létrehozása (`Connection.createStatement()`)
* `Statement.executeUpdate(String sql)` metódus hívása
* Lezárások

---

## Driver elhelyezése a CLASSPATH-on

* Alkalmazás része, külön JAR fájl
* Maven dependency
* Web konténer és alkalmazásszerver esetén a környezet része

---

## `Connection` példány

* `DriverManager` esetén - ServiceLoader mechanizmussal
* Kapcsolódási paraméterek tipikusan properties állományban

```java
Connection conn = DriverManager.getConnection(
  "jdbc:h2:mem:");
```

```java
Connection conn = DriverManager.getConnection(
  "jdbc:mysql://localhost:3306/employees?useUnicode=true",
  "username",
  "password");
```

---

## Módosítás DriverManager használatával

```java
Connection conn =
  DriverManager.getConnection("jdbc:mysql://localhost:3306/employees?useUnicode=true");
Statement stmt = conn.createStatement()
stmt.executeUpdate("insert into employees(emp_name) values ('John Doe')");
```

---

## `DataSource`

* Connection factory
* Connection pool
* Közvetlenül is példányosítható
* Web konténer és alkalmazásszerver esetén konfigurálandó

---


## Módosítás DataSource használatával

```java
MysqlDataSource dataSource = new MysqlDataSource();
dataSource.setUrl("jdbc:mysql://localhost:3306/employees?useUnicode=true");
Connection conn = dataSource.getConnection();
Statement stmt = conn.createStatement()
stmt.executeUpdate("insert into employees(emp_name) values ('John Doe')");
```

---

## Lezárások és kivételkezelés

```java
try (Connection conn = dataSource.getConnection();
Statement stmt = conn.createStatement()
) {
  stmt.executeUpdate("insert into employees(emp_name) values ('John Doe')");
} catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by insert", sqle);
}
```

---

## Kivételkezelés

* Checked `SQLException`
  * `getMessage()`
  * `getSQLState()`
  * `getErrorCode()`

---

## Paraméterezett módosítás

* Driver elhelyezése a CLASSPATH-on
* `Connection` példány létrehozása (`DriverManager` vagy `DataSource`)
* `PreparedStatement` példány létrehozása (`Connection.prepareStatement(String sql)`)
* Paraméterek beállítása (`PreparedStatement.setXXX`)
* `Statement.execute()` metódus hívása
* Lezárások

---

## Paraméterezett módosítás példa

```java
try (Connection conn = dataSource.getConnection();
  PreparedStatement stmt = conn.prepareStatement("insert into employees(emp_name) values (?)");
) {
  stmt.setString(1, name);
  stmt.execute();
} catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by insert", sqle);
}
```

---

class: inverse, center, middle



## Egyszerű JDBC lekérdezés

---

## Használat lépései lekérdezés esetén

* Driver elhelyezése a CLASSPATH-on
* `Connection` példány létrehozása (`DriverManager` vagy `DataSource`)
* `Statement` példány létrehozása (`Connection.createStatement()`)
* `ResultSet Statement.executeQuery(String sql)` metódus hívása
* Végigiterálni a `ResultSet`-en
* Lezárások

---

## Lekérdezés

```java
try (
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select emp_name from employees")
) {
    List<String> names = new ArrayList<>();
    while (rs.next()) {
        String name = rs.getString("emp_name");
        names.add(name);
    }
    return names;
} catch (SQLException sqle) {
    throw new IllegalArgumentException("Error by insert", sqle);
}
```

---

## Paraméterezett lekérdezés

* Driver elhelyezése a CLASSPATH-on
* `Connection` példány létrehozása (`DriverManager` vagy `DataSource`)
* `PreparedStatement` példány létrehozása (`Connection.prepareStatement(String sql)`)
* Paraméterek beállítása (`PreparedStatement.setXXX`)
* `ResultSet Statement.executeQuery()`
* Végigiterálni a `ResultSet`-en
* Lezárások

---

## Paraméterezett lekérdezés példa

```java
try (
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt =
          conn.prepareStatement("select emp_name from employees where id = ?");
) {
    stmt.setLong(1, id);

    // ...
} catch (SQLException sqle) {
    throw new IllegalArgumentException("Error by insert", sqle);
}
```

---

## Paraméterezett lekérdezés példa - folytatás

```java
try (
        ResultSet rs = stmt.executeQuery();
) {
    if (rs.next()) {
        String name = rs.getString("emp_name");
        return name;
    }
    throw new IllegalArgumentException("No result");
} catch (SQLException sqle) {
    throw new IllegalArgumentException("Error by insert", sqle);
}
```

---

## Performancia

* `Statement.setFetchSize(int)`
* Hálózati adatforgalom, roundtrip, memóriaigény

---

class: inverse, center, middle



## Alkalmazás architektúra

---

## DAO

* Műveletek DAO-ban (Data Access Object - Java EE tervezési minta)
* DataSource hozzáférés DI-vel

```java
public class EmployeeDao {

  private DataSource dataSource;

  public EmployeeDao(DataSource dataSource) {
        this.dataSource = dataSource;
  }

  public List<String> listEmployeeNames() {
      Connection conn = dataSource.getConnection();    
      // ...
  }
}
```

---

## Séma inicializálás

* Adatbázis séma létrehozása (táblák, stb.)
* Változások megadása
* Metadata table alapján  

---

## Elvárások

* SQL/XML leírás
* Platform függetlenség
* Lightweight
* Visszaállás korábbi verzióra
* Indítás paranccssorból, alkalmazásból
* Cluster támogatás
* Placeholder támogatás
* Modularizáció
* Több séma támogatása

---

## Megoldások

* Flyway
* Liquibase

---

## pom.xml

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>5.2.4</version>
</dependency>
```

---
## Séma inicializálás Flyway eszközökkel

```java
Flyway flyway = Flyway.configure()
    .dataSource(dataSource).load();

// Db újra létrehozása
flyway.migrate();
```

`src/main/resources/db/migration/V1__employees.sql` állomány

```sql
create table employees (id bigint auto_increment,
  emp_name varchar(255),
    constraint pk_employee primary key (id));
```

---

## Integrációs tesztelés

* Teszteset maga készítse elő a szükséges adatokat
* Teszteset tegyen rendet maga után
* Rollback
* Adatbázis takarítás
  * Séma létrehozás
  * Táblák ürítése (truncate, megszorítások)

---

## Integrációs teszt példa

```java
public class EmployeeDaoTest {

    private EmployeeDao employeeDao;

    @Before
    public void init() {
        MysqlDataSource dataSource = new MysqlDataSource();
	dataSource.setUrl("jdbc:mysql://localhost:3306/employees?useUnicode=true");

        Flyway flyway = Flyway.configure()
            .dataSource(dataSource).load();


        // Db újra létrehozása
        flyway.clean();
        flyway.migrate();

        employeeDao = new EmployeeDao(dataSource);
    }    

}
```

---

## Teszt metódus

```java
@Test
public void testInsertThanQuery() {
    employeeDao.saveEmployee("John Doe");

    List<String> names = employeeDao.listEmployeeNames();
    assertThat(names, equalTo(Arrays.asList("John Doe")));
}
```

---

class: inverse, center, middle



## Generált azonosító lekérdezése

---

## Generált azonosító lekérdezése

* `prepareStatement` paramétereként `Statement.RETURN_GENERATED_KEYS`
* `PreparedStatement.getGeneratedKeys()` metódus `ResultSet` objektumot ad vissza

---

## Generált azonosító lekérdezése példa

```java
try (Connection conn = dataSource.getConnection();
     PreparedStatement stmt = conn.prepareStatement("insert into employees(emp_name) values (?)",
             Statement.RETURN_GENERATED_KEYS)
) {

    stmt.setString(1, name);
    stmt.executeUpdate();
    return executeAndGetGeneratedKey(stmt);
} catch (SQLException sqle) {
    throw new IllegalArgumentException("Error by insert", sqle);
}
```

---

## Generált azonosító lekérdezése folytatás

```java
private long executeAndGetGeneratedKey(PreparedStatement stmt) {
    try (
            ResultSet rs = stmt.getGeneratedKeys();
    ) {
        if (rs.next()) {
            return rs.getLong(1);
        } else {
            throw new SQLException("No key has generated");
        }
    } catch (SQLException sqle) {
        throw new IllegalArgumentException("Error by insert", sqle);
    }
}
```

---

class: inverse, center, middle



## Tranzakciókezelés

---

## Tranzakciókezelés alapok

* Összetartozó utasítások, mindet végre kell hajtani vagy egyiket sem
* Hibakezelés
* Párhuzamos felhasználás
* ACID
    * Atomiság (Atomicity)
    * Konzisztencia (Consistencity)
    * Elszigetelés (Isolation)
    * Tartósság (Durability)

---

## Tranzakciókezelés Javaban

* Alapértelmezetten auto commit
* `conn.setAutoCommit(false)`
* `Connection` példány továbbvitele
* `commit()` és `rollback()` metódusok

---

class: inverse, center, middle



## Blob

---

## Blob

* Binary Large OBject (BLOB)
* Bináris adatok tárolására egy mezőben, pl. dokumentumok, képek, hang, multimédia
* Javaban byte tömb, vagy streamek

```sql
create table images (id bigint auto_increment, filename varchar(255),
  content blob, primary key (id));
```

---

## Blob írás

```java
try (
    Connection conn = dataSource.getConnection();
    PreparedStatement stmt = 
	conn.prepareStatement("insert into images(filename, content) values (?, ?)")

) {
    Blob blob = conn.createBlob();
    fillBlob(blob, image);
    stmt.setString(1, name);
    stmt.setBlob(2, blob);
    stmt.execute();

} catch (SQLException sqle) {
    throw new IllegalArgumentException("Error by insert", sqle);
}
```

---

## Blob írás folytatás

```java
public void fillBlob(Blob blob, InputStream image) {
    try (
            OutputStream os = blob.setBinaryStream(1);
            BufferedInputStream is = new BufferedInputStream(image)
    ) {
        is.transferTo(os);
    } catch (SQLException | IOException e) {
        throw new IllegalArgumentException("Error creating blob", e);
    }
}
```

---

## Blob olvasás

```java
try (
        Connection conn = dataSource.getConnection();
        PreparedStatement ps =
          conn.prepareStatement("select content from images where filename = ?");
) {
    ps.setString(1, name);

    return readBlob(ps);
} catch (SQLException sqle) {
    throw new IllegalArgumentException("Error by query", sqle);
}
```

---

## Blob olvasás folytatás

```java
private InputStream readBlob(PreparedStatement ps) {
    try (
            ResultSet rs = ps.executeQuery()
    ) {
        if (rs.next()) {
            Blob blob = rs.getBlob(1);
            return blob.getBinaryStream();
        }
        throw new IllegalStateException("No result");
    } catch (SQLException sqle) {
        throw new IllegalArgumentException("Error by query", sqle);
    }
}
```


---

class: inverse, center, middle



## Adatbázis metaadatok

---

## Adatbázis metaadatok

* Adatbáziskezelő tulajdonságok
  * Pl. gyártó, verziószám
  * Beállítások, pl. a `null` rendezéskor hova kerül
  * Támogatnak-e bizonyos jellemzőket, pl. támogatja-e a teljes ANSI 92 szabványt
* Táblák, view-k, függvények, tárolt eljárások
* Oszlopok, kulcsok, indexek
* Reflexió
* Ritkán használt

---

## Adatbázis metaadat példa

```java
try (
        Connection conn = dataSource.getConnection();

) {
    DatabaseMetaData meta = conn.getMetaData();
    return getTableNamesByMetadata(meta);
} catch (SQLException sqle) {
    throw new IllegalArgumentException("Cannot read table names", sqle);
}
```

---

## Adatbázis metaadat folytatás

* Javaban `ResultSet` objektumban jön vissza
* Szűrés: katalógus, séma, táblanév minta, tábla típus

```java
public List<String> getTableNamesByMetadata(DatabaseMetaData meta) throws SQLException {
    try (
            ResultSet rs = meta.getTables(null, null, null, null)
    ) {
        List<String> names = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString(3);
            names.add(name);
        }
        return names;
    }
}
```

---

class: inverse, center, middle



## Haladó ResultSet

---

## Görgethető eredménytáblák

* `ResultSet.previous()`
* `first()` és `last()` metódusok, visszatérési értékük `boolean`, hogy sikerült-e
* `beforeFirst()` és `afterLast()`
* `absolute(int i)`, negatív esetén hátulról kezdi a számlálást
* `relative(int i)`

```java
Statement stmt = conn.createStatement(
  ResultSet.TYPE_SCROLL_INSENSITIVE,
  ResultSet.CONCUR_READ_ONLY);
```

---

## Módosítható eredménytáblák

* `ResultSet.updateXXX()` metódusok, majd `updateRow()`
* `ResultSet.insertRow()`
* `ResultSet.deleteRow()`
* `ResultSet.refreshRow()`

```java
Statement stmt = conn.createStatement(
  ResultSet.TYPE_SCROLL_INSENSITIVE,
  ResultSet.CONCUR_UPDATABLE);
```

---

class: inverse, center, middle



## Spring JdbcTemplate

---

## `JdbcTemplate`

* JDBC túl bőbeszédű
* Elavult kivételkezelés
  * Egy osztály, üzenet alapján megkülönböztethető
  * Checked
* Spring Framework: Inversion of Control framework
* Boilerplate kódok eliminálására template-ek

---

## Módosítás

```java
public void saveEmployee(String name) {
    jdbcTemplate.update("insert into employee(emp_name) values ('John Doe')");
}
```

---

## Paraméterezett módosítás

```java
public void saveEmployee(String name) {
    jdbcTemplate.update("insert into employee(emp_name) values (?)", name);
}
```

---

## Lekérdezés

```java
public List<String> listEmployeeNames() {
    return jdbcTemplate.query("select emp_name from employees", new RowMapper<String>() {
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        }
    });
}
```

---

## Lekérdezés lambda kifejezéssel

```java
public List<String> listEmployeeNames() {
    return jdbcTemplate.query("select emp_name from employee", (rs, i) -> rs.getString(1));
}
```

---

## Lekérdezés lambda kifejezéssel, paraméterezetten

```java
public List<String> listEmployeeNames(String prefix) {
    return jdbcTemplate.query("select emp_name from employee where emp_name like ?",
      new Object[]{prefix + "%"}, (rs, i) -> rs.getString(1));
}
```

---

## Lekérdezés egy objektumra, paraméteresen

```java
public String findEmployeeNameById(long id) {
    return jdbcTemplate.queryForObject("select emp_name from employees where id = ?",
            new Object[]{id}, String.class);
}
```

---

## Id visszakérése

```java
public long saveEmployeeAndGetId(String name) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection)
              throws SQLException {
                PreparedStatement ps =
                    connection.prepareStatement("insert into employees(emp_name) values (?)",
                      Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, name);
                return ps;
            }
        }, keyHolder
    );

    return keyHolder.getKey().longValue();
}
```

---

## Id visszakérése lambda kifejezéssel

```java
public long saveEmployeeAndGetId(String name) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement("insert into employees(emp_name) values (?)",
                        Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            return ps;
        }, keyHolder
        );

        return keyHolder.getKey().longValue();
    }
```
