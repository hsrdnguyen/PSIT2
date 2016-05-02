package ch.avocado.share.model.data;

import org.junit.Test;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.util.AssertionErrors;

import java.util.Date;

import static org.junit.Assert.*;

public class MailVerificationTest {
	@Test
	public void testCodeGeneration() {
		String code = MailVerification.generateCode();
		assertNotNull(code);
		assertTrue(code.length() == 32);
		for(char codeCharacter : code.toCharArray()) {
			assertTrue(Character.isAlphabetic(codeCharacter) || Character.isDigit(codeCharacter));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromExpiryInHoursWithNegativeValue() throws Exception {
		MailVerification.fromExpiryInHours(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithEmptyStringAsCode() throws Exception {
		new MailVerification(new Date(), "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullAsCode() throws Exception {
		new MailVerification(new Date(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullAsDateWithCode() throws Exception {
		new MailVerification(null, "123123");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullAsDate() throws Exception {
		new MailVerification(null);
	}

	@Test
	public void testFromExpiryInHours() throws Exception {
		MailVerification verification = MailVerification.fromExpiryInHours(1);
		Date expected = new Date(System.currentTimeMillis() + 1000*60*60);
		System.out.println("Expected: " + expected);
		System.out.println("Actual: " + verification.getExpiry());
		assertTrue(Math.abs(expected.getTime() - verification.getExpiry().getTime()) < 100);

		verification = MailVerification.fromExpiryInHours(0);
		expected = new Date(System.currentTimeMillis());
		System.out.println("Expected: " + expected);
		System.out.println("Actual: " + verification.getExpiry());
		assertTrue(Math.abs(expected.getTime() - verification.getExpiry().getTime()) < 100);


		verification = MailVerification.fromExpiryInHours(24);
		expected = new Date(System.currentTimeMillis() + 1000*60*60 * 24);
		System.out.println("Expected: " + expected);
		System.out.println("Actual: " + verification.getExpiry());
		assertTrue(Math.abs(expected.getTime() - verification.getExpiry().getTime()) < 100);
	}

	@Test
	public void testGetCode() throws Exception {
		String code = "12345";
		MailVerification verification = new MailVerification(new Date(), code);
		assertEquals(code, verification.getCode());

		verification = new MailVerification(new Date());
		assertNotNull(verification.getCode());
		assertEquals(MailVerification.TOKEN_LENGTH, verification.getCode().length());
	}

	@Test
	public void testGetExpiry() throws Exception {
		Date expiry = new Date();
		MailVerification verification = new MailVerification(expiry);
		assertEquals(expiry, verification.getExpiry());

		expiry.setTime(123);
		assertNotEquals(expiry, verification.getExpiry());
	}

	@Test
	public void testIsExpired() throws Exception {
		Date expiry = new Date(System.currentTimeMillis() - 1);
		MailVerification verification = new MailVerification(expiry);
		assertTrue(verification.isExpired());


		expiry = new Date(System.currentTimeMillis() + 100);
		verification = new MailVerification(expiry);
		assertFalse(verification.isExpired());
		Thread.sleep(200);
		assertTrue(verification.isExpired());
	}

	@Test
	public void testEquals() throws Exception {
		Date expiry = new Date();
		String code = "1234";
		MailVerification verification, verificationTwo;
		verification = new MailVerification(expiry, code);
		verificationTwo = new MailVerification(expiry, code);
		assertTrue(verification.equals(verification));
		assertTrue(verification.equals(verificationTwo));
		assertTrue(verificationTwo.equals(verification));
		assertFalse(verification.equals(null));

		verificationTwo = new MailVerification(new Date(321), code);
		assertFalse(verification.equals(verificationTwo));
		assertFalse(verificationTwo.equals(verification));

		verificationTwo = new MailVerification(expiry, code + "321");
		assertFalse(verification.equals(verificationTwo));
		assertFalse(verificationTwo.equals(verification));
	}
}
