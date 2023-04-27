fun isPrime(number: Int): Boolean {
    for (i in 2..(number / 2)) {
        if (number % i != 0)
            continue
        else
            return false
    }
    return true
}

fun main() {
    val result = isPrime(5977)
}