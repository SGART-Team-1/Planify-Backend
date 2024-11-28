package iso.e02.planify.services;

import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.repositories.CommonUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManageUsersServiceTest {

    @Mock
    private CommonUserRepository commonUserRepository;

    @InjectMocks
    private ManageUsersService manageUsersService;

    private CommonUser user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new CommonUser();
        user.setId(1L);
        user.setName("Test User");
        user.setSurnames("User Surname");
        user.setEmail("test@planify.com");
        user.setCentre("Test Centre");
        user.setActivated(true);
        user.setBlocked(false);
    }

    @Test
    void testGetAllUsers() {
        when(commonUserRepository.findAll()).thenReturn(Collections.singletonList(user));

        var users = manageUsersService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("Test User", users.get(0).getName());
        verify(commonUserRepository, times(1)).findAll();
    }

}
