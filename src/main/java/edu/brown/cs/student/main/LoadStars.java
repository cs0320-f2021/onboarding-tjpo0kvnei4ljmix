package edu.brown.cs.student.main;

import java.util.Arrays;
import java.util.HashSet;

public class LoadStars extends Command {
  private StarFinder sf;

  public LoadStars() {
    super(new HashSet<String>(Arrays.asList("stars")));
    this.sf = new StarFinder();
  }

  public String run(String arg) {
    return sf.loadStars(arg);
  }

}
