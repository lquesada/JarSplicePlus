JarSplicePlus
=============

JarSplicePlus (Jar File Merger - An Extension to JarSplice)  
Copyright (c) 2013, Luis Quesada - https://github.com/lquesada

JarSplice (The Fat Jar Creator) was originally published in The Ninja Cave (http://ninjacave.com/).
JarSplice allows merging jar files and native libraries into a single jar or GNU/Linux, Windows, or Mac OS executable files.

However, this tool lacked features such as a command-line interface for integrating it into build chains.
JarSplice site states that "JarSplice is free for any type and application" and that "the source code for the actual JarSplice application will be under a BSD License".
Unfortunately, and despite the requests of JarSplice users, it's been almost two years and neither a version with command-line interface support or the source code have been published.

From the stated intention of the authors, and since I really needed this feature, I decided to write an alternative command-line interface launcher, JarSpliceCLI.

Now, as I still wanted to get acquainted with the internals of JarSplice, I've decided to decompile it and implement the command-line interface support right into it.
Therefore, the source package org.ninjacave.jarsplice contains the source code obtained from decompiling JarSplice using Java Decompiler (http://java.decompiler.free.fr/?q=jdgui) and slightly modified.

In the future, I'll implement support for exporting executable files from the command-line, and for exporting and importing projects from the GUI.
