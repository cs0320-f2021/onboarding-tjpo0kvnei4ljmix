package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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

    MathBot mb = new MathBot(); //Create MathBot for adding and subtracting
    StarFinder sf = new StarFinder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      String input;
      while ((input = br.readLine()) != null) {
        try {
          input = input.trim();
          String[] arguments = input.split(" ");

          switch (arguments[0].toLowerCase()) {
            case "add":
            case "subtract":
              //test if there are exactly three arguments
              int numArguments = arguments.length;
              if (numArguments > 3) {
                System.out.println("ERROR: Cannot add more than two numbers");
              } else if (numArguments < 3) {
                System.out.println("ERROR: Please provide at least two numbers to add");
              } else {
                //Now, make sure that the other two arguments are actually numbers
                double num1, num2;
                try {
                  num1 = Double.parseDouble(arguments[1]);
                } catch (Exception e) {
                  System.out.println("ERROR: Unable to convert '" + arguments[1] + "' to a number");
                  continue;
                }
                try {
                  num2 = Double.parseDouble(arguments[2]);
                } catch (Exception e) {
                  System.out.println("ERROR: Unable to convert '" + arguments[2] + "' to a number");
                  continue;
                }
                //Note: If there were more than two numbers to add/subtract, I'd put them in a loop
                //Finally, after ensuring that both arguments are doubles, we can add/subtract them
                double result;
                if (arguments[0].equalsIgnoreCase("add")) {
                  result = mb.add(num1, num2);
                } else {
                  result = mb.subtract(num1, num2);
                }
                System.out.println(result); //Print the result
              }
              break; //end of add/subtract case
            case "stars":
              //Test if there is a CSV argument (and no other arguments)
              if (arguments.length != 2) {
                System.out.println(
                    "ERROR: Please provide your input in the format 'stars <CSV File>'");
                break;
              }
              sf.loadStars(arguments[1]);
              break; //end of stars case
            case "naive_neighbors":
              if (input.split("\"").length > 1) {
                //likely formatted as 'naive_neighbors <k> <"name">'
                int k;
                String name;
                try {
                  k = Integer.parseInt(arguments[1]);
                  name = input.split("\"")[1];
                } catch (Exception e) {
                  System.out.println("ERROR: Unable to parse input. Make sure the star name "
                      + "is in quotes, and that 'k' is a number.");
                  break;
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
              break; //end of naive_neighbors case
            default:
              System.out.println("ERROR: Unrecognized Command");
              break; //end default case
          }
        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("ERROR: We couldn't process your input");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR: Invalid input for REPL");
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

  private void printStarResults(ArrayList<Star> stars) {
    if (stars.size() == 0) {
      //This is the return when an error occurs in the knn function
      //Stay silent, an error message has already been printed from knn
      return;
    }
    for (Star s : stars) {
      System.out.print(s.getId());
      //System.out.print(" -> " + s.getName() + " at x: "
      //    + s.getX() + ", Y: " + s.getY() + ", Z: " + s.getZ());
      System.out.println();
    }
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
