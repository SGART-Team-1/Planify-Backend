como he modificado una entidad, si tenias la BBDD creada ya se debe modifiar manualmente con 
```
ALTER TABLE credentials ADD valid BIT NOT NULL DEFAULT 0; --añadido en el codigo, como ya se ha creado la bd se modifica a mano
```
