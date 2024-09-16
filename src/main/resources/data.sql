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