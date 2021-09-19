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
      return this.printStarResults(StarFinder.getInstance().namedKnn(k, name));
    } else if (args.length == 4) {
      //formatted as 'naive_neighbors <k> <x> <y> <z>'
      int k;
      double x, y, z;
      try {
        k = Integer.parseInt(args[0]);
        x = Double.parseDouble(args[1]);
        y = Double.parseDouble(args[2]);
        z = Double.parseDouble(args[3]);
      } catch (Exception e) {
        return error("Unable to parse input.");
      }
      return this.printStarResults(StarFinder.getInstance().knn(k, x, y, z));
    } else {
      //formatted wrong
      String output = "ERROR: Please follow one of the following formats:";
      output += " 'naive_neighbors <k> <x> <y> <z>'";
      output += " OR 'naive_neighbors <k> <\"name\">'";
      return output;
    }
  }

  private String printStarResults(ArrayList<Star> stars) {
    if (stars.size() == 0) {
      //This is the return when an error occurs in the knn function
      //Stay silent, an error message has already been printed from knn
      return "";
    }
    String output = "";
    for (Star s : stars) {
      output += String.valueOf(s.getId());
      output += "\n";
      //System.out.print(" -> " + s.getName() + " at x: "
      //    + s.getX() + ", Y: " + s.getY() + ", Z: " + s.getZ())
    }
    return output;
  }

}
