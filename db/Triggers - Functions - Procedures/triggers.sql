USE auleweb;

########## BEFORE INSERTs TRIGGERS ##########

/*
Trigger che verifica se la data di inizio dell'evento che si sta
tentando di inserire è valida, ovvero non è prevista prima di "oggi".
*/

DROP TRIGGER IF EXISTS validita_data_inizio_evento;

DELIMITER $    

CREATE TRIGGER validita_data_inizio_evento
BEFORE INSERT ON evento
FOR EACH ROW
BEGIN 

	IF( DATE(NEW.data_inizio) < curdate() ) 
		THEN BEGIN
			DECLARE messaggio varchar(100); 
			SET messaggio = concat("La data di inizio non è corretta!"); 
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
		END;
    END IF;

END$  
DELIMITER ;

##################################################

/*
Trigger che verifica se la data di fine dell'evento che si sta
tentando di inserire è valida, ovvero non è prevista prima della
data di inizio ma, soprattutto, è prevista nella stessa giornata
della data di inizio, così da rispettare il vincolo per cui un 
evento viene svolto nell'arco della giornata.
*/

DROP TRIGGER IF EXISTS validita_data_fine_evento;

DELIMITER $    

CREATE TRIGGER validita_data_fine_evento
BEFORE INSERT ON evento
FOR EACH ROW
BEGIN 

	IF( SUBTIME( TIME(NEW.data_inizio), TIME(NEW.data_fine) ) > 0 )
		THEN BEGIN
			DECLARE messaggio varchar(100); 
			SET messaggio = concat("L'orario di fine non è corretto!"); 
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
		END;
    END IF;
    
	IF( DATEDIFF(NEW.data_inizio, NEW.data_fine) <> 0 ) 
		THEN BEGIN
			DECLARE messaggio varchar(100); 
			SET messaggio = concat("La data di fine e la data di inizio non corrispondono!"); 
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
		END;
    END IF;

END$  
DELIMITER ;

##################################################

/*
Trigger che verifica se l'orario di inizio e di fine dell'evento 
che si sta tentando di inserire è valido, ovvero è previsto con 
scarti di soli 0 o 15 minuti. 
*/

DROP TRIGGER IF EXISTS validita_ora_inizio_fine_evento;

DELIMITER $    

CREATE TRIGGER validita_ora_inizio_fine_evento
BEFORE INSERT ON evento
FOR EACH ROW
BEGIN 
    
    IF( (MINUTE(NEW.data_inizio) IN (0,15,30,45)) <> 1 ) 
		THEN BEGIN
			DECLARE messaggio varchar(100); 
			SET messaggio = concat("L'ora di inizio non è corretta (solo scarti di 15 minuti) !"); 
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
		END;
    END IF;
    
    IF( (MINUTE(NEW.data_fine) IN (0,15,30,45)) <> 1 )
		THEN BEGIN
			DECLARE messaggio varchar(100); 
			SET messaggio = concat("L'ora di fine non è corretta (solo scarti di 15 minuti) !"); 
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
		END;
    END IF;

END$  
DELIMITER ;

##################################################

/*
Trigger che verifica se l'aula nello slot orario indicato 
nell'evento che si sta tentando di inserire è libera oppure
è già occupata da un altro evento che è stato già definito, 
sia esso "semplice" oppure "ricorrente".
Il trigger viene attivato sia per l'inserimento di eventi
"semplici" sia per eventi "ricorrenti".
*/

DROP TRIGGER IF EXISTS slot_disponibili;

DELIMITER $    

CREATE TRIGGER slot_disponibili
BEFORE INSERT ON evento
FOR EACH ROW
BEGIN 
	IF( eventi_aula(NEW.ID_aula, NEW.data_inizio, NEW.data_fine, NEW.ID) <> 0 )
		THEN BEGIN
			DECLARE messaggio varchar(100); 
			SET messaggio = concat("Slot occupato!"); 
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
		END;
	END IF;

    IF( NEW.tipo_ricorrenza IS NOT NULL )
		THEN BEGIN
			IF( verifica_slot_occupati_eventi_ricorrenti(NEW.ID_aula, NEW.data_inizio, NEW.data_fine, NEW.tipo_ricorrenza, NEW.data_fine_ricorrenza, NEW.ID) <> 0 )
				THEN BEGIN
					DECLARE messaggio varchar(100); 
					SET messaggio = concat("Uno degli slot ricorrenti è occupato!");
					SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
				END;
			END IF;
		END;
	END IF;
END$  
DELIMITER ;

/*
Trigger che effettua l'hashing della password dell'amministratore
mediante SHA 512, che ritorna un digest di 128 caratteri
*/

DROP TRIGGER IF EXISTS hash_password;

DELIMITER $    

CREATE TRIGGER hash_password
BEFORE INSERT ON amministratore
FOR EACH ROW
BEGIN 

	SET NEW.password = SHA2(NEW.password, 512);

END$  
DELIMITER ;

##################################################

########## AFTER INSERTs TRIGGERS ##########

/*
Trigger che chiama la procedura genera_eventi con cui si genera
tutta la serie di eventi ricorrenti associati all'evento "master"
che è stato appena inserito nella tabella evento, ovviamente il 
trigger si attiva ma non effettua nessuna azione nel caso in cui
sia stato appena inserito un evento "semplice".
*/

DROP TRIGGER IF EXISTS genera_eventi_ricorrenti;

DELIMITER $ 

CREATE TRIGGER genera_eventi_ricorrenti
AFTER INSERT ON evento
FOR EACH ROW
BEGIN 
	IF( NEW.tipo_ricorrenza IS NOT NULL )
		THEN BEGIN
			CALL genera_eventi(NEW.ID, NEW.ID_aula, NEW.data_inizio, NEW.data_fine, NEW.tipo_ricorrenza, NEW.data_fine_ricorrenza);
		END;
	END IF;
END$ 
DELIMITER ;

##################################################

########## BEFORE UPDATEs TRIGGERS ##########

/*
Trigger che verifica se la data di inizio dell'evento che si sta
tentando di modificare è valida, ovvero non è prevista prima di "oggi".
*/

DROP TRIGGER IF EXISTS conferma_validita_data_inizio_evento;

DELIMITER $    

CREATE TRIGGER conferma_validita_data_inizio_evento
BEFORE UPDATE ON evento
FOR EACH ROW
BEGIN 

	IF( DATE(NEW.data_inizio) < curdate() ) 
		THEN BEGIN
			DECLARE messaggio varchar(100); 
			SET messaggio = concat("La data di inizio non è corretta!"); 
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
		END;
	END IF;

END$  
DELIMITER ;

##################################################

/*
Trigger che verifica se la data di fine dell'evento che si sta
tentando di modificare è valida, ovvero non è prevista prima della
data di inizio ma, soprattutto, è prevista nella stessa giornata
della data di inizio, così da rispettare il vincolo per cui un 
evento viene svolto nell'arco della giornata.
*/

DROP TRIGGER IF EXISTS conferma_validita_data_fine_evento;

DELIMITER $    

CREATE TRIGGER conferma_validita_data_fine_evento
BEFORE UPDATE ON evento
FOR EACH ROW
BEGIN 

	IF( SUBTIME( TIME(NEW.data_inizio), TIME(NEW.data_fine) ) > 0 )
		THEN BEGIN
			DECLARE messaggio varchar(100); 
			SET messaggio = concat("L'orario di fine non è corretto!"); 
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
		END;
	END IF;

	IF( DATEDIFF(NEW.data_inizio, NEW.data_fine ) <> 0 ) 
		THEN BEGIN
			DECLARE messaggio varchar(100); 
			SET messaggio = concat("La data di fine e la data di inizio non corrispondono!"); 
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
		END;
    END IF;

END$  
DELIMITER ;

##################################################

/*
Trigger che verifica se l'orario di inizio e di fine dell'evento 
che si sta tentando di aggiornare è valido, ovvero è previsto con 
scarti di soli 0 o 15 minuti. 
*/

DROP TRIGGER IF EXISTS conferma_validita_ora_inizio_fine_evento;

DELIMITER $    

CREATE TRIGGER conferma_validita_ora_inizio_fine_evento
BEFORE UPDATE ON evento
FOR EACH ROW
BEGIN 
    
    IF ( MINUTE(NEW.data_inizio) IN (0,15,30,45) ) <> 1 
		THEN BEGIN
			DECLARE messaggio varchar(100); 
			SET messaggio = concat("L'ora di inizio non è corretta (solo scarti di 15 minuti) !");
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
		END;
    END IF;
    
    IF ( MINUTE(NEW.data_fine) IN (0,15,30,45) ) <> 1 
		THEN BEGIN
			DECLARE messaggio varchar(100); 
			SET messaggio = concat("L'ora di fine non è corretta (solo scarti di 15 minuti) !"); 
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
		END;
    END IF;

END$  
DELIMITER ;

##################################################

/*
Trigger che verifica se l'aula nello slot orario indicato 
nell'evento che si sta tentando di modificare è libera oppure
è già occupata da un altro evento che è stato già definito,
sia esso "semplice" oppure "ricorrente".
Il trigger viene attivato sia per la modifica di eventi
"semplici" sia per eventi "ricorrenti", ovviamente nel 
secondo caso va effettuato un controllo ulteriore mediante 
la funzione verifica_slot_occupati_eventi_ricorrenti così 
da evitare conflitti negli slot orari degli eventi "ricorrenti".
*/

DROP TRIGGER IF EXISTS slot_liberi;

DELIMITER $    

CREATE TRIGGER slot_liberi
BEFORE UPDATE ON evento
FOR EACH ROW
BEGIN 
	IF ( NEW.tipo_ricorrenza IS NULL )
		THEN BEGIN
			IF( eventi_aula(NEW.ID_aula, NEW.data_inizio, NEW.data_fine, NEW.ID) <> 0 )
				THEN BEGIN
					DECLARE messaggio varchar(100); 
					SET messaggio = concat("Slot occupato!"); 
					SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
				END;
			END IF;
		END;
    ELSE
		BEGIN
			IF( eventi_aula(NEW.ID_aula, NEW.data_inizio, NEW.data_fine, NEW.ID) <> 0 )
				THEN BEGIN
					DECLARE messaggio varchar(100); 
					SET messaggio = concat("Slot occupato!"); 
					SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
				END;
			END IF;
			IF( verifica_slot_occupati_eventi_ricorrenti(NEW.ID_aula, NEW.data_inizio, NEW.data_fine, NEW.tipo_ricorrenza, NEW.data_fine_ricorrenza, NEW.ID) <> 0 )
				THEN BEGIN
					DECLARE messaggio varchar(100); 
					SET messaggio = concat("Uno degli slot ricorrenti è occupato!");
					SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = messaggio;
                END;
			END IF;
		END;
	END IF;
END$  
DELIMITER ;

/*
Trigger che rieffettua l'hashing della password dell'amministratore
mediante SHA 512, che ritorna un digest di 128 caratteri
*/

DROP TRIGGER IF EXISTS rehash_password;

DELIMITER $    

CREATE TRIGGER rehash_password
BEFORE UPDATE ON amministratore
FOR EACH ROW
BEGIN 

	IF(length(NEW.password) <> 128)
		THEN BEGIN 
			SET NEW.password = SHA2(NEW.password, 512);
		END;
	END IF;

END$  
DELIMITER ;

########## AFTER UPDATEs TRIGGERS ##########

/*
Trigger che chiama la procedura genera_eventi con cui si genera
tutta la serie di eventi ricorrenti associati all'evento "master"
che è stato appena modificato nella tabella evento, ovviamente il 
trigger si attiva ma non effettua nessuna azione nel caso in cui
sia stato appena mdoficato un evento "semplice".
*/

DROP TRIGGER IF EXISTS rigenera_eventi_ricorrenti;

DELIMITER $ 

CREATE TRIGGER rigenera_eventi_ricorrenti
AFTER UPDATE ON evento
FOR EACH ROW
BEGIN 
	DELETE FROM evento_ricorrente WHERE ID_master = NEW.ID;
	IF( NEW.tipo_ricorrenza IS NOT NULL )
		THEN BEGIN
			CALL genera_eventi(NEW.ID, NEW.ID_aula, NEW.data_inizio, NEW.data_fine, NEW.tipo_ricorrenza, NEW.data_fine_ricorrenza);
		END;
	END IF;
END$  
DELIMITER ;
