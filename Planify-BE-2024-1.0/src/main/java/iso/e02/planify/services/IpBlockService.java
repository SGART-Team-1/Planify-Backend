package iso.e02.planify.services;

import iso.e02.planify.entities.IpAttempt;
import iso.e02.planify.repositories.IpAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio para bloquear direcciones IP después de varios intentos fallidos de inicio de sesión.
 */
@Service
public class IpBlockService {

    private static final int MAX_ATTEMPTS = 5; // Número máximo de intentos antes del bloqueo
    private static final long BLOCK_TIME_MINUTES = 2; // Duración del bloqueo en minutos

    @Autowired
    private IpAttemptRepository ipAttemptRepository;

    /**
     * Verifica si la IP está bloqueada.
     * 
     * @param ip Dirección IP a verificar.
     * @return true si la IP está bloqueada, false en caso contrario.
     */
    public boolean isIpBlocked(String ip) {
        Optional<IpAttempt> ipAttemptOpt = ipAttemptRepository.findByIpAddress(ip);

        if (ipAttemptOpt.isPresent()) {
            IpAttempt ipAttempt = ipAttemptOpt.get();
            if (ipAttempt.isBlocked() && ipAttempt.getLastAttempt().isAfter(LocalDateTime.now().minusMinutes(BLOCK_TIME_MINUTES))) {
                return true; // La IP está bloqueada dentro del tiempo de bloqueo
            } else if (ipAttempt.isBlocked()) {
                // Desbloqueo automático después de la duración del bloqueo
                ipAttempt.setBlocked(false);
                ipAttempt.setAttemptCount(0);
                ipAttemptRepository.save(ipAttempt);
            }
        }

        return false;
    }

    /**
     * Registra un intento fallido de inicio de sesión para la IP dada.
     * 
     * @param ip Dirección IP a registrar.
     */
    public void registerFailedAttempt(String ip) {
        IpAttempt ipAttempt = ipAttemptRepository.findByIpAddress(ip)
                .orElseGet(() -> {
                    IpAttempt newIpAttempt = new IpAttempt();
                    newIpAttempt.setIpAddress(ip);
                    return newIpAttempt;
                });

        ipAttempt.setAttemptCount(ipAttempt.getAttemptCount() + 1);
        ipAttempt.setLastAttempt(LocalDateTime.now());

        if (ipAttempt.getAttemptCount() >= MAX_ATTEMPTS && !ipAttempt.isBlocked()) {
            ipAttempt.setBlocked(true);
        }

        ipAttemptRepository.save(ipAttempt);
    }

    /**
     * Restablece los intentos fallidos para la IP dada.
     * 
     * @param ip Dirección IP a restablecer.
     */
    public void resetAttempts(String ip) {
        ipAttemptRepository.findByIpAddress(ip).ifPresent(ipAttemptRepository::delete);
    }
}