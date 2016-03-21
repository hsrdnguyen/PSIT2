package ch.avocado.share.model.data;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import ch.avocado.share.model.data.EmailAddressVerification;

public class EmailAddressVerificationTest {

	@Test
	public void test_code_generation() {
		String code = EmailAddressVerification.generateCode();
		assertNotNull(code);
		assertTrue(code.length() == 32);
		for(char codeCharacter : code.toCharArray()) {
			assertTrue(Character.isAlphabetic(codeCharacter) || Character.isDigit(codeCharacter));
		}
	}
}
