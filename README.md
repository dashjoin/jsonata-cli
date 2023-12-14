# jsonata-cli
JSONata Command Line Tool

Based on the [Java reference implementation of JSONata](https://github.com/dashjoin/jsonata-java), this is a CLI tool for JSON transformation using JSONata expressions.

Inspired by ```jq```, it offers JSON processing and transformation from CLI, and can be easily installed as a single executable file.

By design it's compilable as native executable using GraalVM, and as such offers low startup time, optimized memory requirements, and good peak performance.

# Installation

The jsonata CLI can be installed with homebrew:
```
brew install dashjoin/tap/jsonata
```
The brew installation requires an installed Java runtime (`brew install java`).
We left out the dependency from brew so you can use any Java runtime you like.

## Update

If installed with homebrew, you can update with
```
brew upgrade jsonata
```

# JSONata Command Line Interface

```
% jsonata
JSONata CLI version 0.9.5-0.4 by Dashjoin (https://dashjoin.com)

CLI for the JSONata query and transformation language.
More information at https://github.com/dashjoin/jsonata-cli

Prettify JSON file: jsonata -i input.json -o pretty.json $
Compact JSON file: jsonata -i input.json -o compact.json -c $
Extract info: jsonata -i package-lock.json '[name, version]'

usage: jsonata [options] <expression>
 -b,--bindings <json-string>   JSONata variable bindings
 -bf,--bindings-file <file>    JSONata variable bindings file
 -c,--compact                  Compact JSON output (don't prettify)
 -e,--expression <file>        JSONata expression file to evaluate
 -f,--format <arg>             Input format (default=auto)
 -h,--help                     Display help and version info
 -i,--input <arg>              JSON input file (- for stdin)
 -ic,--icharset <arg>          Input character set (default=UTF-8)
 -o,--output <arg>             JSON output file (default=stdout)
 -oc,--ocharset <arg>          Output character set (default=UTF-8)
 -time                         Print performance timers to stderr
 -v,--version                  Display version info
```

# Examples
```
% echo '{"a":"hello", "b":" world"}' | jsonata '(a & b)'
hello world

% echo '{"a":"hello", "b":" world"}' | jsonata -o helloworld.json $
# helloworld.json written

% ls | jsonata $
Applications
Library
System
...

% ps -o pid="",%cpu="",%mem="" | jsonata '$.$split(/\n/).$trim().[ $split(/\s+/).$number() ]' -c
[[1,3.2,0.1],[382,0,0],[501,0.5,0.1],[502,0,0],...

% curl -s https://raw.githubusercontent.com/jsonata-js/jsonata/master/test/test-suite/datasets/dataset1.json | jsonata '{"Name": FirstName & " " & Surname, "Cities": **.City, "Emails": Email[type="home"].address}'
{
  "Name": "Fred Smith",
  "Cities": [
    "Winchester",
    "London"
  ],
  "Emails": [
    "freddy@my-social.com",
    "frederic.smith@very-serious.com"
  ]
}
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

You can now run the packaged CLI with the script `scripts/jsonata`

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

