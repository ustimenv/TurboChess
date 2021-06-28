-- 
-- El contenido de este fichero se cargará al arrancar la aplicación, suponiendo que uses
-- 		application-default ó application-externaldb en modo 'create'
--


--user: pepa pssw: pepa
INSERT INTO user(id,enabled,username,password,roles,elo,matches_won,matches_lost,matches_played, coins) VALUES (
	1, 1, 'pepa',
	'{bcrypt}$2y$12$dDqDvO5OfYDOiTH1ovu1Gu.1KreRUyMffyOAHI54/8yjsymJMyE1m',
	'USER,ADMIN',
	1350,
	2,6,10,
	100
);
--user: pepito pssw: pepito
INSERT INTO user(id,enabled,username,password,roles,elo,matches_won,matches_lost,matches_played, coins) VALUES (
	2, 1, 'pepito',
	'{bcrypt}$2y$12$EiyIDAUNNqBTq3llI/fTeeRUc7n7EN4qs0Y.m9CLuyXVyKOaDvoVS',
	'USER,ADMIN',
	1300,
	1,3,10,
	100
);
--user: juanito pssw: juanito
INSERT INTO user(id,enabled,username,password,roles,elo,matches_won,matches_lost,matches_played, coins) VALUES (
	3, 1, 'juanito',
	'{bcrypt}$2y$12$jYEcETb2FvTILd9lcRYf/.vZfQE87tg/I.d/BX5YIrDIN0NFM1sBW',
	'USER',
	1500,
	3,0,11,
	100
);
--user: ana pssw: ana
INSERT INTO user(id,enabled,username,password,roles,elo,matches_won,matches_lost,matches_played, coins) VALUES (
	4, 1, 'ana',
	'{bcrypt}$2y$12$DV/PvazrZKPKj/u7Z6.bauGwuwYNPZsAUkSsqFLukRRiA6k7WDY0C',
	'USER',
	1800,
	5,3,11,
	100
);
--user: a pssw: a
INSERT INTO user(id,enabled,username,password,roles,elo,matches_won,matches_lost,matches_played, coins) VALUES (
	5, 1, 'a',
	'{bcrypt}$2y$12$L6usUMQ40avP0m2ztn/iiOwGH9k0lzv.OerZkUHQdHmfTYM7Cxte2',
	'USER',
	2000,
	8,1,11,
	100
);
--user: b pssw: b
INSERT INTO user(id,enabled,username,password,roles,elo,matches_won,matches_lost,matches_played, coins) VALUES (
	6, 1, 'b',
	'{bcrypt}$2y$12$dFS0HQLjY0cJ/Ww/SjqdP.GJmQOenTbVlCfyGMyg8OlM/7uD5H0j.',
	'USER',
	2000,
	8,7,15,
	100
);

INSERT INTO friends(SUBJECT_ID, FRIEND_ID) VALUES(2,3);
INSERT INTO friends(SUBJECT_ID, FRIEND_ID) VALUES(1,3);
INSERT INTO friends(SUBJECT_ID, FRIEND_ID) VALUES(1,4);
INSERT INTO friends(SUBJECT_ID, FRIEND_ID) VALUES(1,5);
INSERT INTO friends(SUBJECT_ID, FRIEND_ID) VALUES(6,1);

--Request friendship de pepa(4) a ana(2)
INSERT INTO friendship(id,state,receiver_id,sender_id) VALUES(-1,0,1,2);
--Request friendship de juanito(3) a ana(2)
INSERT INTO friendship(id,state,receiver_id,sender_id) VALUES(-3,0,2,3);
-- Unos pocos auto-mensajes de prueba
--INSERT INTO MESSAGE VALUES(1,NULL,'2020-03-23 10:48:11.074000','probando 1',1,1);
--INSERT INTO MESSAGE VALUES(2,NULL,'2020-03-23 10:48:15.149000','probando 2',1,1);
--INSERT INTO MESSAGE VALUES(3,NULL,'2020-03-23 10:48:18.005000','probando 3',1,1);
--INSERT INTO MESSAGE VALUES(4,NULL,'2020-03-23 10:48:20.971000','probando 4',1,1);
--INSERT INTO MESSAGE VALUES(5,NULL,'2020-03-23 10:48:22.926000','probando 5',1,1);



INSERT INTO game(id, end_time, moves, result, blacks, whites)
VALUES(1, '2021-06-28 09:11:58.496258', '|a2-a4|a7-a5|b2-b3|b7-b6|d2-d3|d7-d6|e2-e4|e7-e5|f2-f3|f7-f6|h2-h3|h7-h6',	'BLACKS_WON',
	    2, 1);