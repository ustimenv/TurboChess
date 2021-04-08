DROP TABLE IF EXISTS user_roles;
CREATE TABLE user_roles (
       id int PRIMARY KEY,
       role_name VARCHAR(250) NOT NULL
);
INSERT INTO user_roles (id, role_name)
VALUES (0,  'USER'),
       (1,  'ADMIN');

--------------------------------------------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE users (
       id INT AUTO_INCREMENT  PRIMARY KEY,
       username VARCHAR(250) NOT NULL,
       password_hash VARCHAR(250) NOT NULL,
       role_id int,
       elo int,
       avatar BLOB,
       FOREIGN KEY (role_id) REFERENCES user_roles(id)
);
INSERT INTO users (username, password_hash, role_id, elo)
VALUES ('PEPE',  '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 0,  10),
       ('admin', 'euigfhnweiurthn231ws',                                                 1,  666),
       ('pepa',  'pepa1',                                                                0,  6166);
--------------------------------------------------------------


DROP TABLE IF EXISTS user_relationship_types;
CREATE TABLE user_relationship_types (
       id int PRIMARY KEY,
       relationship_name VARCHAR(250) NOT NULL
);
--read it as eg "user1 refused friend request from user2"
INSERT INTO user_relationship_types(id, relationship_name)
VALUES (0, 'NOT FRIENDS WITH'),
       (1, 'FRIENDS WITH'),
       (2, 'SENT FRIEND REQUEST TO'),
       (3, 'RECEIVED FRIEND REQUEST FROM'),
       (4, 'REFUSED FRIEND REQUEST FROM'),
       (5, 'IS BLOCKING'),
       (6, 'IS BLOCKED BY');
--------------------------------------------------------------

DROP TABLE IF EXISTS user_relationships;
CREATE TABLE user_relationships (
       userID1 VARCHAR(250) NOT NULL,
       userID2 VARCHAR(250) NOT NULL,
       relationship_type_id int NOT NULL,
       PRIMARY KEY (userID1, userID2),
       FOREIGN KEY (userID1) REFERENCES users(id),
       FOREIGN KEY (userID2) REFERENCES users(id),
       FOREIGN KEY (relationship_type_id) REFERENCES user_relationship_types(id)
);
INSERT INTO user_relationships (userID1, userID2, relationship_type_id)
VALUES (1, 2, 2), -- 1 SENT FRIEND REQUEST TO 2
       (2, 1, 3), -- 2 RECEIVED FRIEND REQUST FROM 1
       (1, 3, 1), -- 1 FRIENDS WITH 3
       (3, 1, 1), -- 3 FRIENDS WITH 1
       (2, 3, 5), -- 2 IS BLOCKING 3
       (3, 2, 6); -- 3 IS BLOCKED BY 2
--------------------------------------------------------------

DROP TABLE IF EXISTS messages;
CREATE TABLE messages (
       id int AUTO_INCREMENT PRIMARY KEY,
       sender_id INT NOT NULL,
       recipient_id INT NOT NULL,  
       content VARCHAR(250) NOT NULL,
       time_sent TIMESTAMP NOT NULL,
       time_read TIMESTAMP,               -- if null, not yet read by the recipient
       FOREIGN KEY (sender_id) REFERENCES users(id),
       FOREIGN KEY (recipient_id) REFERENCES users(id),
       CONSTRAINT secondary_key UNIQUE (sender_id, recipient_id, time_sent)
);

INSERT INTO messages (sender_id, recipient_id, time_sent, content)
VALUES (1, 3, current_timestamp(), 'Hello there!'),
       (3, 1, current_timestamp(), 'Hey what''s up'),
       (1, 3, current_timestamp()+1, 'Let''s play chess');