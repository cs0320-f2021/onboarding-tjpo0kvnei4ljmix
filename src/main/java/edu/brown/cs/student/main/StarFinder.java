package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class StarFinder {
  private boolean invalid = false; //false until proven otherwise
  private ArrayList<Star> starData;
  private String starName;

  /**
   * Constructor for the StarFinder class.
   * @param path The path for the CSV to import
   */
  public StarFinder(String path) throws IOException {
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
      int id, x, y, z = 0;
      try {
        id = Integer.parseInt(rawStarData[0]);
        x = Integer.parseInt(rawStarData[2]);
        y = Integer.parseInt(rawStarData[3]);
        z = Integer.parseInt(rawStarData[4]);
      } catch (Exception e) {
        System.out.println("ERROR: Could not parse CSV - Check for corruption");
        this.invalid = true;
        return;
      }
      String properName = rawStarData[1];
      starData.add(new Star(id, properName, x, y, z));
    }

    //debug
    System.out.println("Succesfully imported " + starData.size() + " stars!");
    System.out.println("The first star is named " + starData.get(0).name);

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
  public ArrayList<Star> knn(int k, int x, int y, int z) {
    //ArrayList<Pair<Integer, Star>> sortedStars = new ArrayList<Pair<int, Star>>();
    return this.starData;
  }

  /**
   *
   * @param k number of neighbors to find
   * @param name name of the star to be searched around
   * @return Arraylist of the k closest stars, sorted from closest to furthest
   */
  public ArrayList<Star> namedKnn(int k, String name) {
    //Find the x/y/z coordinates of the star with starName, then pass that info to knn
    //lightly inspired by https://www.geeksforgeeks.org/arraylist-iterator-method-in-java-with-examples/
    Iterator<Star> iter = this.starData.iterator();
    Star tmp;
    while (iter.hasNext()) {
      tmp = iter.next();
      if (tmp.name.equals(name)) {
        //Found it!
        return this.knn(k, tmp.x, tmp.y, tmp.z);
      }
    }
    //If we exit the while loop without returning, this means none of the stars matched the name
    System.out.println("ERROR: Name did not match any known star");
    return new ArrayList<Star>();
  }



}
