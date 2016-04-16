package ch.avocado.share.common;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.avocado.share.common.BinaryTokenGenerator;
import ch.avocado.share.common.TokenGenerator;

public class BinaryTokenGeneratorTest {
		
	/**
	 * Test that {@link BinaryTokenGenerator.generateInsecureToken} throws an {@link IllegalArgumentException} 
	 * when called with a length of zero.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_generation_with_zero_length() {
		TokenGenerator generator = new BinaryTokenGenerator();
		generator.generateToken(0);
	}

	/**
	 * Test that {@link BinaryTokenGenerator.generateInsecureToken} throws an {@link IllegalArgumentException} 
	 * when called with a negative length.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_generation_with_minus_one_length() {
		TokenGenerator generator = new BinaryTokenGenerator();
		generator.generateToken(-1);
	}	
	
	/**
	 * Test {@link Generator.generateToken}
	 */
	@Test
	public void test_generation() {
		byte[] token;
		TokenGenerator generator = new BinaryTokenGenerator();
		assertNotNull(generator);
		token = generator.generateToken(10);
		assertNotNull(token);
		assertTrue(token.length == 10);
		token = generator.generateToken(1);
		assertNotNull(token);
		assertTrue(token.length == 1);
		token = generator.generateToken(1024);
		assertNotNull(token);
		assertTrue(token.length == 1024);
		
		generator = new BinaryTokenGenerator();
		token = generator.generateToken(10);
		assertNotNull(token);
		assertTrue(token.length == 10);
		token = generator.generateToken(1);
		assertNotNull(token);
		assertTrue(token.length == 1);
		token = generator.generateToken(1024);
		assertNotNull(token);
		assertTrue(token.length == 1024);
	}
}
