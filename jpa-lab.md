# Perzisztencia, adatbázis programozás JPA technológiával

A feladatokban egy úgynevezett Activity Tracker alkalmazás szimulációját kell megvalósítani,
mellyel nyilvántarthatjuk sportolási tevékenységeinket, és lekérdezhetjük eredményeinket.

## Egyszerű mentés JPA-val

Ellenőrizd, hogy fut-e a MySQL szerver a lokális számítógépen (`localhost`)!
Jelentkezz be `root` felhasználóval HeidiSQL kliens használatával, és futtasd le a következő
parancsot:

```sql
use mysql;
create schema if not exists activitytracker default character set utf8 collate utf8_hungarian_ci;

create user 'activitytracker'@'localhost' identified by 'activitytracker';
grant all on *.* to 'activitytracker'@'localhost';
```

Ekkor létrejön az `activitytracker` séma, és az `activitytracker` felhasználó, ugyanezzel a jelszóval.

Írj egy `activitytracker.Activity` osztályt, mely attribútumai:

* `id` - egyedi azonosító, egész szám
* `startTime` - kezdési idő, `LocalDateTime`
* `description` - szöveges leírás (vigyázz, a `desc` foglalt szó SQL-ben)
* `type` - felsorolásos típus, mely felveheti a következő értékeket: `BIKING`, `HIKING`, `RUNNING`, `BASKETBALL`

Írj egy `activitytracker.ActivityTrackerMain` osztályt, mely egy `main()` metódusban, mely példányosít pár `Activity`
példányt, és értéküket beszúrja az `activities` táblába! Az adatbázisban hozd létre az `activities` táblát! Az `id` legyen `auto_generated`!
A mezőnevek legyenek hasonlóak az osztály attribútumainak neveihez, azonban a szavakat aláhúzásjel (`_`) válassza el!

Amennyiben kész, szervezd ki a beszúrást egy külön metódusba!

## További egyszerű műveletek JPA-val

A `main()` metódusban próbáld ki a listázást, beolvasást id alapján, módosítást és törlést!

## Architektúra és integrációs tesztelés

Hozz létre egy `ActivityDao` osztályt a következő metódusokkal:

* `void saveActivity(Activity)`
* `List<Activity> listActivities()`
* `Activity findActivityById(long id)`
* `void deleteActivity(long id)`

Hozz létre egy integrációs tesztet!

## Entitások konfigurálása

Módosítsd úgy az `Activity` osztályt, hogy ne a `type` sorszáma, hanem a neve kerüljön az adatbázisban elmentésre!

Módosítsd úgy az oszlopok konfigurációját, hogy a `startTime`, `desc` és `type` is kötelező legyen (nem lehet `null`)!

A `desc` mező hossza maximum 200 karakter lehet, a `type` mező hossza maximum 20 karakter.

## Azonosítógenerálás

Módosítsd az `Activity` osztályt, hogy ne `auto_increment` mező alapján történjen az azonosítókiosztás, hanem a Hibernate
saját táblája alapján! Legyen a tábla neve `act_id_gen`, mezői rendre `id_gen` és `id_val`!

## Entitások életciklusa

Hozz létre az `Activity` osztályban egy `createdAt` mezőt, valamint egy `updatedAt` mezőt, mind a kettő
`LocalDateTime` tipusú legyen! Mielőtt lementésre kerül, a Hibernate állítsa be a `createdAt` mező értékét
az aktuális időre! Módosítás esetén a Hibernate automatikusan állítsa be az `updatedAt` mező értékét!

Írj a dao-ba egy metódust:

```java
void updateActivity(long id, String desc)
```

Ez módosítsa az adott azonosítójú aktivitás leírását a második paraméterként megadottra! Ezzel ellenőrizheted, hogy
működnek-e a mentések.

Hozz létre egy integrációs tesztet!

## Többértékű attribútumok

Lehessen az aktivitást címkékkel ellátni! Legyen az `Activity` osztálynak egy `List<String> labels`
attribútuma.

Legyen egy új metódus, mely az aktivitásokat a címkékkel együtt tölti be!

```java
Activity findActivityByIdWithLabels(long id)
```

Hozz létre egy integrációs tesztet!

## Kapcsolatok

Az aktivitásokhoz pontokat is lehet felvinni, ha pl. GPS-szel nyomon követtük a mozgásunk, pl. a futásunk.

Írj egy `TrackPoint` osztályt (entitást), melynek attribútumai:

* `id` - egyedi azonosító
* `time` - `LocalDate`
* `lat` és `lon` - koordináták, szélességi és hosszúsági fok

Az `Activity` tartalmazzon egy `List<TrackPoint>` attribútumot!

Legyen a két entitás között kétirányú 1-n kapcsolat, azaz egy aktivitáshoz több pont tartozhat, de
egy pont csak egy aktivitáshoz tartozhat. Legyen a mentés és törlés kaszkádolt!
A pontokat mindig a `time` nevű attribútuma alapján növekvő sorrendben töltse be!

Legyen egy új metódus, mely az aktivitásokat a pontokkal együtt tölti be!

```java
Activity findActivityByIdWithTrackPoints(long id)
```

Hozz létre egy integrációs tesztet!

## Több-több kapcsolatok

Hozz létre egy tájegység entitást (`Area`)! Legyen egy azonosítója és neve.

Egy aktivitáshoz több tájegységet is hozzá lehet rendelni, és egy tájegységben többször is
mozoghatunk. Ezért alakíts ki egy kétirányú m-n kapcsolatot a két entitás között!

Írj egy `AreaDao` osztályt, mellyel el lehet menteni egy tájegységet!

Hozz létre egy integrációs tesztet!

## Entitások mapekben

A tájegységhez kapcsolódjon 1-n kétirányú kapcsolattal a települések (`City` entitás).
Azonban a tájegységben található településeket egy `Map`-ben kell nyilvántartani,
ahol a kulcs a település neve. Legyen kaszkádolt mentés és törlés.

```java
Map<String, City>
```

A `City` entitás tartalmazza a következő attribútumokat:

* `id` - egyedi azonosító
* `name` - település neve
* `population` - népesség

Hozz létre egy integrációs tesztet!

## Beágyazott objektumok és másodlagos tábla

Legyenek az `Activity` entitásnak további attribútumai!

* `distance` - össztáv
* `duration` - hossz, egész szám másodpercben

Ezen oszlopok értékeit szervezd ki egy másodlagos `activity_details` táblába!

## Öröklődés

Egy teljesen külön projektbe dolgozz!

Hozz létre benne egy `Activity` entitást a következő mezőkkel:

* `id` - egyedi azonosító, egész szám
* `startTime` - kezdési idő, `LocalDateTime`
* `desc` - szöveges leírás

Hozz létre két leszármazottat: `SimpleActivity` és `ActivityWithTrack`.

Az előbbi tartalmazzon egy plusz attribútumot:

* `place` - szöveges típusú, hol történt az aktivitás

Az utóbbi tartalmazzon további két attribútumot:

* `distance` - össztáv
* `duration` - hossz, egész szám másodpercben

Vizsgáld meg, hogy lehet az öröklődést konfigurálni!

## Lekérdezések

Hozz létre egy metódust, mely azon aktivitásokhoz tartozó pontok koordinátáit
adja vissza, melyek egy megadott dátum 2018-01-01 után történtek.
Lehessen lapozást megadni. A lekérdezést named query-ben add meg!

```java
List<Coordinate> findTrackPointCoordinatesByDate(LocalDateTime afterThis, int start, int max)
```

Ehhez hozz létre egy `Coordinate` DTO-t, melynek egy `lat` és `lon`
attribútuma van!

## Haladó lekérdezések

Írj egy metódust, mely tevékenységenként visszaadja (tevékenység leírását adja vissza), hogy hány pont tartozik hozzá, név szerint rendezett sorrendben!

```java
List<Object[]> findTrackPointCountByActivity()
```

## Bulk műveletek

Írj egy olyan metódust, mely kitörli a paraméterül megadott dátum után szereplő bizonyos típusú tevékenységeket!

```java
public void removeActivitiesByDateAndType(LocalDateTime afterThis, ActivityType type)
```

## JPA Spring Boottal

Egy új projektbe másold át az osztályokat, és módosítsd úgy, hogy Spring Boottal működjön!

## JPA Java EE-vel

Egy új projektbe másold át az osztályokat, és módosítsd úgy, hogy Java EE-t használva egy Wildfly alkalmazásszerveren belül működjön!
