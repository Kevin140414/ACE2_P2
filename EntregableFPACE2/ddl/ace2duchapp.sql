CREATE TABLE  "USUARIO" 
   (	"IDUSUARIO" NUMBER NOT NULL ENABLE, 
	"CORREO" VARCHAR2(100 CHAR) NOT NULL ENABLE, 
	"NOMBRE" VARCHAR2(100 CHAR) NOT NULL ENABLE, 
	"APELLIDO" VARCHAR2(100 CHAR), 
	"CLAVE" VARCHAR2(100 CHAR) NOT NULL ENABLE, 
	 CONSTRAINT "PK_USUARIO" PRIMARY KEY ("IDUSUARIO")
  USING INDEX  ENABLE, 
	 CONSTRAINT "UQ_CORREO" UNIQUE ("CORREO")
  USING INDEX  ENABLE
   )
/
CREATE TABLE  "AGENDA" 
   (	"IDUSUARIO" NUMBER NOT NULL ENABLE, 
	"IDCONTACTO" NUMBER NOT NULL ENABLE, 
	 CONSTRAINT "PK_AGENDA" PRIMARY KEY ("IDUSUARIO", "IDCONTACTO")
  USING INDEX  ENABLE
   )
/
CREATE TABLE  "REGISTRO" 
   (	"IDREGISTRO" NUMBER NOT NULL ENABLE, 
	"FECHA" TIMESTAMP (6) DEFAULT systimestamp NOT NULL ENABLE, 
	"TIEMPOTOTAL" NUMBER NOT NULL ENABLE, 
	"TIEMPOEFECTIVO" NUMBER NOT NULL ENABLE, 
	"IDUSUARIO" NUMBER NOT NULL ENABLE, 
	"LITROS" NUMBER NOT NULL ENABLE, 
	 CONSTRAINT "PK_REGISTRO" PRIMARY KEY ("IDREGISTRO")
  USING INDEX  ENABLE
   )
/
ALTER TABLE  "AGENDA" ADD CONSTRAINT "FK_AGENDA_CONTACTO" FOREIGN KEY ("IDCONTACTO")
	  REFERENCES  "USUARIO" ("IDUSUARIO") ENABLE
/
ALTER TABLE  "AGENDA" ADD CONSTRAINT "FK_AGENDA_USUARIO" FOREIGN KEY ("IDUSUARIO")
	  REFERENCES  "USUARIO" ("IDUSUARIO") ON DELETE CASCADE ENABLE
/
ALTER TABLE  "REGISTRO" ADD CONSTRAINT "FK_REG_USUARIO" FOREIGN KEY ("IDUSUARIO")
	  REFERENCES  "USUARIO" ("IDUSUARIO") ON DELETE CASCADE ENABLE
/
CREATE OR REPLACE EDITIONABLE FUNCTION  "GETTIEMPO" ( 
                      nSeg IN NUMBER DEFAULT 0 
                    ) RETURN VARCHAR2 
  AS 
    cTiempo    VARCHAR2(50); 
    nDias      NUMBER; 
    nHoras     NUMBER; 
    nMinutos   NUMBER; 
    nSegundos  NUMBER; 
  BEGIN 
    nDias       := nSeg/86400; 
    nHoras      := (nDias - TRUNC(nDias))*24; 
    nMinutos    := (nHoras - TRUNC(nHoras))*60; 
    nSegundos   := TRUNC((nMinutos - TRUNC(nMinutos))*60); 
 
    	cTiempo     := 	   TRIM(TO_CHAR(trunc(nDias), '9999999900')) 
                      || ':' 
                      || TRIM(TO_CHAR(trunc(nHoras), '9999999900')) 
                      || ':' 
                      || TRIM(TO_CHAR(trunc(nMinutos), '9999999900')) 
                      || ':' 
                      || TRIM(TO_CHAR(trunc(nSegundos), '9999999900')) 
                      ; 
    RETURN REGEXP_REPLACE(cTiempo,'0+\:','',1); 
  END GETTIEMPO; 
 
/

CREATE OR REPLACE EDITIONABLE FUNCTION  "HASH_MD5" (p_msisdn IN VARCHAR2) RETURN VARCHAR2 IS 
  v_secret VARCHAR2(100);
BEGIN 
  select standard_hash(p_msisdn, 'MD5') 
  into v_secret
  from dual;
  
  RETURN v_secret;
END Hash_MD5; 
 
/

CREATE INDEX  "IDX_REGISTRO_USUARIO" ON  "REGISTRO" ("IDUSUARIO")
/
 CREATE SEQUENCE   "SQREGISTRO"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 41 CACHE 10 NOORDER  NOCYCLE  NOKEEP  GLOBAL
/
 CREATE SEQUENCE   "SQUSUARIO"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 61 CACHE 10 NOORDER  NOCYCLE  NOKEEP  GLOBAL
/
CREATE OR REPLACE EDITIONABLE TRIGGER  "TSU_REGISTRO_SQREGISTRO" AFTER UPDATE OF idregistro 
ON registro FOR EACH ROW 
BEGIN 
  RAISE_APPLICATION_ERROR(-20010,'Cannot update column idregistro in table registro as it uses sequence.'); 
END; 

/
ALTER TRIGGER  "TSU_REGISTRO_SQREGISTRO" ENABLE
/
CREATE OR REPLACE EDITIONABLE TRIGGER  "TSU_USUARIO_SQUSUARIO" AFTER UPDATE OF idusuario 
ON usuario FOR EACH ROW 
BEGIN 
  RAISE_APPLICATION_ERROR(-20010,'Cannot update column idusuario in table usuario as it uses sequence.'); 
END; 

/
ALTER TRIGGER  "TSU_USUARIO_SQUSUARIO" ENABLE
/
CREATE OR REPLACE EDITIONABLE TRIGGER  "TS_REGISTRO_SQREGISTRO" BEFORE INSERT 
ON registro FOR EACH ROW 
BEGIN 
  :new.idregistro := sqregistro.nextval; 
END; 

/
ALTER TRIGGER  "TS_REGISTRO_SQREGISTRO" ENABLE
/
CREATE OR REPLACE EDITIONABLE TRIGGER  "TS_USUARIO_SQUSUARIO" BEFORE INSERT 
ON usuario FOR EACH ROW 
BEGIN 
  :new.idusuario := squsuario.nextval; 
  :new.clave := Hash_MD5(:new.clave); 
END; 

/
ALTER TRIGGER  "TS_USUARIO_SQUSUARIO" ENABLE
/
CREATE OR REPLACE FORCE EDITIONABLE VIEW  "VW_LISTACONTACTOS" ("IDUSUARIO", "IDCONTACTO", "CORREO", "NOMBRE") AS 
  SELECT AG.IDUSUARIO, 
    AG.IDCONTACTO, 
    CON.CORREO, 
    CON.NOMBRE 
    || DECODE(CON.APELLIDO, NULL, '', ' ' 
    || CON.APELLIDO) AS nombre 
  FROM AGENDA AG 
  JOIN USUARIO US 
  ON US.IDUSUARIO = AG.IDUSUARIO 
  JOIN USUARIO CON 
  ON CON.IDUSUARIO = AG.IDCONTACTO 
  ORDER BY AG.IDUSUARIO, 
    CON.NOMBRE
/
CREATE OR REPLACE FORCE EDITIONABLE VIEW  "VW_REGPROMEDIO" ("IDUSUARIO", "PROMEDIOTOTAL", "PROMEDIOEFECTIVO", "PROMEDIOLITROS") AS 
  SELECT reg.idusuario, 
    GETTIEMPO(TRUNC(AVG(reg.TIEMPOTOTAL))) PROMEDIOTOTAL, 
    GETTIEMPO(TRUNC(AVG(reg.TIEMPOEFECTIVO))) PROMEDIOEFECTIVO,
    AVG(LITROS) PROMEDIOLITROS
  FROM registro reg 
  GROUP BY reg.idusuario
/
CREATE OR REPLACE FORCE EDITIONABLE VIEW  "VW_REGPROMEDIOAGENDA" ("IDUSUARIO", "IDCONTACTO", "CORREO", "NOMBRE", "PROMEDIOTOTAL", "PROMEDIOEFECTIVO", "PROMEDIOLITROS") AS 
  SELECT  CON.IDUSUARIO 
        , CON.IDCONTACTO 
        , CON.CORREO 
        , CON.NOMBRE 
        , GETTIEMPO(TRUNC(AVG(reg.TIEMPOTOTAL))) PROMEDIOTOTAL 
        , GETTIEMPO(TRUNC(AVG(reg.TIEMPOEFECTIVO))) PROMEDIOEFECTIVO 
        , AVG(REG.LITROS) PROMEDIOLITROS
  FROM VW_LISTACONTACTOS CON 
  JOIN REGISTRO REG ON REG.IDUSUARIO = CON.IDCONTACTO 
  GROUP BY CON.IDUSUARIO 
        , CON.IDCONTACTO 
        , CON.CORREO 
        , CON.NOMBRE 
  ORDER BY CON.IDUSUARIO, CON.NOMBRE
/
CREATE OR REPLACE FORCE EDITIONABLE VIEW  "VW_REGPROMEDIO_ANIOMES" ("IDUSUARIO", "ANIO", "MES", "PROMEDIOTOTAL", "PROMEDIOEFECTIVO", "PROMEDIOLITROS") AS 
  SELECT  reg.idusuario 
        , extract(year from reg.fecha) anio
		, extract(month from reg.fecha) mes
	    , GETTIEMPO(TRUNC(AVG(reg.TIEMPOTOTAL))) PROMEDIOTOTAL
        , GETTIEMPO(TRUNC(AVG(reg.TIEMPOEFECTIVO))) PROMEDIOEFECTIVO 
        , AVG(reg.LITROS) PROMEDIOLITROS
  FROM registro reg 
  GROUP BY reg.idusuario, extract(year from fecha), extract(month from fecha)
  ORDER BY 1, 2, 3
/
CREATE OR REPLACE FORCE EDITIONABLE VIEW  "VW_REGPROMEDIO_DIA" ("IDUSUARIO", "FECHA", "PROMEDIOTOTAL", "PROMEDIOEFECTIVO", "PROMEDIOLITROS") AS 
  SELECT  reg.idusuario 
        , TO_CHAR(reg.fecha, 'YYYYMMDD') fecha
	    , GETTIEMPO(TRUNC(AVG(reg.TIEMPOTOTAL))) PROMEDIOTOTAL
        , GETTIEMPO(TRUNC(AVG(reg.TIEMPOEFECTIVO))) PROMEDIOEFECTIVO 
        , AVG(reg.LITROS) PROMEDIOLITROS
  FROM registro reg 
  GROUP BY reg.idusuario, TO_CHAR(reg.fecha, 'YYYYMMDD')
  ORDER BY 1,2
/