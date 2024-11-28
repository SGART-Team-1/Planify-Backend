package iso.e02.planify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import iso.e02.planify.entities.Administrator;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.entities.Credentials;
import iso.e02.planify.repositories.UsersRepository;
import iso.e02.planify.services.ValidateUserService;

import java.time.LocalDate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

@RestController
public class sqlEjemplo {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private ValidateUserService validateUserService;

    @GetMapping("/usuarios-de-prueba")
    public void registrarUsuarios() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode usersNode = objectMapper.readTree(new File("./src/main/resources/usuariosPruebas.json"));
            if (usersNode.isArray()) {
                for (JsonNode userNode : usersNode) {
                    CommonUser user = new CommonUser();
                    user.setEmail(userNode.get("mail").asText());
                    user.setName(userNode.get("name").asText());
                    user.setSurnames(userNode.get("surnames").asText());
                    user.setCentre(userNode.get("centro").asText());
                    user.setProfile(userNode.get("profile").asText());
                    user.setDepartment(userNode.get("department").asText());
                    user.setRegistrationDate(LocalDate.now());
                    user.setBlocked(userNode.get("blocked").asBoolean());
                    user.setActivated(userNode.get("activated").asBoolean());

                    Credentials credentials = new Credentials();
                    credentials.setPassword(this.validateUserService.hashPassword(userNode.get("password").asText()));
                    user.setCredentials(credentials);

                    this.userRepository.save(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @GetMapping("/admin-de-prueba")
    public void registrarAdmin() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode usersNode = objectMapper.readTree(new File("./src/main/resources/administradoresPruebas.json"));
            if (usersNode.isArray()) {
                for (JsonNode userNode : usersNode) {
                    Administrator user = new Administrator();
                    user.setEmail(userNode.get("mail").asText());
                    user.setName(userNode.get("name").asText());    
                    user.setSurnames(userNode.get("surnames").asText());
                    user.setCentre(userNode.get("centro").asText());
                    user.setInternal(userNode.get("internal").asBoolean());

                    Credentials credentials = new Credentials();
                    credentials.setPassword(this.validateUserService.hashPassword(userNode.get("password").asText()));
                    user.setCredentials(credentials);

                    this.userRepository.save(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}