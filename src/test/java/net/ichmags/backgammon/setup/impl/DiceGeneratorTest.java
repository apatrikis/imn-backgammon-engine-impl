package net.ichmags.backgammon.setup.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

public class DiceGeneratorTest {

	@Test
	public void testParseNumericValues()
	throws IOException {
		Reader r = new StringReader("8,4");

		int readErrors=0;
		int charValue;
		while((charValue = r.read()) != -1) { // read() throws IOException
			int val = Character.getNumericValue(charValue);
			if(val < 0) { readErrors++; };
		}
		Assert.assertEquals("One non numerical value", 1, readErrors);
	}
	
	@Test
	public void testLoadStream() {
		ByteArrayInputStream inStream = new ByteArrayInputStream("0,7,5".getBytes());
		DiceGenerator.get().load(inStream);
		int value = DiceGenerator.get().roll();
		Assert.assertEquals("Ignored invalid values", 5, value);
	}
	
	@Test
	public void testLoadValuesArray() {
		DiceGenerator.get().load(new int[]{-5,0,7,5});
		int value = DiceGenerator.get().roll();
		Assert.assertEquals("Ignored invalid values", 5, value);
	}
}
