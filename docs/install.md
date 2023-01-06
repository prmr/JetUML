# Installation Instructions

JetUML is distributed in two formats:

* **Self-contained application:** A large OS-specific download that must be installed on your system, but that does not require the Java platform. Currently available for Windows only, with plans to offer binaries for Windows and Linux when resources permit it.
* **Thin Jar:** A small Java archive (jar) file that does _not_ include any of the dependencies. This option is available for users who just want to download a tiny file and run it from the command-line. The thin jar is OS-independent but requires to have Java 17 or later *and* JavaFX 17 or later installed. 

### Self-Contained Application

* Download the file `JetUML-<Version>-<OS>.<ext>` that corresponds to your operating system from the [latest release page](https://github.com/prmr/JetUML/releases).

* Run or install the file as customary on the appropriate operating system. You will be asked where to extract the application and a shortcut will be added to the desktop. 

* If you are upgrading from a previous version, you can install the new application on top of the older version.

### Thin Jar

*This format requires that you have both [Java](https://openjdk.java.net/) and [JavaFX](https://openjfx.io/) version 17 or above running on your system.* 

Download file `JetUML-<Version>.jar` from the [latest release page](https://github.com/prmr/JetUML/releases), to a local directory. 

To run JetUML, open a command-line terminal window and enter the command below from the same directory where you downloaded the file, or write a script to execute it more conveniently (use `java` instead of `javaw` on OSX/Linux).

```shell
javaw --module-path "PATH_TO_JAVAFX_LIB" --add-modules=javafx.controls,javafx.swing,java.desktop,java.prefs -jar JETUML_FILE
```

Where `PATH_TO_JAVAFX_LIB` is the full path to the `lib` directory of the `javafx` installation and `JETUML_FILE` is the path to the JetUML jar downloaded. For example:

```shell
javaw --module-path "C:\local\Java\javafx-sdk-17.0.2\lib" --add-modules=javafx.controls,javafx.swing,java.desktop,java.prefs -jar JetUML-3.4.jar
```

## Package Managers

Some systems may have a pre-packaged version of JetUML available.

| System | Link | Maintainers |
|--------|------|-------------|
| Arch   | [aur.archlinux.org/packages/jetuml](https://aur.archlinux.org/packages/jetuml) | Community   |
