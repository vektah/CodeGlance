CodeGlance [![CircleCI](https://circleci.com/gh/Vektah/CodeGlance/tree/master.svg?style=svg)](https://circleci.com/gh/Vektah/CodeGlance/tree/master)
==========

Plugin Repository: http://plugins.jetbrains.com/plugin/7275  
Latest build: https://github.com/Vektah/CodeGlance/releases

Intelij plugin that displays a zoomed out overview or minimap similar to the one found in Sublime into the editor pane. The minimap allows for quick scrolling letting you jump straight to sections of code.

 - Works with both light and dark themes using your customized colors for syntax highlighting.
 - Worker thread for rendering
 - Color rendering using intelij's tokenizer
 - Scrollable!
 - Embedded into editor window
 - Complete replacement for Code Outline that supports new Intellij builds.

![Dracula](https://raw.github.com/Vektah/CodeGlance/master/pub/example.png)


Building using gradle
====================
```
git clone https://github.com/Vektah/CodeGlance
cd CodeGlance
# run the tests
./gradlew test

# build the plugin and install it in the sandbox then start idea
./gradlew runIdea

# build a release
./gradlew buildPlugin

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
