package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class StarFinder {
  private boolean invalid = true; //true until stars are loaded
  private ArrayList<Star> starData;

  /**
   * Constructor for the StarFinder class.
   */
  public StarFinder() {

  }

  /**
   * Loads CSV file into StarFinder.
   *
   * @param path path to CSV file
   * @throws IOException
   */

  public void loadStars(String path) throws IOException {
    this.invalid = false; //valid until proven otherwise by a CSV read error
    BufferedReader starReader;
    try {
      starReader = new BufferedReader(new FileReader(path));
    } catch (Exception e) {
      System.out.println("ERROR: Could not find the file specified. Check for spelling errors.");
      this.invalid = true;
      return;
    }
    String line = starReader.readLine();
    if (!(line.equals("StarID,ProperName,X,Y,Z"))) {
      //First line of the CSV is in the wrong format, notify the user and mark CSV as invalid.
      System.out.println("ERROR: Invalid CSV, make sure the CSV formatting is correct.");
      this.invalid = true;
      return;
    }
    starData = new ArrayList<Star>();
    while ((line = starReader.readLine()) != null) {
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
    System.out.println("Succesfully imported " + starData.size() + " stars!");
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
  public ArrayList<Star> knn(int k, double x, double y, double z) {
    if (this.invalid) {
      System.out.println("ERROR: Star CSV has not been loaded or is invalid");
      return new ArrayList<Star>();
    }
    if (k > starData.size()) {
      //Searching for too many stars
      System.out.println("ERROR: Number of stars requested is more than number available");
    }
    //Make a copy of the starData, fill in distances, then sort by distance.
    ArrayList<Star> sortedStarData = new ArrayList<Star>(this.starData);
    for (Star s : sortedStarData) {
      double dist = Math.pow(Math.abs(x - s.getX()), 2)
          + Math.pow(Math.abs(y - s.getY()), 2)
          + Math.pow(Math.abs(z - s.getZ()), 2);
      //No need to square-root and find the 'true' distance, sorting will still work the same.
      s.setDist(dist);
    }
    //Now, all the stars have distances. Sort by distance.
    sortedStarData.sort(new Comparator<Star>() {
      @Override
      public int compare(Star o1, Star o2) {
        try {
          return (int) Math.floor(o1.getDist() - o2.getDist()); //Math.floor because must return int
        } catch (Exception e) {
          //This should never happen, since we just set the distances for all stars
          System.out.println("ERROR: Sorting error "
              + "The programmer of this app messed up, ask for a refund.");
          return 0; //probably bad practice, but this should never happen anyways.
        }
      }
    });
    return new ArrayList<Star>(sortedStarData.subList(0, k));
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
