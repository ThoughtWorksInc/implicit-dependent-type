# implicit-dependent-type <a href="http://thoughtworks.com/"><img align="right" src="https://www.thoughtworks.com/imgs/tw-logo.png" title="ThoughtWorks" height="15"/></a>

[![Join the chat at https://gitter.im/ThoughtWorksInc/implicit-dependent-type](https://badges.gitter.im/ThoughtWorksInc/implicit-dependent-type.svg)](https://gitter.im/ThoughtWorksInc/implicit-dependent-type?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/ThoughtWorksInc/implicit-dependent-type.svg)](https://travis-ci.org/ThoughtWorksInc/implicit-dependent-type)

**implicit-dependent-type** is a Scala compiler plugin that resolves dependent types from implicit type classes,
especially useful when working with [Shapeless](https://github.com/milessabin/shapeless) or other type-level programming frameworks.

This plugin provides a syntactic sugar that substitutes all `Foo[Bar]##Baz` with ```shapeless.the.`Foo[Bar]`.Baz```,
which inlines the resolved implicit type class into the type declaration position.

``` sbt
addCompilerPlugin("com.thoughtworks.implicit-dependent-type" %% "implicit-dependent-type" % "latest.release")

libraryDependencies += "com.chuusai" %% "shapeless" % "latest.release"
```

``` scala
import shapeless._

final case class Foo(bar: Int, baz: String)

val hlistForFoo: Generic[Foo]##Repr = 1 :: "xxx" :: HNil
val foo: Foo = Generic[Foo].from(hlistForFoo)
```

The above example equals to the following code:

``` scala
import shapeless._

final case class Foo(bar: Int, baz: String)

val g = Generic[Foo]
val hlistForFoo: g.Repr = 1 :: "xxx" :: HNil
val foo: Foo = Generic[Foo].from(hlistForFoo)
```

As you see, without this plugin, `Generic[Foo]` is not a stable value,
thus it is unable to inline to a type position.
You will have to assign it to a temporary variable `g`.
