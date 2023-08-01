# jsonata-cli
JSONata Command Line Tool

Based on the [Java reference implementation of JSONata](https://github.com/dashjoin/jsonata-java), this is a CLI tool for JSON transformation using JSONata expressions.

By design it's compilable as native executable using GraalVM, and as such offers low startup time, optimized memory requirements, and good peak performance.

More features will be added over time.

```
% jsonata
JSONata CLI (C) Dashjoin GmbH
usage: jsonata
 -e,--expression <jsonata>   (required) JSONata expression to evaluate
 -h,--help                   Display help and version info
 -i,--input <arg>            JSON input file (- for stdin)
 -o,--output <arg>           JSON output file (default=stdout)
```

# Preliminary results with large JSON files

Data files:
- https://github.com/json-iterator/test-data/blob/master/large-file.json
- https://github.com/zemirco/sf-city-lots-json/blob/master/citylots.json

```sh
# Format 26 MB input JSON into pretty format (33 MB output)
% jsonata -time -e $ -i json/large.json -o json/formatted.json
Performance(millis): total=552 t(in)=191 t(parse)=22 t(eval)=26 t(out)=313

# Format 190 MB input JSON into pretty format (412 MB output)
% jsonata -time -e $ -i json/citylots.json -o json/citylotsf.json
Performance(millis): total=5550 t(in)=1996 t(parse)=23 t(eval)=178 t(out)=3353

# Count logins (Github events) from 26 MB input JSON
% jsonata -time -e '$count(actor.login)' -i json/large.json                    
Performance(millis): total=222 t(in)=181 t(parse)=26 t(eval)=15 t(out)=0
11351

# Count distinct logins
% jsonata -time -e '$count($distinct(actor.login))' -i json/large.json
Performance(millis): total=337 t(in)=187 t(parse)=24 t(eval)=126 t(out)=0
5250

# Extract logins into logins.json
% jsonata -time -e 'actor.login' -i json/large.json -o json/logins.json 
Performance(millis): total=229 t(in)=187 t(parse)=22 t(eval)=15 t(out)=5

# Count geometry types from 190MB JSON
% jsonata -time -e '$count(features.geometry.type)' -i json/citylots.json                            
Performance(millis): total=2235 t(in)=2025 t(parse)=24 t(eval)=186 t(out)=0
206554

# Distinct geometry types from 190MB JSON
% jsonata -time -e '$distinct(features.geometry.type)' -i json/citylots.json
Performance(millis): total=2419 t(in)=2166 t(parse)=23 t(eval)=230 t(out)=0
[
  "Polygon",
  "MultiPolygon"
]

# Count coords from 190MB JSON
% jsonata -time -e '$count(features.geometry.coordinates)' -i json/citylots.json 
Performance(millis): total=2364 t(in)=2047 t(parse)=24 t(eval)=293 t(out)=0
206966

# Extract 206k coordinates into 195MB JSON (pretty printed)
% jsonata -time -e 'features.geometry.coordinates' -i json/citylots.json  -o json/citylots-coords.json
Performance(millis): total=4981 t(in)=2057 t(parse)=23 t(eval)=437 t(out)=2464
```

# Next steps

- Support for JSONata bindings
- Allow JSON as input arguments
    - useful for feeding previous jsonata results into jsonata child commands
