package edu.brown.cs.student.main;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StarFinderTest {

  StarFinder gsf = new StarFinder(); // global starfinder used in some tests, tests reloading data

  @Test
  public void testInstantiation() {
    StarFinder sf = new StarFinder();
    assertTrue(sf.isInvalid());
  }

  @Test
  public void testCSVCorrect() {
    //read a valid CSV
    gsf.loadStars("data/stars/stardata.csv");
    assertFalse(gsf.isInvalid());
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
    gsf.loadStars("src/test/java/edu/brown/cs/student/main/extra-comma.csv");
    assertTrue(gsf.isInvalid());
  }

  @Test
  public void testNonIntCoordinate() {
    gsf.loadStars("src/test/java/edu/brown/cs/student/main/non-int.csv");
    assertTrue(gsf.isInvalid());
  }

  //knn tests

  @Test
  public void knnCore() {
    //Core functionality of knn
    StarFinder sf = new StarFinder();
    sf.loadStars("data/stars/ten-star.csv");
    ArrayList<Star> result = sf.knn(10, 0,0, 0);
    ArrayList<Star> correct = new ArrayList<>(); //hardcoded correct answer
    correct.add(new Star(0, "Sol", 0, 0, 0));
    correct.add(new Star(70667,"Proxima Centauri",-0.47175,-0.36132,-1.15037));
    correct.add(new Star(71454,"Rigel Kentaurus B",-0.50359,-0.42128,-1.1767));
    correct.add(new Star(71457,"Rigel Kentaurus A",-0.50362,-0.42139,-1.17665));
    correct.add(new Star(87666,"Barnard's Star",-0.01729,-1.81533,0.14824));
    correct.add(new Star(118721,"",-2.28262,0.64697,0.29354));
    correct.add(new Star(3759,"96 G. Psc",7.26388,1.55643,0.68697));
    correct.add(new Star(2,"",43.04329,0.00285,-15.24144));
    correct.add(new Star(1,"",282.43485,0.00449,5.36884));
    correct.add(new Star(3,"",277.11358,0.02422,223.27753));
    for(int i = 0; i < result.size(); i++) {
      assertEquals(result.get(i).getX(), correct.get(i).getX(), 0);
      assertEquals(result.get(i).getY(), correct.get(i).getY(), 0);
      assertEquals(result.get(i).getZ(), correct.get(i).getZ(), 0);
      //Don't feel like writing a .equals, and if X/Y/Z are equal it's going to be the same star
    }
  }

  @Test
  public void testTooLargeK() {
    //Load 10 stars, ask for 11 nearest neighbors
    StarFinder sf = new StarFinder();
    sf.loadStars("data/stars/ten-star.csv");
    ArrayList<Star> result = sf.knn(11, 0,0, 0);
    assertTrue(result.size() == 0);
    assertFalse(sf.isInvalid()); //results are not invalidated by bad knn query
  }

  @Test
  public void testInvalidData() {
    //try calling knn before loading data
    StarFinder sf = new StarFinder();
    ArrayList<Star> result = sf.knn(3, 1, 2, 3);
    assertTrue(result.size() == 0);
  }

  @Test
  public void testBadData() {
    //try calling knn after loading bad data
    gsf.loadStars("src/test/java/edu/brown/cs/student/main/corrupt-body.csv");
    ArrayList<Star> result = gsf.knn(3, 1, 2, 3);
    assertTrue(result.size() == 0);
  }

  @Test
  public void testNonzeroCenter() {
    gsf.loadStars("data/stars/ten-star.csv");
    ArrayList<Star> result = gsf.knn(3, 200, 70, -20);
    ArrayList<Star> correct = new ArrayList<>(); //hardcoded correct answer
    correct.add(new Star(1,"",282.43485,0.00449,5.36884));
    correct.add(new Star(2,"",43.04329,0.00285,-15.24144));
    correct.add(new Star(3759,"96 G. Psc",7.26388,1.55643,0.68697));
    for(int i = 0; i < result.size(); i++) {
      assertEquals(result.get(i).getX(), correct.get(i).getX(), 0);
      assertEquals(result.get(i).getY(), correct.get(i).getY(), 0);
      assertEquals(result.get(i).getZ(), correct.get(i).getZ(), 0);
      //Don't feel like writing a .equals, and if X/Y/Z are equal it's going to be the same star
    }
  }

  @Test
  public void testSmallList() {
    StarFinder sf = new StarFinder();
    sf.loadStars("data/stars/ten-star.csv");
    ArrayList<Star> result = sf.knn(3, 0,0, 0);
    ArrayList<Star> correct = new ArrayList<>(); //hardcoded correct answer
    correct.add(new Star(0, "Sol", 0, 0, 0));
    correct.add(new Star(70667,"Proxima Centauri",-0.47175,-0.36132,-1.15037));
    correct.add(new Star(71454,"Rigel Kentaurus B",-0.50359,-0.42128,-1.1767));
    for(int i = 0; i < result.size(); i++) {
      assertEquals(result.get(i).getX(), correct.get(i).getX(), 0);
      assertEquals(result.get(i).getY(), correct.get(i).getY(), 0);
      assertEquals(result.get(i).getZ(), correct.get(i).getZ(), 0);
      //Don't feel like writing a .equals, and if X/Y/Z are equal it's going to be the same star
      //I know this is copy/pasted from above. If I was smarter, I would've written a function.
    }

  }

  @Test
  public void testZeroK() {
    gsf.loadStars("data/stars/ten-star.csv");
    ArrayList<Star> result = gsf.knn(0, 0,0, 0);
    assertTrue(result.size() == 0);
  }

  @Test
  public void testNegativeID() {
    gsf.loadStars("src/test/java/edu/brown/cs/student/main/negative-id.csv");
    ArrayList<Star> result = gsf.knn(10, 123, 456, 789);
    assertTrue(result.size() == 10);
    for (Star s : result) {
      assertTrue(s.getId() < 0);
    }
  }

  @Test
  public void testTies() {
    gsf.loadStars("src/test/java/edu/brown/cs/student/main/tied-stars.csv");
    ArrayList<Star> result = gsf.knn(7, 50, -50, 50);
    assertTrue(result.size() == 7);
    //Test stars which are always in the same place
    assertTrue(result.get(0).getId() == 1);
    assertTrue(result.get(1).getId() == 0);
    assertTrue(result.get(6).getId() == 999);
    ArrayList<Integer> tiedIDs = new ArrayList<>(Arrays.asList(3, 8, 99, 238));
    for (int i = 0; i < 1000; i++) {
      result = gsf.knn(3, 50, -50, 50);
      //make sure that all stars that are tied sometimes show up in the 3rd spot (index 2)
      //if the ID still exists in tiedIDs, remove it.
      tiedIDs.remove(Integer.valueOf(result.get(2).getId()));
      if (tiedIDs.size() == 0) {
        //all stars have shown up, we're done.
        break;
      }
    }
    if (tiedIDs.size() != 0){
      //there were some stars that didn't appear!
      fail("Ties are not randomized");
    }
  }

  //TODO: Test named_knn


}
