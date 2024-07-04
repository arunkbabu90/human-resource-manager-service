INSERT INTO permissions (name) VALUES
 ('CREATE'),
 ('READ'),
 ('UPDATE'),
 ('DELETE'),
 ('BLOCK');

INSERT INTO roles (name) VALUES
 ('ROLE_ADMIN'), ('ROLE_HR'), ('ROLE_EMPLOYEE');

INSERT INTO permission_role (permission_id, role_id) VALUES
 (1, 1),
 (2, 1),
 (3, 1),
 (4, 1),
 (5, 1),
 (1, 2),
 (2, 2),
 (3, 2),
 (4, 2),
 (2, 3);

-- Insert users
INSERT INTO "user" (username, password, is_blocked) VALUES
 ('root', '{bcrypt}$2y$10$c1hz87yRKM44ShEwZ.kIA.3JQzkBfF0CMg0QF73Bs5MHn2BiH2BzK', FALSE),
 ('jithin', '{bcrypt}$2y$10$XzzQZjnUYIAkOuH8VZte3OfvTzcSb5gPXxRv0Z80W9S9Q5nJRFSoC', FALSE),
 ('joseph', '{bcrypt}$2y$10$tHo6HmnNkPevH3ebudrTR.0hYNJ1Z6bYsEBBTOe.kN22wQT4c4VVa', FALSE);

-- Insert role-user associations
INSERT INTO role_user (role_id, user_id) VALUES
 (3, 1),
 (3, 2),
 (2, 3);
