package chat_server;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {
    private PublicKey publicKey; // Public key used during key exchange
    private KeyAgreement keyAgreement; // Provides functionality for key exchange protocol
    private Key key; // The shared secret key

    // Constructor
    public Encryptor() {
        try {
            // Generate a key pair using elliptic cryptography
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(128);
            KeyPair kp = kpg.generateKeyPair();

            // Get the public key
            publicKey = kp.getPublic();

            // Setup the key agreement
            keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(kp.getPrivate());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    // Encrypt a message
    public String encrypt(String plaintext) {
        try {
            // Get an instance of the cipher
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // Generate a random initialization vector
            SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
            byte[] iv = new byte[cipher.getBlockSize()];
            randomSecureRandom.nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            // Initialize the cipher in encrypt mode using the IV and key
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);

            // Encrypt the plaintext, encode it into a string, and return the ciphertext
            byte[] encoded = cipher.doFinal(plaintext.getBytes("utf-8"));
            return Base64.getEncoder().encodeToString(ivParams.getIV()) + Base64.getEncoder().encodeToString(encoded);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Decrypt a message
    public String decrypt(String data) {
        try {
            // Get an instance of the cipher in decrypt mode
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // Split the ciphertext from the IV (IV should end in "==")
            String iv = data.split("==")[0] + "==";
            String ciphertext = data.substring(iv.length());

            // Generate the IV params object from the IV string
            IvParameterSpec ivParams = new IvParameterSpec(Base64.getDecoder().decode(iv));

            // Initialize the cipher in decrypt mode using the IV and key
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);

            // Decrypt the ciphertext, encode it into a string, and return the plaintext
            byte[] encoded = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(encoded, "utf-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Get the public key
    public String getPublicKey() {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        return Base64.getEncoder().encodeToString(keySpec.getEncoded());
    }

    // Generate the shared key
    public void generateSharedKey(String encodedClientKey) {
        try {
            // Generate the client's public key from the received encoded key
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedClientKey));
            Key clientPublicKey = keyFactory.generatePublic(keySpec);

            // Generate the shared secret key
            keyAgreement.doPhase(clientPublicKey, true);
            key = new SecretKeySpec(keyAgreement.generateSecret(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
