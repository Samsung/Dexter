# Dexter
Dexter is a static analysis platform to find and remove defects efficiently and immediately during the coding-time. Whenever you save a source file, Dexter analyzes it and shows its defects on your editor in real-time. In addition, Dexter saves your learning cost because it contains multiple static analysis tools as a plug-in type.

![](https://github.com/Samsung/Dexter/tree/master/wiki/image/overview.png)

(* Detailed guide will be provided on a wiki soon)

## Requirements

### Dexter Clients
- Eclipse Juno(4.2)+ for Java Development
- Source Insight 3.50.0072+ for C/C++ Development

### Dexter Server (optional)
- MySql 5.x
- NodeJs 0.12.x


## Quick User Guide

### Java Developers Who use Eclipse
- 1) download and unzip 'deploy/0.9.0/eclipse-update_64.zip' file
- 2) copy '64/plugins/*.jar' files into dropins folder on your eclipse
- 3) check "Run in Standalone mode" checkbox on the Dexter Server Login dialog (blue water drop icon)
- 4) click 'Active/InActive Static Analysis Feature(Dexter)' menu on the context menu on your project
- 5) edit and save your source file, if there is a defect, you will see the error/warning/info indicator for them.

### C/C++ Developers Who use Source Insight
TBD

### Dexter Server Installation (optional)
TBD

## How to build
TBD

## License
Dexter is BSD-licensed.
