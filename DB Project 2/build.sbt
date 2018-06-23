name := "Project2"

organization := "UGA"

version := "1.0"
// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

// library dependencies. (orginization name) % (project name) % (version)
// http://mvnrepository.com/artifact/junit/junit
libraryDependencies += "junit" % "junit" % "4.4"

