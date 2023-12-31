# DB auleweb

DROP DATABASE IF EXISTS `auleweb`;
CREATE DATABASE `auleweb`;

DROP USER IF EXISTS 'aulewebsite'@'localhost';
CREATE USER 'aulewebsite'@'localhost' IDENTIFIED BY 'aulewebpass';
GRANT ALL ON `auleweb`.* TO 'aulewebsite'@'localhost';

USE `auleweb`;

# TABLE ammininistratore

DROP TABLE IF EXISTS `amministratore`;

CREATE TABLE `amministratore` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(30) NOT NULL,
  `cognome` varchar(30) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(128) NOT NULL,
  `telefono` varchar(10) NOT NULL,
  `version` int unsigned NOT NULL DEFAULT 1,
  `token` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE (`email`),
  UNIQUE (`telefono`)
);

# TABLE aula

DROP TABLE IF EXISTS `aula`;

CREATE TABLE `aula` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `luogo` varchar(50) NOT NULL,
  `edificio` varchar(50) NOT NULL,
  `piano` int NOT NULL,
  `capienza` int NOT NULL,
  `email_responsabile` varchar(50) NOT NULL,
  `numero_prese_rete` int NOT NULL,
  `numero_prese_elettriche` int NOT NULL,
  `note` varchar(255) NOT NULL,
  `version` int unsigned NOT NULL DEFAULT 1,
  PRIMARY KEY (`ID`),
  UNIQUE (`nome`,`luogo`,`edificio`,`piano`)
);

# TABLE attrezzatura

DROP TABLE IF EXISTS `attrezzatura`;

CREATE TABLE `attrezzatura` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `numero_seriale` varchar(10) NOT NULL,
  `descrizione` varchar(255) NOT NULL,
  `ID_aula` int DEFAULT NULL,
  `version` int unsigned NOT NULL DEFAULT 1,
  PRIMARY KEY (`ID`),
  UNIQUE (`numero_seriale`),
  CONSTRAINT `aula_attrezzatura` FOREIGN KEY (`ID_aula`) REFERENCES `aula`(`ID`) ON DELETE SET NULL ON UPDATE CASCADE
);

# TABLE evento

DROP TABLE IF EXISTS `evento`;

CREATE TABLE `evento` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `data_inizio` datetime NOT NULL,
  `data_fine` datetime NOT NULL,
  `nome` varchar(50) NOT NULL,
  `descrizione` varchar(255) NOT NULL,
  `email_responsabile` varchar(50) NOT NULL,
  `ID_aula` int NOT NULL,
  `tipologia` enum('LEZIONE','ESAME','SEMINARIO','PARZIALE','RIUNIONE','LAUREE','ALTRO') NOT NULL,
  `nome_corso` varchar(50) DEFAULT NULL,
  `tipo_ricorrenza` enum('GIORNALIERA','SETTIMANALE','MENSILE') DEFAULT NULL,
  `data_fine_ricorrenza` date DEFAULT NULL,
  `version` int unsigned NOT NULL DEFAULT 1,
  PRIMARY KEY (`ID`),
  UNIQUE (`data_inizio`,`data_fine`,`nome`,`ID_aula`),
  CONSTRAINT `aula_evento` FOREIGN KEY (`ID_aula`) REFERENCES `aula` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
);

# TABLE evento_ricorrente

DROP TABLE IF EXISTS `evento_ricorrente`;

CREATE TABLE `evento_ricorrente` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `data_inizio` datetime NOT NULL,
  `data_fine` datetime NOT NULL,
  `ID_master` int NOT NULL,
  `version` int unsigned NOT NULL DEFAULT 1,
  PRIMARY KEY (`ID`),
  CONSTRAINT `evento_master_evento_ricorrente` FOREIGN KEY (`ID_master`) REFERENCES `evento` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
);

# TABLE gruppo

DROP TABLE IF EXISTS `gruppo`;

CREATE TABLE `gruppo` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `descrizione` varchar(255) DEFAULT NULL,
  `version` int unsigned NOT NULL DEFAULT 1,
  PRIMARY KEY (`ID`),
  UNIQUE (`nome`)
);

# TABLE gruppo_aula

DROP TABLE IF EXISTS `gruppo_aula`;

CREATE TABLE `gruppo_aula` (
  `ID_aula` int NOT NULL,
  `ID_gruppo` int NOT NULL,
  `version` int unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID_aula`,`ID_gruppo`),
  CONSTRAINT `aula_gruppo_aula` FOREIGN KEY (`ID_aula`) REFERENCES `aula` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `gruppo_gruppo_aula` FOREIGN KEY (`ID_gruppo`) REFERENCES `gruppo` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
);
