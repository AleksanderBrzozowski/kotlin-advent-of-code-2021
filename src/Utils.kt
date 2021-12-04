import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads string lines from the given input txt file.
 */
fun readStringInput(name: String) = File("src", "$name.txt").readLines()

fun readIntInput(name: String) = File("src", "$name.txt").readLines().map { it.toInt() }

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
