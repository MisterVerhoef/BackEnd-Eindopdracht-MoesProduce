-- Controleer eerst of de admin gebruiker al bestaat
INSERT INTO users (email, username, password, terms_accepted)
SELECT 'admin@admin.org', 'admin', '$2a$12$YW9ZQAARk8rgOsNjGQqkveQ7vmorNPbHC6pGajcJeFlKXwXAl/MgW', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Voeg de admin rol toe aan de gebruiker
INSERT INTO user_roles (user_id, roles)
SELECT id, 'ADMIN'
FROM users
WHERE username = 'admin'
AND NOT EXISTS (
    SELECT 1
    FROM user_roles
    WHERE user_id = (SELECT id FROM users WHERE username = 'admin')
    AND roles = 'ADMIN'
);

-- Maak een gebruikersprofiel aan voor de admin
INSERT INTO user_profiles (name, user_id)
SELECT 'Admin', id
FROM users
WHERE username = 'admin'
AND NOT EXISTS (
    SELECT 1
    FROM user_profiles
    WHERE user_id = (SELECT id FROM users WHERE username = 'admin')
);


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