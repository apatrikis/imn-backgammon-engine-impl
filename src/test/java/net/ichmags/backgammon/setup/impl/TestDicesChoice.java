package net.ichmags.backgammon.setup.impl;

import net.ichmags.backgammon.setup.IDice;
import net.ichmags.backgammon.setup.IDices;

import org.junit.Assert;
import org.junit.Test;

public class TestDicesChoice {

	@Test
	public void testAddIdentical() {
		DiceGenerator.get().load(new int[]{1,2,2,1});
		
		IDices first = new Dices().roll();
		Assert.assertEquals("Expected dice value", 1, first.get(0).getValue());
		IDices second = new Dices().roll();
		Assert.assertEquals("Expected dice value", 2, second.get(0).getValue());
		
		first.get(0).setStatus(IDice.Status.USED);
		second.get(1).setStatus(IDice.Status.USED);
		
		DicesChoice dicesChoice = new DicesChoice();
		dicesChoice.addOption(first);
		dicesChoice.addOption(second);
		
		Assert.assertTrue("Playable dices equal", dicesChoice.isSingleOption());
	}
	
	@Test
	public void testAddDifferent() {
		DiceGenerator.get().load(new int[]{1,2,2,1});
		
		IDices first = new Dices().roll();
		Assert.assertEquals("Expected dice value", 1, first.get(0).getValue());
		IDices second = new Dices().roll();
		Assert.assertEquals("Expected dice value", 2, second.get(0).getValue());
		
		first.get(0).setStatus(IDice.Status.USED);
		second.get(0).setStatus(IDice.Status.USED);
		
		DicesChoice dicesChoice = new DicesChoice();
		dicesChoice.addOption(first);
		dicesChoice.addOption(second);
		
		Assert.assertFalse("Playable dices not equal", dicesChoice.isSingleOption());
	}
	
	@Test
	public void testInvert() {
		DiceGenerator.get().load(new int[]{1,2});
		
		IDices first = new Dices().roll();
		first.get(0).setStatus(IDice.Status.USED);
		
		DicesChoice dicesChoice = new DicesChoice();
		dicesChoice.addOption(first);
		
		Assert.assertTrue("Dice used", first.get(0).getStatus().equals(IDice.Status.USED));
		Assert.assertTrue("Dice unused", first.get(1).getStatus().equals(IDice.Status.AVAILABLE));
		
		boolean converted = dicesChoice.convertStatusFromTestToPlay();
		Assert.assertTrue("Conversion done", converted);
		
		Assert.assertTrue("Dice unused", first.get(0).getStatus().equals(IDice.Status.AVAILABLE));
		Assert.assertTrue("Dice used", first.get(1).getStatus().equals(IDice.Status.BLOCKED));
		
		converted = dicesChoice.convertStatusFromTestToPlay();
		Assert.assertFalse("Conversion not done", converted);
	}
}
