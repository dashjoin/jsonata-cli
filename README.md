# jsonata-cli
JSONata Command Line Tool

Based on the [Java reference implementation of JSONata](https://github.com/dashjoin/jsonata-java), this is a CLI tool for JSON transformation using JSONata expressions.

By design it's compilable as native executable using GraalVM, and as such offers low startup time, optimized memory requirements, and good peak performance.

```
% jsonata
usage: jsonata [options] <expression>
 -b,--bindings <json-string>   JSONata variable bindings
 -bf,--bindings-file <file>    JSONata variable bindings file
 -c,--compact                  Compact JSON output (don't prettify)
 -e,--expression <file>        JSONata expression file to evaluate
 -h,--help                     Display help and version info
 -i,--input <arg>              JSON input file (- for stdin)
 -o,--output <arg>             JSON output file (default=stdout)
 -time                         Print performance timers to stderr
```

# Downloads

## CLI executables

Download single file native CLI [executable artifacts for MacOS (x64 and aarch64),
Linux (x64) and Windows (x64)](https://github.com/dashjoin/jsonata-cli/releases/)

## Runnable Java Archive (JAR)

Download ```jsonata-cli-VERSION-jar-with-dependencies.jar``` where VERSION is the latest version:

https://repo1.maven.org/maven2/com/dashjoin/jsonata-cli/

Now you can run the CLI with
```sh
java -jar jsonata-cli-VERSION-jar-with-dependencies.jar
```

# Build from source

After cloning the source code, build the JAR:
```
mvn package
```

Building the native executable (requires installed GraalVM):
```
mvn package -Pnative
```

# Performance

In general, the performance is comparable (and in many cases higher) to other CLI tools,
specifically we compared it to ```jq```, ```jfq```, ```jaq``` and ```gojq```.
[Detailed performance figures can be found here](performance.md).

All in all jsonata-cli offers decent performance for most JSON processing and transformation tasks,
with reasonable memory and CPU consumption, with JSONata at its core as an easy to understand yet powerful JSON transformation and query language.

Installation is very easy due to the single executable file (or the single JAR file) that contains all dependencies.

