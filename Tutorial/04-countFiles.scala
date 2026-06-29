//
// $ scala run counter.scala
//

//> using scala 384
//> using toolkit 0.5.0

@main
def countFiles(): Unit =
  val paths = os.list(os.pwd)
  println(paths.length)
