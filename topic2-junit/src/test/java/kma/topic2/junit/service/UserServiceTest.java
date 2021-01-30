package kma.topic2.junit.service;

import kma.topic2.junit.exceptions.ConstraintViolationException;
import kma.topic2.junit.exceptions.UserNotFoundException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.model.User;
import kma.topic2.junit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceTest {

	private static final String CREATED_USER_LOGIN = "created";
	private static final String NOT_CREATED_USER_LOGIN = "notCreated";
	private static final String TEST_FULL_NAME = "fullName";
	private static final String VALID_PASSWORD = "pass";
	private static final String TOO_SHORT_PASSWORD = "pa";
	private static final String TOO_LONG_PASSWORD = "password";
	private static final String NOT_MATCHING_REGEX_PASSWORD = "*pAss";

	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;

	private NewUser newUser;
	private User createdUser;

	@BeforeEach
	void setUp() {
		newUser = NewUser.builder()
				.login(CREATED_USER_LOGIN)
				.fullName(TEST_FULL_NAME)
				.password(VALID_PASSWORD)
				.build();
		createdUser = User.builder()
				.login(CREATED_USER_LOGIN)
				.fullName(TEST_FULL_NAME)
				.password(VALID_PASSWORD)
				.build();
	}

	@Test
	void shouldCreateNewUserAndReturnHimById() {
		newUser = NewUser.builder()
				.login(CREATED_USER_LOGIN)
				.fullName(TEST_FULL_NAME)
				.password(VALID_PASSWORD)
				.build();

		userService.createNewUser(newUser);

		assertThat(userRepository.getUserByLogin(CREATED_USER_LOGIN)).isEqualTo(createdUser);
		assertThat(userService.getUserByLogin(CREATED_USER_LOGIN)).isEqualTo(createdUser);
	}

	@Test
	void shouldNotCreateNewUserWhenPasswordTooShort() {
		newUser = NewUser.builder()
				.login(NOT_CREATED_USER_LOGIN)
				.fullName(TEST_FULL_NAME)
				.password(TOO_SHORT_PASSWORD)
				.build();

		assertThrows(ConstraintViolationException.class, () -> userService.createNewUser(newUser));

		assertThrows(UserNotFoundException.class, () -> userRepository.getUserByLogin(NOT_CREATED_USER_LOGIN));
	}

	@Test
	void shouldNotCreateNewUserWhenPasswordTooLong() {
		newUser = NewUser.builder()
				.login(NOT_CREATED_USER_LOGIN)
				.fullName(TEST_FULL_NAME)
				.password(TOO_LONG_PASSWORD)
				.build();

		assertThrows(ConstraintViolationException.class, () -> userService.createNewUser(newUser));

		assertThrows(UserNotFoundException.class, () -> userRepository.getUserByLogin(NOT_CREATED_USER_LOGIN));
	}

	@Test
	void shouldNotCreateNewUserWhenPasswordDoesNotMatchRegex() {
		newUser = NewUser.builder()
				.login(NOT_CREATED_USER_LOGIN)
				.fullName(TEST_FULL_NAME)
				.password(NOT_MATCHING_REGEX_PASSWORD)
				.build();

		assertThrows(ConstraintViolationException.class, () -> userService.createNewUser(newUser));

		assertThrows(UserNotFoundException.class, () -> userRepository.getUserByLogin(NOT_CREATED_USER_LOGIN));
	}
}
