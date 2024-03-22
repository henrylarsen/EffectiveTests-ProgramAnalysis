# Effective Tests

Our tool uses a combination of static analysis and source code injected dynamic analysis to create a stronger test
coverage metric: checking that all side effects of class methods are covered by assertions.

See checkins in CHECKINS.md for project progress.

## Building & Developing
This is a multi-project gradle repository. Its current members are:

```
root
├── plugin 
├── library
└── testproject
```

It's possible we'll also want a `shared` project for our library and plugin to have a common interface, but if this is
needed it's probably easier for `plugin` to include `library` itself if possible

### Plugin
This is a gradle plugin containing StaticAnalysisPlugin. To publish the StaticAnalysisPlugin, first consider incrementing the version in build.gradle, then run:

```shell
./gradlew -DpublishPlugin=true :plugin:publishToMavenLocal
```

It can then be consumed by another gradle project (or test subproject) as long as it's looking in the maven local cache.

```groovy
plugins {
    id 'org.comprehensive.analysis' version '1.0-SNAPSHOT'
}
```

### library
This is a place to publish any library code we will need users to consume (i.e. like normal imports, not like a task).

### testproject
This project is set up to include our plugin for testing. Once you've published a new plugin version, update the version number
in library/build.gradle and run

``` shell
./gradlew :testproject:analyze
```
