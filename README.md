# jdt-codemining

[![Build Status](https://secure.travis-ci.org/angelozerr/jdt-codemining.png)](http://travis-ci.org/angelozerr/jdt-codemining)

Eclipse plugin for `JDT Java CodeMining` (Experimental)

JDT CodeMining is a an Eclipse plugin which requires Eclipse Photon M7 based on JFace Text which provides [CodeMining support](https://www.eclipse.org/eclipse/news/4.8/M5/#Platform-Dev). 

Please star this project if you want to have those CodeMinings inside JDT. After discussing with JDT Team, this CodeMining support will not included for Photon release in June but perhaps in september if jdt-codemining receives good feedback. Indeed as CodeMining is a new feature and JDT Team are very busy, they prefer not include my work on Java CodeMining.  
 
So please star this github project and [create any issues](https://github.com/angelozerr/jdt-codemining/issues) to know your feelings and bugs. Thanks!

Issues can be about:

 * performance: indeed StyledText is not performant with big file and variable line height mode. As CodeMining can affect the line height (variable line height) to draw the mining before a line (like references), you could have trouble with performance. StyledText was improved to have better performance with variable line height, but it's not perfect again yet. So please fill issues if you have troubles by describing your case (attach the big Java file, describes what you do, etc).
 * regression: TODO:
 * new feature: TODO

# Features

jdt-codemining provides several JDT Java CodeMining:

 * Show references
 * Show implementations
 * Show method parameters
 * Show JUnit status  
 * Show JUnit run
 * Show JUnit debug
 
By default minings are disabled, you must activate them with preferences:

![](images/JavaCodeMiningPreferences.png)
 
# Demos 
 
Here several demos;

 * Show references:
  
![Java CodeMining Demo](images/JavaCodeMiningDemo.gif)

 * Method Parameter CodeMining
 
![JUnit CodeMining Demo](images/JavaCodeMiningParameterDemo.gif)

 * JUnit CodeMining
 
![JUnit CodeMining Demo](images/JUnitCodeMiningDemo.gif)

# 