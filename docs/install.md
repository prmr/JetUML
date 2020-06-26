# Installation Instructions

Starting with Release 3.0, JetUML is distributed in two formats:

* **Thin Jar:** A single small Java archive (jar) file that does _not_ include any of the dependencies. Choose this format if you have Java 11 or later and JavaFX 11 or later installed on your system and just want to download a tiny file and run it from the command line. The thin jar is **OS-independent**.
* **Runtime image:** A large download that includes all dependencies necessary to run the application. Choose this format if you don't have both Java and JavaFX installed on your system, and/or you want to be able to start the application using a desktop shortcut. The runtime image is **OS-dependent**.

## Thin Jar

*This format requires that you have both [Java](https://openjdk.java.net/) and [JavaFX](https://openjfx.io/) version 11 or above running on your system.* 

Download file `JetUML-<Version>.jar` from the latest release page, to a local directory. 

To run JetUML, open a command-line terminal window and enter the command below from the same directory where you downloaded the file, or write a script to execute it more conveniently.

```shell
java --module-path "PATH_TO_JAVAFX_LIB" --add-modules=javafx.controls,javafx.swing,java.desktop,java.prefs -jar JETUML_FILE
```

Where `PATH_TO_JAVAFX_LIB` is the full path to the `lib` directory of the `javafx` installation and `JETUML_FILE` is the path to the JetUML jar downloaded. For example:

```shell
java --module-path "C:\Program Files\Java\javafx-sdk-11.0.2\lib" --add-modules=javafx.controls,javafx.swing,java.desktop,java.prefs -jar JetUML-3.0.jar
```

## Runtime Image

*We plan to have natively installable distributions in the future. For the moment, please go through the following simple steps.*

Download the file `JetUML-<Version>-<OS>.zip` that corresponds to your operating system from the latest release page and extract the zip file to a location of your choice.

#### Windows

1. In the extracted files, find `bin\javaw.exe`, create a shortcut, and rename it `JetUML`;
2. Right-click on the JetUML shortcut and select `Properties`.
3. Under `Target`, append the following string to the current target: ` -m jetuml/ca.mcgill.cs.jetuml.JetUML` (making sure to leave a space between the existing command and the appended text).
4. (Optional) Still under `Properties`, click the button `Change Icon...` and browse to the file `JetUML.ico` in the same directory as the `java.exe` file. Click `Apply`.
5. You can now move your shortcut to where you like and double-click it to start JetUML.

