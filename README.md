# DB Tester

We have many database queries in ADBS and RTDS written by our user that do not have automated tests.
If our users where to be able to define test cases for their queries they could change them more safely.

Ubiquitous Language:
- given == data to prepare a test
- query == the database query to test
- want == the expected result
- got == the result of the query after execution on the db
- case == a test case including given, query and want
- suite == a test suit consisting of multiple cases

Executing a test case:
- insert given data into database
- execute query
- compare the result you got with the thing you want

Initial focus on MongoDB
- here given, query, want and got are all in json format.

## start the database docker compose
- docker-compose -f mongo-docker-compose.yaml up -d
- docker-compose -f mongo-docker-compose.yaml down

# TODO

## get rid of start code - D

Currently the app executes some test case using the StartClass.
This should be executed in a test case.
Usually Rest APIs just sit there waiting for requests.
If you want to test them manually use a Rest Client (browser, curl, postman)

## terrible-query testcase

Ich habe given und want für die terrible query gepusht.
In der query war ein Platzhalter für die Zeitzone ( ${timezone} ) den ADBS ersetzt.
Ich habe das manuell durch "Europe/Paris" ersetzt.
Um durch den ersten $match stage der Aggregation zu kommen darf die Zeit die im Feld arrivalTime im letzten array element des history arrays steht nicht älter als 5 Minuten sein.
Dieser Zustand muss also vor dem Ausführen der Query hergestellt werden (etwa durch ein Update).

## Datenmodell & REST Api

Der User sollte Testdaten (also Givens), Queries, Wants und Test-Cases definieren können.
Ein Case besteht aus Given, Query und Want.
Wenn der User ein Case definiert sollte er aus bestehenden Givens, Queries und Wants auswählen können.
Jedes dieser Konstrukte braucht auch eine ID oder einen Namen.

Ich habe den GivenController als Beispiel implementiert.
Hier siehst du die Http Methoden GET, DELETE, POST und PUT sehr einfach implementiert.
Es wird entweder der Name eines Givens oder ein ganzes Given übergeben.
Die Datenbank ist noch nicht angeschlossen.
Testfälle sind keine dabei aber ich glaube du findest schnell heraus welche request an die API gesendet werden können.

Ausserdem habe ich das ganze in ein Paket "given" verpackt.
Das ist eine Alternative zur technischen Aufteilung in Model, Service, Controller und so weiter.
Falls jemand an den Givens arbeiten muss beschränkt sich die Arbeit hoffentlich auf dieses Paket.
Die Kohäsion ist also höher (Design Prinzip: Low Coupling, High Cohesion)

## Postgres

Du kannst Postgres so ähnlich wie MongoDB im container starten.
Die Anleitung dazu ist auf Dockerhub: https://hub.docker.com/_/postgres

Wenn wir statt MongoDB auch Postgres benutzen müssen wir die Datenbank abstrahieren.
Das heisst gewisse Model Klassen werden zu Interfaces die dann entweder von MongoDB oder Postgres spezifischen Klassen implementiert werden.

Ausserdem müssen wir bei Postgres Dinge wie Datentypen und CREATE TABLE statements unterstützen.

Ich baue einen Vorschlag mit den Givens zusammen.

## Challenge: MongoDB Compass Aggregation Tab Nachbauen

Im Compass Aggregation Tab wird das Ergebnis nach jedem Pipeline Step angezeigt.
https://www.google.com/search?q=compass+aggregation+tab&source=lnms&tbm=isch&sa=X&ved=2ahUKEwiVqer257H6AhUfgf0HHXiBBw4Q_AUoAXoECAEQAw&biw=2048&bih=1304&dpr=1
Ich weiss nicht genau wie die das Performant hinbekommen.
Das nachzubauen scheint mir eine interessante Aufgabe.
