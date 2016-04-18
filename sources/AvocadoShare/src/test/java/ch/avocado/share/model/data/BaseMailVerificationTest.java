package ch.avocado.share.model.data;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
