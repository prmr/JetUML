# Download and Installation

Download the application from the [latest release](https://github.com/prmr/JetUML/releases) page. JetUML is available under the terms of the [GNU General Public License v3](https://www.gnu.org/licenses/gpl.html). The application is distributed as a Java archive (jar) file. Please support the project by starring [the GitHub repo](https://github.com/prmr/JetUML) and by [writing an endorsement](mailto:jetuml@cs.mcgill.ca).

## Privacy Policy

JetUML does not collect any information. The application only accesses the network to open web pages via the commands in the Help menu.

## Installation Instructions

JetUML is distributed in different formats:

* **Fat Jar:** A large OS-specific Java archive (jar) file. Requires the Java platform.
* **Thin Jar:** A small OS-independent Java archive (jar) file. Requires the Java platform and the JavaFX binaries.
* **Nix package:** This is a community-contributed distribution. A standalone application installed on your system via the Nix package manager. Available for MacOS/Linux systems with the Nix package manager installed. Does not require the Java platform.

### Fat Jar

Ensure you can run Java by entering `java -version` on a terminal console. Download file `JetUML-<OS>-<VERSION>.jar` from the [latest release page](https://github.com/prmr/JetUML/releases), to a local directory. Enter `java -jar JetUML-<OS>-<VERSION>.jar`.

### Thin Jar

Download file `JetUML-<VERSION>.jar`. The process is similar to the one used to run the fat jar, but requires linking to dependencies. See the [Guide for JetUML Developers](developers.md) for details.

### Nix Package

_Note that external builds are not verified by the developers of JetUML. Only install packages from sources you trust._

Make sure you have [Nix package manager](https://nixos.org/download/) installed (not required if you are using NixOS), and the `nixpkgs` channel is set to at least version `25.11`. 

**Installation on MacOS and Linux**
```
nix-env -iA nixpkgs.jetuml
```

**Installation on NixOS**

Put the following code in your `configuration.nix` file:
```
  environment.systemPackages = [
    pkgs.jetuml
  ];
```
And then run `sudo nixos-rebuild switch`.

**Running the application**

After successful installation, simply run `jetuml` in the terminal to launch the program. 

More information can be found in [the Nixpkgs registry](https://search.nixos.org/packages?channel=25.11&show=jetuml&query=jetuml)