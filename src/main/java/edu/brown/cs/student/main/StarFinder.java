package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class StarFinder {
  private boolean invalid = false; //false until proven otherwise

  /**
   * Constructor for the StarFinder class
   * @param path The path for the CSV to import
   *             the path should be checked for validity before the StarFinder is created
   */
  public StarFinder(String path) throws IOException {
    BufferedReader starReader;
    try {
      starReader = new BufferedReader(new FileReader(path));
    } catch (Exception e) {
      System.out.println("Internal Error: Path Invalid");
      throw e;
    }
    String line = starReader.readLine();
    if (!(line.equals("StarID,ProperName,X,Y,Z"))) {
      //First line of the CSV is in the wrong format, notify the user and mark CSV as invalid.
      System.out.println("ERROR: Invalid CSV, make sure the CSV formatting is correct.");
      this.invalid = true;
      return;
    }
    ArrayList<Star> starData = new ArrayList<Star>();
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



}
