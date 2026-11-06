package com.dhaliwal.notemindai.data.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * This class handles encryption (scrambling) and decryption (unscrambling) of sensitive data like auth tokens.
 * It uses the Android KeyStore, which stores the secret encryption key inside the device's secure hardware module.
 */
object CryptoManager {

    // Unique alias name to find our encryption key inside the Android KeyStore
    private const val KEY_ALIAS = "notemind_ai_keystore_key"

    // Encryption settings:
    // - AES (Advanced Encryption Standard) algorithm
    // - GCM (Galois/Counter Mode) block mode for strong security and tamper detection
    // - NoPadding since GCM mode handles padding automatically
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE

    // Full transformation string used by Cipher: "AES/GCM/NoPadding"
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    // Authentication tag length for GCM security validation (128 bits is standard)
    private const val GCM_TAG_LENGTH_BITS = 128

    // Access the Android KeyStore system storage where security keys are safely kept
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    /**
     * Gets our existing secret key from the Android KeyStore, or creates a new 256-bit AES key if one doesn't exist yet.
     */
    private fun getOrCreateSecretKey(): SecretKey {
        // Step 1: Check if the key was already generated and saved in the KeyStore
        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existingKey != null) {
            return existingKey // Return the existing key
        }

        // Step 2: If no key exists, configure a new AES-256 key inside Android KeyStore
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM, "AndroidKeyStore")
        val keyGenSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            // Allow this key to be used for both encryption and decryption
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(PADDING)
            .setKeySize(256) // Use 256-bit key size for AES-256
            .build()

        // Step 3: Initialize the generator with our rules and generate the secret key
        keyGenerator.init(keyGenSpec)
        return keyGenerator.generateKey()
    }

    /**
     * Encrypts a plain text string (e.g. "my_jwt_token") using AES-256-GCM.
     * Returns a safe Base64 string containing both the Initialization Vector (IV) and the encrypted data separated by a colon:
     * Format: "IV_IN_BASE64:ENCRYPTED_TEXT_IN_BASE64"
     */
    fun encryptString(plainText: String): String {
        // Convert the input text string into raw bytes (UTF-8 format)
        val bytesToEncrypt = plainText.toByteArray(Charsets.UTF_8)

        // Initialize the Cipher tool in ENCRYPT mode using our hardware secret key
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())

        // Get the randomly generated Initialization Vector (IV) created automatically by the Cipher
        val iv = cipher.iv

        // Perform the actual AES-256-GCM encryption on the text bytes
        val encryptedBytes = cipher.doFinal(bytesToEncrypt)

        // Convert the raw IV bytes and encrypted bytes into readable Base64 text strings
        val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
        val encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

        // Combine both Base64 strings separated by a colon so we can easily split them later during decryption
        return "$ivBase64:$encryptedBase64"
    }

    /**
     * Decrypts a previously encrypted string ("IV_IN_BASE64:ENCRYPTED_TEXT_IN_BASE64") back into original text.
     * Returns the original plain text, or null if decryption fails or data was tampered with.
     */
    fun decryptToString(encryptedPayload: String): String? {
        return try {
            // Step 1: Separate the IV part from the encrypted data part using the colon delimiter
            val parts = encryptedPayload.split(":")
            if (parts.size != 2) return null

            // Step 2: Convert the Base64 strings back into raw byte arrays
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val encryptedBytes = Base64.decode(parts[1], Base64.NO_WRAP)

            // Step 3: Prepare the Cipher tool for DECRYPT mode using the exact same IV parameter
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)

            // Step 4: Perform decryption to get the original unencrypted bytes
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            // Step 5: Convert the raw bytes back into a human-readable text string
            String(decryptedBytes, Charsets.UTF_8)
        } catch (_: Exception) {
            // Return null if decryption fails (e.g., invalid key or corrupted data)
            null
        }
    }
}
