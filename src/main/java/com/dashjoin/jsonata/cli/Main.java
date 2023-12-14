package com.dashjoin.jsonata.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.dashjoin.jsonata.Functions;
import com.dashjoin.jsonata.Jsonata;
import com.dashjoin.jsonata.json.Json;

/**
 * JSONata CLI tool
 */
public class Main {

    Options options;

    Main() {
        initCli();
    }

    /**
     * Init CLI options
     */
    void initCli() {
        options = new Options();
        options.addOption("v", "version", false, "Display version info");
        options.addOption("h", "help", false, "Display help and version info");
        // TODO options.addOption("j", "jsonargs", true, "Specify arguments as JSON object");
        options.addOption(Option.builder("e").longOpt("expression")
                         .argName("file")
                         .hasArg()
                         .desc("JSONata expression file to evaluate")
                         //.required()
                         .build());
        options.addOption("i", "input", true, "JSON input file (- for stdin)");
        options.addOption("f", "format", true, "Input format (default=auto)");
        options.addOption("o", "output", true, "JSON output file (default=stdout)");
        options.addOption("time", false, "Print performance timers to stderr");
        options.addOption("c", "compact", false, "Compact JSON output (don't prettify)");
        options.addOption(Option.builder("b").longOpt("bindings").
            argName("json-string").hasArg().desc("JSONata variable bindings").build());
        options.addOption(Option.builder("bf").longOpt("bindings-file").
            argName("file").hasArg().desc("JSONata variable bindings file").build());
    }

    /**
     * Reads the given file into String
     * @param file
     * @return
     * @throws IOException
     */
    String readFile(String file) throws IOException {
        return Files.readString(new File(file).toPath());
    }

    /**
     * Runs the given args as JSONata command
     * @param args
     * @throws Throwable
     */
    void run(String[] args) throws Throwable {

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("v")) {
            printVersion();
            return;
        }
        if (cmd.hasOption("h")) {
            printHelp();
            return;
        }
        if (!cmd.hasOption("e") && cmd.getArgList().isEmpty()) {
            printHelp();
            return;
        }
        String expr = cmd.getOptionValue("e");
        if (expr != null)
            expr = readFile(expr);
        else
            expr = cmd.getArgList().get(0);

        boolean prettify = !cmd.hasOption("c");

        String bindingsStr = cmd.getOptionValue("bf");
        if (bindingsStr != null)
            bindingsStr = readFile(bindingsStr);
        else
            bindingsStr = cmd.getOptionValue("b");
        Map<String, Object> bindingsObj = bindingsStr != null ? (Map<String, Object>)Json.parseJson(bindingsStr) : null;

        InputStream in = null;
        Object input = null;
        {
            String arg = cmd.getOptionValue("i", "-");
            if ("-".equals(arg)) {
                // stdin is used by default if we get input through a pipe,
                // or if the arg "-i -" is forced by the user.
                // If we are only calling the executable without input (console()==null)
                // use NULL input (which allows to execute expressions without input)
                if (System.console()==null || cmd.hasOption("i"))
                    in = System.in;
                else
                    input = Jsonata.NULL_VALUE;
            }
            else
                in = new FileInputStream(arg);
        }

        OutputStream out = System.out;
        if (cmd.hasOption("o")) {
            String arg = cmd.getOptionValue("o");
            out = new FileOutputStream(arg);
        }

        PrintStream pout = new PrintStream(out);

        long t0 = System.currentTimeMillis();

        String formatStr = cmd.getOptionValue("f", TerminalUtil.InputFormat.auto.name());
        TerminalUtil.InputFormat format = TerminalUtil.InputFormat.valueOf(formatStr);

        if (in!=null)
            input = TerminalUtil.readInput(in, format);

        long t1 = System.currentTimeMillis();

        var jsonata = Jsonata.jsonata(expr);
        var bindings = bindingsObj != null ? jsonata.createFrame() : null;
        if (bindings != null) {
            for (Map.Entry<String,Object> e : bindingsObj.entrySet()) {
                bindings.bind( e.getKey(), e.getValue() );
            }
        }

        long t2 = System.currentTimeMillis();

        var res = jsonata.evaluate(input, bindings);

        long t3 = System.currentTimeMillis();

        pout.println(Functions.string(res, prettify));

        long t4 = System.currentTimeMillis();

        if (cmd.hasOption("time")) {
            System.err.println("Performance(millis): total="+(t4-t0)+" t(in)="+(t1-t0)+
            " t(parse)="+(t2-t1)+" t(eval)="+(t3-t2)+" t(out)="+(t4-t3));
        }

        pout.flush();
    }

    /**
     * Print CLI help
     */
    void printHelp() {
        System.out.println("JSONata CLI version "+Version.getVersion()+" by Dashjoin (https://dashjoin.com)\n"+
        "\n" +
        "CLI for the JSONata query and transformation language.\n"+
        "More information at https://github.com/dashjoin/jsonata-cli\n"+
        "\n" +
        "Prettify JSON file: jsonata -i input.json -o pretty.json $\n"+
        "Compact JSON file: jsonata -i input.json -o compact.json -c $\n"+
        "Extract info: jsonata -i package-lock.json '[name, version]'\n" //+
        //"Extract info: jsonata -i package-lock.json -o out.json '$keys(packages){$:$lookup($$.packages,$).version}'\n"
        );

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("jsonata [options] <expression>", options);
    }

    /**
     * Prints the version.
     */
    void printVersion() {
        System.out.println(Version.getVersion());
    }

    /**
     * Main program
     * 
     * @param args CLI arguments
     * @throws Throwable Error
     */
    public static void main(String[] args) throws Throwable {
        Main main = new Main();
        main.run(args);
    }
}
