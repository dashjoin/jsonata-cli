# Performance

## JSON throughput

Data files:
- https://github.com/json-iterator/test-data/blob/master/large-file.json
- https://github.com/zemirco/sf-city-lots-json/blob/master/citylots.json

```sh
# Format 26 MB input JSON into pretty format (33 MB output)
# jsonata
% time jsonata -i - -time $ <json/large.json >json/out2.json
Performance(millis): total=483 t(in)=204 t(parse)=22 t(eval)=26 t(out)=231
jsonata -i - -time $ < json/large.json > json/out2.json  1.16s user 0.13s system 211% cpu 0.613 total

# jq
% time jq <json/large.json >json/out.json 
jq < json/large.json > json/out.json  1.01s user 0.04s system 99% cpu 1.058 total 

# Format 190 MB input JSON into pretty format (412 MB output)
# jsonata
% time jsonata -i - -time $ <json/citylots.json >json/out2.json
Performance(millis): total=5003 t(in)=2106 t(parse)=24 t(eval)=181 t(out)=2692
jsonata -i - -time $ < json/citylots.json > json/out2.json  7.60s user 1.00s system 165% cpu 5.195 total

#jq
% time jq <json/citylots.json >json/out.json 
jq < json/citylots.json > json/out.json  10.55s user 0.50s system 99% cpu 11.109 total 

# Count logins (Github events) from 26 MB input JSON
% time jsonata -time '$count(actor.login)' -i json/large.json      
11351
Performance(millis): total=233 t(in)=198 t(parse)=21 t(eval)=14 t(out)=0
jsonata -time '$count(actor.login)' -i json/large.json  0.64s user 0.07s system 202% cpu 0.351 total

# Count distinct logins
% time jsonata -time '$count($distinct(actor.login))' -i json/large.json
5250
Performance(millis): total=226 t(in)=189 t(parse)=21 t(eval)=16 t(out)=0
jsonata -time '$count($distinct(actor.login))' -i json/large.json  0.62s user 0.07s system 199% cpu 0.346 total

# Extract logins into logins.json
% time jsonata -time 'actor.login' -i json/large.json -o json/logins.json
Performance(millis): total=240 t(in)=201 t(parse)=20 t(eval)=14 t(out)=5
jsonata -time 'actor.login' -i json/large.json -o json/logins.json  0.65s user 0.07s system 200% cpu 0.361 total

# Count geometry types from 190MB JSON
% time jsonata -time '$count(features.geometry.type)' -i json/citylots.json
206554
Performance(millis): total=2280 t(in)=2072 t(parse)=24 t(eval)=184 t(out)=0
jsonata -time '$count(features.geometry.type)' -i json/citylots.json  4.39s user 0.52s system 201% cpu 2.434 total

# Distinct geometry types from 190MB JSON
% time jsonata -time '$distinct(features.geometry.type)' -i json/citylots.json
[
  "Polygon",
  "MultiPolygon"
]
Performance(millis): total=2356 t(in)=2105 t(parse)=23 t(eval)=227 t(out)=1
jsonata -time '$distinct(features.geometry.type)' -i json/citylots.json  4.43s user 0.52s system 197% cpu 2.501 total

# Count coords from 190MB JSON
% time jsonata -time '$count(features.geometry.coordinates)' -i json/citylots.json 
206966
Performance(millis): total=2360 t(in)=2052 t(parse)=24 t(eval)=284 t(out)=0
jsonata -time '$count(features.geometry.coordinates)' -i json/citylots.json  4.46s user 0.53s system 198% cpu 2.517 total

# Extract 206k coordinates into 195MB JSON (pretty printed)
% time jsonata -time 'features.geometry.coordinates' -i json/citylots.json  -o json/citylots-coords.json
Performance(millis): total=4676 t(in)=2110 t(parse)=23 t(eval)=391 t(out)=2152
jsonata -time 'features.geometry.coordinates' -i json/citylots.json -o   7.58s user 0.75s system 172% cpu 4.836 total
```

## Native compiled executable

Advantages of native compiled executables are extremely fast startup time, and no Java runtime requirements (only the executable is required).
The total size of the CLI executable is around 14 MB, which contains all dependencies.

Java CLI execution of a simple expression is around 120 milliseconds, whereas the native CLI executes in around 6 milliseconds:

```sh
# Startup time of Java CLI tool
% time java -jar target/jsonata-cli-0.1-jar-with-dependencies.jar -e 1
1
java -jar target/jsonata-cli-0.1-jar-with-dependencies.jar -e 1  0.12s user 0.03s system 118% cpu 0.122 total

# Startup time of native CLI tool
% time ./jsonata -e 1                                                 
1
./jsonata -e 1  0.00s user 0.00s system 76% cpu 0.006 total
```

So startup is around 20x faster for the native CLI tool.
Peak performance of the Java JIT is however up to 2x faster, even with GraalVM's profile-guided optimizations (PGO).
