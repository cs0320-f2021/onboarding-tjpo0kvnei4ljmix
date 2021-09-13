package edu.brown.cs.student.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathBotTest {

  @Test
  public void testAddition() {
    MathBot matherator9000 = new MathBot();
    double output = matherator9000.add(10.5, 3);
    assertEquals(13.5, output, 0.01);
  }

  @Test
  public void testLargerNumbers() {
    MathBot matherator9001 = new MathBot();
    double output = matherator9001.add(100000, 200303);
    assertEquals(300303, output, 0.01);
  }

  @Test
  public void testSubtraction() {
    MathBot matherator9002 = new MathBot();
    double output = matherator9002.subtract(18, 17);
    assertEquals(1, output, 0.01);
  }

  @Test
  public void testMultipleInstances() {
    MathBot mb1 = new MathBot();
    MathBot mb2 = new MathBot();
    MathBot mb3 = new MathBot();
    double output = mb1.add(mb2.subtract(6, 2), mb2.subtract(132, 122));
    assertEquals(output, 14, 0.01);
  }

  @Test
  public void testFractions() {
    MathBot mb1 = new MathBot();
    double output = mb1.subtract(3.14159, 2.56);
    assertEquals(output, 0.58159, 0.01);
  }

  @Test
  public void testMutlipleCalculations() {
    MathBot mb1 = new MathBot();
    double output = 0;
    for (int i = 0; i < 100; i++){
      output = mb1.add(output, 13.5);
    }
    assertEquals(output, 1350, 0.01);
  }

  @Test
  public void testAddSub() {
    MathBot mb1 = new MathBot();
    double output = 0;
    for (int i = 0; i < 100; i++){
      output = mb1.add(output, 16.258);
      output = mb1.subtract(output, 1.258);
    }
    assertEquals(output, 1500, 0.01);
  }
}
