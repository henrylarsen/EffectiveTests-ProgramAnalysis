# Effective Tests

Our tool uses a combination of value agnostic static analysis and source code injected dynamic analysis to create a stronger test
coverage metric: checking that all side effects of class methods are covered by assertions. 

The motivation is to help developers ensure their tests are actually asserting on values that should be asserted on, 
addressing the assertions problem in a way that traditional test coverage metrics fail to do.

See checkins in CHECKINS.md for project progress, and USERSTUDIES.md for the results of our user studies.

## Running our Project
Our project is two packages: a gradle plugin and a java library. You could set up a new gradle project with our plugin
and import our library code and it would work, but the simplest way to run our project is to use `testproject/`.

To run our project on the source code in `testproject/`, complete the following steps:
1. Run `./gradlew --version` and ensure the JVM version is recent, like 20
2. Run `./gradlew -DpublishPlugin=true :plugin:publishToMavenLocal` to publish our plugin to your local maven cache
3. Run any tests in `testproject/` and observe our program runs automatically. You can run tests in `testproject/` by either:
   
   - Running `./gradlew :testproject:test` in the command line. This runs all tests in the project.
   - Clicking any of the green 'run' arrows in a test class in IntelliJ.


## Building & Developing
This is a multi-project gradle repository. Its current members are:

```
root
├── plugin 
├── library
└── testproject
```

### Plugin
This is a gradle plugin containing StaticAnalysisPlugin. To publish the StaticAnalysisPlugin, first consider incrementing the version in build.gradle, then run:

```shell
./gradlew -DpublishPlugin=true :plugin:publishToMavenLocal
```

It can then be consumed by another gradle project (or test subproject) as long as it's looking in the maven local cache.

```groovy
plugins {
    id 'org.effective.tests' version '1.0'
}
```

### library
This is a place to publish any library code we will need users to consume (i.e. like normal imports, not like a task).

### testproject
This project is set up to include our plugin for testing. Once you've published a new plugin version, update the version number
in library/build.gradle and run

``` shell
./gradlew :testproject:test
```

This will:
1. Run static analysis on your src and test code to determine where to inject code for dynamic analysis purposes
2. Place the injected code in `build/injectedSrc/`
3. Run the rest of compilation with `build/injectedSrc/` as the source set
4. (Todo) Output the results of the dynamic analysis from the ran test command