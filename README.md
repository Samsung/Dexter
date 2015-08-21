# Dexter
Dexter is a static analysis platform to find and remove defects efficiently and immediately during the coding-time. Whenever you save a source file, Dexter analyzes it and shows its defects on your editor in real-time. In addition, Dexter saves your learning cost because it contains multiple static analysis tools as a plug-in type.

![](https://github.com/Samsung/Dexter/blob/master/wiki/image/overview.png)

(* Detailed guide will be provided on a wiki soon)

## Requirements

### Dexter Clients
- Eclipse Juno(4.2)+ for Java Development
- Source Insight 3.50.0072+ for C/C++ Development
- Dexter CLI for Comand Line Console or CI tool(Jenkins)

### Dexter Server (optional)
- MySql 5.x
- NodeJs 0.12.x

## How to build
![document](https://github.com/Samsung/Dexter/blob/master/doc/Dexter_Build_Guide.pdf)
### Prerequisite
- Install JDK 7 update 40+
- Install Gradle (http://gradle.org)
- Install NodeJS (https://nodejs.org)
- Download Eclipse RCP/RAP Juno + (http://www.eclipse.org/downloads/packages/release/juno/sr2)
- Download Dexter Source Codes (https://github.com/Samsung/Dexter)

### Import Dexter Projects into Eclipse 
- run with suffice memory (in eclipse.ini) : -Xmx1024m --launcher.XXMaxPermSize256m
- make sure your text file encoding setting is 'UTF-8' : eclipse > Preferences > General > Workspace
- import Dexter Projects that you already downloaded except dexter-server project
- use 'gradle build' command in a command line console : '/project' folder
- refresh all projects in eclipse, then all errors will be gone

### Build Dexter CLI
- use ant script on build-install.xml file in dexter-executor project
- you can see the 'dexter-cli_#.#.#_bit.zip file in dexter-executor/install folder
- after unzip this file, you can run dexter in a command line: unzip-folder/bin/dexter.bat or dexter.sh
- before you run dexter.bat file, you have to set dexter_cfg.json file to set the scope of analysis (refer to dexter_cfg.json.help file)

### Build Dexter Daemon for source insight
- open dexter-daemon.product file in dexter-daemon project
- click 'Eclipse Product export wizard' link on the 'Exporting' tab in a editor
- set the fields - Root direoct:dexter-daemon  Destination/Directory: your directory
- click "Finish" button
- check export folder, there will be dexter-daemon folder
- run dexter.exe file, then you can see the login dialog
- check 'Run in Standalong mode', then you can run dexter without Dexter Server
- run source insight, and open 'Base' project
- add 'project/script/dexter.em' macro file into 'Base' project
- after editing and saving your source file, you will see the result of analysis in your editor and Dexter Daemon

### Build Dexter Eclipse Plugins
- create feature project in your eclipse
- include all of dexter projects witout dexter-daemon and dexter-cppcheck project
- create update site on the feature.xml file
- include your feature and build all, then you will see the feature and plugins folders in you update project
- you can make a update site or just copy plugin folder into your new eclipse
- then, you can use Dexter

## License
Dexter is BSD-licensed.
