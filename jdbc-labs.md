# Perzisztencia, adatbázis programozás JDBC technológiával

A feladatokban egy úgynevezett Activity Tracker alkalmazás szimulációját kell megvalósítani,
mellyel nyilvántarthatjuk sportolási tevékenységeinket, és lekérdezhetjük eredményeinket.

## Egyszerű JDBC adatmódosítás

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
* `description` - szöveges leírás
* `type` - felsorolásos típus, mely felveheti a következő értékeket: `BIKING`, `HIKING`, `RUNNING`, `BASKETBALL`

Írj egy `activitytracker.ActivityTrackerMain` osztályt, mely egy `main()` metódusban példányosít pár `Activity`
példányt, és értéküket beszúrja az `activities` táblába! Az adatbázisban hozd létre az `activities` táblát! Az `id` legyen `auto_increment`!
A mezőnevek legyenek hasonlóak az osztály attribútumainak neveihez, azonban a szavakat aláhúzásjel (`_`) válassza el!
Mivel a `type` foglalt szó, ezért ezen oszlop neve legyen `activity_type`!

A `LocalDateTime` értéket JDBC-vel a `PreparedStatement` `setTimestamp()` metódusával lehet beszúrni. Létrehozni a 
`Timestamp.valueOf(LocalDateTime)` metódussal lehet.

Amennyiben kész, szervezd ki a beszúrást egy külön metódusba!

## Egyszerű JDBC lekérdezés

A `main()` metódust egészítsd ki úgy, hogy kérdezd le az összes rekordot az `activities` táblából! Példányosíts egy `List<Activity>` listát, 
amit feltöltesz a lekérdezett adatok alapján! Szervezd ki egy külön metódusba!

Írj egy másik metódust, mely paraméterül kap egy `id`-t és az alapján betölt egy `Activity`-t! Hívd meg ezt a metódust a `main()` metódusban, 
az előzőleg elkészített metódus által visszaadott listában szerepelő `Activity`-k `id`-jával ciklusban!

## Alkalmazás architektúra

Hozz létre egy `ActivityDao` osztályt, mely a következő metódusokat tartalmazza:

* `void saveActivity(Activity)`
* `Activity findActivityById(long id)`
* `List<Activity> listActivities()`

Töröld ki az adatbázisban a táblát, és használd a Flyway-t, hogy hozza létre azt!

Írj egy JUnit integrációs tesztet az `ActivityDao` tesztelésére!

## Generált azonosító lekérdezése

Módosítsd úgy a `void saveActivity(Activity)` metódust, hogy `Activity`-t adjon vissza,
aminek már fel van töltve az `id` mezője!

## Tranzakciókezelés

Az aktivitásokhoz pontokat is lehet felvinni, ha pl. GPS-szel nyomon követtük a mozgásunk, pl. a futásunk.

Írj egy `TrackPoint` osztályt, melynek attribútumai:

* `id` - egyedi azonosító
* `time` - `LocalDate`
* `lat` és `lon` - koordináták, szélességi és hosszúsági fok

Az `Activity` tartalmazzon egy `List<TrackPoint>` attribútumot! Módosítsd a `saveActivity()` metódust,
hogy egy tranzakcióban mentse le a `TrackPoint` objektumokat is a `track_point` táblába! Hozd létre a táblát!
A `track_point` táblának egy külső kulcsot kell tartalmaznia az `activities` táblára. Módosítsd
a `findActivityById()` metódust, hogy betöltse a `TrackPoint` értékeket is!

Írj rá tesztesetet!

Szabályok a koordinátákra:

* Szélesség : +90 - -90
* Hosszúság : +180 - -180

Amennyiben valamelyik pont nem felel meg a szabályoknak, vissza kell görgetni a tranzakciót, és
kivételt kell dobni.

## Blob

Lehessen megadni képeket is a tevékenységeinkhez!

Ehhez legyen egy `Image` osztály, a következő attribútumokkal:

* `id` - egyedi azonosító, egész
* `filename` - fájlnév
* `content` - tartalom, `byte[]`

Írj a dao-ba egy új metódust: `saveImageToActivity(long activityId, Image image)`! Ez lementi a képet
az adatbázisba. Készítsd el hozzá a táblát is! Legyen egy külső kulcs az `activities` táblára!

Írj a dao-ba egy új metódust: `loadImageToActivity(long activityId, String filename)`! Ez töltse be az
adott tevékenységhez tartozó képet!

Írj rá tesztesetet!

## Adatbázis metaadatok

Írj egy új osztályt, `DatabaseMetadataDao` néven, és ebbe egy `List<String> getColumnsForTable(String table)` metódust,
ami visszaadja egy tábla oszlopának neveit!

Írj rá tesztesetet!

## Görgethető eredménytáblák

Írj egy `List<TrackPoint> someTrackPoints(long activityId)` metódust, mely visszaadja egy tevékenységhez tartozó
első, középső és utolsó pontot! (Ugorj a végére, nézd meg, hogy hanyadik, majd oszd el kettővel!)

Írj rá tesztesetet!

## Spring JdbcTemplate

Az előbbi feladatokat oldd meg Spring JdbcTemplate használatával egy új projektben!
