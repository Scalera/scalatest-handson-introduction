# ScalaTest hands-on introduction

*“You have to eat your vegetables to grow and become stronger, ‘cause one day you’ll have to program in Scala”* - moms always say. We don’t pay too much attention to them until we have to face the challenge. When you have no background of Java, it can be easier. However, at the end we realize what everybody say: Scala learning curve is kind of tough, not because of the syntax, but of the paradigm it uses: the functional one. 

After reading a lot and asking in forums for answers (and programming some years with it), you get a little bit used to the language. But then, you realize that there is a lot stuff that you usually managed unconsciously and you have no idea to handle in a functional way. Such as code testing. In these few lines we’ll see how to deal with it.

## Prerequisites

What will you need before reading futher?

* [sbt 0.13.8](http://www.scala-sbt.org/download.html) or higher installed.
* small notions about Scala
* a greedy desire to learn!

## Our domain code

For shedding light on the main concepts, let’s suppose we have some cool just-implemented Scala classes we want to test. These ones could be classes from a simple snippet that models a Hotel, and the action of checking in:

```scala
case class Guest(name: String)

case class Room(number: Int, guest: Option[Guest] = None){ room =>

  def isAvailable(): Boolean = ???

  def checkin(guest: Guest): Room = ???

  def checkout(): Room = ???

}

/*
 * We will create automatically a bunch of 10 rooms 
 * if these are not specified.
 */
case class Hotel(
  rooms: List[Room] = (1 to 10).map(n => Room(number=n)).toList){

  def checkin(personName: String): Hotel = ???

}
```

As you can see, methods are still unimplemented by using ```???``` notation. If we want to use a [TDD](http://martinfowler.com/bliki/TestDrivenDevelopment.html) approach, we have to define first the expected behavior of these components by creating a bunch of tests.

Before doing so, let’s organize and put them into a proper SBT project. We can start defining a SBT project definition (build.sbt). In this file, we should include some info about the name of our project and the version of Scala that we're currently using:

```sbt
name := "hotel-management"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.4"
```

After that, we should create the class files (one per class would be nice).
The scaffolding should look like this:

```
hotel-management/
  src/
    main/
      resources/ (all resource data should be allocated here)
      scala/
        org.me.hotel/
          Guest.scala
          Hotel.scala
          Room.scala
    test/
      resources/ (all resources used in test should be allocated here)
      scala/
        org.me.hotel/ (all test classes should go in here)
  build.sbt

```

## Alternatives for testing

For testing these brand new classes, we’ll first choose a proper testing framework. There are two outstanding Scala testing frameworks: Scalatest and Scalacheck. Both are easy to use, but there are a few differences based on the test approach you want to use.

For example, [Scalacheck](http:://www.scalacheck.org) would be a nice choice if we wanted to define some property-based tests. Without giving too many details, we could define, for example, the way to test Room’s checkin:

```scala
object RoomSpec extends Properties(“Room”){
  property(“is available when there’s no guest”) = 
    forAll{ (room: Room) =>
      room.isAvailable() == room.guest.isEmpty
    }
}
```

This specification for Rooms indicates that the property that rules if the room is available, has to check “for all possible rooms” that the property is directly related to the existence of a guest that occupies the room. How can Scalacheck make it possible to test this property for “all” possible rooms? Actually, by choosing a bunch of them [automatically generated](https://github.com/rickynils/scalacheck/wiki/User-Guide#generators). Cool, right?

On the other hand, what can [Scalatest](http://www.scalatest.org) offer us? Appart of the ease to integrate with Scalacheck, its capabilities and ease of use. It’s a versatile testing framework that allows testing both Java and Scala code. It’s also remarkable its integration with powerful tools such as JUnit, TestNG, Ant, Maven, sbt, ScalaCheck, JMock, EasyMock, Mockito, ScalaMock, Selenium (browser testing automation), ... 

We'll see through next paragraphs how to use Scalatest specs for testing the classes we added to our recently created SBT project.

## Adding Scalatest dependency to SBT project.

We will start by appending Scalatest dependency at the end of our ````build.sbt```` file:

```sbt
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
```

Be careful about the Scala version you're using. Remember that ```%%``` represents the addition of Scala current version to artifact's name. In this case, it equals to:

```sbt
libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.4" % "test"
```
## Choosing a testing style

Scalatest offers several [testing styles](http://www.scalatest.org/user_guide/selecting_a_style): property-based tests (```PropSpec```), descriptive-language-based testing (```WordSpec```), simple styles (```FunSpec```), ... 

All of them are very easy to use and try to provide a higher level abstraction layer. Our suggestion consist on choosing a style for unit testing and another one for acceptance (for example). This is due to the need of mantaining uniformity accross the same kind of tests. And when a developer has to change from creating unit tests to creating acceptance ones, this difference would help to change the thoughts about the type of task they're doing.

For this example, we will choose a pretty legible testing style like ```FlatSpec```.

### Base classes

Base classes are fully recommended to gather all traits that we will use for all of our test classes, just in order to reuse all common functionality without too much boilerplate.

For example, we could define a class called ```UnitTest``` at ```org.me.hotel``` package. 
Let's create a file called ```UnitTest.scala``` at ```hotel-management/src/test/org/me/hotel``` with the following content:

```scala
package org.me.hotel

import org.scalatest.{FlatSpec,Matchers}

abstract class UnitTest(component: String) extends FlatSpec 
  with Matchers{

  behavior of component

}

```

When extending ```FlatSpec``` we're choosing the testing style and when we do the mixin with ```Matchers``` we are including some special functionality that we'll explain later.

What about ```behavior of component ```? This defines the name of the component that is being tested and the pretty printing of the class that extends our base class. For example, if we define some test class like this:

```scala
class MyClassUnitTest extends UnitTest("MyClass")
```

when launching the test class, it will print out the **behavior of "MyClass"**.

### Using matchers

As you could previously see, our base class is mixing in with ```Matchers``` trait. This provides an easy way, and very close to human language, to express assertions and conditions that should be checked in our tests. For example, we could express requirements like these:

```scala
(2+2) should equal (4)
(2+2) shouldEqual 5
(2+2) should === (4)
(2+2) should be (4)
(2+2) shouldBe 5
```

In case of any of them failed (for example, the last one), an exception like the following would be thrown: ```4 did not equal 5```.

If we are expecting some kind of exception (even though it's not very functional) we could assert it with:

```scala
an [IndexOutOfBoundsException] should be thrownBy "my string".charAt(-1)
```

There are [many other ways](http://scalatest.org/user_guide/using_matchers) to express assertions like **greater and less than**, **regular expressions** in Strings, **type checking**, ...
For example:

```scala
// greater and less than

1 should be < 3
1 should be > 0
1 should be >= 0
1 should be <= 2

// reg. exp.

"Hello friends" should startWith ("Hello")
"You rock my world" should endWith ("world")
"In my dreams" should include ("my")

// type checking

1 shouldBe a [Int]
true shouldBe a [Boolean]
Guest("Alfred") shouldBe a [Guest]

```

## Defining test classes

So, if we apply all we have exposed before to our domain classes, we could define the Room specification.
First of all, we create a file called ```RoomTest.scala``` at ```hotel-management/src/test/scala/org/me/hotel/``` and then we add something like this:

```scala
package org.me.hotel

class RoomTest extends UnitTest("Room") {
  
  it should "provide info about its occupation" in {
    Room(1).isFree() shouldEqual true
    Room(1,None).isFree() shouldEqual true
    Room(1,Some(Guest("Bruce"))).isFree() shouldEqual false
  }

  it should "allow registering a new guest if room is free" in {
    val occupiedRoom = Room(1).checkin(Guest("James"))
    occupiedRoom.isFree shouldEqual false
    occupiedRoom.guest shouldEqual(Option(Guest("James")))
  }

  it should "deny registering a new guest if room is already occupied" in {
    an [IllegalArgumentException] should be thrownBy {
      Room(1,Some(Guest("Barbara"))).checkin(Guest("Bruce"))
    }
  }

  it should "deny checking out if room is already free" in {
    an [IllegalArgumentException] should be thrownBy {
      Room(1).checkout()
    }
  }

  it should "allow checking out if room is occupied by someone" in {
    val room = Room(1,Some(Guest("Carmine")))
    val freeRoom = room.checkout()
    freeRoom.isFree shouldEqual true
  }

}
```

As you can see, test cases are defined with 
```
it should "your-test-case-description" in { /* your test code */ }
```

For example, at first test case, we're checking that ```isFree()``` method result is directly related to the existence of a guest that occupies the room. We can also highlight those test cases where we want to catch some exception, like the case where we are trying to register a guest in an already occupied room.

## Test it!

Once you have your test classes implemented, you can run your tests by using the SBT task called ```test```. If we launch this right now, we'll have some problems with unimplemented methods (remember that we used ```???``` for leaving them unimplemented). Tests will fail and result output will be like

```sbt
> test
[info] RoomTest:
[info] Room
[info] - should provide info about its occupation *** FAILED ***
[info]   scala.NotImplementedError: an implementation is missing
[info]   at scala.Predef$.$qmark$qmark$qmark(Predef.scala:252)
[info]   at org.me.hotel.Room.isFree(Room.scala:6)
[info]   at org.me.hotel.RoomTest$$anonfun$1.apply$mcV$sp(RoomTest.scala:6)
[info]   at org.me.hotel.RoomTest$$anonfun$1.apply(RoomTest.scala:5)

...

[info] Run completed in 699 milliseconds.
[info] Total number of tests run: 15
[info] Suites: completed 6, aborted 0
[info] Tests: succeeded 3, failed 12, canceled 0, ignored 0, pending 0
[info] *** 12 TESTS FAILED ***
[error] Failed tests:
[error]   org.me.hotel.RoomTest
[error]   org.me.hotel.HotelTest
[error] (test:test) sbt.TestsFailedException: Tests unsuccessful
[error] Total time: 6 s, completed 12-ago-2015 16:08:47
```

So, let's implement abstract methods in order to make these test running. ```Room.scala``` file should look like the following:

```scala
package org.me.hotel

case class Room(number: Int, guest: Option[Guest] = None){ room =>

  def isFree(): Boolean =
    guest.isEmpty

  def checkin(guest: Guest): Room = {
    require(room.guest.isEmpty, "Room is occupied")
    Room(number,Some(guest))
  }

  def checkout(): Room = {
    require(guest.isDefined,"Room is already free")
    Room(number,None)
  }

}
```

After having implemented all abstract methods, if we execute ```test``` again, this will launch a test discovery in your project, and once finished, it will launch all discovered test classes, showing something similar to:

```sbt
> test
[info] GuestTest:
[info] RoomTest:
[info] Guest
[info] Room
[info] - should have its name defined
[info] - should provide info about its occupation
[info] - should allow registering a new guest if room is free
[info] - should deny registering a new guest if room is already occupied
[info] - should deny checking out if room is already free
[info] RoomTest:
[info] Room
[info] - should provide info about its occupation
[info] - should allow registering a new guest if room is free
[info] - should deny registering a new guest if room is already occupied
[info] - should deny checking out if room is already free
[info] - should allow checking out if room is occupied by someone
[info] - should allow checking out if room is occupied by someone
[info] GuestTest:
[info] Guest
[info] - should have its name defined
[info] HotelTest:
[info] Hotel
[info] - should forbid creating a Hotel with no rooms
[info] - should forbid checking in if there are no free rooms
[info] - should allow checking in
[info] Run completed in 859 milliseconds.
[info] Total number of tests run: 15
[info] Suites: completed 6, aborted 0
[info] Tests: succeeded 15, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 1 s, completed 04-ago-2015 18:23:13
```

### Ignoring test cases

Another cool feature to talk about is that we can avoid testing all test cases. Imagine we want to **ignore** some of them, we don't have to comment all test code, or even the test case code; we can ignore it by replacing the ```it``` word with ```ignore```. For example:

```scala
//...

class RoomTest extends UnitTest("Room") {
  
  ignore should "provide info about its occupation" in {
    //...
  }

  it should "allow registering a new guest if room is free" in {
    //...
  }

  //...
```

If we ignore some test cases, result view will be like this:

```sbt
> test
[info] GuestTest:
[info] RoomTest:
[info] Guest
[info] Room
[info] - should have its name defined !!! IGNORED !!!
[info] - should provide info about its occupation
[info] - should allow registering a new guest if room is free !!! IGNORED !!!
[info] - should deny registering a new guest if room is already occupied
[info] - should deny checking out if room is already free
[info] RoomTest:
[info] Room
[info] - should provide info about its occupation
[info] - should allow registering a new guest if room is free !!! IGNORED !!!
[info] - should deny registering a new guest if room is already occupied
[info] - should deny checking out if room is already free
[info] - should allow checking out if room is occupied by someone
[info] - should allow checking out if room is occupied by someone
[info] GuestTest:
[info] Guest
[info] - should have its name defined
[info] HotelTest:
[info] Hotel
[info] - should forbid creating a Hotel with no rooms
[info] - should forbid checking in if there are no free rooms
[info] - should allow checking in
[info] Run completed in 859 milliseconds.
[info] Total number of tests run: 15
[info] Suites: completed 6, aborted 0
[info] Tests: succeeded 12, failed 0, canceled 0, ignored 3, pending 0
[info] All tests passed.
[success] Total time: 1 s, completed 04-ago-2015 18:23:13
```

### Testing an only spec

If we have several test classes and want to test only one of them, we can also use the SBT task ```test-only``` (or ```testOnly``` depending on SBT version).

```sbt
> test-only org.me.hotel.GuestTest
[info] GuestTest:
[info] Guest
[info] - should have its name defined
[info] Run completed in 2 seconds, 262 milliseconds.
[info] Total number of tests run: 1
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 1, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 29 s, completed 04-ago-2015 18:21:52
```

## Another useful concepts

### Grouping test classes

Using test discovery (as we have seen previously) all discovered test in current project will be launched (sounds logic...), but maybe we just want to launch a bunch of them. For grouping tests, we can use a special class called ```Suites```. For example:

```scala
package org.me.hotel

class MySuites extends Suites(
  new GuestTest, 
  new RoomTest)
```

This way, when executing SBT task ```test-only org.me.hotel.MySuite```, only GuestTest and RoomTest will be launched, instead of all test classes. 

Anyway, have in mind that, if you run ```test``` task when having defined at least a ```Suites```, there will be repeated test executions (both the ```Suites``` and the test itself).

### Parallel tests

What about parallelism? If our tests are properly designed, and can run independently, we could execute all of them [**in parallel**](http://www.scala-sbt.org/0.13/docs/Testing.html#Forking+tests) by adding at SBT definition file:

```sbt
testForkedParallel in Test := true
```

If you only want to run tests **sequentially**, but in a different JVM, you can achieve it by adding:

```sbt
fork in Test := true
```

## So...

As you can see, Scalatest is quite a powerful testing framework but, despite of that, you can start working with it only learning a few concepts. The closeness to the user's language makes really easy the use of a wide range of its features.

All used code examples can be found [here](https://github.com/Scalera/scalatest-handson-introduction/tree/master/hotel-management). 

Thanks to [Semaphore](https://semaphoreci.com/community) and [Scalera](http://scalera.es) staff for making possible this tutorial :-)
