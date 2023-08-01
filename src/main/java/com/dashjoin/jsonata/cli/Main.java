package com.dashjoin.jsonata.cli;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

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

    void initCli() {
        options = new Options();
        options.addOption("h", "help", false, "Display help and version info");
        // TODO options.addOption("j", "jsonargs", true, "Specify arguments as JSON object");
        options.addOption(Option.builder("e").longOpt("expression")
                         .argName("jsonata")
                         .hasArg()
                         .desc("(required) JSONata expression to evaluate")
                         //.required()
                         .build());
        //options.addOption("e", "expression", true, "JSONata expression to evaluate");
        // TODO options.addOption("b", "bindings", true, "Optional JSONata bindings");
        options.addOption("i", "input", true, "JSON input file (- for stdin)");
        options.addOption("o", "output", true, "JSON output file (default=stdout)");
        options.addOption("time", false, "Print performance timers to stderr");
    }

    void run(String[] args) throws Throwable {

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("h")) {
            printHelp();
            return;
        }
        if (!cmd.hasOption("e")) {
            printHelp();
            return;
        }

        String expr = cmd.getOptionValue("e");

        InputStream in = null;
        if (cmd.hasOption("i")) {
            String arg = cmd.getOptionValue("i");
            if ("-".equals(arg))
                in = System.in;
            else
                in = new FileInputStream(arg);

            in = new BufferedInputStream(in, 262144);
        }
        OutputStream out = System.out;
        if (cmd.hasOption("o")) {
            String arg = cmd.getOptionValue("o");
            out = new FileOutputStream(arg);
        }

        PrintStream pout = new PrintStream(new BufferedOutputStream(out, 262144));

        // byte[] buf = new byte[65536];
        // StringBuilder sb = new StringBuilder();
        // while (in.available()>0) {
        //     int sz = in.read(buf);
        //     sb.append(new String(buf, 0, sz, "utf-8"));
        // }
        long t0 = System.currentTimeMillis();

        var input = in!=null ? Json.parseJson(new InputStreamReader(in)) : null;

        long t1 = System.currentTimeMillis();

        var jsonata = Jsonata.jsonata(expr);

        long t2 = System.currentTimeMillis();

        var res = jsonata.evaluate(input);

        long t3 = System.currentTimeMillis();

        pout.println(Functions.string(res,true));

        long t4 = System.currentTimeMillis();

        if (cmd.hasOption("time")) {
            System.err.println("Performance(millis): total="+(t4-t0)+" t(in)="+(t1-t0)+
            " t(parse)="+(t2-t1)+" t(eval)="+(t3-t2)+" t(out)="+(t4-t3));
        }

        pout.flush();
    }

    void printHelp() {
        System.out.println("JSONata CLI (C) Dashjoin GmbH");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("jsonata", options);
    }

    public static void main(String[] args) throws Throwable {
        Main main = new Main();
        main.run(args);
    }
}
