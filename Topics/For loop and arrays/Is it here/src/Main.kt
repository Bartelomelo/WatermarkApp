fun main() {
    val n = readln().toInt()
    val t = mutableListOf<Int>()
    for (i in 0 until n) {
        t.add(readln().toInt())
    }
    val m = readln().toInt()
    if(t.contains(m)) {
        println("YES")
    } else {
        println("NO")
    }
}
