package edu.brown.cs.student.main;

import java.util.Arrays;
import java.util.HashSet;

public class LoadStars extends Command {

  public LoadStars() {
    super(new HashSet<String>(Arrays.asList("stars")));
  }

  public String run(String arg) {
    return StarFinder.getInstance().loadStars(arg);
  }

}
