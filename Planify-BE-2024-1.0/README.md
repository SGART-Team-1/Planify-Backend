como he modificado una entidad, si tenias la BBDD creada ya se debe modifiar manualmente con 
```
ALTER TABLE credentials ADD valid BIT NOT NULL DEFAULT 0; --añadido en el codigo, como ya se ha creado la bd se modifica a mano
```

He incluido un disparador para que cuando el estado de la notificación sea 1 (leído), se pueda eliminar directamente de la base de datos.
```sql
-- Disparador para eliminar notificaciones leídas
CREATE TRIGGER TRG_EliminarNotificacionesLeidas
ON notifications
AFTER UPDATE
AS
BEGIN
    -- Eliminar notificaciones cuando el estado cambie a 1 (leído)
    DELETE FROM notifications
    WHERE notification_state = 1;
END;
```
