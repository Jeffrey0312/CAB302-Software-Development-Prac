CAB302 Software Development
===========================

# Week 11: The Integrated Build Process

The classes for this week are about integrated builds in the modern development environment. We begin slowly, but build up to generating a professional build file for some existing code. We will work with the source code from a past semester's assignment that implements a simple simulation of a warehouse. We also have some associated unit tests and we will be exploring that relationship a little.

## Ant

Ant is a command line program written in Java that can be downloaded and installed on any platform. IntelliJ itself with a bundled version of Ant (unless you explicitly disabled this during installation), but for this exercise we will download a recent version of Ant and either use it from the command line or tell IntelliJ to use it instead. It might be preferable to follow the instructions just so you can edit Ant build files from IntelliJ with functioning autocomplete.

First install Ant by following the instructions from the [Ant manual](http://ant.apache.org/manual/index.html). 

Once you have installed Ant, type `ant` at the command prompt. You should see a failure message as follows indicating that the `ant` executable ran
but lacks a build file:

    Buildfile: build.xml does not exist! Build failed

You can actually supply any buildfile name you wish, but people seldom do this, so we will create a `build.xml` file of
our own from scratch, and use it to control the compilation, testing and deployment of the application.

Next, because we want to use JUnit 5 with Ant, we need to go and add some additional files to Ant's lib directory. Copy the following files from the `lib` directory in this exercise's repository into Ant's `lib` directory:

* junit-jupiter-api-5.4.0.jar
* junit-jupiter-engine-5.4.0.jar
* junit-platform-commons-1.4.0.jar
* junit-platform-engine-1.4.0.jar
* junit-platform-launcher-1.4.0.jar
* opentest4j-1.1.1.jar

While you are in Ant's `lib` directory, verify that `ant-junitlauncher.jar` is also present- otherwise you will have to
[download the latest version](https://mvnrepository.com/artifact/org.apache.ant/ant-junitlauncher) and put it in there. 

We add the files to Ant's `lib` directory just to ensure that they all end up in the classpath when we run Ant, 
whether we run it from the command-line or from within IntelliJ.

## Project structure

We are going to use a slightly different directory structure for this project; separating the unit
test code from the code for the model classes, a standard approach employed by Java developers. We will, however, need to be very careful as there are significant advantages to maintaining the same package structure for the test classes as for the model.

The source code for this practical is contained within subdirectories of the `src` folder of the
repository, as you have come to expect. We now
assume that we need to build the project repeatedly, and in a professional environment. We first work to produce a
directory structure that matches the package structure, and thence to achieve a build file that handles both the
compilation of the model and test files, along with the execution of the unit tests themselves. Maven and Gradle are designed around a common project directory structure (known as the Standard Directory Layout) and expect application and test source files to be in specific locations. Ant is more flexible, but it still helps to separate test and application code.

Upon cloning the repository, the top-level project directory will be called `prac10`. These instructions will assume the name of this directory has not been changed.

Now examine the provided source code. In the `src/answer` directory you will see that the first line of each of the
source files specifies package membership as shown:

    package answer;

The first thing we want to do is move the test files into their own directory, which we will create now. Create a directory called `utest` in te top-level project directory (in other words, alongside `src`). The separation used here is that the .java files for unit tests will go in the `utest` directory and the .java files for the application will go in the `src` directory. Next, within the `utest` directory, create another directory called `answer`, corresponding to the package that the two unit test files are found in. Then move the `LedgerTest.java` and `TransactionsTest.java` files from the `src/answer` directory to the `utest/answer` directory.
Repeat these tasks for the `question` directory, again placing the files in the appropriate locations. You should
see this time that there are no unit tests to be considered and so no directory of this name is needed in the `utest`
hierarchy.

By now we should have:

* \...\prac10\src\question
   * Ledger.java
   * Simulation.java
   * SimulationComponents.java
   * SimulationFrame.java
   * Transactions.java
   * WarehouseException.java
* \...\prac10\src\answer
   * WarehouseLedger.java
   * WarehouseTransactions.java
* \...\prac10\utest\answer
   * LedgerTest.java
   * TransactionsTest.java

The root of the path will vary with your own installations. The key is that the source and unit test directories follow the same package structure, but with separate locations for the application source files and the unit test source files, which are of course very different things. You can use 'Mark Directory As -> Test Sources Root' to mark the `utest` directory as a directory for unit tests, which will allow you to continue running unit tests from IntelliJ; however, this step is optional as we will be doing builds from Ant hereforth.

(As you will recall, when writing Java, the directory structure is chosen to match the package structure, which is why we needed to replicate the package name in the `utest` directory structure too.)

## Build file

Now change to the top directory of `prac10`, which at this point should contain `src` and `utest` directories. Create a file called `build.xml` with the elements and attributes described below. You should run ant from the command line after you've completed each significant edit, as this will confirm that the syntax is OK. You will find it easier to close each tag as it is opened, thus maintaining syntactically correct XML.

Adjust any paths as needed in the instructions below. You may also find it helpful to have the lecture slides up for this as you will be writing an Ant build script from scratch here.

The required steps are as follows:

1) In the `build.xml` file, create an ant `<project>` called `prac10`, with a default target of `build`, and a basedir of `.`

Run ant on the command line by navigating to the top-level project directory and typing

`ant`

You should see an error message similar to: 

`BUILD FAILED`
`Target "build" does not exist in the project "prac10".`

Keep running ant after each step. You should see the same error message up until step 8. If you see something different then there is likely a syntax error with your XML that you will need to fix.

As an extra step, so that we will be get correct syntax highlighting from within IntelliJ, and so we can run the build file from within IntelliJ at all, we want to tell IntelliJ to use our Ant, rather than the bundled one. Right-click `build.xml` from within IntelliJ and choose the option 'Add as Ant Build File'. This will cause the 'Ant Build' frame to appear on the side (if it doesn't expand automatically, you may have to click 'Ant Build' to get it to expand. It should show a single Ant project named `prac10`. Right-click on this project and click 'Properties'. This will open up a new window. Within this window, click the 'Execution' tab, then tell it to 'Use custom Ant'. In the next window, click to add a new Ant install, then navigate to the directory where you installed Ant and add it. Select this Ant install, then click OK to go back to IntelliJ. You should now be able to run each step either from the command-line or from within IntelliJ.

2) Create a property called `base` with a `value` of `artifacts`, meaning that it will sit below the basedir
specified above. This is a working target directory. At this point you should also create a property for a working directory for test
output (the property name should be `testBase` and the value should be `testFiles`) and a deployment directory for the final output from the build. The deployment
directory is completely up to you, but one example is given here to use as a model for all property setting:

```
<property name="deploy" value="C:/Prac10Deployment" />
```

Note that this does not need to be an absolute path- if you give it a relative path (e.g. just `Prac10Deployment`) the resulting build will be deployed into that directory, relative to the top-level project directory where the Ant build script is located.

3) Create more properties to specify the location of the JUnit jar files (we do not need all of them- just these four.)

```
<property name="junitJar" value="lib/junit-jupiter-api-5.4.0.jar" />
<property name="junitPlatformJar" value="lib/junit-platform-commons-1.4.0.jar" />
<property name="opentestJar" value="lib/opentest4j-1.1.1.jar" />
<property name="apiguardianJar" value="lib/apiguardian-api-1.0.0.jar" />
```

4) Create a target called `prepare`, specifying its description attribute as `"Create target directories"`. Within the
target element (i.e. before the closing tag), insert five `mkdir` tasks to create the `deploy`, `base` and `testBase` directories (remember to use `${propname}` when referring to a property named propname), the JavaDoc directory, and a directory for the unit test class files. In the latter case, the syntax is as follows:

```
<mkdir dir="utest/classes" />
```

5) Now create a new target to compile the java source files. You should specify the path as shown, but you need not
specify the source files explicitly. My own practice is to add source files package by package to this command. This is
a balance between automation and actually understanding what is going on. The syntax will look something like this:

```
<target name="compile" depends="prepare"
    description="Compile source" >
  <javac srcdir="src" classpath="${base}" destdir="${base}"
      includeantruntime="false">
    <include name="question/*.java" />
    <include name="answer/*.java" />
  </javac>
</target>
```

Note the dependency on the `prepare` target, and the use of the Linux-style forward slashes for the directory. Use these
regardless of the system, windows or otherwise. The `classpath` attribute is necessary so that the compiler can find
imports in the packages of the current system. Remember that Java places classfiles by default within a directory
structure mirroring the packages. Class files will thus appear (based on the `destdir` setting) in directories under
`${base}`. Always use the `include` element as shown -- it allows convenient control of the files in the build. The
`includeantruntime` attribute is optional but setting it to false eliminates a warning about it not being set. This
attribute is used to indicate if your code makes use of the ant runtime classes.

6) Now create targets to handle the unit tests. The first is to compile the unit test source, and should largely mimic
`compile`. We then have to run the unit tests using a `junit` task. Copy and paste the compile target and use it as a
basis for the new target, which we will call `compileTests`. Its dependency is `compile` and the `srcdir` is plainly
`utest`, and the `destdir` `utest/classes`.

7) The critical point is to update the `classpath` to reference the JUnit jar files. Your target should thus look
something like this:

```
    <target name="compileTests" depends="compile"
            description="Compile unit test source">
        <!-- Compile unit test source -->
        <javac srcdir="utest" classpath="${base}:${junitJar}:${junitPlatformJar}:${opentestJar}:${apiguardianJar}"
               destdir="utest/classes" includeantruntime="false">
            <include name="answer/*.java" />
        </javac>
    </target>
```

8) Run ant at the command line with `ant compileTests` which should cause all of the targets to be executed, and thus
the compilation of the model and the test files. Do not proceed until you get output something like that seen below.
Note that ant will be pretty silent if some tasks have already been executed, and it may be very silent if you specify
compilation of a directory of source files that simply doesn't exist. So, your results may vary somewhat, but take a
good look at the resulting structures and see what you get.

(don't worry about any compiler warnings- some of the code is quite old and results in warnings in modern versions of Java)

9) The `junitlauncher` test runner task is tricky, so I will provide it in full here and explain it:

```
	<target name="utest" depends="compileTests" description="Run JUnit">
		<junitlauncher printSummary="true" failureProperty="junit.failure">
			<classpath>
				<pathelement path="utest/classes:${base}" />
				<pathelement path="${junitJar}:${junitPlatformJar}:${opentestJar}:${apiguardianJar}" />
			</classpath>
			<testclasses outputDir="${testBase}">
				<fileset dir="utest/classes"/>
				<listener type="legacy-xml"/>
			</testclasses>
		</junitlauncher>
	</target>
```

10) The key issues for successful testing are the dependency on `compileTests`, the specification of the `testclasses`
element, which runs all of the test classes available (as specified by the `fileset` element), and the use of an
XML-based test result formatter. Note the four elements of the `classpath`, separated by `':'` characters. However, note
the critical `failureProperty`, which allows us to specify the behaviour when the tests fail. We shall come to this
below.

11) Invoke ant with the command line `ant utest` which should execute two test classes successfully. Take note of the
failure counts and error counts.

12) We are now almost complete, but there are a couple of things to do. Have a look in the `${testBase}` directory and
you will find a couple of xml files containing the results from the `junit` run. Browse them and get a sense of their
structure.  Now add in the final line of the `utest` target, which
invokes the error facilities of JUnit and thus of ant:

    <fail if="junit.failure" message="junit tests failed - aborting"/>

13) Open up the test class `LedgerTest.java`, and change one of the parameters in a specified test, causing it to fail.
What is the behaviour of ant in these circumstances?

14) Next, we are going to add two targets to the build file: one, called `build`, will pull everything together by
producing a jar from the completed code. This will become the default target, and the chain of dependencies will mean
that it cannot proceed without all the other tasks working. For now, leave the error in the `LedgerTest` class. The
`jar` task is very straightforward, but we will do a basic manifest as well -- a specification of those things which are
placed in the file. The `jar` task may be specified as follows:
```
    <target name="build" depends="utest" >
      <jar destfile="WarehouseSimulation.jar" basedir="${base}">
        <manifest>
          <!-- Who is building this jar? -->
          <attribute name="Built-By" value="${user.name}"/>
          <!-- Information about the program itself -->
          <attribute name="Implementation-Vendor" value="QUT"/>
          <attribute name="Implementation-Title" value="CAB302 Exercise"/>
          <attribute name="Implementation-Version" value="1.0.0"/>
        </manifest>
      </jar>
    </target>
```
15) Run it (either by typing `ant` or `ant build`) before fixing the error in the `LedgerTest` class, and we see that the test causes the whole build to fail,
and the jar is not produced. Fix the error, and the build completes successfully.

Your `ant build` should look like this:

```
Buildfile: /home/ben/IdeaProjects/prac10/build.xml

prepare:
    [mkdir] Created dir: /home/ben/IdeaProjects/prac10/deployment
    [mkdir] Created dir: /home/ben/IdeaProjects/prac10/testFiles
    [mkdir] Created dir: /home/ben/IdeaProjects/prac10/artifacts
    [mkdir] Created dir: /home/ben/IdeaProjects/prac10/utest/classes
    [mkdir] Created dir: /home/ben/IdeaProjects/prac10/utest/report

compile:
    [javac] Compiling 8 source files to /home/ben/IdeaProjects/prac10/artifacts

compileTests:
    [javac] Compiling 2 source files to /home/ben/IdeaProjects/prac10/utest/classes

utest:
[junitlauncher] 
[junitlauncher] Test run finished after 65 ms
[junitlauncher] [         2 containers found      ]
[junitlauncher] [         0 containers skipped    ]
[junitlauncher] [         2 containers started    ]
[junitlauncher] [         0 containers aborted    ]
[junitlauncher] [         2 containers successful ]
[junitlauncher] [         0 containers failed     ]
[junitlauncher] [        41 tests found           ]
[junitlauncher] [         0 tests skipped         ]
[junitlauncher] [        41 tests started         ]
[junitlauncher] [         0 tests aborted         ]
[junitlauncher] [        41 tests successful      ]
[junitlauncher] [         0 tests failed          ]
[junitlauncher] 
[junitlauncher] 
[junitlauncher] Test run finished after 11 ms
[junitlauncher] [         2 containers found      ]
[junitlauncher] [         0 containers skipped    ]
[junitlauncher] [         2 containers started    ]
[junitlauncher] [         0 containers aborted    ]
[junitlauncher] [         2 containers successful ]
[junitlauncher] [         0 containers failed     ]
[junitlauncher] [        25 tests found           ]
[junitlauncher] [         0 tests skipped         ]
[junitlauncher] [        25 tests started         ]
[junitlauncher] [         0 tests aborted         ]
[junitlauncher] [        25 tests successful      ]
[junitlauncher] [         0 tests failed          ]
[junitlauncher] 

build:
      [jar] Building jar: /home/ben/IdeaProjects/prac10/WarehouseSimulation.jar

BUILD SUCCESSFUL
Total time: 1 second
```

16) Next you should produce some Javadoc. We do this by specifying which packages we would like to create Javadoc for, and then list some options. Javadoc may be produced as follows:
```
    <target name="doc" >
        <javadoc packagenames="answer, question"
  		 sourcepath="src"
  		 destdir="doc"
  		 author="true"
  		 version="true"
  		 use="true"
  		 windowtitle="Warehouse Ledger Project API">
                 <doctitle><![CDATA[<h1>Warehouse Ledger Simulation</h1>]]></doctitle>
                 <bottom><![CDATA[<i>Copyright &#169;  QUT. All Rights Reserved.</i>]]></bottom>
        </javadoc>
   </target>
```
Run ant with the appropriate target to produce the Javadoc. Have a look at the HTML files that you produce and see how the options present themselves in the produced files. Delete the Javadoc directory, change the options above, and see how these changes are reflected in the new files produced. 

17) You should now introduce a few more targets to finish. The first, which will depend on `build`, will be called
`deploy`, and will involve creating a directory somewhere on the system containing a jar of the executable code and a
batch file to enable you to execute it. This is common practice for java apps in order to avoid the need to type huge
command lines. If you are struggling to specify an appropriate run command in java, try appropriate entries here:

```
java –classpath <jarname> <package.className>
```

and place this inside (say) `WarehouseSimulation.bat` in the prac10 directory. Then write the `deploy` target, which
simply copies this batch file and `WarehouseSimulation.jar` to the deployment directory you specified earlier. You now have a very
good, well structured build file. What is the chain of dependencies for build and deploy?

18) Often it is a good idea to produce a target that ‘cleans’ the directories of everything that you have built. This allows you to start fresh if you want to rebuild everything from scratch. Create a target called `clean`  that will delete all the files and directories that you have created. Run it, then try to rebuild everything. 

19) Finally, we want to produce a zip file of the source which can be bundled with the javadoc. What are the dependencies required for the source target?
