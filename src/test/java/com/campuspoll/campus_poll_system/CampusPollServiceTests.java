package com.campuspoll.campus_poll_system;

import com.campuspoll.campus_poll_system.dto.*;
import com.campuspoll.campus_poll_system.entity.Poll;
import com.campuspoll.campus_poll_system.entity.PollOption;
import com.campuspoll.campus_poll_system.entity.User;
import com.campuspoll.campus_poll_system.entity.Vote;
import com.campuspoll.campus_poll_system.exception.BadRequestException;
import com.campuspoll.campus_poll_system.exception.ConflictException;
import com.campuspoll.campus_poll_system.repository.PollOptionRepository;
import com.campuspoll.campus_poll_system.repository.PollRepository;
import com.campuspoll.campus_poll_system.repository.UserRepository;
import com.campuspoll.campus_poll_system.repository.VoteRepository;
import com.campuspoll.campus_poll_system.security.JwtUtils;
import com.campuspoll.campus_poll_system.security.UserDetailsImpl;
import com.campuspoll.campus_poll_system.service.AuthService;
import com.campuspoll.campus_poll_system.service.PollService;
import com.campuspoll.campus_poll_system.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampusPollServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PollRepository pollRepository;

    @Mock
    private PollOptionRepository pollOptionRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @InjectMocks
    private PollService pollService;

    @InjectMocks
    private VoteService voteService;

    private User sampleStudent;
    private User sampleAdmin;
    private Poll samplePoll;
    private PollOption option1;
    private PollOption option2;

    @BeforeEach
    void setUp() {
        sampleStudent = new User("John Doe", "STU101", "john@campus.edu", "$2a$10$hashedPassword", "STUDENT");
        sampleStudent.setId(10L);

        sampleAdmin = new User("Admin User", "ADM001", "admin@campus.edu", "$2a$10$hashedAdminPassword", "ADMIN");
        sampleAdmin.setId(1L);

        samplePoll = new Poll();
        samplePoll.setId(100L);
        samplePoll.setTitle("Campus Cafeteria Vendor");
        samplePoll.setQuestion("Which vendor do you prefer?");
        samplePoll.setStartTime(LocalDateTime.now().minusHours(1));
        samplePoll.setEndTime(LocalDateTime.now().plusHours(5));
        samplePoll.setPublished(true);
        samplePoll.setStatus("ACTIVE");
        samplePoll.setResultVisibility("ALWAYS");

        option1 = new PollOption(samplePoll, "Subway");
        option1.setId(101L);
        option2 = new PollOption(samplePoll, "Starbucks");
        option2.setId(102L);
        samplePoll.addOption(option1);
        samplePoll.addOption(option2);
    }

    @Test
    @DisplayName("Registration: Successfully creates student account with hashed password")
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Alice Smith");
        req.setStudentId("STU202");
        req.setEmail("alice@campus.edu");
        req.setPassword("Secret@123");
        req.setConfirmPassword("Secret@123");

        when(userRepository.existsByEmail("alice@campus.edu")).thenReturn(false);
        when(userRepository.existsByStudentId("STU202")).thenReturn(false);
        when(passwordEncoder.encode("Secret@123")).thenReturn("$2a$10$mockHashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(20L);
            return u;
        });
        when(jwtUtils.generateToken(any(), any(), any())).thenReturn("mock-jwt-token");

        AuthResponse resp = authService.register(req);

        assertNotNull(resp);
        assertEquals("mock-jwt-token", resp.getToken());
        assertEquals("alice@campus.edu", resp.getUser().getEmail());
        assertEquals("STUDENT", resp.getUser().getRole());
        verify(passwordEncoder).encode("Secret@123");
    }

    @Test
    @DisplayName("Registration: Reject duplicate email")
    void testRegisterDuplicateEmail() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Alice Smith");
        req.setStudentId("STU202");
        req.setEmail("alice@campus.edu");
        req.setPassword("Secret@123");
        req.setConfirmPassword("Secret@123");

        when(userRepository.existsByEmail("alice@campus.edu")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registration: Reject password mismatch")
    void testRegisterPasswordMismatch() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Alice Smith");
        req.setStudentId("STU202");
        req.setEmail("alice@campus.edu");
        req.setPassword("Secret@123");
        req.setConfirmPassword("Different@123");

        assertThrows(BadRequestException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login: Successful authentication returns token and UserDto without password")
    void testLoginSuccess() {
        LoginRequest req = new LoginRequest("john@campus.edu", "Secret@123");

        when(userRepository.findByEmail("john@campus.edu")).thenReturn(Optional.of(sampleStudent));
        when(jwtUtils.generateToken("john@campus.edu", "STUDENT", 10L)).thenReturn("jwt-john-token");

        AuthResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("jwt-john-token", resp.getToken());
        assertEquals("John Doe", resp.getUser().getFullName());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Poll Creation: Date validation fails if end date before start date")
    void testPollCreationInvalidDates() {
        PollRequest req = new PollRequest();
        req.setTitle("Invalid Poll");
        req.setQuestion("Why?");
        req.setStartTime(LocalDateTime.now().plusDays(2));
        req.setEndTime(LocalDateTime.now().plusDays(1)); // Before start!
        req.setOptions(Arrays.asList("A", "B"));

        assertThrows(BadRequestException.class, () -> pollService.createPoll(req, 1L));
        verify(pollRepository, never()).save(any());
    }

    @Test
    @DisplayName("Poll Creation: Fails if fewer than two options are provided")
    void testPollCreationFewerThanTwoOptions() {
        PollRequest req = new PollRequest();
        req.setTitle("One Option Poll");
        req.setQuestion("Only one choice?");
        req.setStartTime(LocalDateTime.now());
        req.setEndTime(LocalDateTime.now().plusDays(1));
        req.setOptions(List.of("Only Option"));

        assertThrows(BadRequestException.class, () -> pollService.createPoll(req, 1L));
        verify(pollRepository, never()).save(any());
    }

    @Test
    @DisplayName("Poll Creation: Successfully creates poll with options")
    void testPollCreationSuccess() {
        PollRequest req = new PollRequest();
        req.setTitle("Campus Library Hours");
        req.setQuestion("Should library open 24/7?");
        req.setStartTime(LocalDateTime.now().minusHours(1));
        req.setEndTime(LocalDateTime.now().plusDays(3));
        req.setPublished(true);
        req.setOptions(Arrays.asList("Yes, 24/7", "No, keep normal hours"));

        when(pollRepository.save(any(Poll.class))).thenAnswer(inv -> {
            Poll p = inv.getArgument(0);
            p.setId(200L);
            return p;
        });

        PollResponse resp = pollService.createPoll(req, 1L);

        assertNotNull(resp);
        assertEquals(200L, resp.getId());
        assertEquals("Campus Library Hours", resp.getTitle());
        assertEquals(2, resp.getOptions().size());
        assertEquals("ACTIVE", resp.getStatus());
    }

    @Test
    @DisplayName("Voting: Successful vote submission")
    void testVoteSuccess() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleStudent));
        when(pollRepository.findById(100L)).thenReturn(Optional.of(samplePoll));
        when(pollOptionRepository.findById(101L)).thenReturn(Optional.of(option1));
        when(voteRepository.existsByStudentIdAndPollId(10L, 100L)).thenReturn(false);
        when(voteRepository.save(any(Vote.class))).thenAnswer(inv -> {
            Vote v = inv.getArgument(0);
            v.setId(500L);
            return v;
        });

        VoteResponse resp = voteService.submitVote(100L, 101L, 10L);

        assertNotNull(resp);
        assertTrue(resp.isSuccess());
        assertEquals(500L, resp.getVoteId());
        assertEquals(100L, resp.getPollId());
        assertEquals(101L, resp.getOptionId());
    }

    @Test
    @DisplayName("Voting: Prevent duplicate vote from same student")
    void testVoteDuplicateFails() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleStudent));
        when(pollRepository.findById(100L)).thenReturn(Optional.of(samplePoll));
        when(pollOptionRepository.findById(101L)).thenReturn(Optional.of(option1));
        when(voteRepository.existsByStudentIdAndPollId(10L, 100L)).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () ->
                voteService.submitVote(100L, 101L, 10L)
        );

        assertEquals("You have already voted in this poll.", ex.getMessage());
        verify(voteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Voting: Rejects vote in closed poll")
    void testVoteInClosedPollFails() {
        samplePoll.setStatus("CLOSED");

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleStudent));
        when(pollRepository.findById(100L)).thenReturn(Optional.of(samplePoll));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                voteService.submitVote(100L, 101L, 10L)
        );

        assertTrue(ex.getMessage().contains("Poll is closed"));
        verify(voteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Voting: Rejects vote before poll start time")
    void testVoteBeforeStartTimeFails() {
        samplePoll.setStartTime(LocalDateTime.now().plusDays(2)); // Future start
        samplePoll.setStatus("SCHEDULED");

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleStudent));
        when(pollRepository.findById(100L)).thenReturn(Optional.of(samplePoll));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                voteService.submitVote(100L, 101L, 10L)
        );

        assertTrue(ex.getMessage().contains("Voting has not started yet"));
        verify(voteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Voting: Rejects vote with option belonging to another poll")
    void testVoteWithOptionFromDifferentPollFails() {
        Poll otherPoll = new Poll();
        otherPoll.setId(999L);
        PollOption alienOption = new PollOption(otherPoll, "Alien Option");
        alienOption.setId(999L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleStudent));
        when(pollRepository.findById(100L)).thenReturn(Optional.of(samplePoll));
        when(pollOptionRepository.findById(999L)).thenReturn(Optional.of(alienOption));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                voteService.submitVote(100L, 999L, 10L)
        );

        assertTrue(ex.getMessage().contains("does not belong"));
        verify(voteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Poll Results: Calculates correct vote counts and percentages")
    void testPollResultsCalculation() {
        when(pollRepository.findById(100L)).thenReturn(Optional.of(samplePoll));
        when(pollOptionRepository.findByPollId(100L)).thenReturn(List.of(option1, option2));
        when(voteRepository.countByPollId(100L)).thenReturn(10L);
        when(voteRepository.countByOptionId(101L)).thenReturn(7L);
        when(voteRepository.countByOptionId(102L)).thenReturn(3L);

        UserDetailsImpl adminDetails = UserDetailsImpl.build(sampleAdmin);

        PollResultResponse results = pollService.calculatePollResults(100L, adminDetails);

        assertNotNull(results);
        assertEquals(10L, results.getTotalVotes());
        assertEquals("Subway", results.getHighestVotedOption());
        assertEquals(2, results.getOptions().size());

        OptionResultDto opt1Res = results.getOptions().stream().filter(o -> o.getOptionId().equals(101L)).findFirst().orElseThrow();
        assertEquals(7L, opt1Res.getVotesCount());
        assertEquals(70.0, opt1Res.getPercentage());

        OptionResultDto opt2Res = results.getOptions().stream().filter(o -> o.getOptionId().equals(102L)).findFirst().orElseThrow();
        assertEquals(3L, opt2Res.getVotesCount());
        assertEquals(30.0, opt2Res.getPercentage());
    }
}
