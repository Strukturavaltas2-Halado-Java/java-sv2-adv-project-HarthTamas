# Berthbooking

## A project leírása


A balatonon túravitorlázni sokak számára kellemes kikapcsolódást jelent, de a kikötési helyek előzetes megtervezését jelenleg nem támogatja semmilyen rendszer. Ezért gyakran megesik, hogy a célkikötőbe beérve derül csak ki, hogy nincs egyáltalán szabad hely, vagy nincs olyan, amely a hajó méreteinek vagy az utasok elvásársainak megfelel, és ilyenkor az elképzelt túraútvonalakon hirtelen módosítani kell akkor is, ha éppen szelesebbé válik az idő, eső közeleg, vagy kezd besötétedni. Esetleg mindhárom egyszerre.

A kikötök üzemeltetői szempontjából a problémát többek között az okozza, hogy a kikötőhelyek közel 100%-a éves szerződésekkel van lekötve, a más anyakikötőkből érkező hajóknak csak néhány helyre van lehetőségük beállni a túrázás során. A helyzetet nehezíti, hogy az egyes hajók között jelentős méretbeli differenciák vannak, és az érkezések és távozások sem kalkulálhatók pontosan, hiszen ez nagymértékben függ az időjárási körülméányektől. Sőt, viharos időben a kikötőknek menedéket kell nyújtani a menekülő hajóknak, illetve nem kötelezhetőek a bent lévők, hogy elhagyják a védett helyet. 

#### A Berthbooking egy balatoni túravitorlázást támogató rendszer része, mely egy kikötőhálózat kikötőhelyeinek menedzselésére szolgál.

Használata lehetővé teszi a túrázóknak fenntartott kikötőhelyek nyilvántartását a lánc egyes kikötőihez rendelten. A kikötőhelyek adatai, így a méretek is módosíthatóak, ami nagyobb rugalmasságot tesz lehetővé pontonos kikötők esetében, és változtathatóak a helyekhez kapcsolódó szolgáltatások is, ami pedig az infrastrukturális fejlesztések eredményének integrálását teszi lehetővé az alkalmazásba. A szoftver képes rá, hogy a kikötőhelyekre vonatkozóan gyűjtse a leadott foglalásokat az aktuális szezonra vonatkozóan. A szezon minden évben 04.01-10.31-ig tart. Fontos, hogy egy központi döntés előre meghatározza, hogy az egyes kikötőkben hány vendéghely alakítható ki. Ezt túllépni nem lehet, illetve mivel a kereslet jelentősen meghaladja a kínálatot, ezért egy hajó egyszerre csak 3 napot foglalhat le egy kikötőhelyen. 

---

## Felépítés

### Port entitás (kikötő)

A `Port` entitás a következő attribútumokkal rendelkezik:

* id (Long) - azonosító (adatbázis generálja)
* portName (String) - A kikötő neve (nem lehet üres)
* email (String) - A kikötő emailes elérhetősége (szabvány email formátum)
* numberOfGuestBerths (int) - A kikötőben létrehozható vendég helyek maximális száma (nem lehet negatív)
* berths (List&lt;Berth&gt;) - A kapcsolódó kikötőhelyek listája 

Az attributumok validálását a Spring Bootra bíztam.

Végpontok: 

| HTTP metódus | Végpont                 | Leírás                                                                 |
| ------------ | ----------------------- | ---------------------------------------------------------------------- |
| GET          | `"/api/ports"`        | Lekérdezi az összes Kikötő entitást. Az eredmény minden esetben ABC szerint lesz rendezve. Két opcionális paramétert lehet megadni: a `name` paraméterrel a kikötő nevére lehet szűrni, a `capacity` paraméterrel pedig a benne található minimális vengéghelyek számára.
| GET          | `"/api/ports/{id}"`   | Lekérdez egy Kikötő entitást `id` alapján. Ha a kikötő nem létezik, egy application/problem+json-nel tér vissza.                                   |
  | POST        | `"/api/ports/"`   | Létrehoz egy Kikötő entitást                                 |
  | POST         | `"/api/ports/{id}"`   | Hozzáad egy új Kikötőhely entitást az `id`-vel rendelkező Kikötőhöz. Ha a kikötő nem létezik, egy application/problem+json-nel tér vissza.        |
  | PUT         | `"/api/ports/{id}"`   | Módosít egy Kikötő entitást `id` alapján.  Ha a kikötő nem létezik, egy application/problem+json-nel tér vissza.                                     |
  | DELETE         | `"/api/ports/{id}"`   | Töröl egy Kikötő entitást `id` alapján.  Ha a kikötő nem létezik, egy application/problem+json-nel  tér vissza.                                      |

Üzleti logika két funkció megvalósításához tartozik.
1. A kikötő vendéghelyeinek számát csak olyan szintre engedi csökkenteni, hogy az a már foglalásokkal rendelkező kikötőhelyek száma alá ne mehessen. Ha a felhasználó kevesebbre szeretné állítani a vendéghelyek számát, mint ami lehetséges, akkor RequestedNumberOfGuestBerthsIsLessThaneBookedBerthsException  generálódik, és az alkalmazás egy  egy application/problem+json-nel tér vissza. 
2. Új kikötőhely létrehozásakor figyelembe veszi, hogy az adott kikötőben mennyi lehet az engedélyezett vendég helyek maximális száma. Ha a felhasználó több vendéghelyet szeretne létrehozni, mint ami lehetséges, akkor NumberOfBerthsExceedsLimitException generálódik, és az alkalmazás egy egy application/problem+json-nel tér vissza. 

---

### Berth entitás (kikötőhely)

A `Berth` entitás a következő attribútumokkal rendelkezik:

* id (Long) - azonosító (adatbázis generálja)
* code (String) - A kikötőhely kódja (nem lehet üres)
* length (int)  - A kikörőhely hossza (pozitív egész szám)
* width (int) - A kikötőhely szélessége (pozitív egész szám)
* berthType (BerthType) - A kikötőhely típusa a kapcsolódó szolgáltatások alapján (BASE, POWER_WATER, POWER_WATER_WIFI)
* bookings (List&lt;Booking&gt;) - A kikötőhelyhez tartozó foglalások listája
* port (Port)- A gazda kikötő 

Az attributumok validálását a Spring Bootra bíztam.

A `Port` és a `Berth` entitások között kétirányú, 1-n kapcsolat van.

Végpontok:

| HTTP metódus | Végpont                 | Leírás                                                                 |
| ------------ | ----------------------- | ---------------------------------------------------------------------- |
| GET          | `"/api/berths"`        | Lekérdezi az összes Kikötőhely entitást. Az eredmény minden esetben a foglalások kezdeti dátuma szerint lesz rendezve. Két opcionális paramétert lehet megadni: a `portName` paraméterrel a kikötő nevére lehet szűrni, a `width` paraméterrel pedig a kikötőhelyek minimális szélességére.                                 |
| GET          | `"/api/berths/{id}"`   | Lekérdez egy Kikötőhely entitást `id` alapján.  Az eredmény a foglalások kezdeti dátuma szerint lesz rendezve. Ha a kikötőhely nem létezik, egy application/problem+json-nel tér vissza.                           |
  | POST         | `"/api/berths/{id}"`   | Hozzáad egy Booking-ot az `id`-val rendelkező kikötőhelyhez. Ha a kikötőhely nem létezik, egy application/problem+json-nel tér vissza.           |
  | PUT         | `"/api/berths/{id}/bookings"`   | Módosít egy Kikötőhely entitást `id` alapján. Ha a kikötőhely nem létezik, egy application/problem+json-nel tér vissza.                    |
  | DELETE         | `"/api/berths/{id}"`   | Töröl egy Kikötőhely entitást `id` alapján. Ha a kikötőhely nem létezik, egy application/problem+json-nel tér vissza.                          |

Üzleti logika a foglalások létrehozásához tartozik:

1. Foglalás rögzítése akkor lehetéges, ha az az aktuális év szezonjára vonatkozik, amely minden év 04.01-10.31-ig. Ha nem teljesül a feltétel, akkor OutOfActualYearsSeasonException generálódik, és az alkalmazás egy application/problem+json-nel tér vissza. 
2. Foglalás olyan napokra lehetséges, amelyekre korábban nem érkezett foglalás. Ha nem teljesül a feltétel, akkor BookingTimeConflictException generálódik, és az alkalmazás egy application/problem+json-nel tér vissza. 
3. Foglalás akkor lehetséges, ha a hajó befér a kívánt kikötőhelyre. Ha nem teljesül a feltétel, akkor BoatSizeException generálódik, és az alkalmazás egy application/problem+json-nel tér vissza. 

---

### Booking (embeddable)

A `Booking` listája a `Berth`-be van ágyazva és a következő attribútumokkal rendelkezik:

* boatName (String) - A hajó neve (nem lehet üres)
* registrationNumber (String)  - A hajó lajstromszáma (nem lehet üres)
* boatLength (int) - A hajó hossza (pozitív egész szám)
* boatWidth (int) - A hajó szélessége (pozitív egész szám)
* fromDate ((LocalDate) - A foglalás kezdeti dátuma (csak a jelenlegi vagy jövőbeni dátum engedélyezett)
* numberOfDays (int) - A foglalt napok száma (maximum 3 nap foglalása lehetséges)
* timeOfBooking (LocalDateTime) - A foglalás leadásának időpontja (alkalmazás adja)

Az attributumok validálását a Spring Bootra bíztam.

---

## Technológiai részletek

A Berthbooking egy három rétegű alkalmazás, amely Docker konténerben futtatható, és a 8080-as porton érhetjük el a fent részletezett funkcióit.

A controller rétegben van implementálva a RESTful API, itt vannak definiálva a végpontok, és hozzájuk rendelve a megfelelő HTTP metódusok.
kihasználva azt, hogy a Spring Boot beépítetten tartalmaz Bean Validation 2.0 támogatást, a controller réteg felel az attributumok validációjáért is.
Az alkalmazáshoz a SWAGGER UI biztosít API dokumentációt, és lehetőséget a webszolgáltatásokat kipróbálásához.

A service réteg közvetíti a controll réteg felől érkező kéréseket a Repository réteg felé, és vissza, figyelve a DTOk-entitássá és vissza alakítására, hogy entitás sose kerülhessen vissza a controll rétegbe.  Szintén ebben a rétegben van implementálva az üzleti logika megvalósítása is. Ezek az entitásokhoz tartozó végpontok alatt lettek kifejtve .

A repository réteg Spring Data JPA-val van megvalósítva, és egy Maria DB adatbázishoz kapcsolódik. Maga az adatbázis külön konténerben fut, és a 3306-os porton érhető el. A sémák inicializálását a Flyway végzi. 

---
