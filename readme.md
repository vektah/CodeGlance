CodeGlance [![Build Status](https://travis-ci.org/Vektah/CodeGlance.png?branch=master)](https://travis-ci.org/Vektah/CodeGlance)
==========

Plugin Repository: http://plugins.jetbrains.com/plugin/7275  
Latest build: http://public.vektah.net/codeglance/net/vektah/CodeGlance/1.4.4/CodeGlance-1.4.4.jar

Intelij plugin that displays a zoomed out overview or minimap similar to the one found in Sublime into the editor pane. The minimap allows for quick scrolling letting you jump straight to sections of code.

 - Works with both light and dark themes using your customized colors for syntax highlighting.
 - Worker thread for rendering
 - Color rendering using intelij's tokenizer
 - Scrollable!
 - Embedded into editor window
 - Complete replacement for Code Outline that supports new Intellij builds.

Dark:
![Dracula](https://raw.github.com/Vektah/CodeGlance/master/pub/dark.png)

Light:
![Default](https://raw.github.com/Vektah/CodeGlance/master/pub/light.png)


Building using maven
====================
With gradle installed you can:
```
git clone https://github.com/Vektah/CodeGlance
cd CodeGlance
# run the tests
gradle test

# build the plugin and install it in the sandbox then start idea
gradle runIdea

# build a release
gradle buildPlugin

```
The result will be saved as build/distributions/CodeGlance-{version}.zip


Running from source in Intellj
===================
1. Make sure you have the Plugin DevKit installed.
2. Checkout sources from github
3. Create a new Intellij Platform plugin project
4. Select source directory, chose a plugin sdk (create one that points to your intellij install).
5. Mark src/main/java as source root, and src/test/java as test root.
6. In order to run tests you will need to find mockito and testng jars. I usually do this with gradle.
7. In module settings set the path to META-INF to src/main/resources
8. Hit Run.
