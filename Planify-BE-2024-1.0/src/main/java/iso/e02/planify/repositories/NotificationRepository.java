package iso.e02.planify.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iso.e02.planify.entities.Notification;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    // Buscar todas las notificaciones por usuario
    List<Notification> findByUser(Long user);
}

