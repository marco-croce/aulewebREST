USE auleweb;

/*
La funzione verifica_slot_occupati_eventi_ricorrenti verifica che 
la serie di eventi ricorrenti che si vogliono inserire non siano 
associati a un'aula i cui slot orari siano già occupati da un 
altro evento presente all'interno del database, che può essere
sia "semplice" sia ricorrente.
La funzione ritorna il valore 0 se non sono presenti slot orari
già occupati, mentre ritorna immediatamente il valore -1 nel
momento in cui trova uno slot orario già occupato.
*/

DROP FUNCTION IF EXISTS verifica_slot_occupati_eventi_ricorrenti;

DELIMITER $

CREATE FUNCTION verifica_slot_occupati_eventi_ricorrenti
(_ID_aula INTEGER, _data_inizio DATETIME, _data_fine DATETIME, _tipo_ricorrenza VARCHAR(50), _data_fine_ricorrenza DATE, _ID_master INTEGER) 
RETURNS TINYINT(1) DETERMINISTIC
BEGIN
	DECLARE _inizio DATETIME;
    DECLARE _fine DATETIME;

	SET _inizio = _data_inizio;
    SET _fine = _data_fine;

	WHILE ( DATEDIFF(_inizio,_data_fine_ricorrenza) <= 0 )
		DO BEGIN
			DECLARE eventi CURSOR FOR
				SELECT e.data_inizio, e.data_fine
				FROM evento e
				WHERE e.ID_aula = _ID_aula AND ( DATE(e.data_inizio) = DATE(_inizio) )
					AND e.ID <> _ID_master;
			DECLARE eventi_ricorrenti CURSOR FOR
				SELECT er.data_inizio, er.data_fine
				FROM evento_ricorrente er JOIN evento e ON er.ID_master = e.ID
				WHERE e.ID_aula = _ID_aula AND ( DATE(er.data_inizio) = DATE(_inizio) ) 
					AND (er.ID_master <> _ID_master);
						
			OPEN eventi;

verifica_eventi: 
			BEGIN 
				DECLARE data_inizio2 datetime; 
				DECLARE data_fine2 datetime; 
				DECLARE EXIT HANDLER FOR NOT FOUND BEGIN END;
					LOOP 
						FETCH eventi INTO data_inizio2, data_fine2;
						IF( (_inizio BETWEEN data_inizio2 AND data_fine2) OR 
						(_fine BETWEEN data_inizio2 AND data_fine2 ) OR 
						(data_inizio2 BETWEEN _inizio AND _fine) OR 
						(data_fine2 BETWEEN _inizio AND _fine) )
							THEN BEGIN
								RETURN -1;
								LEAVE verifica_eventi;
							END;
						END IF;
					END LOOP;
			END;
            
			CLOSE eventi;
            
            OPEN eventi_ricorrenti;

verifica_eventi_ricorrenti: 
			BEGIN 
				DECLARE data_inizio3 datetime; 
				DECLARE data_fine3 datetime; 
				DECLARE EXIT HANDLER FOR NOT FOUND BEGIN END;
					LOOP 
						FETCH eventi_ricorrenti INTO data_inizio3, data_fine3;
						IF( (_inizio BETWEEN data_inizio3 AND data_fine3) OR 
						(_fine BETWEEN data_inizio3 AND data_fine3 ) OR 
						(data_inizio3 BETWEEN _inizio AND _fine) OR 
						(data_fine3 BETWEEN _inizio AND _fine) )
							THEN BEGIN
								RETURN -1;
								LEAVE verifica_eventi_ricorrenti;
							END;
						END IF;
					END LOOP;
			END;
            
			CLOSE eventi_ricorrenti;
            
			CASE
				WHEN _tipo_ricorrenza = "GIORNALIERA" 
				THEN 
					SET _inizio = DATE_ADD(_inizio, INTERVAL 1 DAY); 
					SET _fine = DATE_ADD(_fine, INTERVAL 1 DAY);
				WHEN _tipo_ricorrenza = "SETTIMANALE"
				THEN
					SET _inizio = DATE_ADD(_inizio, INTERVAL 7 DAY); 
					SET _fine = DATE_ADD(_fine, INTERVAL 7 DAY); 
				WHEN _tipo_ricorrenza = "MENSILE"
				THEN
					SET _inizio = DATE_ADD(_inizio, INTERVAL 1 MONTH); 
					SET _fine = DATE_ADD(_fine, INTERVAL 1 MONTH);  
			END CASE;
    
			IF DATEDIFF(_fine, _data_fine_ricorrenza) > 0 
				THEN BEGIN 
					RETURN 0;
				END;
			END IF; 

		END;
	END WHILE;

	RETURN 0;

END$
DELIMITER ;

##################################################

/*
La funzione eventi_aula verifica che l'evento che si vuole inserire
non sia associato a un'aula il cui slot orario è già occupato da un 
altro evento presente all'interno del database, che può essere sia 
"semplice" sia ricorrente.
La funzione ritorna il valore 0 se non sono presenti slot orari
già occupati, mentre ritorna immediatamente il valore -1 nel
momento in cui trova uno slot orario già occupato.
*/

DROP FUNCTION IF EXISTS eventi_aula;

DELIMITER $

CREATE FUNCTION eventi_aula
(_aula INT, _data_inizio DATETIME, _data_fine DATETIME, _ID_master INTEGER) 
RETURNS TINYINT(1) DETERMINISTIC
BEGIN 
	DECLARE eventi CURSOR FOR
		SELECT e.data_inizio, e.data_fine
		FROM evento e
		WHERE e.ID_aula =_aula AND ( DATE(e.data_inizio) = DATE(_data_inizio) )
			AND e.ID <> _ID_master;
	DECLARE eventi_ricorrenti CURSOR FOR
		SELECT er.data_inizio, er.data_fine
		FROM evento_ricorrente er JOIN evento e ON er.ID_master = e.ID
		WHERE e.ID_aula = _aula AND ( DATE(er.data_inizio) = DATE(_data_inizio) ) 
			AND er.ID_master <> _ID_master;
        
    OPEN eventi;

verifica: 
	BEGIN 
	DECLARE data_inizio2 datetime; 
	DECLARE data_fine2 datetime; 
	DECLARE EXIT HANDLER FOR NOT FOUND BEGIN END;
		LOOP 
			FETCH eventi INTO data_inizio2, data_fine2;
			IF( (_data_inizio BETWEEN data_inizio2 AND data_fine2) OR 
				(_data_fine BETWEEN data_inizio2 AND data_fine2) OR 
				(data_inizio2 BETWEEN _data_inizio AND _data_fine) OR 
				(data_fine2 BETWEEN _data_inizio AND _data_fine) )
				THEN BEGIN
					RETURN -1;
					LEAVE verifica;
				END;
			END IF;
		END LOOP;
	END;

	CLOSE eventi;
    
    OPEN eventi_ricorrenti;

verifica_eventi_ricorrenti: 
	BEGIN 
	DECLARE data_inizio3 datetime; 
	DECLARE data_fine3 datetime; 
	DECLARE EXIT HANDLER FOR NOT FOUND BEGIN END;
		LOOP 
			FETCH eventi_ricorrenti INTO data_inizio3, data_fine3;
			IF( (_data_inizio BETWEEN data_inizio3 AND data_fine3) OR 
				(_data_fine BETWEEN data_inizio3 AND data_fine3) OR 
				(data_inizio3 BETWEEN _data_inizio AND _data_fine) OR 
				(data_fine3 BETWEEN _data_inizio AND _data_fine) )
				THEN BEGIN
					RETURN -1;
					LEAVE verifica_eventi_ricorrenti;
				END;
			END IF;
		END LOOP;
	END;
    
	CLOSE eventi_ricorrenti;
    
    RETURN 0;

END$
DELIMITER ;
