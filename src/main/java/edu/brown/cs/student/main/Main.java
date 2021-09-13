package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
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
    StarFinder sf; //Don't initialize yet, since we have no CSV
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
                } else if (arguments[0].equalsIgnoreCase("subtract")) {
                  result = mb.subtract(num1, num2);
                } else {
                  //This should literally never happen, since arguments[0] was as one of these
                  System.out.println("Congratulations, you broke logic");
                  throw new Exception("Reached unreachable case, arguments[0] is not a command.");
                }
                System.out.println(result); //Print the result
              }
              break; //end of add/subtract case
            case "stars":
              //Test if there is a CSV argument (and no other arguments)
              if (arguments.length != 2) {
                System.out.println(
                    "ERROR: Please provide your input in the format 'stars <CSV File>'");
              }
              System.out.println("Not Implemented Yet");
              break; //end of stars case
            case "naive_neighbors":
              System.out.println("Not Implemented Yet");
              break; //end of naive_neighbors case
            default:
              System.out.println(arguments[0]); //default behavior is to print the first word
              break; //end default case
          }
        } catch (Exception e) {
          // e.printStackTrace();
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
