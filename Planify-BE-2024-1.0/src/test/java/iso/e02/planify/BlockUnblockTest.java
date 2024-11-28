package iso.e02.planify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.server.ResponseStatusException;

import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.repositories.CommonUserRepository;
import iso.e02.planify.repositories.MeetingAttendanceRepository;
import iso.e02.planify.repositories.MeetingRespository;
import iso.e02.planify.services.ManageUsersService;

@ContextConfiguration(classes = {ManageUsersService.class, MeetingRespository.class, MeetingAttendanceRepository.class})
@SpringBootTest
public class BlockUnblockTest {

    @Autowired
    private ManageUsersService manageUsersService;

    @MockBean
    private CommonUserRepository commonUserRepository;

    @MockBean
    private MeetingRespository meetingRepository;

    @MockBean
    private MeetingAttendanceRepository meetingAttendanceRepository;

    @Test
    public void testBlock() {
        CommonUser user = new CommonUser();
        user.setId(1L);
        user.setBlocked(false);

        Assertions.assertTrue(this.manageUsersService.updateUserBlockedStatus(user, true));
    }

    @Test
    public void testBlockError() {
        CommonUser user = new CommonUser();
        user.setId(3L);
        user.setBlocked(true);

        Assertions.assertThrows(ResponseStatusException.class, () -> this.manageUsersService.updateUserBlockedStatus(user, true));
    }

    @Test
    public void testUnblock() {
        CommonUser user = new CommonUser();
        user.setId(1L);
        user.setBlocked(true);

        Assertions.assertTrue(this.manageUsersService.updateUserBlockedStatus(user, false));
    }

    @Test
    public void testUnblockError() {
        CommonUser user = new CommonUser();
        user.setId(3L);
        user.setBlocked(false);

        Assertions.assertThrows(ResponseStatusException.class, () -> this.manageUsersService.updateUserBlockedStatus(user, false));
    }

}