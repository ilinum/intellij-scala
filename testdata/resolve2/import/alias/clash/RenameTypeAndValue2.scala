import actors.Actor
import collection.immutable.{LinearSeq => Actor}

println(/* resolved: false */ LinearSeq.getClass)
println(classOf[/* resolved: false */ LinearSeq])

println(/* resolved: false */ Actor.getClass)
println(classOf[/* resolved: false */ Actor])