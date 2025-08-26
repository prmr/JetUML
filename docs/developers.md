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

The current version of JetUML is built with Java 21, the latest long-term support (LTS) release. To build JetUML, it is necessary to have the [Java Development Kit](https://www.oracle.com/java/technologies/downloads/) version 21 and the jar files of [JavaFX](https://jdk.java.net/javafx21/) version 21 available. The JetUML code base is configured to build easily with [Eclipse](https://eclipseide.org/), but use of this IDE is optional. To contribute code, it will be necessary to run the [Checkstyle](https://checkstyle.org/) tool.

## Building the Application in Eclipse

1. Ensure that you meet the system requirements, including a working version of Eclipse.
2. In Eclipse, ensure that the JDK 21 is the default workspace JRE (_Window > Preferences > Java > Installed JREs_).
3. Create a new _user library_ called `JavaFX` that includes all the JavaFX 21 jar files. To create this library, access _Window > Preferences > Java > User Libraries_, select _New..._, enter the exact string `JavaFX`. Then, select this library, and click _Add External JARS..._, then find and select the jar files under the `lib` directory of your JavaFX download.
4. Import the [JetUML repo](https://github.com/prmr/JetUML.git) in Eclipse (_File > Import > Git > Projects from Git > Clone URI_). If you meet the system requirements, the project should build automatically.
5. To run JetUML, right-click on the project in the Package Explorer and select _Run As > Java Application_, selecting `JetUML` as the main file.
6. **If you are using a Mac**, to run the application, open the run configuration and make sure the checkbox "Use the -XstartOnFirstThread argument when launching with SWT" is not checked.

## Committing Code

1. All committed code must respect all the JetUML style guidelines. These are available as a [style file](../style/Style.xml) for the [Checkstyle Eclipse Plug-in](https://marketplace.eclipse.org/content/checkstyle-plug). Before committing code for JetUML, make sure to install the plug-in, activate it, and check for any warning.
2. All commits must be associated with an issues using an issue tag as the first token in the commit comment, using the format `[$NB]` where `NB` is the issue number. For example, [`[#519] Add constraint for connection to notes`](https://github.com/prmr/JetUML/commit/6af09b1289153cb5fd0aa5b0683da77bca2e5e58).
3. Ensure that any new file includes the [copyright notice](#copyright-notice).
3. Ensure that all unit tests pass.

## Packaging the Application

JetUML can be packaged as a self-contained application for Windows, Linux, and possibly Mac. This is done with the [jpackage tool](https://docs.oracle.com/en/java/javase/14/docs/specs/man/jpackage.html) distributed with OpenJDK. 

### Windows

Run this from the git repo root (where `FXMODS` points to the JavaFX mods directory, e.g.,):

```
set FXMODS="C:\local\Java\javafx-jmods-21.0.2"
jpackage --module-path %FXMODS%;bin\main --add-modules jetuml --module jetuml/org.jetuml.JetUML --app-version 3.9 --icon docs\JetUML.ico --win-shortcut --win-dir-chooser
```

### Linux

Run this from the git repo root (adjust paths as needed):

``` 
FXMODS=/usr/lib/jvm/javafx-jmods-21.0.2
JMODS=/usr/lib/jvm/jdk-17-oracle-x64/jmods
jpackage --module-path $JMODS:$FXMODS:bin/jetuml --add-modules jetuml --module jetuml/org.jetuml.JetUML --app-version 3.9 --icon icons/jet.png 
```

### Mac

Run this from the git repo root (where `PATH_TO_FX_MODS` points to the JavaFX mods directory)

```
$JAVA_HOME/bin/jpackage -n JetUML --module-path $PATH_TO_FX_MODS:bin/jetuml --add-modules jetuml --module jetuml/org.jetuml.JetUML --app-version 3.9 --module jetuml/org.jetuml.JetUML --type pkg --icon docs/JetUML.icns
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