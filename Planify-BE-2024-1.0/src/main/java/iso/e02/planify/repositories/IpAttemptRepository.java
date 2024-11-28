package iso.e02.planify.repositories;

import iso.e02.planify.entities.IpAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IpAttemptRepository extends JpaRepository<IpAttempt, Long> {

    Optional<IpAttempt> findByIpAddress(String ipAddress);
}