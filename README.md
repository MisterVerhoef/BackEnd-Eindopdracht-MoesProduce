# BackEnd-Eindopdracht-MoesProduce
Back-end installatiehandleiding

Benodigde software

•	Jetbrains IntelliJ 2024.3.1.1
•	Apache Maven 3.9.6
•	Java Development Kit 21 of hoger
•	Postman V11.19
•	PostgreSQL 14 
•	pgAdmin 4 v8

Benodigde instellingen

Stap 1: Installeer zo nodig de Benodigde software
1.	IntelliJ
•	Download en installeer IntelliJ.
2.	Java JDK 
•	Download en installeer de JDK.
3.	Apache Maven
•	Download en installeer Maven.
4.	PostgreSQL en pgAdmin
•	Download en installeer PostgreSQL. 
•	Selecteer de componenten ‘PostgreSQL Server’ en ‘pgAdmin’.
5.	Postman
•	Download en installeer Postman.
Stap 2: Clone de repository:
•	Clone de repository via eén van de onderstaande link:
o	SSH: git@github.com:MisterVerhoef/BackEnd-Eindopdracht-MoesProduce.git
o	HTTPS: https://github.com/MisterVerhoef/BackEnd-Eindopdracht-MoesProduce.git
Stap 3: Start de applicaties:
•	Start IntelliJ en en kies voor clone repository.
•	Paste de koppeling en druk op enter
•	Open de application.properties:
o	Onder my.upload_location voer een lokale locatie in bestanden op te slaan.
o	Pas onder datasource PostgreSql je username en wachtwoord aan of kopieer deze voor gebruik in je pgAdmin
•	Na het starten van de aplicatie zal de database worden gevuld met een lijst testgebruikers en advertenties.
Stap 4: Aanwezige gebruikersrollen
•	Admin met ROLE Admin
o	 Gebruikersnaam: admin
o	Emailadres: admin@admin.org
o	Wachtwoord: admin123
•	Test users Met ROLE User
o	gebruikersnamen: user1 t/m user6
o	Emailadres: user1@example.com t/m user6@example.com
o	Wachtwoord: password

Lijst van API-endpoints voor de MoesProduce-backendapplicatie

AdminController
•	GET /api/admin/users - Haal alle gebruikers op (vereist ADMIN-rol)
•	PUT /api/admin/users/{userId}/role - Wijzig de rol van een gebruiker
AdvertController
•	GET /api/adverts - Haal alle advertenties op
•	GET /api/adverts/{id} - Haal een advertentie op aan de hand van ID
•	GET /api/adverts/search - Zoek advertenties met een queryparameter
•	POST /api/adverts - Maak een nieuwe advertentie aan
•	POST /api/adverts/{advertId}/upload-image - Upload een afbeelding voor een advertentie
•	GET /api/adverts/user - Haal advertenties op van de huidige gebruiker
•	POST /api/adverts/{id}/save - Sla een advertentie op
•	POST /api/adverts/{id}/unsave - Verwijder een opgeslagen advertentie
•	GET /api/adverts/saved - Haal opgeslagen advertenties op
•	DELETE /api/adverts/{id} - Verwijder een advertentie
AuthenticationController
•	POST /auth/login - Inloggen van een gebruiker
UploadedFileController
•	POST /api/uploads/profile - Upload een profielafbeelding
•	POST /api/uploads/advert/{advertId} - Upload een afbeelding voor een advertentie
UserController
•	POST /api/users/register - Registreer een nieuwe gebruiker
•	POST /api/users/login - Inloggen van een gebruiker
•	PUT /api/users/change-password - Wijzig het wachtwoord
•	GET /api/users/{userId} - Haal gebruikersdetails op
•	PUT /api/users/{userId} - Werk gebruikersinformatie bij
•	DELETE /api/users/{userId} - Verwijder een gebruiker
•	PUT /api/users/{userId}/promote - Promoot een gebruiker naar verkoper
UserProfileController
•	GET /api/users/profile - Haal het profiel van de huidige gebruiker op
•	PUT /api/users/profile - Werk het gebruikersprofiel bij
•	POST /api/users/profile/upload-profile-image - Upload een profielafbeelding
VegetableController
•	GET /api/vegetables - Haal alle groenten op
•	POST /api/vegetables - Voeg een nieuwe groente toe

