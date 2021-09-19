package edu.brown.cs.student.main;

import java.util.Set;

/**
 * Abstract class which every REPL command must extend
 * the run method is called when the command is invoked
 * after command invocation, the isActive method is polled
 * if isActive, then subsequent user input will be routed to run.
 * (Therefore, it may be useful to implement some state-machine-like logic)
 */

public abstract class Command {
  private boolean active = false;
  private final Set<String> commandNames;

  public Command(Set<String> commandNames) {
    this.commandNames = commandNames;
  }

  public Set<String> getNames() {
    return this.commandNames;
  }

  public boolean isActive() {
    return this.active;
  }

  public void deactivate() {
    this.active = false;
  }

  public String error(String errMsg) {
    return "ERROR: " + errMsg;
  }

  /**
   * @param argString the entire user-inputted string except for the command
   *
   * @return output to print to the user
   */
  abstract String run(String argString);

  public String help() {
    return "This command has no help documentation";
  }
}
