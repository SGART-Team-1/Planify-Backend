como he modificado una entidad, si tenias la BBDD creada ya se debe modifiar manualmente con 
```
ALTER TABLE credentials ADD valid BIT NOT NULL DEFAULT 0; --añadido en el codigo, como ya se ha creado la bd se modifica a mano
```

Para poder crear la tabla de notificaciones y poder trabajar con ella, este es el script:
```sql
-- Crear tabla de notificaciones
CREATE TABLE Notifications (
    notification_id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY, -- ID único
    user_id BIGINT NOT NULL, -- ID del usuario
    meeting_id BIGINT NOT NULL, -- ID de la reunión asociada (puede ser NULL si no aplica)
    notification_description NVARCHAR(500) NOT NULL, -- Descripción o mensaje de la notificación
    notification_state BIT DEFAULT 0 NOT NULL, -- Estado de la invitación (0 = no leído, 1 = leído)
    creation_date DATETIME DEFAULT GETDATE() NOT NULL, -- Fecha de creación de la notificación
    reading_date DATETIME NULL -- Fecha de lectura (opcional, se actualiza al leer)
 -- Definir las claves foráneas
    CONSTRAINT FK_Notifications_User FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT FK_Notifications_Meeting FOREIGN KEY (meeting_id) REFERENCES meeting(id)
);

-- Disparador para eliminar notificaciones leídas
CREATE TRIGGER TRG_delete_read_notifications
ON Notifications
AFTER UPDATE
AS
BEGIN
    -- Eliminar notificaciones cuando el estado cambie a 1 (leído)
    DELETE FROM Notifications
    WHERE notification_state = 1;
END;
```
He incluido un disparador para que cuando el estado de la notificación sea 1 (leído), se pueda eliminar directamente de la base de datos.