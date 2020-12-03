# Guide for JetUML Developers

### Contents

* [Building the Application](building-the-application)
* [Committing Code](committing-code)
* [Packaging the Application](packaging-the-application)
* [Releasing the Application](releasing-the-application)
* [See Also](see-also)

## Building the Application in Eclipse

* To build JetUML in Eclipse, it is necessary to [define a user library for JavaFX](https://openjfx.io/openjfx-docs/#IDE-Eclipse).

* The application should then run in Eclipse as a normal Run configuration.

## Committing Code

* All committed code must respect all the JetUML style guidelines. These are available as a [../style/Style.xml](style file) for the [Checkstyle Eclipse Plug-in](https://marketplace.eclipse.org/content/checkstyle-plug). Before committing code for JetUML, make sure to install the plug-in, activate it, and check for any warning.
* All commits must be associated with an issues using an issue tag as the first token in the commit comment (e.g., `#394`).
* All JUnit tests must pass.

## Packaging the Application

Starting with Release 3.0 JetUML needs to be packaged as a self-contained application for Windows, Mac, and Linux. This is done with the [jpackage tool](https://docs.oracle.com/en/java/javase/14/docs/specs/man/jpackage.html) distributed with OpenJDK.

### Windows

Run this from the git repo root (where `FXMODS` points to the JavaFX mods directory)

```
jpackage --module-path %FXMODS%;bin\jetuml --add-modules jetuml --module jetuml/ca.mcgill.cs.jetuml.JetUML --app-version 3.0 --icon docs\JetUML.ico --win-shortcut --win-dir-chooser
```

### Mac


Run this from the git repo root (where `PATH_TO_FX_MODS` points to the JavaFX mods directory)

```
$JAVA_HOME/bin/jpackage -n JetUML --module-path $PATH_TO_FX_MODS:bin/jetuml --add-modules jetuml --module jetuml/ca.mcgill.cs.jetuml.JetUML --app-version 3.0 --module jetuml/ca.mcgill.cs.jetuml.JetUML --type pkg --icon docs/JetUML.icns
```

### Linux

After compiling the project, run this from the git repo root (where FXMODS points to the JavaFX mods directory) 

``` 
jpackage --module-path %FXMODS%:bin/jetuml --add-modules jetuml --module jetuml/ca.mcgill.cs.jetuml.JetUML --app-version 3.0 --icon docs/JetUML.ico 
```

To build JetUML from the command line, after downloading the necessary JUnit 5 dependencies and placing them into a `junit` folder at the root of the git repo, run the following: 

``` 
rm -rf bin 
mkdir bin 
cp -r icons/** bin/jetuml/ 
find . -name "*.java" > files.txt 
javac -cp test:src -p %FXMODS%:junit --add-modules javafx.controls,javafx.swing -d bin/jetuml @files.txt 
cd src/ 
cp --parent `find -name "*.css"` ../bin/jetuml 
cp --parent `find -name "*.properties"` ../bin/jetuml 
cd ../test/ 
cp --parent `find -name "*.properties"` ../bin/jetuml cd .. 
``` 

To test that the files were correctly compiled, run the following: 

``` 
java -p %FXMODS%:bin/jetuml --add-modules javafx.controls,javafx.swing,jetuml ca.mcgill.cs.jetuml.JetUML 
```

## Runtime image

Normally it is not necessary to create a run-time image separately from the one created through the jpackage tool. However, if for any reason one is needed, here is how to create it:

* To [build a run-time image](https://openjfx.io/openjfx-docs/#IDE-Eclipse), use the command `jlink --module-path %FXMODS%;bin\jetuml --add-modules=jetuml --output image` from the root of the git repo.

* To run the image, do `image\bin\java -m jetuml/ca.mcgill.cs.jetuml.UMLEditor`. 

* Don't forget to move or delete the runtime image from the git repo.

## Releasing the Application

In the JetUML project, releases map one-to-one with milestones.

1. Create a new issue titled "Release X" where X is the number of the release;
2. Make sure all other issues for the milestone are closed;
3. Run all the unit tests;
4. Increment the version number in `JetUML.java`;
5. Update the copyrights using the copyright tool (see below);
5. Create a new release on GitHub;
6. Write the release notes;
7. Release the application on GitHub;
8. Create a thin jar that only includes the source code (excluding tests) and the license;
9. Package the application as above for the three different platforms;
10. Upload the binaries to the release page;
11. Close the milestone;
12. Tweet about the new release.

### Copyright Notice

*To be used in the Eclipse copyright tool.*

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

## See Also

* [Contributing Guidelines](docs/CONTRIBUTING)
* [Architecture Description](/docs/architecture)