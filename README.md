# Implicit Dependent Type plugin

**Implicit Dependent Type plugin** is a Scala compiler plugin that resolves dependent types from implicit type classes, 
especially useful when working with [Shapeless](https://github.com/milessabin/shapeless) or other type-level programming frameworks.

This plugin provides a syntactic sugar that substitutes all `Foo[Bar]##Baz` with ```shapeless.the.`Foo[Bar]`.Baz```.

``` scala
import shapeless._

final case class Foo(bar: Int, baz: String)

val hlistForFoo: Generic[Foo]##Repr = 1 :: "xxx" :: HNil
val foo: Foo = Generic[Foo].from(hlistForFoo)
```
