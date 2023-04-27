fun printColor(myImage: BufferedImage) {
    val xy = readln().split(" ")
    val x = xy[0].toInt()
    val y = xy[1].toInt()

    val color = Color(myImage.getRGB(x, y), true)
    println("${color.red} ${color.green} ${color.blue} ${color.alpha}")
}
