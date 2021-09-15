package edu.brown.cs.student.main;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StarFinderTest {

  @Test
  public void testInstantiation() {
    StarFinder sf = new StarFinder();
    assertTrue(sf.isInvalid());
  }

  @Test
  public void testCSVCorrect() {
    //read a valid CSV
    StarFinder sf = new StarFinder();
    sf.loadStars("data/stars/stardata.csv");
    assertFalse(sf.isInvalid());
  }

  @Test
  public void testBadPath() {
    StarFinder sf = new StarFinder();
    sf.loadStars("data/stars/thisCSVisfake.lol");
    assertTrue(sf.isInvalid());
  }

  @Test
  public void testBadHeader() {
    //test with corrupted CSV first line
    StarFinder sf = new StarFinder();
    sf.loadStars("src/test/java/edu/brown/cs/student/main/no-header.csv");
    assertTrue(sf.isInvalid());
  }

  @Test
  public void testBadBody() {
    StarFinder sf = new StarFinder();
    sf.loadStars("src/test/java/edu/brown/cs/student/main/corrupt-body.csv");
    assertTrue(sf.isInvalid());
  }

  @Test
  public void testExtraComma() {
    StarFinder sf = new StarFinder();
    sf.loadStars("src/test/java/edu/brown/cs/student/main/extra-comma.csv");
    assertTrue(sf.isInvalid());
  }

  @Test
  public void testNonIntCoordinate() {
    StarFinder sf = new StarFinder();
    sf.loadStars("src/test/java/edu/brown/cs/student/main/non-int.csv");
    assertTrue(sf.isInvalid());
  }


}
