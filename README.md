# Dexter <img src="../master/wiki/images/DevelopedBySamsung.svg" height="auto" width="20%" title="Not loading? Click to see full size!"/>
The **DE**fect e**XTER**minator

**Dexter is a static analysis tool** for detecting and removing defects 
and improving code quality even before a third party review. 
In addition, Dexter allows storing the analysis results on Dexter Server 
and using customized checkers as plugins. You can code safely now!

<details open>
<summary>
<b>Table of contents</b>
</summary>
<li> <a href="#how-does-it-work">How does it work?</a>
<li> <a href="#defects-same-as-ide-hints">Defects? Same as IDE hints?</a>
<li> <a href="#ok-but-do-you-need-dexter">Ok, but do <u>YOU</u> need Dexter?</a>
<li> <a href="#what-ides-does-dexter-support">What IDEs does Dexter support?</a>
<li> <a href="#where-can-i-find-more">Where can I find more?</a>
<li> <a href="#download-dexter-now">Download Dexter now! </a>
</details>

<details>
<summary>
<font size="1">
<b>Disclaimer</b>
</summary>

GitHub no longer supports Internet Explorer, neither does this page. 
If anything is displayed incorrectly, illegibly, not at all etc. 
please make sure that you are using one of the 
<a href="https://help.github.com/articles/supported-browsers/">supported browsers</a>. 
</font>
</details>

## How does it work?

<img src="../master/wiki/images/DexterDiagram.svg" height="auto" width="100%" title="Not loading? Click to see full size!"/>

Whenever you commission the scrutiny 
(manually or after saving the file - it's configurable) 
Dexter performs the analysis on a scope of a file, project or solution. 
It checks only the appropriate files (e.g. *.cs for C#) 
and shows you directly in code where the **defects** may appear. 

<img src="../master/wiki/images/Catch (Bug[] bugs).svg" height="auto" width="50%" title="Not loading? Click to see full size!"/>
<i>Psst! If you've already found the mistake in the code above, 
perhaps you should <a href="#contribution">think of contributing to Dexter</a>?</i>

## Defects? Same as IDE hints?

<img src="../master/wiki/images/DexterVsIDE.svg" height="auto" width="40%" title="Not loading? Click to see full size!"/>

**Not exactly**. 
What Dexter finds are not the obvious errors that any IDE can find. 
It looks for defects as trivial as possible "array index out of bounds" 
up to less anticipated like "usleep misuse" (see [the full list of checkers here](https://dexter.atlassian.net/wiki/spaces/DW/pages/3211284/Checker+List)).

## Ok, but do <u>YOU</u> need Dexter?

<img src="../master/wiki/images/DexterYesOrNo.svg" height="auto" width="40%" title="Not loading? Click to see full size!"/>

**You would definitely benefit from having Dexter if** 
you fall into any of the categories:
<ul>
<li> A coder who hates coding gremlins
<li> A developer who is so passionate about coding that sometimes forget about quality (we all do!)
<li> A developer who needs a peer reviewer (but is to shy to ask)
<li> A software engineer who wants to elevate the rank of their code
<li> A static analysis engine developer who needs UI and server-side features
</ul>

However, *you do not need Dexter* if you are a developer 
and you find yourself in one of these groups:

<ul>
<li> 
</ul>

## What IDEs does Dexter support?
Dexter supports the most popular programming Environments and also provides a CLI version for you to use!

<details>
<summary>
See which ones!
</summary>

<img src="../master/wiki/images/logos/EclipseLogo.svg" height="50px" width="auto" title="Not loading? Click to see full size!"/>

**Eclipse** IDE for Java (Kepler 4.3 and highers)

<img src="../master/wiki/images/logos/MicrosoftVisualStudioLogo.svg" height="50px" width="auto" title="Not loading? Click to see full size!"/>

**Microsoft Visual Studio** (2017 and higher) for C#

<img src="../master/wiki/images/logos/SourceInsightLogo.png" height="40px" width="auto" title="Not loading? Click to see full size!"/>

**Source Insight** for C/C++ (3.50.0072+)

<img src="../master/wiki/images/logos/CLILogo.svg" height="50px" width="auto" title="Not loading? Click to see full size!"/>

**CLI** - yes, command-line interface is not an IDE, but you can also execute Dexter from it (Jenkins, other editors)
</details>

## Where can I find more?

<details>
<summary>
There are two ways to find out more about Dexter...
</summary>

For much more detailed information and a rich Dexter resource please refer to Dexter Wiki at Atlassian:
- [Dexter Wiki (How to install/Use)](https://dexter.atlassian.net)

...and in the mean time check out the (old but gold) video about Dexter!
- [Dexter Introduction Video (4min)](https://youtu.be/86exIHcwi6c) 
</details>

## Download Dexter now! 
<details>
<summary>
<b>Latest Version 0.10.6</b> (20. Oct. 2016)
</summary>

#### Eclipse Update Site

##### Eclipse JAVA+CPP 
- Win [32bit](http://dl.bintray.com/minho/dexter-eclipse-32)
 / [64bit](http://dl.bintray.com/minho/dexter-eclipse-64)

##### Eclipse JAVA
- Win [32bit](http://dl.bintray.com/minho/dexter-eclipse-java-32)
 / [64bit](http://dl.bintray.com/minho/dexter-eclipse-java-64)

##### Eclipse C/C++
- Win [32bit](http://dl.bintray.com/minho/dexter-eclipse-cpp-32)
 /[64bit](http://dl.bintray.com/minho/dexter-eclipse-cpp-64)

##### Tizen SDK 
- Win [32bit](http://dl.bintray.com/minho/dexter-tizen-sdk-32)
 / [64bit](http://dl.bintray.com/minho/dexter-tizen-sdk-64)

#### Dexter Daemon for Source Insight
- Win [32bit](https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-daemon_0.10.6_32.zip?api=v2) 
/ [64bit](https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-daemon_0.10.6_64.zip?api=v2)
 

#### Dexter CLI
- Win/Linux/Mac [32bit](https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-cli_0.10.6_32.zip?api=v2)
 / [64bit](https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-cli_0.10.6_64.zip?api=v2)

#### Dexter Server
- [Win/Linux/Mac](https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-server_0.10.6.zip?api=v2)

#### Dexter Monitor
- [Win/Linux/Mac](https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-monitor_0.10.6.zip?api=v2)
</details>

# Contribution
Dexter is fully open source so do not hesitate to explore the deepest guts of it 
and we heartfully invite you to contribute. 
Simply check the "How to build" below and setup your first self-made instance of Dexter. 

Then - if you are feeling brave and creative - just hit us with a clever GitHub Pull Request! 

# How to build
***For mighty developers!***

**Check out the detailed document** at https://github.com/Samsung/Dexter/blob/master/doc/Dexter_Build_Guide.pdf

<details>
<summary>
<b>... or see the general steps!</b>
</summary>

## Prerequisites
- Install JDK 7 update 40+
- Install Gradle (http://gradle.org)
- Install NodeJS v4.4.2+ (https://nodejs.org) 
- Download Eclipse RCP/RAP Kepler + (http://www.eclipse.org/downloads/packages/release/Kepler/sr2)
- Download Dexter Source Codes (https://github.com/Samsung/Dexter)

## Import Dexter Projects into Eclipse 
- run with sufficient memory (in eclipse.ini) : -Xmx1024m --launcher.XXMaxPermSize256m
- make sure your text file encoding setting is 'UTF-8' : Eclipse > Preferences > General > Workspace
- import Dexter Projects that you already downloaded except dexter-server project
- use 'gradle build' command in a command line console : '/project' folder
- refresh all projects in eclipse, then all errors should disapear

## Build Dexter CLI
- use ant script on build-install.xml file in dexter-executor project
- you can see the 'dexter-cli_#.#.#_bit.zip file in dexter-executor/install folder
- after unzipping this file, you can run dexter in a command line: unzip-folder/bin/dexter.bat or dexter.sh
- before running dexter.bat file, you have to set dexter_cfg.json file to set the scope of analysis (refer to dexter_cfg.json.help file)

## Build Dexter Daemon for source insight
- open dexter-daemon.product file in dexter-daemon project
- click 'Eclipse Product export wizard' link on the 'Exporting' tab in a editor
- set the fields - Root directory:dexter-daemon  Destination/Directory: your directory
- click "Finish" button
- check export folder, it should contain dexter-daemon folder
- run dexter.exe, then you can see the login dialog
- check 'Run in Standalone mode' (running Dexter without Dexter Server)
- run Source Insight, and open 'Base' project
- add 'project/script/dexter.em' macro file to 'Base' project
- after editing and saving your source file, you will see the result of analysis in your editor and Dexter Daemon

## Build Dexter Eclipse Plugins
- create a Feature project in Eclipse
- include all of Dexter projects without dexter-daemon and dexter-cppcheck projects
- create update site on the feature.xml file
- include your feature and build all, then you will see the feature and plugins folders in you update project
- you can make an update site or just copy plugin folder into your new Eclipse
- then, you can use Dexter

## Build Dexter Visual Studio Plugin
In order to build a plugin for Visual Studio you need what follows:
- Microsoft Visual Studio 2013 or newer
- Microsoft Visual Studio SDK (2013 or newer)
- (Optionally) NUnit3 Test Adapter (Visual Studio Extension) - for executing unit tests

To build a plugin:
- open a solution file "dexter-vs.sln" located in project/dexter-vs directory
- right click on "Build/Rebuild dexter-vs". NuGet will download all required dependencies and Visual Studio will rebuild you project
- after this, you should have a self-installing extension file "dexter.vsix" located in "dexter-vs/bin/{ConfigurationName}"
- you can install an extension by double clicking it

The most convenient way to debug this plugin is to use an Experimental Instance of Visual Studio
- go to the "Project/dexter-vs Properties..." and then go to the "Debug" tab
- select "Start external program:" and provide path to Visual Studio executable (devenv.exe)
- enter "/RootSuffix Exp" in "Command line arguments"
- save your changes 
- run a project (F5) 
- it should start Visual Studio Experimental Instance with dexter-vs installed as an extension

However, if you decide to test the extension by installing it from the .vsix file (generated in the Debug folder), 
remember to **disable the Visual Studio "automatic extension update"** 
(Options->Environment->Extensions and Update->Automatically update extensions). 
The automatic update will change the plugin to the most recent version on the Marketplace, 
i.e. get rid of all the development changes. 

</details>

# Future plans
<ul>
<li>Supporting more languages
    <ul>
    <li> JavaScript
    </ul>
</ul>

<ul>
<li>Supporting more IDEs
    <ul>
    <li>Tizen SDK
    <li>WebStorm
    <li>Android Studio
    <li>Enhance Source Insight
    </ul>
</ul>

<ul>
<li>Support for more Static Analysis Open Source tools
    <ul>
    <li>Java: PMD, Checkstyle, etc.
    <li>JavaScript: Flow, JSHint
    <li>Custom Checkers
    </ul>
</ul>

<ul>
<li>Dexter Web Monitor for organizations (SE)
    <ul>
    <li>TBD
    </ul>
</ul>

# License
Dexter is fully open source and BSD-licensed. See [the full license](../master/LICENSE) (it's really short, even we've read it).

<br>
<br>
<br>
<p align="center">
<img src="../master/wiki/images/DevelopedBySamsung.svg" height="auto" width="50%" title="Not loading? Click to see full size!"/>
</p>
<br>
<br>