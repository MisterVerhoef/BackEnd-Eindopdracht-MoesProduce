-- Controleer eerst of de admin gebruiker al bestaat
INSERT INTO users (email, username, password, terms_accepted)
SELECT 'admin@admin.org', 'admin', '$2a$12$YW9ZQAARk8rgOsNjGQqkveQ7vmorNPbHC6pGajcJeFlKXwXAl/MgW', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Voeg de admin rol toe aan de gebruiker
INSERT INTO user_roles (user_id, roles)
SELECT id, 'ADMIN'
FROM users
WHERE username = 'admin'
  AND NOT EXISTS (SELECT 1
                  FROM user_roles
                  WHERE user_id = (SELECT id FROM users WHERE username = 'admin')
                    AND roles = 'ADMIN');

-- Maak een gebruikersprofiel aan voor de admin
INSERT INTO user_profiles (name, user_id)
SELECT 'Admin', id
FROM users
WHERE username = 'admin'
  AND NOT EXISTS (SELECT 1
                  FROM user_profiles
                  WHERE user_id = (SELECT id FROM users WHERE username = 'admin'));


-- Voeg extra test users toe met hetzelfde wachtwoord
INSERT INTO users (email, username, password, terms_accepted)
VALUES ('user1@example.com', 'user1', '$2a$12$HhCOFUPVzZFl8V7ClO9lguEnfUKq8dXKJS6/plcVVlUHXHC8t9Koy', true),
       ('user2@example.com', 'user2', '$2a$12$HhCOFUPVzZFl8V7ClO9lguEnfUKq8dXKJS6/plcVVlUHXHC8t9Koy', true),
       ('user3@example.com', 'user3', '$2a$12$HhCOFUPVzZFl8V7ClO9lguEnfUKq8dXKJS6/plcVVlUHXHC8t9Koy', true),
       ('user4@example.com', 'user4', '$2a$12$HhCOFUPVzZFl8V7ClO9lguEnfUKq8dXKJS6/plcVVlUHXHC8t9Koy', true),
       ('user5@example.com', 'user5', '$2a$12$HhCOFUPVzZFl8V7ClO9lguEnfUKq8dXKJS6/plcVVlUHXHC8t9Koy', true),
       ('user6@example.com', 'user6', '$2a$12$HhCOFUPVzZFl8V7ClO9lguEnfUKq8dXKJS6/plcVVlUHXHC8t9Koy', true);

-- Voeg de gebruikersrollen toe aan de nieuwe users
INSERT INTO user_roles (user_id, roles)
VALUES ((SELECT id FROM users WHERE username = 'user1'), 'USER'),
       ((SELECT id FROM users WHERE username = 'user2'), 'USER'),
       ((SELECT id FROM users WHERE username = 'user3'), 'USER'),
       ((SELECT id FROM users WHERE username = 'user4'), 'USER'),
       ((SELECT id FROM users WHERE username = 'user5'), 'USER'),
       ((SELECT id FROM users WHERE username = 'user6'), 'USER');

-- Maak gebruikersprofielen aan voor de nieuwe users
INSERT INTO user_profiles (name, user_id)
VALUES ('User 1', (SELECT id FROM users WHERE username = 'user1')),
       ('User 2', (SELECT id FROM users WHERE username = 'user2')),
       ('User 3', (SELECT id FROM users WHERE username = 'user3')),
       ('User 4', (SELECT id FROM users WHERE username = 'user4')),
       ('User 5', (SELECT id FROM users WHERE username = 'user5')),
       ('User 6', (SELECT id FROM users WHERE username = 'user6'));

-- Bladgroenten
INSERT INTO vegetables (name, category)
VALUES ('Andijvie', 'Bladgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Boerenkool', 'Bladgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Spinazie', 'Bladgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Sla', 'Bladgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Snijbiet', 'Bladgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Rucola', 'Bladgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Raapstelen', 'Bladgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Krulandijvie', 'Bladgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Veldsla', 'Bladgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Postelein', 'Bladgroenten');

-- Koolsoorten
INSERT INTO vegetables (name, category)
VALUES ('Bloemkool', 'Koolsoorten');
INSERT INTO vegetables (name, category)
VALUES ('Broccoli', 'Koolsoorten');
INSERT INTO vegetables (name, category)
VALUES ('Savooiekool', 'Koolsoorten');
INSERT INTO vegetables (name, category)
VALUES ('Spruitjes', 'Koolsoorten');
INSERT INTO vegetables (name, category)
VALUES ('Witte kool', 'Koolsoorten');
INSERT INTO vegetables (name, category)
VALUES ('Rode kool', 'Koolsoorten');
INSERT INTO vegetables (name, category)
VALUES ('Chinese kool', 'Koolsoorten');
INSERT INTO vegetables (name, category)
VALUES ('Palmenkool', 'Koolsoorten');

-- Wortel- en knolgewassen
INSERT INTO vegetables (name, category)
VALUES ('Wortel', 'Wortel- en knolgewassen');
INSERT INTO vegetables (name, category)
VALUES ('Rode biet', 'Wortel- en knolgewassen');
INSERT INTO vegetables (name, category)
VALUES ('Pastinaak', 'Wortel- en knolgewassen');
INSERT INTO vegetables (name, category)
VALUES ('Knolselderij', 'Wortel- en knolgewassen');
INSERT INTO vegetables (name, category)
VALUES ('Radijs', 'Wortel- en knolgewassen');
INSERT INTO vegetables (name, category)
VALUES ('Rammenas', 'Wortel- en knolgewassen');
INSERT INTO vegetables (name, category)
VALUES ('Knolraap', 'Wortel- en knolgewassen');
INSERT INTO vegetables (name, category)
VALUES ('Aardpeer', 'Wortel- en knolgewassen');
INSERT INTO vegetables (name, category)
VALUES ('Schorseneren', 'Wortel- en knolgewassen');
INSERT INTO vegetables (name, category)
VALUES ('Zwarte radijs', 'Wortel- en knolgewassen');
INSERT INTO vegetables (name, category)
VALUES ('Koolrabi', 'Wortel- en knolgewassen');

-- Uiengewassen
INSERT INTO vegetables (name, category)
VALUES ('Ui', 'Uiengewassen');
INSERT INTO vegetables (name, category)
VALUES ('Knoflook', 'Uiengewassen');
INSERT INTO vegetables (name, category)
VALUES ('Prei', 'Uiengewassen');
INSERT INTO vegetables (name, category)
VALUES ('Sjalot', 'Uiengewassen');

-- Peulvruchten
INSERT INTO vegetables (name, category)
VALUES ('Doperwten', 'Peulvruchten');
INSERT INTO vegetables (name, category)
VALUES ('Tuinbonen', 'Peulvruchten');
INSERT INTO vegetables (name, category)
VALUES ('Kapucijners', 'Peulvruchten');
INSERT INTO vegetables (name, category)
VALUES ('Sperziebonen', 'Peulvruchten');
INSERT INTO vegetables (name, category)
VALUES ('Sugar snaps', 'Peulvruchten');
INSERT INTO vegetables (name, category)
VALUES ('Peultjes', 'Peulvruchten');

-- Vruchtgroenten
INSERT INTO vegetables (name, category)
VALUES ('Tomaat', 'Vruchtgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Komkommer', 'Vruchtgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Paprika', 'Vruchtgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Courgette', 'Vruchtgroenten');
INSERT INTO vegetables (name, category)
VALUES ('Pompoen', 'Vruchtgroenten');

-- Overige groenten
INSERT INTO vegetables (name, category)
VALUES ('Asperge', 'Overige groenten');
INSERT INTO vegetables (name, category)
VALUES ('Venkel', 'Overige groenten');
INSERT INTO vegetables (name, category)
VALUES ('Zeekraal', 'Overige groenten');
INSERT INTO vegetables (name, category)
VALUES ('Artisjok', 'Overige groenten');
INSERT INTO vegetables (name, category)
VALUES ('Maïs', 'Overige groenten');
INSERT INTO vegetables (name, category)
VALUES ('Winterpostelein', 'Overige groenten');
INSERT INTO vegetables (name, category)
VALUES ('Hopscheuten', 'Overige groenten');

-- Aardappelen
INSERT INTO vegetables (name, category)
VALUES ('Aardappel', 'Aardappelen');
INSERT INTO vegetables (name, category)
VALUES ('Zoete aardappel', 'Aardappelen');

-- Exotische gewassen (kas)
INSERT INTO vegetables (name, category)
VALUES ('Aubergine', 'Exotische gewassen (kas)');
INSERT INTO vegetables (name, category)
VALUES ('Okra', 'Exotische gewassen (kas)');
INSERT INTO vegetables (name, category)
VALUES ('Paksoi', 'Exotische gewassen (kas)');
-- Sample data for advertisements in data.sql
INSERT INTO adverts (title, description, created_date, user_profile_id, view_count, save_count)
VALUES ('Rode Biet',
        'Haal 20 kg van de meest verse, handgeplukte rode bieten op in het hart van Breda. Perfect voor een stevige stamppot!',
        '2024-11-08', 1, 0, 0),
       ('Wortelen',
        'Verse, knapperige wortelen, 15 kg, rechtstreeks van het veld in Rotterdam. Ideaal voor een gezonde snack of salade.',
        '2024-11-08', 1, 0, 0),
       ('Sperziebonen', 'Proef de zomer met 10 kg sperziebonen uit Amsterdam, knapperig en vol van smaak!',
        '2024-11-08', 2, 0, 0),
       ('Courgette',
        '25 kg verse courgettes beschikbaar in Utrecht. Perfect voor grillen, bakken of een heerlijk courgettetaart.',
        '2024-11-08', 2, 0, 0),
       ('Pompoen', '10 prachtige pompoenen, ideaal voor soepen of herfstdecoratie, af te halen in Den Haag.',
        '2024-11-08', 3, 0, 0);

-- Sample data for vegetables in data.sql
INSERT INTO advert_vegetables (advert_id, vegetable_id)
SELECT a.id, v.id
FROM adverts a
         JOIN vegetables v ON v.name IN ('Rode biet', 'Wortel', 'Sperziebonen', 'Courgette', 'Pompoen')
WHERE a.title IN ('Rode Biet', 'Wortelen', 'Sperziebonen', 'Courgette', 'Pompoen');