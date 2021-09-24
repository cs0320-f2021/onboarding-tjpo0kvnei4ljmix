package edu.brown.cs.student.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Add extends Command {

  public Add() {
    super(new HashSet<String>(Arrays.asList("add")));
  }

  public String run(String arg) {
    //Make sure there are only two numbers to add
    String[] args = arg.split(" ");
    if (args.length != 2) { //Artificial restriction, this could be removed without issues.
      return error("Incorrect number of arguments!");
    }
    //Now, make sure that the other two arguments are actually numbers
    ArrayList<Double> nums = new ArrayList<>();
    for (String s : args) {
      //Parse the numbers into doubles
      double num;
      try {
        num = Double.parseDouble(s);
        nums.add(num);
      } catch (Exception e) {
        return error("Unable to convert \"" + s + "\" to a number");
      }
    }
    double result = 0;
    for (double n : nums) {
      result = result + n;
    }
    return String.valueOf(result);
  }

  public String help() {
    return "'add <num1> <num2>' -> Returns the result of adding these two numbers";
  }

}
