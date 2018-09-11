package io.devcon5.password;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.Year;

/**
 * <ul>
 * <li>Original Author havoc AT defuse.ca <a href="https://crackstation.net/hashing-security.htm">Hashing Security</a></li>
 * <li><a href="https://nakedsecurity.sophos.com/2013/11/20/serious-security-how-to-store-your-users-passwords-safely/">How to store your users
 * passwords safely</a></li>
 * <li>original code from <a href="https://gist.github.com/jtan189/3804290">JTan189</a></li>
 * </ul>
 */
public class Passwords {

  public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
  public static final int ITERATION_INDEX = 0;
  public static final int SALT_INDEX = 1;
  public static final int PBKDF2_INDEX = 2;
  public static final int MIN_SALT = 16;
  public static final int MIN_HASHSIZE = 24;
  public static final int MIN_ITERATIONS = 10000;
  public static final int DEFAULT_SALT = 24;
  public static final int SALT_BYTES = getSaltBytes();
  public static final int DEFAULT_HASHSIZE = 32;
  public static final int HASH_BYTES = getHashBytes();
  public static final int DEFAULT_ITERATIONS_PER_YEAR = 10000;
  public static final int PBKDF2_ITERATIONS = getIterations();

  private static int getSaltBytes() {

    return Math.max(MIN_SALT, Integer.getInteger("passwords.saltBytes", DEFAULT_SALT));
  }

  private static int getHashBytes() {

    return Math.max(MIN_HASHSIZE, Integer.getInteger("passwords.hashBytes", DEFAULT_HASHSIZE));
  }

  private static int getIterations() {

    return Math.max(MIN_ITERATIONS, Integer.getInteger("passwords.iterationIncrementPerYear", DEFAULT_ITERATIONS_PER_YEAR) * (Year.now().getValue() - 2013));
  }

  /**
   * Returns a salted PBKDF2 hash of the password.
   *
   * @param password
   *     the password to hash
   *
   * @return a salted PBKDF2 hash of the password
   */
  public static String createHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {

    return createHash(password.toCharArray());
  }

  /**
   * Returns a salted PBKDF2 hash of the password in the format &lt;iterations&gt;:&lt;salt&gt;:&lt;hash&gt;
   *
   * @param password
   *     the password to hash
   *
   * @return a salted PBKDF2 hash of the password
   */
  public static String createHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {

    final SecureRandom random = new SecureRandom();
    final byte[] salt = new byte[SALT_BYTES];
    random.nextBytes(salt);

    final byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTES);
    // format iterations:salt:hash
    return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" + toHex(hash);
  }

  /**
   * Validates a password using a hash.
   *
   * @param password
   *     the password to check
   * @param goodHash
   *     the hash of the valid password
   *
   * @return true if the password is correct, false if not
   */
  public static boolean validatePassword(String password, String goodHash) throws NoSuchAlgorithmException, InvalidKeySpecException {

    return validatePassword(password.toCharArray(), goodHash);
  }

  /**
   * Validates a password using a hash.
   *
   * @param password
   *     the password to check
   * @param goodHash
   *     the hash of the valid password
   *
   * @return true if the password is correct, false if not
   */
  public static boolean validatePassword(char[] password, String goodHash) throws NoSuchAlgorithmException, InvalidKeySpecException {

    final String[] params = goodHash.split(":");
    final int iterations = Integer.parseInt(params[ITERATION_INDEX]);
    final byte[] salt = fromHex(params[SALT_INDEX]);
    final byte[] hash = fromHex(params[PBKDF2_INDEX]);
    // Compute the hash of the provided password, using the same salt,
    // iteration count, and hash length
    final byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
    // Compare the hashes in constant time. The password is correct if
    // both hashes match.
    return slowEquals(hash, testHash);
  }

  /**
   * Compares two byte arrays in length-constant time. This comparison method
   * is used so that password hashes cannot be extracted from an on-line
   * system using a timing attack and then attacked off-line.
   *
   * @param a
   *     the first byte array
   * @param b
   *     the second byte array
   *
   * @return true if both byte arrays are the same, false if not
   */
  private static boolean slowEquals(byte[] a, byte[] b) {

    int diff = a.length ^ b.length;
    for (int i = 0; i < a.length && i < b.length; i++) {
      diff |= a[i] ^ b[i];
    }
    return diff == 0;
  }

  /**
   * Computes the PBKDF2 hash of a password.
   *
   * @param password
   *     the password to hash.
   * @param salt
   *     the salt
   * @param iterations
   *     the iteration count (slowness factor)
   * @param bytes
   *     the length of the hash to compute in bytes
   *
   * @return the PBDKF2 hash of the password
   */
  private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {

    final PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
    final SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
    return skf.generateSecret(spec).getEncoded();
  }

  /**
   * Converts a string of hexadecimal characters into a byte array.
   *
   * @param hex
   *     the hex string
   *
   * @return the hex string decoded into a byte array
   */
  private static byte[] fromHex(String hex) {

    byte[] binary = new byte[hex.length() / 2];
    for (int i = 0; i < binary.length; i++) {
      binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
    }
    return binary;
  }

  /**
   * Converts a byte array into a hexadecimal string.
   *
   * @param array
   *     the byte array to convert
   *
   * @return a length*2 character string encoding the byte array
   */
  private static String toHex(byte[] array) {

    BigInteger bi = new BigInteger(1, array);
    String hex = bi.toString(16);
    int paddingLength = (array.length * 2) - hex.length();
    if (paddingLength > 0) {
      return String.format("%0" + paddingLength + "d", 0) + hex;
    } else {
      return hex;
    }
  }

}
