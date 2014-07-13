CodeGlance [![Build Status](https://travis-ci.org/Vektah/CodeGlance.png?branch=master)](https://travis-ci.org/Vektah/CodeGlance)
==========

Plugin Repository: http://plugins.jetbrains.com/plugin/7275  
Latest build: http://public.vektah.net/codeglance/net/vektah/CodeGlance/1.4.0/CodeGlance-1.4.0.jar

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
With maven installed building the plugin yourself is a simple as:
```
git clone https://github.com/Vektah/CodeGlance
cd CodeGlance
mvn package
```
After it finsihes downloading dependencies and building you should now have
a **CodeGlance-${VERSION}.jar** in the directory. This can be tested in intellij by 
going to settings->plugins->install from disk.



Running from source in Intellj
===================
1. Make sure you have the Plugin DevKit installed. 
2. Checkout sources from github
3. Create a new Intellij Platform plugin project
4. Select source directory, chose a plugin sdk (create one that points to your intellij install).
5. Mark src/main/java as source root, and src/test/java as test root.
6. In order to run tests you will need to find mockito and testng jars. I usually do this with maven.
7. In module settings set the path to META-INF to src/main/resources
8. Hit Run.
