package edu.brown.cs.student.main;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StarFinderTest {

  StarFinder gsf = new StarFinder(); // global starfinder used in some tests, tests reloading data

  /**
   * @param l1 first list to compare
   * @param l2 second list to compare
   * @return true if both arraylists contain the same stars in the same order, false otherwise
   */

  public boolean starsEqual(ArrayList<Star> l1, ArrayList<Star> l2) {
    if (l1.size() != l2.size()) {
      return false;
    }
    for (int i = 0; i < l2.size(); i++) {
      Star s1 = l1.get(i);
      Star s2 = l2.get(i);
      if (!(s1.getName().equals(s2.getName())
          && s1.getId() == s2.getId()
          && s1.getX() == s2.getX()
          && s1.getY() == s2.getY()
          && s1.getZ() == s2.getZ())) {
        //If any of these are false, then l1 and l2 do not contain the same stars
        return false;
      }
    }
    //getting here means we checked all the stars, none returned false
    return true;
  }

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
  public void testKnnCore() {
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
    assertTrue(starsEqual(result, correct));
  }

  @Test
  public void testTooLargeK() {
    //Load 10 stars, ask for 11 nearest neighbors
    StarFinder sf = new StarFinder();
    sf.loadStars("data/stars/ten-star.csv");
    ArrayList<Star> result = sf.knn(11, 0,0, 0);
    assertEquals(0, result.size());
    assertFalse(sf.isInvalid()); //results are not invalidated by bad knn query
  }

  @Test
  public void testKnnInvalidData() {
    //try calling knn before loading data
    StarFinder sf = new StarFinder();
    ArrayList<Star> result = sf.knn(3, 1, 2, 3);
    assertEquals(0, result.size());
  }

  @Test
  public void testBadData() {
    //try calling knn after loading bad data
    gsf.loadStars("src/test/java/edu/brown/cs/student/main/corrupt-body.csv");
    ArrayList<Star> result = gsf.knn(3, 1, 2, 3);
    assertEquals(0, result.size());
  }

  @Test
  public void testNonzeroCenter() {
    gsf.loadStars("data/stars/ten-star.csv");
    ArrayList<Star> result = gsf.knn(3, 200, 70, -20);
    ArrayList<Star> correct = new ArrayList<>(); //hardcoded correct answer
    correct.add(new Star(1,"",282.43485,0.00449,5.36884));
    correct.add(new Star(2,"",43.04329,0.00285,-15.24144));
    correct.add(new Star(3759,"96 G. Psc",7.26388,1.55643,0.68697));
    assertTrue(starsEqual(result, correct));
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
    assertTrue(starsEqual(result, correct));

  }

  @Test
  public void testZeroK() {
    gsf.loadStars("data/stars/ten-star.csv");
    ArrayList<Star> result = gsf.knn(0, 0,0, 0);
    assertEquals(0, result.size());
  }

  @Test
  public void testNegativeID() {
    gsf.loadStars("src/test/java/edu/brown/cs/student/main/negative-id.csv");
    ArrayList<Star> result = gsf.knn(10, 123, 456, 789);
    assertEquals(10, result.size());
    for (Star s : result) {
      assertTrue(s.getId() < 0);
    }
  }

  @Test
  public void testTies() {
    gsf.loadStars("src/test/java/edu/brown/cs/student/main/tied-stars.csv");
    ArrayList<Star> result = gsf.knn(7, 50, -50, 50);
    assertEquals(7, result.size());
    //Test stars which are always in the same place
    assertEquals(1, result.get(0).getId());
    assertEquals(0, result.get(1).getId());
    assertEquals(999, result.get(6).getId());
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

  //namedKnn tests

  @Test
  public void testNamedKnnCore() {
    StarFinder sf = new StarFinder();
    sf.loadStars("data/stars/stardata.csv");
    ArrayList<Star> stars = new ArrayList<>();
    stars.add(new Star(58708,"Noreen_8",-716.19999,-14.46457,573.17391));
    stars.add(new Star(12,"Kaleigh",199.36567,0.14237,-144.63632));
    stars.add(new Star(115,"Marchello",96.23159,0.6258,-188.24457));
    stars.add(new Star(119604,"Jakobe_17",19.18416,-0.49694,-17.00538));
    stars.add(new Star(65648,"Anneliese_9",-175.81072,-72.76531,130.89025));
    stars.add(new Star(3,"Mortimer",277.11358,0.02422,223.27753));
    stars.add(new Star(524,"Candyce",87.85805,2.42944,76.13068));
    for (Star s : stars) {
      ArrayList<Star> namedResult = sf.namedKnn(100, s.getName());
      ArrayList<Star> coodinateResult = sf.knn(100, s.getX(), s.getY(), s.getZ());
      assertTrue(starsEqual(namedResult, coodinateResult));
    }
  }

  @Test
  public void testSpaces() {
    gsf.loadStars("data/stars/ten-star.csv");
    assertTrue(starsEqual(gsf.knn(8, -0.50359,-0.42128,-1.1767),
                          gsf.namedKnn(8, "Rigel Kentaurus B")));
  }

  @Test
  public void testBlank() {
    gsf.loadStars("data/stars/ten-star.csv");
    assertEquals(0, gsf.namedKnn(4, "").size());
  }

  @Test
  public void testBadName() {
    gsf.loadStars("data/stars/ten-star.csv");
    assertEquals(0, gsf.namedKnn(4, "MadeupName").size());
  }

  @Test
  public void testNamedKnnInvalidData() {
    gsf.loadStars("src/test/java/edu/brown/cs/student/main/corrupt-body.csv");
    assertEquals(0, gsf.namedKnn(4, "Sol").size());
  }

  @Test
  public void testCaseSensitive() {
    gsf.loadStars("data/stars/stardata.csv");
    assertEquals(123, gsf.namedKnn(123, "Rhona").size());
    assertEquals(0, gsf.namedKnn(123, "rHoNA").size());
  }

}
