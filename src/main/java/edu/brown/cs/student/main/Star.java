package edu.brown.cs.student.main;

public class Star {

  public int id;
  public String name;
  public int x;
  public int y;
  public int z;

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

}
