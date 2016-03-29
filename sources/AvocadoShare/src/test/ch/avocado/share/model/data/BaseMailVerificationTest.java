package ch.avocado.share.model.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class BaseMailVerificationTest {
	@Test
	public void testCodeGeneration() {
		String code = BaseMailVerification.generateCode();
		assertNotNull(code);
		assertTrue(code.length() == 32);
		for(char codeCharacter : code.toCharArray()) {
			assertTrue(Character.isAlphabetic(codeCharacter) || Character.isDigit(codeCharacter));
		}
	}
}
