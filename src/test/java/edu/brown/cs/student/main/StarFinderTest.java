package edu.brown.cs.student.main;

import org.junit.Test;

public class StarFinderTest {

  @Test
  public void testInstantiation() {
    StarFinder sf = new StarFinder();
  }

  @Test
  public void testCSVCorrect() {
    //read a valid CSV
    StarFinder sf = new StarFinder();
    sf.loadStars("data/stars/stardata.csv");
  }
}
