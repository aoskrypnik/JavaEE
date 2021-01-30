package kma.topic2.junit.validation;

import kma.topic2.junit.exceptions.ConstraintViolationException;
import kma.topic2.junit.exceptions.LoginExistsException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

	private static final String LOGIN_EXISTS_EXCEPTION_MESSAGE = "Login %s already taken";
	private static final String CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE = "You have errors in you object";

	private static final String TEST_LOGIN = "login";
	private static final String TEST_FULL_NAME = "fullName";
	private static final String VALID_PASSWORD = "pass";
	private static final String TOO_SHORT_PASSWORD = "pa";
	private static final String TOO_LONG_PASSWORD = "password";
	private static final String NOT_MATCHING_REGEX_PASSWORD = "*pAss";

	@InjectMocks
	private UserValidator testInstance;
	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		when(userRepository.isLoginExists(TEST_LOGIN)).thenReturn(false);
	}

	@Test
	void shouldPassValidationWhenUserNotExists() {
		NewUser user = NewUser.builder()
				.fullName(TEST_FULL_NAME)
				.login(TEST_LOGIN)
				.password(VALID_PASSWORD)
				.build();

		assertDoesNotThrow(() -> testInstance.validateNewUser(user));
	}

	@Test
	void shouldThrowLoginExistsExceptionWhenUserExists() {
		NewUser user = NewUser.builder()
				.fullName(TEST_FULL_NAME)
				.login(TEST_LOGIN)
				.password(VALID_PASSWORD)
				.build();
		when(userRepository.isLoginExists(TEST_LOGIN)).thenReturn(true);

		LoginExistsException exception = assertThrows(LoginExistsException.class,
				() -> testInstance.validateNewUser(user));

		assertThat(exception.getMessage())
				.isEqualTo(String.format(LOGIN_EXISTS_EXCEPTION_MESSAGE, user.getLogin()));
	}

	@Test
	void shouldThrowConstraintViolationExceptionWhenPasswordTooShort() {
		NewUser user = NewUser.builder()
				.fullName(TEST_FULL_NAME)
				.login(TEST_LOGIN)
				.password(TOO_SHORT_PASSWORD)
				.build();

		ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
				() -> testInstance.validateNewUser(user));

		assertThat(exception.getMessage()).isEqualTo(CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE);
	}

	@Test
	void shouldThrowConstraintViolationExceptionWhenPasswordTooLong() {
		NewUser user = NewUser.builder()
				.fullName(TEST_FULL_NAME)
				.login(TEST_LOGIN)
				.password(TOO_LONG_PASSWORD)
				.build();

		ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
				() -> testInstance.validateNewUser(user));

		assertThat(exception.getMessage()).isEqualTo(CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE);
	}

	@Test
	void shouldThrowConstraintViolationExceptionWhenPasswordDoesNotMatchRegex() {
		NewUser user = NewUser.builder()
				.fullName(TEST_FULL_NAME)
				.login(TEST_LOGIN)
				.password(NOT_MATCHING_REGEX_PASSWORD)
				.build();

		ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
				() -> testInstance.validateNewUser(user));

		assertThat(exception.getMessage()).isEqualTo(CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE);
	}
}