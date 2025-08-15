# DBCommandLineJava

This is a toy program that shows the basics of interacting with a PostgreSQL database
using Java + JDBC + the PostgreSQL JDBC adapter.

This is a maven project and should work with most build environments, though admittedly
I've only tested with vscode on Mac. If doesn't work in your build environment, let me
know.

## Using the project

The code depends on finding a file called `dbconnection.json` in the current
directory. This file simply specifies your username, password, database, and
database host name. There's an example `dbconnection-example.json` in the
repo that is a skeleton.

To build the project using maven:

```sh
$ mvn package exec:java
...
Connection established successfully.
Current directory: /Users/jason/IdeaProjects/DBCommandLineJava
Server: PostgreSQL 16.9 (Ubuntu 16.9-0ubuntu0.24.04.1)
Driver: PostgreSQL JDBC Driver 42.7.7
Row: id=92, name=William Hartnell, age=58
Row: id=93, name=Patrick Troughton, age=49
Row: id=94, name=Jon Pertwee, age=54
Row: id=95, name=Tom Baker, age=47
Row: id=96, name=Peter Davison, age=32
Row: id=97, name=Colin Baker, age=43
Row: id=98, name=Sylvester McCoy, age=46
Row: id=99, name=Paul McGann, age=36
Row: id=100, name=Christopher Eccleston, age=41
Row: id=101, name=David Tennant, age=38
Row: id=102, name=Matt Smith, age=31
Row: id=103, name=Peter Capaldi, age=59
Row: id=104, name=Jodie Whittaker, age=40
Row: id=105, name=David Tennant, age=52
Row: id=106, name=Ncuti Gatwa, age=32
Maximum age of starting Dr. Who: 59
Mr. Gatwa's age when starting Dr. Who: 32
...
```

The project has several dependencies, and they can be found in `pom.xml` and are
handled automatically by maven.