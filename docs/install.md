# Installation Instructions

JetUML can be run most easily on Java 8, the last long-term support (LTS) Java release that bundles JavaFX with the run-time environment. JetUML can also be executed with later releases of Java, but this requires a bit more work.

*To contribute instructions for other system configurations, please submit a pull request.*

## Download JetUML

Download the latest version from the [releases page](https://github.com/prmr/JetUML/releases).

## Install Dependencies

### Java 8 (Cross-Platform)

If your OS environment supports executable jars, simply double-click the jar file to start the application. If this is not possible, your can start it from the command line with `java -jar file.jar` (where `file.jar` is the name of the JetUML jar you downloaded).

### Java 11/12/13 (macOS)
1. **Download and install [Java SE](https://www.oracle.com/technetwork/java/javase/downloads)**. The macOS installer will automatically set up the path for you, so you do not need to manually add the path.
2. **Download and install [JavaFX macOS](https://gluonhq.com/products/javafx/)**. Notice that you only need the *SDK*, not the *jmods*.


### OpenJDK 11/12 (Windows 10)

1. **Download and install [OpenJDK](https://openjdk.java.net/)** by following the instructions. Make sure to include the Java binaries on the path. The installation is complete when typing `java -version` on the command line displays the downloaded and extracted version of the JDK.

2. **Download and install [OpenJFX](https://openjfx.io/)** by following the instructions. 

## Run JetUML
At this point is should be possible to run JetUML by entering the command:

```
java --module-path "PATH_TO_JAVAFX_LIB" --add-modules=javafx.controls -jar JETUML_FILE
```

Where `PATH_TO_JAVAFX_LIB` is the full path to the `lib` directory of the `javafx` installation and `JETUML_FILE` is the path to the JetUML jar downloaded. For example:

```
java --module-path "C:\local\java\javafx-sdk-11.0.2\lib" --add-modules=javafx.controls -jar JetUML-2.4.jar
```
#### For Windows Users only:
To make it possible to execute JetUML by **double-clicking the jar file** (optional/advanced), it is necessary to modify the Windows registry. Using `Regedit`, open the Windows registry editor and find the entry `HKEY_CLASSES_ROOT\jar_auto_file\shell\open\command` and modify its data field so it has the value:

```
"PATH_TO_JAVAW" --module-path "PATH_TO_JAVAFX_LIB" --add-modules=javafx.controls -jar "%1"
```

where `PATH_TO_JAVAW` is the path to the executable `javaw.exe`, normally found in the `bin` directory of the JDK root directory. `PATH_TO_JAVAFX_LIB` is the same value as for step 2.

