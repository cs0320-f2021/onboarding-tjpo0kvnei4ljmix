package edu.brown.cs.student.main;

public class Star {

  private final int id;
  private final String name;
  private final double x;
  private final double y;
  private final double z;
  private double dist = -1;

  /**
   * Constructor for the Star class, which simply holds all info about a star.
   * @param id the id of the star
   * @param name the name of the star
   * @param x the x coordinate of the star
   * @param y the y coordinate of the star
   * @param z the z coordinate of the star
   */
  public Star(int id, String name, double x, double y, double z) {
    this.id = id;
    this.name = name;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public int getId() {
    return this.id;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

  public double getZ() {
    return this.z;
  }

  public String getName() {
    return this.name;
  }

  public double getDist(){
    if (this.dist == -1) {
      //Distance was never set, send out a warning!
      System.out.println("ERROR: Distance on star " + this.name + " is not set!");
    }
    return this.dist;
  }

  public void setDist(double d) {
    this.dist = d;
  }

}
