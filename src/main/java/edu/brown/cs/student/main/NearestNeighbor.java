package edu.brown.cs.student.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class NearestNeighbor extends Command {

  public NearestNeighbor() {
    super(new HashSet<String>(Arrays.asList("naive_neighbors")));
  }

  public String run(String arg) {
    String[] args = arg.split(" ");
    if (arg.split("\"").length > 1) {
      //likely formatted as 'naive_neighbors <k> <"name">'
      int k;
      String name;
      try {
        k = Integer.parseInt(args[0]);
        name = arg.split("\"")[1];
      } catch (Exception e) {
        return error("ERROR: Unable to parse input. Make sure the star name "
            + "is in quotes, and that 'k' is a number.");
      }
      this.printStarResults(sf.namedKnn(k, name));
    } else if (arguments.length == 5) {
      int k;
      double x, y, z;
      //formatted as 'naive_neighbors <k> <x> <y> <z>'
      try {
        k = Integer.parseInt(arguments[1]);
        x = Double.parseDouble(arguments[2]);
        y = Double.parseDouble(arguments[3]);
        z = Double.parseDouble(arguments[4]);
      } catch (Exception e) {
        System.out.println("ERROR: Unable to parse input.");
        break;
      }
      this.printStarResults(sf.knn(k, x, y, z));
    } else {
      //formatted wrong
      System.out.print("ERROR: Please follow one of the following formats:");
      System.out.print(" 'naive_neighbors <k> <x> <y> <z>'");
      System.out.println(" OR 'naive_neighbors <k> <\"name\">'");
    }
  }

  private String printStarResults(ArrayList<Star> stars) {
    if (stars.size() == 0) {
      //This is the return when an error occurs in the knn function
      //Stay silent, an error message has already been printed from knn
      return "";
    }
    for (Star s : stars) {
      System.out.print(s.getId());
      //System.out.print(" -> " + s.getName() + " at x: "
      //    + s.getX() + ", Y: " + s.getY() + ", Z: " + s.getZ());
      System.out.println();
    }
  }

}
