# Perzisztencia, adatbázis programozás JDBC és JPA technológiákkal

## Javasolt haladás

* Először nézd meg a videót! A videóban szereplő forráskódot a [demos](demos) könyvtárban találod. A slide-ok
  a JDBC esetén a [jdbc-slides.md](jdbc-slides.md) fájlban, JPA esetén
  a [jpa-slides.md](jpa-slides.md) fájlban vannak.
* Old meg a videóhoz tartozó gyakorlati feladatot, mely JDBC esetén a [jdbc-gyak.md](jdbc-gyak.md) fájlban, JPA esetén
  a [jpa-gyak.md](jpa-gyak.md) fájlban van. 
  Dolgozhatsz ugyanabba a projektbe, a gyakorlati feladatok egymásra épülnek.

# Tartalom

## JDBC 

* Egyszerű JDBC adatmódosítás (`simpleupdate`)
* Egyszerű JDBC lekérdezés (`simplequery`)
* Alkalmazás architektúra (`architecture`)
* Generált azonosító lekérdezése (`generatedid`)
* Tranzakciókezelés (`transaction`)
* Blob (`blob`)
* Adatbázis metaadatok (`metadata`)
* Haladó ResultSet (`advancedrs`)
* Spring JdbcTemplate (`spring`)

## JPA

* Egyszerű mentés és lekérdezés JPA-val (`simple-jpa`)
* Architektúra és integrációs tesztelés (`architectures`)
* Entitások konfigurálása (`config-entities`)
* Elsődleges kulcs (`id-table`, `id-composite`)
* Entitások életciklusa (`lifecycle`)
* Többértékű attribútumok (`element-collection`)
* Kapcsolatok (`relationships`)
* Több-több kapcsolat (`many-to-many`)
* Entitások MAP-ekben (`map`)
* Beágyazott objektumok és másodlagos tábla (`embedded`)
* Öröklődés (`inheritance`)
* Lekérdezések (`queries`)
* Haladó lekérdezések
* Bulk műveletek (`bulk`)
* JPA használata Spring Frameworkkel
* JPA Spring Boot-tal (`springboot`)
* Spring Data
* JPA Java EE-vel (`javaee`)
* Deklaratív tranzakciókezelés (`transaction`)

## Kivételkezelés

A videóban hibásan szerepelt, hogy úgy dobunk kivételt, hogy nem csomagoltuk be a forrás kivételt.

```java
try {

} catch (SQLException e) {
   throw new IllegalStateException("Can not query"); // Helytelen, nincs megadva második paraméter
}
```

Ehelyett mindig csomagoljuk be az eredeti kivételt, tehát:

```java
try {

} catch (SQLException e) {
   throw new IllegalStateException("Can not query", e);
}
```


## MariaDB

Ha MySQL helyett MariaDB-t használunk, akkor választhatjuk a MariaDB
JDBC drivert is, melynek neve MariaDB Connector/J.

Ekkor a függőség:

```xml
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <version>2.3.0</version>
</dependency>
```

Érdemes ekkor a megfelelő JDBC url-t is használni:

```
jdbc:mariadb://localhost:3306/employees?useUnicode=true
```

FIGYELEM! A `2.3.0` verzió azt mondja az adatbázisról, hogy MySQL, azonban a `2.4.0` verzió
már azt mondja, hogy MariaDB. Ezt viszont nem ismeri fel a Flyway `5.2.4` verziója, a következő hiba keletkezik:

```
org.flywaydb.core.api.FlywayException: Unsupported Database: MariaDB 10.1
```

Ekkor térjünk vissza a `2.3.0` verzióra.

A MariaDB JCBC driver esetén `MysqlDataSource` helyett `org.mariadb.jdbc.MariaDbDataSource` osztályt kell használnunk,
azonban ennek metódusai `SQLException` kivételt dobnak.

```java
try {
    MariaDbDataSource dataSource = new MariaDbDataSource();
    dataSource.setUrl(url);
    dataSource.setUser(username);
    dataSource.setPassword(password);
}
catch (SQLException se) {
    throw new IllegalStateException("Can not create data source", se);
}
```

## JDBC url

Abban az esetben, ha a legfrissebb MySQL JDBC drivert (connector) alkalmazzuk,
azaz szerepel a `pom.xml` állományban (pl. `mysql:mysql-connector-java:8.0.15`),
akkor a következőhöz hasonló hiba keletkezhet:

```
Caused by: com.mysql.cj.exceptions.InvalidConnectionAttributeException: The server time zone value 'KÃ¶zÃ©p-eurÃ³pai tÃ©li idÃµ' is unrecognized or represents more than one time zone. You must configure either the server or JDBC driver (via the serverTimezone configuration property) to use a more specifc time zone value if you want to utilize time zone support.
	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:490)
	at com.mysql.cj.exceptions.ExceptionFactory.createException(ExceptionFactory.java:61)
	at com.mysql.cj.exceptions.ExceptionFactory.createException(ExceptionFactory.java:85)
	at com.mysql.cj.util.TimeUtil.getCanonicalTimezone(TimeUtil.java:132)
	at com.mysql.cj.protocol.a.NativeProtocol.configureTimezone(NativeProtocol.java:2241)
	at com.mysql.cj.protocol.a.NativeProtocol.initServerSession(NativeProtocol.java:2265)
	at com.mysql.cj.jdbc.ConnectionImpl.initializePropsFromServer(ConnectionImpl.java:1319)
	at com.mysql.cj.jdbc.ConnectionImpl.connectOneTryOnly(ConnectionImpl.java:966)
	at com.mysql.cj.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:825)
	... 33 more
```

Ekkor használjuk a következő JDBC url-t:

```
jdbc:mysql://localhost/employees?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
```

Vagy térjünk vissza egy régebbi JDBC driverre:

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.47</version>
</dependency>
```

Ekkor azonban a `MysqlDataSource` csomagja más, az osztály neve minősítve
`com.mysql.jdbc.jdbc2.optional.MysqlDataSource`.

## Flyway

A Flyway videóban szereplő létrehozási módja az újabb verziókban deprecated, helyette használjuk ezt:

```java
Flyway flyway = Flyway.configure().dataSource(dataSource).load();
flyway.migrate();
```
