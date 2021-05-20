CAB302 Software Development
===========================

# Week 12: The Integrated Build Process

The classes for this week are about integrated builds in a modern development environment. We begin slowly, but build up to generating a professional build file for some existing code. We will work with the source code from a past semester's assignment that implements a simple simulation of a warehouse. We also have some associated unit tests and we will be exploring that relationship a little.

## Ant

Ant is a command line program written in Java that can be downloaded and installed on any platform. However, for the purpose of this exercise, we will be using the version of Ant bundled with IntelliJ IDEA. You may wish to check (via File -> Settings -> Plugins) that you have the Ant plugin enabled, but it should be by default.

Instructions for installing a standalone version of Ant are available [in the Ant documentation](https://ant.apache.org/manual/index.html) if you want to install Ant separately from IntelliJ. You will need to do some extra work, mostly around setting environment variables and possibly copying some of the .jar files in the`lib` directory into Ant's `bin` directory, if you choose to go down this route.

## Project structure

We are going to use a slightly different directory structure for this project; separating the unit test code from the code for the model classes; a standard approach employed by Java developers. We will, however, need to be very careful as there are significant advantages to maintaining the same package structure for the test classes as for the model.

The source code for this practical is contained within subdirectories of the `src` folder of the
repository, as you have come to expect. We now assume that we need to build the project repeatedly, and in a professional environment. We first work to produce a directory structure that matches the package structure, and thence to achieve a build file that handles both the compilation of the model and test files, along with the execution of the unit tests themselves. Maven and Gradle are designed around a common project directory structure (known as the Standard Directory Layout) and expect application and test source files to be in specific locations. Ant is more flexible, but it still helps to separate test and application code.

Upon cloning the repository, the top-level project directory will be called `prac11`. These instructions will assume the name of this directory has not been changed.

Now examine the provided source code. In the `src/answer` directory you will see that the first line of each of the
source files specifies package membership as shown:

    package answer;

The `utest` directory is similar, but contains the source code for unit tests. Note that only the `answer` package features unit tests; as a result, only the `answer` package is present in the `utest` directory. The `utest` directory has been marked as the test sources root in IntelliJ, so you can run the tests from within IntelliJ to verify that everything is working.

By now we should have:

* \...\prac11\src\question
  * Ledger.java
  * Simulation.java
  * SimulationComponents.java
  * SimulationFrame.java
  * Transactions.java
  * WarehouseException.java
* \...\prac11\src\answer
  * WarehouseLedger.java
  * WarehouseTransactions.java
* \...\prac11\utest\answer
  * LedgerTest.java
  * TransactionsTest.java

The root of the path will vary with your own installations. The key is that the source and unit test directories follow the same package structure, but with separate locations for the application source files and the unit test source files, which are of course very different things.

(As you will recall, when writing Java, the directory structure is chosen to match the package structure, which is why the package structure needs to be reproduced in the `utest` directory structure.)

## Build file

Now change to the top directory of `prac11`, which at this point should contain `src` and `utest` directories. Open the file called `build.xml` in this root directory. The file contains some properties, which have been defined for your convenience, but you will need to create build targets.

You should run Ant from the Ant tool window in IntelliJ (if it is not already open, go to View -> Tool Windows -> Ant to show it) after you've completed each significant edit, as this will confirm that the syntax is OK. Ant messages will appear in the 'Messages' window, which by default is on the bottom pane of IntelliJ, but you can also bring it up by going to View -> Tool Windows -> Messages.

You will likely find it helpful to have the lecture slides up for this as you will be writing an Ant build script from scratch here.

The required steps are as follows:

1) Create a target called `prepare`, specifying its description attribute as `"Create target directories"`. Within the
   target element (i.e. before the closing tag), insert five `mkdir` tasks to create the `base` and `testBase` directories (remember to use `${propname}` when referring to a property named propname), the JavaDoc directory, and a directory for the unit test class files. In the latter case, the syntax is as follows:

```
<mkdir dir="utestclasses" />
```

2) Now create a new target to compile the Java source files. You should specify the path as shown, but you need not specify the source files explicitly. My own practice is to add source files package by package to this command. This is a balance between automation and actually understanding what is going on. The syntax will look something like this:

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

Note the dependency on the `prepare` target, and the use of the Linux-style forward slashes for the directory. Use these regardless of the system, Windows or otherwise. The `classpath` attribute is necessary so that the compiler can find imports in the packages of the current system. Remember that Java places classfiles by default within a directory structure mirroring the packages. Class files will thus appear (based on the `destdir` setting) in directories under `${base}`. Always use the `include` element as shown -- it allows convenient control of the files in the build. The `includeantruntime` attribute is optional but setting it to false eliminates a warning about it not being set. This attribute is used to indicate if your code makes use of the Ant runtime classes.

3) Now create targets to handle the unit tests. The first is to compile the unit test sources, and this should largely mimic `compile`. We then have to run the unit tests using a `junit` task. Copy and paste the compile target and use it as a basis for the new target, which we will call `compileTests`. Its dependency is `compile` and the `srcdir` is plainly `utest`, and the `destdir` `utestclasses`.

4) The critical point is to update the `classpath` to reference the JUnit jar files. Your target should thus look something like this:

```
    <target name="compileTests" depends="compile"
            description="Compile unit test source">
        <!-- Compile unit test source -->
        <javac srcdir="utest" classpath="${base}:${junitJar}:${junitPlatformJar}:${opentestJar}:${apiguardianJar}"
               destdir="utestclasses" includeantruntime="false">
            <include name="answer/*.java" />
        </javac>
    </target>
```

5) Run the `compileTests` target from the Ant window, which should cause all of the targets to be executed, and thus the compilation of the model and the test files. Do not proceed until you can see that running `compileTests` produces these files where you expect them to. Note that Ant will be pretty silent if some tasks have already been executed, and it may be very silent if you specify compilation of a directory of source files that simply doesn't exist. So, your results may vary somewhat, but take a good look at the resulting structures and see what you get.

(don't worry about any compiler warnings- some of the code is quite old and results in warnings in modern versions of Java)

6) The `junitlauncher` test runner task is tricky, so I will provide it in full here and explain it:

```
	<target name="utest" depends="compileTests" description="Run JUnit">
		<junitlauncher printSummary="true" failureProperty="junit.failure">
			<classpath>
				<pathelement path="utestclasses:${base}" />
				<pathelement path="${junitJar}:${junitPlatformJar}:${opentestJar}:${apiguardianJar}" />
			</classpath>
			<testclasses outputDir="${testBase}">
				<fileset dir="utestclasses"/>
				<listener type="legacy-xml"/>
			</testclasses>
		</junitlauncher>
	</target>
```

7) The key issues for successful testing are the dependency on `compileTests`, the specification of the `testclasses` element, which runs all of the test classes available (as specified by the `fileset` element), and the use of an XML-based test result formatter. Note the four elements of the `classpath`, separated by `':'` characters. However, note the critical `failureProperty`, which allows us to specify the behaviour when the tests fail. We shall come to this below.

8) Run the `utest` target from the Ant window which should execute two test classes successfully. Take note of the
    failure counts and error counts.

9) We are now almost complete, but there are a couple of things to do. Have a look in the `${testBase}` directory and  you will find a couple of xml files containing the results from the `junit` run. Browse them and get a sense of their structure.  Now add in the final line of the `utest` target, which invokes the error facilities of Ant:
```
    <fail if="junit.failure" message="junit tests failed - aborting"/>
```
10) Open up the test class `LedgerTest.java`, and change one of the parameters in a specified test, causing it to fail. What is the behaviour of Ant in these circumstances?

11) Next, we are going to add two targets to the build file: one, called `build`, will pull everything together by producing a jar from the completed code. This will become the default target, and the chain of dependencies will mean that it cannot proceed without all the other tasks working. For now, leave the error in the `LedgerTest` class. The `jar` task is very straightforward, but we will do a basic manifest as well -- a specification of those things which are placed in the file. The `jar` task may be specified as follows:
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
12) Run it before fixing the error in the `LedgerTest` class, and we see that the test causes the whole build to fail, and the jar is not produced. Fix the error, and the build completes successfully.

The output of `build` should look like this:

```
Buildfile: /home/ben/IdeaProjects/prac11/build.xml

prepare:
    [mkdir] Created dir: /home/ben/IdeaProjects/prac11/testFiles
    [mkdir] Created dir: /home/ben/IdeaProjects/prac11/artifacts
    [mkdir] Created dir: /home/ben/IdeaProjects/prac11/utestclasses

compile:
    [javac] Compiling 8 source files to /home/ben/IdeaProjects/prac11/artifacts

compileTests:
    [javac] Compiling 2 source files to /home/ben/IdeaProjects/prac11/utestclasses

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
      [jar] Building jar: /home/ben/IdeaProjects/prac11/WarehouseSimulation.jar

BUILD SUCCESSFUL
Total time: 1 second
```

13) Next you should produce some Javadoc. We do this by specifying which packages we would like to create Javadoc for, and then list some options. Javadoc may be produced as follows:
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
Run Ant with the appropriate target to produce the Javadoc. Have a look at the HTML files that you produce and see how the options present themselves in the produced files. Delete the Javadoc directory, change the options above, and see how these changes are reflected in the new files produced.

14) Finally, it is usually a good idea to produce a target that ‘cleans’ the directories of everything that you have built. This allows you to start fresh if you want to rebuild everything from scratch. Create a target called `clean`  that will delete all the files and directories that you have created. Run it, then try to rebuild everything.

15) Finally, we want to produce a zip file of the source which can be bundled with the javadoc. What are the dependencies required for the source target?

# Continuous integration

For the final step, we want to set up a continuous integration (CI) pipeline to automatically run tests and produce builds whenever we update our code. There are different CI solutions available that would be suitable for this task. You may choose either GitHub Actions or Jenkins for this practical. Here is a brief overview of the pros and cons of each:

- GitHub Actions is usually quicker to get started with as you do not need to install any software. However, you do need to have your repository on GitHub and GitHub Actions are paid for out of a limited number of free minutes. If you already heavily use GitHub Actions you may be out of these. We recommend using GitHub Actions for this practical.

- Jenkins is a locally run Java service and requires manual setup. It may be difficult to get Jenkins working on a machine that you do not control. We only recommend using Jenkins if you are unable to use GitHub Actions.

## GitHub Actions

Note that JUnitLauncher (the Ant task used to run JUnit 5 unit tests) is not a default part of Ant, although it is included in the version of Ant packaged with IntelliJ. In this practical we have provided a .jar of JUnitLauncher in the `lib` directory, which we will modify the .yaml file to point Ant to. However, for your own projects you will need to download this separately. You can download it from Maven in the usual way (from Project Structure -> Libraries): enter `org.apache.ant:ant-junitlauncher:1.10.9` (or a later ver) in the text field, disable transitive dependencies and select the option that stores the .jar in the `lib` directory. Note that this is not necessary for this prac, but may be necessary for your own projects.

1) The first thing you will need to do is get your prac 11 code onto GitHub. Go to GitHub, create an account (if you haven't done so already) and create a new repository - ideally named `prac11`. Make it private and do not initialise the repository with any files.

2) Get your newly created repository's SSH URL, go to Git -> Manage Remotes in IntelliJ and replace the `origin` URL with the new SSH URL.

3) Commit your changes to the repository, then push them, handling authentication where necessary (you may need to set up SSH keys to do this). Your prac 11 code should now appear on GitHub in your private repository.

4) On your repository page on GitHub, you will see a button titled 'Actions' (in between the 'Pull Requests' and 'Projects' buttons). Click that.

5) For convenience, choose one of the provided workflows. You may need to scroll down and click on 'More continuous integration workflows...' to find it, but you want the one titled 'Java with Ant' as it is very close to what we need.

6) The default .yaml file that this workflow provides uses JDK 11 and runs Ant on your `build.xml` file. This is almost perfect. Change it to use JDK 15 instead (edit the `'java-version'` parameter).

7) We need to give Ant the path to our `lib` directory so it will find the JUnitLauncher task (and anything else it needs). Add `-lib lib ` to the invocation of Ant in the .yaml file.

8) Now commit the .yaml file. It may take a little while, but soon you will be able to click on Actions and see the results of that run. If the run was successful, there should be a green tick next to it. If the run failed, something went wrong somewhere. You will need to modify your .yaml file (or fix any other problems, if the problem was caused by something else in your build) and commit those changes on GitHub. Because the pipeline will trigger every time the source is changed, you do not need to manually rerun the action.

## Jenkins

1) Install Jenkins. Detailed instructions are given in the Jenkins online documentation: [https://www.jenkins.io/doc/book/installing/](https://www.jenkins.io/doc/book/installing/). On Windows I found it easier to actually download the .WAR file and run it from Java on the command-line, rather than bothering with the graphical installer that tries to set up a Windows service.

2) Follow the instructions to get Jenkins running on your platform. When you come to installing plugins, make sure you get the Ant and FSTrigger plugins.

3) Create a new freestyle project (e.g. named `prac11`). The process of configuring it should be fairly straightforward as the client is graphical. Set the FSTrigger to run whenever a .java file in your project changes (`**/*.java`).

4) You may need to install command-line Ant - follow the instructions found in Ant's online documentation to get Ant installed and environment variables configured: [https://ant.apache.org/manual/install.html#installing](https://ant.apache.org/manual/install.html#installing).

5) Copy the .jar files in the `lib` directory of this practical and paste them into Ant's `lib` directory. This is an easy way of getting Ant installed with little effort.

6) Configure your project in Jenkins to launch Ant as one of the build steps. You should not need to configure this.

7) Now you need to find Jenkins' workspace directory for this project. The exact location depends on your setup, but you should be able to find the Jenkins directory under Manage Jenkins -> System Information. Then just look for the `prac11` directory under the `workspace` directory inside it.

8) Now that you have found the workspace directory, copy and paste the entire contents of your IntelliJ project directory into it (so that the `build.xml` file falls into the Jenkins `workspace/prac11` directory.

9) Jenkins should now spot the changed directory structure and launch a build. Return to the dashboard and wait for it to happen. You may need to debug things if nothing happens after a minute.
