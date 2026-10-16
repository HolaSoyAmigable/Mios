package com.example.mios.util

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    data class HashResult(val hash: String, val salt: String)

    private const val ITERATIONS = 20_000
    private const val KEY_BITS = 160
    private const val ALGORITHM = "PBKDF2WithHmacSHA1"

    fun create(pin: String): HashResult {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return HashResult(hash = encode(derive(pin, salt)), salt = encode(salt))
    }

    fun verify(pin: String, hash: String, salt: String): Boolean {
        return try {
            val expected = decode(hash)
            val computed = derive(pin, decode(salt))
            MessageDigest.isEqual(expected, computed)
        } catch (e: Exception) {
            false
        }
    }

    private fun derive(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, ITERATIONS, KEY_BITS)
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
    }

    private fun encode(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)

    private fun decode(text: String): ByteArray = Base64.decode(text, Base64.NO_WRAP)
}