package net.ichmags.backgammon.setup.impl;

import java.util.NoSuchElementException;

import net.ichmags.backgammon.setup.IAvailableDices;
import net.ichmags.backgammon.setup.IDice;
import net.ichmags.backgammon.setup.IDices;

import org.junit.Assert;
import org.junit.Test;

public class AvailableDicesTest {

	@Test
	public void testAllPossible() {
		DiceGenerator.get().load(new int[]{2, 2});
		IDices testDices = new Dices().roll();
		
		boolean check = testDices.isDoubleDices();
		Assert.assertTrue("Double dices", check);
		
		IAvailableDices testAvail = new AvailableDices().initialize(testDices, false);
		for(int pos = 0; pos < 4; pos++) {
			IDice testDiceFromList = testDices.get().get(pos);
			IDice testDiceFromAvail = testAvail.nextElement();
			
			Assert.assertSame("Same dice objects", testDiceFromList, testDiceFromAvail);
		}
	}
	
	@Test
	public void testNotAllPossible() {
		DiceGenerator.get().load(new int[]{2, 2});
		IDices testDices = new Dices().roll();
		
		boolean check = testDices.isDoubleDices();
		Assert.assertTrue("Double dices", check);
		
		testDices.get().get(0).setStatus(IDice.Status.BLOCKED);
		testDices.get().get(2).setStatus(IDice.Status.USED);
		
		IAvailableDices testAvail = new AvailableDices().initialize(testDices, false);
		for(int pos = 1; pos < 4; pos+=2) {
			IDice testDiceFromList = testDices.get().get(pos);
			IDice testDiceFromAvail = testAvail.nextElement();
			
			Assert.assertSame("Same dice objects", testDiceFromList, testDiceFromAvail);
		}
	}
	
	@Test
	public void testForward() {
		DiceGenerator.get().load(new int[]{1, 2});
		IDices testDices = new Dices().roll();
		
		IAvailableDices testAvail = new AvailableDices().initialize(testDices, false);
		
		Assert.assertEquals("Dice Value mismatch", 1, testAvail.nextElement().getValue());
		Assert.assertEquals("Dice Value mismatch", 2, testAvail.nextElement().getValue());
	}
	
	@Test
	public void testReverse() {
		DiceGenerator.get().load(new int[]{1, 2});
		IDices testDices = new Dices().roll();
		
		IAvailableDices testAvail = new AvailableDices().initialize(testDices, true);
		
		Assert.assertEquals("Dice Value mismatch", 2, testAvail.nextElement().getValue());
		Assert.assertEquals("Dice Value mismatch", 1, testAvail.nextElement().getValue());
	}
	
	@Test
	public void testUndoLast() {
		DiceGenerator.get().load(new int[]{1, 2});
		IDices testDices = new Dices().roll();
		
		IAvailableDices testAvail = new AvailableDices().initialize(testDices, false);
		
		Assert.assertEquals("Dice Value mismatch", 1, testAvail.nextElement().getValue());
		Assert.assertEquals("Dice Value mismatch", 2, testAvail.nextElement().getValue());
		
		testAvail.reactivateElement();
		Assert.assertEquals("Dice Value mismatch", 2, testAvail.nextElement().getValue());
	}
	
	@Test
	public void testUndoAll() {
		DiceGenerator.get().load(new int[]{1, 2});
		IDices testDices = new Dices().roll();
		
		IAvailableDices testAvail = new AvailableDices().initialize(testDices, false);
		
		Assert.assertEquals("Dice Value mismatch", 1, testAvail.nextElement().getValue());
		Assert.assertEquals("Dice Value mismatch", 2, testAvail.nextElement().getValue());
		
		testAvail.reactivateElement();
		testAvail.reactivateElement();
		
		Assert.assertEquals("Dice Value mismatch", 1, testAvail.nextElement().getValue());
		Assert.assertEquals("Dice Value mismatch", 2, testAvail.nextElement().getValue());
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testExceptionNoNext() {
		DiceGenerator.get().load(new int[]{1, 2});
		IDices testDices = new Dices().roll();
		
		IAvailableDices testAvail = new AvailableDices().initialize(testDices, false);
		
		Assert.assertEquals("Dice Value mismatch", 1, testAvail.nextElement().getValue());
		Assert.assertEquals("Dice Value mismatch", 2, testAvail.nextElement().getValue());
		
		testAvail.nextElement().getValue(); // Exception
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testExceptionNoUndo() {
		DiceGenerator.get().load(new int[]{1, 2});
		IDices testDices = new Dices().roll();
		
		IAvailableDices testAvail = new AvailableDices().initialize(testDices, false);
		
		Assert.assertEquals("Dice Value mismatch", 1, testAvail.nextElement().getValue());
		Assert.assertEquals("Dice Value mismatch", 2, testAvail.nextElement().getValue());
		
		testAvail.reactivateElement();
		testAvail.reactivateElement();
		testAvail.reactivateElement(); // Exception
	}
}
