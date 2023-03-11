@main def hello: Unit =
  // Expressions
  println(1)
  println(1 + 1)
  println("Hello!")
  println("Hello," + " world!")

  // Values
  val x = 1 + 1
  println(x)

  // Variables
  var y = 1 + 1
  y = 3
  println(y * y)

  // Blocks
  println({
    val x = 1 + 1
    x + 1
  })

  // Functions
  val addOne = (x: Int) => x + 1
  println(addOne(1))

  val add = (x: Int, y: Int) => x + y
  println(add(1, 2))

  val getTheAnswer = () => 42
  println(getTheAnswer())


  // Methods
  def add2(x: Int, y: Int): Int = x + y
  println(add2(1, 2))

  def addThenMultiply(x: Int, y: Int)(multiplier: Int): Int = (x + y) * multiplier
  println(addThenMultiply(1, 2)(3))

  def name: String = System.getProperty("user.name")
  println("Hello, " + name + "!")

  def getSquareString(input: Double): String =
    val square = input * input
    square.toString
  println(getSquareString(2.5))

  // Classes
  class Greeter(prefix: String, suffix: String):
    def greet(name: String): Unit =
      println(prefix + name + suffix)

  val greeter = Greeter("Hello, ", "!")
  greeter.greet("Scala developer")

  // Case Classes
  case class Point(x: Int, y: Int)

  val point = Point(1, 2)
  val anotherPoint = Point(1, 2)
  val yetAnotherPoint = Point(2, 2)

  if point == anotherPoint then
    println(s"$point and $anotherPoint are the same.")
  else
    println(s"$point and $anotherPoint are different.")

  if point == yetAnotherPoint then
    println(s"$point and $yetAnotherPoint are the same.")
  else
    println(s"$point and $yetAnotherPoint are different.")

  // Objects
  object IdFactory:
    private var counter = 0
    def create(): Int =
      counter += 1
      counter

  val newId: Int = IdFactory.create()
  println(newId)
  val newerId: Int = IdFactory.create()
  println(newerId)


  // Traits
  trait Greeter2:
    def greet(name: String): Unit =
      println("Hello, " + name + "!")

  class DefaultGreeter extends Greeter2

  class CustomizableGreeter(prefix: String, postfix: String) extends Greeter2:
    override def greet(name: String): Unit =
      println(prefix + name + postfix)

  val defaultGreeter = DefaultGreeter()
  greeter.greet("Scala developer")

  val customGreeter = CustomizableGreeter("How are you, ", "?")
  customGreeter.greet("Scala developer")
