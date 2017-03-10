# implicit-dependent-type <a href="http://thoughtworks.com/"><img align="right" src="https://www.thoughtworks.com/imgs/tw-logo.png" title="ThoughtWorks" height="15"/></a>

[![Join the chat at https://gitter.im/ThoughtWorksInc/implicit-dependent-type](https://badges.gitter.im/ThoughtWorksInc/implicit-dependent-type.svg)](https://gitter.im/ThoughtWorksInc/implicit-dependent-type?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/ThoughtWorksInc/implicit-dependent-type.svg)](https://travis-ci.org/ThoughtWorksInc/implicit-dependent-type)

**implicit-dependent-type** is a Scala compiler plugin that resolves dependent types from implicit type classes,
especially useful when working with [shapeless](https://github.com/milessabin/shapeless) or other type-level programming libraries.

## Setup

``` sbt
addCompilerPlugin("com.thoughtworks.implicit-dependent-type" %% "implicit-dependent-type" % "latest.release")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

libraryDependencies += "com.chuusai" %% "shapeless" % "latest.release"
```

## `Foo[Bar]##Baz` syntax

This plugin provides a syntactic sugar that substitutes all `Foo[Bar]##Baz` with ```shapeless.the.`Foo[Bar]`.Baz```,
which inlines resolved implicit type classes into type declaration positions.

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
thus it is unable to be placed at a type position.
You will have to assign it to a temporary variable `g`.

This plugin resolves this problem.

## `Foo @Bar` syntax

Another syntactic sugar provided by this plugin is converting `Foo @Bar` to ```shapeless.the.`Bar[Foo]`.`@` ```.

For example:

``` scala
trait GetElement[A] {
  type `@`
}

implicit def getArrayElement[Element] = new GetElement[Array[Element]] {
  override type `@` = Element
}

val i: Array[Int] @GetElement = 1
val s: Array[String] @GetElement = "text"
```

In the above example, `@GetElement` acts as a type level function, calculating the element type of given type


Note that the `Foo @Bar` syntax only applied if `Bar` starts with an upper case character.
Thus, this plugin does not affect built-in annotations like `@specialize`, `@cps` or `@suspendable` because they start with a lower case character.
