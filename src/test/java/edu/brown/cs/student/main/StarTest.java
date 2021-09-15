package edu.brown.cs.student.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import java.util.Random;

public class StarTest {

  @Test
  public void testBasic() {
    Star s = new Star(123, "New Kid", 456, 789, 876);
    assertEquals(123, s.getId(), 0);
    assertEquals(456, s.getX(), 0);
    assertEquals(789, s.getY(), 0);
    assertEquals(876, s.getZ(), 0);
    assertEquals("New Kid", s.getName());
  }

  @Test
  public void testDouble() {
    //Generates random doubles and makes sure the star class correctly stores them
    Random r = new Random();
    int id;
    double x, y, z;
    for (int i = 0; i < 100; i++){
      id = r.nextInt();
      x = r.nextDouble() * 10000;
      y = r.nextDouble() * 10000;
      z = r.nextDouble() * 10000;
      Star s = new Star(id, "Test Star", x, y, z);
      assertEquals(id, s.getId(), 0.00001);
      assertEquals(x, s.getX(), 0.00001);
      assertEquals(y, s.getY(), 0.00001);
      assertEquals(z, s.getZ(), 0.00001);
      assertEquals("Test Star", s.getName());
    }
  }

  @Test
  public void testDist() {
    Random r = new Random();
    Star s = new Star(1, "test", 2, 3, 4);
    double dist;
    for (int i = 0; i < 100; i++){
      dist = r.nextDouble() * 100000;
      s.setDist(dist);
      assertEquals(dist, s.getDist(), 0.00001);
      assertEquals(dist, s.getDist(), 0.00001);
      assertEquals(dist, s.getDist(), 0.00001);
      //do this three times, to make sure data is persistent.
    }
  }

  @Test
  public void testDistUninitialized() {
    Star s = new Star(1, "test", 2, 3, 4);
    assertEquals(-1, s.getDist(), 0);
  }

  @Test
  public void testNegativeDistance() {
    Star s = new Star(9, "Nine", 9, 9, 9);
    assertThrows(Exception.class, () -> s.setDist(-123));
  }
}
