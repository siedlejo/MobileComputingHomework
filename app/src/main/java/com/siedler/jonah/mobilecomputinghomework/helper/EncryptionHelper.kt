package com.siedler.jonah.mobilecomputinghomework.helper

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec


const val PASSWORD_KEY_ALIAS = "passwordKey"
const val ANDROID_KEY_STORE = "AndroidKeyStore"
private const val AES_MODE = "AES/GCM/NoPadding"
private const val IV_SEPARATOR = ";"

class EncryptionHelper {
    private var keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE)

    init {
        keyStore.load(null)

        if (!keyStore.containsAlias(PASSWORD_KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(PASSWORD_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .build()
            )
            keyGenerator.generateKey()
        }
    }

    fun getPasswordKey(): Key {
        return keyStore.getKey(PASSWORD_KEY_ALIAS, null)
    }

    fun encrypt(plaintext: String, key: Key): String {
        val cipher: Cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encodedBytes: ByteArray = cipher.doFinal(plaintext.toByteArray())
        val ciphertext = Base64.encodeToString(encodedBytes, Base64.NO_WRAP)

        // append the iv to the ciphertext
        val initializationVector = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        return initializationVector + IV_SEPARATOR + ciphertext
    }

    /*
    The given ciphertext was include a initialization vector in the form IV;cipher
     */
    fun decrypt(ciphertextPlusIV: String, key: Key): String {
        var splitCiphertext = ciphertextPlusIV.split(IV_SEPARATOR.toRegex())
        if (splitCiphertext.size != 2) return ""

        // extract the parts of the given ciphertext
        val initializationVector = Base64.decode(splitCiphertext[0], Base64.NO_WRAP)
        val ciphertext = Base64.decode(splitCiphertext[1], Base64.NO_WRAP)

        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(
            Cipher.DECRYPT_MODE,
            key,
            GCMParameterSpec(128, initializationVector)
        )

        return String(cipher.doFinal(ciphertext))
    }
}