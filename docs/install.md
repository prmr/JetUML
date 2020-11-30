# Installation Instructions

*For older releases please see the [Legacy Section](#legacy) at the bottom of this page.*

## Release 3.0 and Up

Starting with Release 3.0, JetUML is distributed in two formats:

* **Self-contained application:** A large OS-specific download that must be installed on your system, but that does not require the Java platform.
* **Thin Jar:** A small Java archive (jar) file that does _not_ include any of the dependencies. This option is available for experienced users who just want to download a tiny file and run it from the command-line. The thin jar is OS-independent but requires to have Java 14 or later *and* JavaFX 14 or later installed. 

### Self-Contained Application

* Download the file `JetUML-<Version>-<OS>.<ext>` that corresponds to your operating system from the [latest release page](https://github.com/prmr/JetUML/releases).

* Run or install the file as customary on the appropriate operating system. You will be asked where to extract the application and a shortcut will be added to the desktop. 

### Thin Jar

*This format requires that you have both [Java](https://openjdk.java.net/) and [JavaFX](https://openjfx.io/) version 14 or above running on your system.* 

Download file `JetUML-<Version>.jar` from the [latest release page](https://github.com/prmr/JetUML/releases), to a local directory. 

To run JetUML, open a command-line terminal window and enter the command below from the same directory where you downloaded the file, or write a script to execute it more conveniently.

```shell
javaw --module-path "PATH_TO_JAVAFX_LIB" --add-modules=javafx.controls,javafx.swing,java.desktop,java.prefs -jar JETUML_FILE
```

Where `PATH_TO_JAVAFX_LIB` is the full path to the `lib` directory of the `javafx` installation and `JETUML_FILE` is the path to the JetUML jar downloaded. For example:

```shell
javaw --module-path "C:\Program Files\Java\javafx-sdk-11.0.2\lib" --add-modules=javafx.controls,javafx.swing,java.desktop,java.prefs -jar JetUML-3.0.jar
```

## Legacy

Versions prior to 2.0 will run as a self-executing jar on any version of Java. Versions 2.0 and higher require JavaFX. They will run as a self-executing jar on Java 8 only. To run versions 2.0-2.6 on Java 11 and later, follow the instructions for [thin jar](#thin-jar), above.