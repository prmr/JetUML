# Guide for JetUML Developers

Thanks for considering contributing to the JetUML project. Please consult the [Contributing Guidelines](CONTRIBUTING.md) and [Code of Conduct](CODE_OF_CONDUCT.md).

### Contents

* [System Requirements](#system-requirements)
* [Building the Application](#building-the-application-in-eclipse)
* [Committing Code](#committing-code)
* [Packaging the Application](#packaging-the-application)
* [Copyright Notice](#copyright-notice)
* [Architecture Description](#architecture-description)

## System Requirements

The current version of JetUML is built with Java 25. To build JetUML, it is necessary to have the [Java Development Kit](https://www.oracle.com/java/technologies/downloads/) version 25 and the jar files of [JavaFX](https://jdk.java.net/javafx25/) version 25 available. The JetUML code base is configured to build easily with [Maven](https://maven.apache.org/), [Eclipse](https://eclipseide.org/), or both. To contribute code, it will be necessary to run the [Checkstyle](https://checkstyle.org/) tool.

## Building and Running with Maven

The following commands can be run from the command line or, in Eclipse, by right-clicking on the `pom.xml` file and selecting `Run As...`.

* Compiling: `mvn clean compile`
* Validating style: `mvn compile checkstyle:check`
* Testing: `mvn clean test`
* Packaging: `mvn clean package` or `mvn clean package -Pfat` to create a fat jar.
* Running: `mvn clean javafx:run@run`

By default, packaging creates a thin jar as `target/JetUML-<VERSION>.jar`. To run the packaged application from the thin jar, you must have the JavaFX library downloaded somewhere, assumed to be `PATH_TO_JAVAFX_LIB`. To run the jar, open a command-line terminal window and enter the command below from the same directory where you downloaded the file, or write a script to execute it more conveniently.

```
java --module-path <PATH_TO_JAVAFX_LIB> --add-modules=javafx.controls,javafx.swing,java.desktop,java.prefs -jar JetUML-<VERSION>.jar
```

To run the packaged application from the far jar, simply run the jar as:

```
java -jar JetUML-<VERSION>.jar
```

On Windows, you can use `javaw` instead of `java` to run without linking to the console.

## Building and Running with Eclipse

If you used Maven to fetch the dependencies, you can simply run the application and test directly from Eclipse.

* Ensure that the application was built by selecting `Project -> Clean...`.
* Right-click on the project and select `Run As -> Java Application`. Select `JetUML` from the list. 
* To run the tests, select `Run As - > JUnit Test`.

_**MacOs Users**: When you run the application, from the run configuration, make sure the checkbox "Use the -XstartOnFirstThread argument when launching with SWT" is _not_ checked._ 

## Committing Code

1. All committed code must respect all the JetUML style guidelines. These are available as a [style file](../style/Style.xml) for the [Checkstyle Eclipse Plug-in](https://marketplace.eclipse.org/content/checkstyle-plug). Before committing code for JetUML, make sure to install the plug-in, activate it, and check for any warning.
2. All commits must be associated with an issues using an issue tag as the first token in the commit comment, using the format `[$NB]` where `NB` is the issue number. For example, [`[#519] Add constraint for connection to notes`](https://github.com/prmr/JetUML/commit/6af09b1289153cb5fd0aa5b0683da77bca2e5e58).
3. Any new file must include the [copyright notice](#copyright-notice).
4. All unit tests must pass.
5. Pull requests must included a detailed description of the design decisions and their rationale.

## Packaging the Application as an Installable Executable

JetUML can be packaged as a self-contained application for Windows, Linux, and possibly Mac. This is done with the [jpackage tool](https://docs.oracle.com/en/java/javase/14/docs/specs/man/jpackage.html) distributed with OpenJDK. 

### Windows

Run this from the git repo root (where `FXMODS` points to the JavaFX mods directory, e.g.,):

```
set FXMODS="...\javafx-jmods-25"
jpackage --module-path %FXMODS%;classes --add-modules jetuml --module jetuml/org.jetuml.JetUML --app-version 3.10 --icon ..\docs\JetUML.ico --win-shortcut --win-dir-chooser
```

### Linux

Run this from the git repo root (adjust paths as needed):

``` 
FXMODS=/usr/lib/jvm/javafx-jmods-25
JMODS=/usr/lib/jvm/jdk-25/jmods
jpackage --module-path $JMODS:$FXMODS:classes --add-modules jetuml --module jetuml/org.jetuml.JetUML --app-version <VERSION> --icon icons/jet.png 
```

### Mac

Run this from the git repo root (where `PATH_TO_FX_MODS` points to the JavaFX mods directory)

```
$JAVA_HOME/bin/jpackage -n JetUML --module-path $PATH_TO_FX_MODS:classes --add-modules jetuml --module jetuml/org.jetuml.JetUML --app-version <VERSION>> --module jetuml/org.jetuml.JetUML --type pkg --icon ../docs/JetUML.icns
```

## Copyright Notice

```
JetUML - A desktop application for fast UML diagramming.

Copyright (C) ${date} by McGill University.
    
See: https://github.com/prmr/JetUML

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see http://www.gnu.org/licenses.
```

## Architecture Description

This section captures the major decisions related to the development of JetUML.

## Architectural Principles

The following principles guide the development of JetUML:

* **No dependencies:** The application depends on no external libraries. This decision is to minimize the development and evolution cost, minimize the risk of having to do effort-intensive library adaptations, and lower entry barriers for contributors.
* **Minimalist feature set:** The application only supports core UML diagramming features. 
* **Violate Encapsulation for Testing:** To goal for the design is to support the highest possible level of encapsulation, and this implies the most restrictive access modifiers. When necessary, the 
classes in the `test` source folders can use reflection to get around accessibility restrictions.
* **No reflection:** To avoid fragile and hard-to-understand code, the project does not rely on any heavily-reflective framework, such as Javabeans. 
* **No streaming:** The use of [streaming](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html) is explicitly avoided in the interface of classes. JetUML has few data-intensive operations, as diagrams typically have only a handful of elements. In this context, the downsides of streaming (harder to debug, problems with checked exceptions, dual-paradigm design) are deemed to outweigh the advantages (more compact code). When appropriate, use of streams provided by API classes can be used if limited to the scope of a method.

## Functional View

The functional view is split by functional concern.

 * [Diagram State Management](functional/DiagramState.md)
 * [Tab Management](functional/TabManagement.md)
 * [Diagram Element Properties](functional/properties.md)
 * [Node Storage](functional/NodeStorage.md)