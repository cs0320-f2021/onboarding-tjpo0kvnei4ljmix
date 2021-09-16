package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class StarFinder {
  private boolean invalid = true; //true until stars are loaded
  private ArrayList<Star> starData;

  /**
   * Constructor for the StarFinder class.
   */
  public StarFinder() {

  }

  public boolean isInvalid() {
    return this.invalid;
  }

  /**
   * Loads CSV file into StarFinder.
   *
   * @param path path to CSV file
   * @throws IOException
   */

  public void loadStars(String path) {
    this.invalid = false; //valid until proven otherwise by a CSV read error
    BufferedReader starReader;
    String line;
    try {
      starReader = new BufferedReader(new FileReader(path));
      line = starReader.readLine();
    } catch (Exception e) {
      System.out.println("ERROR: Could not find the file specified. Check for spelling errors.");
      this.invalid = true;
      return;
    }
    if (!(line.equals("StarID,ProperName,X,Y,Z"))) {
      //First line of the CSV is in the wrong format, notify the user and mark CSV as invalid.
      System.out.println("ERROR: Invalid CSV, make sure the CSV formatting is correct.");
      this.invalid = true;
      return;
    }
    starData = new ArrayList<Star>();
    while (true) {
      try {
        line = starReader.readLine();
        if (line == null) {
          break;
        }
      } catch (IOException e) {
        System.out.println("ERROR: There was an issue reading the file, check for corruption");
      }
      String[] rawStarData = line.split(",");
      if (rawStarData.length != 5) {
        System.out.println("ERROR: The CSV has an incorrect number of fields and/or is broken!");
        this.invalid = true;
        return;
      }
      //Convert id and x/y/z int
      int id;
      double x, y, z;
      try {
        id = Integer.parseInt(rawStarData[0]);
        x = Double.parseDouble(rawStarData[2]);
        y = Double.parseDouble(rawStarData[3]);
        z = Double.parseDouble(rawStarData[4]);
      } catch (Exception e) {
        System.out.println("ERROR: Could not parse CSV - Check for corruption");
        this.invalid = true;
        return;
      }
      String properName = rawStarData[1];
      starData.add(new Star(id, properName, x, y, z));
    }
    System.out.println("Read " + this.starData.size() + " stars from " + path);
    //System.out.println("The first star is named " + starData.get(0).getName());
  }

  /**
   *
   * @param k number of nearest neighbors to find
   * @param x x-coordinate to center search around
   * @param y y-coordinate to center search around
   * @param z z-coordinate to center search around
   * @return ArrayList of the k closest stars, sorted from closest to furthest.
   *
   * If there is a tie, picks randomly between the stars that are tied.
   *
   */
  @SuppressWarnings({"checkstyle:LocalFinalVariableName", "checkstyle:MagicNumber"})
  public ArrayList<Star> knn(int k, double x, double y, double z) {
    if (this.invalid) {
      System.out.println("ERROR: Star CSV has not been loaded or is invalid");
      return new ArrayList<Star>();
    }
    if (k > starData.size()) {
      //Searching for too many stars
      System.out.println("ERROR: Number of stars requested is more than number available");
      return new ArrayList<Star>();
    }
    //Make a copy of the starData, fill in distances, then sort by distance.
    ArrayList<Star> sortedStarData = new ArrayList<Star>(this.starData);
    for (Star s : sortedStarData) {
      double dist = Math.pow(x - s.getX(), 2)
          + Math.pow(y - s.getY(), 2)
          + Math.pow(z - s.getZ(), 2);
      //No need to square-root and find the 'true' distance, sorting will still work the same.
      s.setDist(dist);
    }
    //Now, all the stars have distances. Sort by distance.
    sortedStarData.sort((o1, o2) -> {
      return (int) Math.floor(o1.getDist() - o2.getDist()); //Math.floor because must return int
    });

    ArrayList<Star> results = new ArrayList<>(sortedStarData.subList(0, k));

    //Test for ties, and randomize the returned results.
    if (results.size() < 2 || results.size() == sortedStarData.size()) {
      //can't have a tie with less than 2 elements, can't have a tie if everything is returned
      return results;
    }
    double cutoffDist = results.get(results.size() - 1).getDist(); //distance of last star
    assert results.get(results.size() - 1) == sortedStarData.get(k - 1); //should be identical
    if (sortedStarData.get(k - 1).getDist() == sortedStarData.get(k).getDist()) {
      //we have a tie, because last elem of results has same distance as an excluded element (idx k)
      int firstIdx = -1;
      int lastIdx = -1;
      for (int i = 0; i < sortedStarData.size(); i++) {
        if (firstIdx == -1 && sortedStarData.get(i).getDist() == cutoffDist) {
          //This is the first index where things are tied
          firstIdx = i;
        }
        if (sortedStarData.get(i).getDist() > cutoffDist) {
          //This is greater than the cuttoff distance
          //therefore the previous element was the last tied element
          lastIdx = i; //setting to i (not i - 1) because subList doesn't include the bound
          break;
        }
      }
      results = new ArrayList<>(sortedStarData.subList(0, firstIdx));
      ArrayList<Star> randomizedTies = new ArrayList<>(sortedStarData.subList(firstIdx, lastIdx));
      Collections.shuffle(randomizedTies);
      results.addAll(randomizedTies.subList(0, k - results.size()));
      assert results.size() == k;
    }
    return results;
  }

  /**
   *
   * @param k number of neighbors to find
   * @param name name of the star to be searched around
   * @return Arraylist of the k closest stars, sorted from closest to furthest
   */
  public ArrayList<Star> namedKnn(int k, String name) {
    if (this.invalid) {
      System.out.println("ERROR: Star CSV has not been loaded or is invalid");
      return new ArrayList<Star>();
    }
    if (name.isEmpty()) {
      System.out.println("Star name cannot be empty");
      return new ArrayList<Star>();
    }

    //Find the x/y/z coordinates of the star with starName, then pass that info to knn

    for (Star s : this.starData) {
      if (s.getName().equals(name)) {
        //Found it!
        return this.knn(k, s.getX(), s.getY(), s.getZ());
      }
    }
    //If we exit the loop without returning, this means none of the stars matched the name
    System.out.println("ERROR: Name did not match any known star");
    return new ArrayList<Star>();
  }



}
