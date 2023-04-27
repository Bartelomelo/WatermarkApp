fun main() {
    val n = readln().toInt()
    val t = mutableListOf<Int>()
    for (i in 0 until n) {
        t.add(readln().toInt())
    }
    val c = readln().split(" ")
    val c1: Int = c[0].toInt()
    val c2: Int = c[1].toInt()
    var occur: Boolean = false
    for (i in 0 until n - 1) {
        if (t[i] == c1 && t[i + 1] == c2 || t[i] == c2 && t[i + 1] == c1) {
            occur = true
        }
    }
    if (occur) println("NO") else println("YES")
}
