# Dexter
Dexter helps you to improve coding skills. Whenever you save a source file, Dexter analyzes it and shows its improvements point on your editor in real-time.

## Who needs Dexter?
- A developer who sometimes forgets to check code quality (just like me !)
- A developer who needs a peer reviewer (but you are shy)
- A S/W engineering team member who wants to improve developers' code quality without annoying
- A static analysis engine developer who needs UI and server-side features

## Links
- [Dexter Introduction Video(4min)](https://youtu.be/86exIHcwi6c)
- [Dexter Wiki (How to install/Use)](https://dexter.atlassian.net)

## Downloads (ver. 0.9.2, 11. Jan. 2016)

#### Eclipse Update Site
- Win 32bit: http://dl.bintray.com/minho/dexter-eclipse-32/
- Win 64bit: http://dl.bintray.com/minho/dexter-eclipse-64/

#### Dexter Daemon for Source Insight
- [Win 32bit](https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-daemon_0.9.2_32.zip?version=1&modificationDate=1452312234246&api=v2)
- [Win 64bit](https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-daemon_0.9.2_64.zip?version=1&modificationDate=1452477778902&api=v2)
 
#### Dexter CLI
- [Win/Linux/Mac 32bit](https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-cli_0.9.2_32.zip?version=1&modificationDate=1452310884778&api=v2)
- [Win/Linux/Mac 64bit](https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-cli_0.9.2_64.zip?version=1&modificationDate=1452311046828&api=v2)

#### Dexter Server
- [Win/Linux/Mac](https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-server_v0.9.2.zip?version=1&modificationDate=1452470183908&api=v2)
 
## Supports
- Eclipse IDE for Java language (Juno 4.2+)
- Source Insight for C/C++ (3.50.0072+)
- CLI for executing by a script (Jenkins, other editors)

## Concepts 
Dexter is a static analysis platform to find and remove defects efficiently and immediately during the coding-time.  In addition, Dexter saves your learning cost because it contains multiple static analysis tools as a plug-in type.

![](https://github.com/Samsung/Dexter/blob/master/wiki/image/overview.png)


# How to build
(refer to detailed document : https://github.com/Samsung/Dexter/blob/master/doc/Dexter_Build_Guide.pdf)

## Prerequisites
- Install JDK 7 update 40+
- Install Gradle (http://gradle.org)
- Install NodeJS (https://nodejs.org)
- Download Eclipse RCP/RAP Juno + (http://www.eclipse.org/downloads/packages/release/juno/sr2)
- Download Dexter Source Codes (https://github.com/Samsung/Dexter)

## Import Dexter Projects into Eclipse 
- run with suffice memory (in eclipse.ini) : -Xmx1024m --launcher.XXMaxPermSize256m
- make sure your text file encoding setting is 'UTF-8' : eclipse > Preferences > General > Workspace
- import Dexter Projects that you already downloaded except dexter-server project
- use 'gradle build' command in a command line console : '/project' folder
- refresh all projects in eclipse, then all errors will be gone

## Build Dexter CLI
- use ant script on build-install.xml file in dexter-executor project
- you can see the 'dexter-cli_#.#.#_bit.zip file in dexter-executor/install folder
- after unzip this file, you can run dexter in a command line: unzip-folder/bin/dexter.bat or dexter.sh
- before you run dexter.bat file, you have to set dexter_cfg.json file to set the scope of analysis (refer to dexter_cfg.json.help file)

## Build Dexter Daemon for source insight
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

## Build Dexter Eclipse Plugins
- create feature project in your eclipse
- include all of dexter projects witout dexter-daemon and dexter-cppcheck project
- create update site on the feature.xml file
- include your feature and build all, then you will see the feature and plugins folders in you update project
- you can make a update site or just copy plugin folder into your new eclipse
- then, you can use Dexter

# Future Plan
## support more languages
- JavaScript

## support more IDEs
- Tizen SDK
- WebStorm
- Android Studio
- Enhance Source Insight

## support more Static Analysis Open Source
- Java: PMD, Checkstyle, etc.
- JavaScript: Flow, JSHint
- Custom Checkers

## Dexter Web Monitor for organiztion(SE)
- TBD 

# License
Dexter is BSD-licensed.
