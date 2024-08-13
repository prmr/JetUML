# Download

JetUML is available both as a self-contained application and as a Java archive file under the terms of the [GNU General Public License v3](https://www.gnu.org/licenses/gpl.html). Download the application from the [latest release](https://github.com/prmr/JetUML/releases){:target="_blank"} page and if necessary see the [installation guide](#installation-instructions). Please consider supporting the project by starring this repo and by [writing an endorsement](mailto:jetuml@cs.mcgill.ca).

## Installation Instructions

JetUML is distributed in two formats:

* **Self-contained application:** A large OS-specific download that must be installed on your system, but that does not require the Java platform. Currently available for Windows and Linux, with plans to offer binaries for MacOs when resources permit it.
* **Thin Jar:** A small Java archive (jar) file that does _not_ include any of the dependencies. This option is available for users who just want to download a tiny file and run it from the command-line. The thin jar is OS-independent but requires to have Java 21 or later *and* JavaFX 21 or later installed. 

### Self-Contained Application

* Download the file `JetUML-<Version>-<OS>.<ext>` that corresponds to your operating system from the [latest release page](https://github.com/prmr/JetUML/releases).

* Run or install the file as customary on the appropriate operating system. You will be asked where to extract the application and a shortcut will be added to the desktop. 

* If you are upgrading from a previous version, you can install the new application on top of the older version.

### Thin Jar

*This format requires that you have both [Java](https://openjdk.java.net/) and [JavaFX](https://openjfx.io/) version 21 or above running on your system.* 

Download file `JetUML-<Version>.jar` from the [latest release page](https://github.com/prmr/JetUML/releases), to a local directory. 

To run JetUML, open a command-line terminal window and enter the command below from the same directory where you downloaded the file, or write a script to execute it more conveniently (use `java` instead of `javaw` on OSX/Linux).

```shell
javaw --module-path "PATH_TO_JAVAFX_LIB" --add-modules=javafx.controls,javafx.swing,java.desktop,java.prefs -jar JETUML_FILE
```

Where `PATH_TO_JAVAFX_LIB` is the full path to the `lib` directory of the `javafx` installation and `JETUML_FILE` is the path to the JetUML jar downloaded. For example:

```shell
javaw --module-path "C:\local\Java\javafx-sdk-21.0.2\lib" --add-modules=javafx.controls,javafx.swing,java.desktop,java.prefs -jar JetUML-3.8.jar
```