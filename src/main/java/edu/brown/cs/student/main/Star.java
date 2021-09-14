package edu.brown.cs.student.main;

public class Star {

  public final int id;
  public final String name;
  public final int x;
  public final int y;
  public final int z;
  private int dist = -1;

  /**
   * Constructor for the Star class, which simply holds all info about a star.
   * @param id the id of the star
   * @param name the name of the star
   * @param x the x coordinate of the star
   * @param y the y coordinate of the star
   * @param z the z coordinate of the star
   */
  public Star(int id, String name, int x, int y, int z) {
    this.id = id;
    this.name = name;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public int getDist() throws Exception {
    if (this.dist == -1) {
      throw new Exception("Star distance was accessed before being set!");
    }
    return this.dist;
  }

  public void setDist(int d) {
    this.dist = d;
  }

}
