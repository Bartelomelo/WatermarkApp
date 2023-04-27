fun main() {
    val n = readln().toInt()
    val t = mutableListOf<Int>()
    for (i in 0 until n) {
        t.add(readln().toInt())
    }
    val c = readln().toInt()
    var counter: Int = 0
    for (number in t) {
        if (number == c) counter++
    }
    println(counter)
}