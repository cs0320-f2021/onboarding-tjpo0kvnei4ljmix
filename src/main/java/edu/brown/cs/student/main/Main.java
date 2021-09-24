package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  // use port 4567 by default when running server
  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // set up parsing of command line flags
    OptionParser parser = new OptionParser();

    // "./run --gui" will start a web server
    parser.accepts("gui");

    // use "--port <n>" to specify what port on which the server runs
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    ArrayList<Command> commands = new ArrayList<>(Arrays.asList(
        new Add(), new Subtract(), new LoadStars(), new NearestNeighbor()
    ));
    Command previousCommand = null;
    boolean help = false;

    //Sanity check, make sure no name is repeated twice, and make sure isActive is false.
    HashSet<String> allNames = new HashSet<>();
    for (Command c : commands) {
      if (c.isActive()) {
        System.out.println("ERROR: " + c + " is active when initializing!");
      }
      for (String s : c.getNames()) {
        if (!allNames.add(s)) {
          //returns false if the string already exists in the set
          System.out.println("ERROR: Duplicate name '" + s + "' detected!");
        }
      }
    }

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String input;
    while (true) {
      try {
        input = br.readLine(); //get the next line of user input
        if (input == null) {
          break;
        }
      } catch (IOException e) {
        System.out.println("ERROR: There was an issue parsing your input");
        continue;
      }
      input = input.trim(); //Trim input, getting rid of extra spaces
      String[] arguments = input.split(" ", 2); //Split into two pieces
      String commandName = arguments[0].toLowerCase(); //REPL is case-insensitive
      String commandArgs;
      try {
        commandArgs = arguments[1];
      } catch (ArrayIndexOutOfBoundsException e) {
        //There is no index 1, no arguments
        commandArgs = "";
      }

      if (commandName.equals("quit")) {
        //override everything else, if quit is entered deactivate the previous task.
        previousCommand.deactivate();
        System.out.println("---");
        continue;
      }

      //If there is a currently active command, run that one.
      if (previousCommand != null && previousCommand.isActive()) {
        String output = previousCommand.run(commandArgs);
        System.out.print(output);
        continue;
      }

      //if the user types help, they're looking for documentation
      //switch up the arguments so that help is called
      if (arguments[0].equals("help")) {
        help = true;
        commandName = commandArgs; //In this case, the function we need to find is the argument
      }

      //Loop through all commands to see if one matches
      boolean found = false;
      for (Command c : commands) {
        if (c.getNames().contains(arguments[0])) {
          //This is the command we should run
          found = true;
          if (help) {
            //call the help command
            System.out.println(c.help());
            help = false;
          } else {
            //call the standard run method
            String output = c.run(arguments[1]);
            System.out.println(output);
            previousCommand = c;
          }
          break;
        }
      }
      if (!found) {
        System.out.println("ERROR: Unrecognized command");
      }
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration(Configuration.VERSION_2_3_0);

    // this is the directory where FreeMarker templates are placed
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    // set port to run the server on
    Spark.port(port);

    // specify location of static resources (HTML, CSS, JS, images, etc.)
    Spark.externalStaticFileLocation("src/main/resources/static");

    // when there's a server error, use ExceptionPrinter to display error on GUI
    Spark.exception(Exception.class, new ExceptionPrinter());

    // initialize FreeMarker template engine (converts .ftl templates to HTML)
    FreeMarkerEngine freeMarker = createEngine();

    // setup Spark Routes
    Spark.get("/", new MainHandler(), freeMarker);
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Request req, Response res) {
      // status 500 generally means there was an internal server error
      res.status(500);

      // write stack trace to GUI
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * A handler to serve the site's main page.
   *
   * @return ModelAndView to render.
   * (main.ftl).
   */
  private static class MainHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // this is a map of variables that are used in the FreeMarker template
      Map<String, Object> variables = ImmutableMap.of("title",
          "Go go GUI");

      return new ModelAndView(variables, "main.ftl");
    }
  }
}
