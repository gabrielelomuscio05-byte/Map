-- Script di inizializzazione per il database di Esercitazione 6 (MAP6)

CREATE DATABASE IF NOT EXISTS MapDB;

CREATE USER IF NOT EXISTS 'MapUser'@'localhost' IDENTIFIED BY 'map';
GRANT SELECT ON MapDB.* TO 'MapUser'@'localhost';
FLUSH PRIVILEGES;

USE MapDB;

CREATE TABLE IF NOT EXISTS MapDB.provaC(
    X varchar(10),
    Y float(5,2),
    C float(5,2)
);

DELETE FROM MapDB.provaC;

INSERT INTO MapDB.provaC VALUES('A', 2, 1);
INSERT INTO MapDB.provaC VALUES('A', 2, 1);
INSERT INTO MapDB.provaC VALUES('A', 1, 1);
INSERT INTO MapDB.provaC VALUES('A', 2, 1);
INSERT INTO MapDB.provaC VALUES('A', 5, 1.5);
INSERT INTO MapDB.provaC VALUES('A', 5, 1.5);
INSERT INTO MapDB.provaC VALUES('A', 6, 1.5);
INSERT INTO MapDB.provaC VALUES('B', 6, 10);
INSERT INTO MapDB.provaC VALUES('A', 6, 1.5);
INSERT INTO MapDB.provaC VALUES('A', 6, 1.5);
INSERT INTO MapDB.provaC VALUES('B', 10, 10);
INSERT INTO MapDB.provaC VALUES('B', 5, 10);
INSERT INTO MapDB.provaC VALUES('B', 12, 10);
INSERT INTO MapDB.provaC VALUES('B', 14, 10);
INSERT INTO MapDB.provaC VALUES('A', 1, 1);

COMMIT;
