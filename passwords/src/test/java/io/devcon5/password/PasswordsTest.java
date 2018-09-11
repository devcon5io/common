package io.devcon5.password;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 *
 */
public class PasswordsTest {

  final Random rand = new Random();

  @Test
  public void createHash() throws Exception {

    for (int i = 0; i < 100; i++) {
      String password = generatePassword(i);
      String wrongPassword = password + i;

      String hash = Passwords.createHash(password);
      String secondHash = Passwords.createHash(password);

      System.out.println("testing password " + password + " with hash " + hash);

      assertNotEquals("Hashes for same password are equal", hash, secondHash);
      assertFalse("Wrong password should not be accepted", Passwords.validatePassword(wrongPassword, hash));
      assertTrue("Correct password should be accepted", Passwords.validatePassword(password, hash));
    }
  }

  private String generatePassword(final int i) {

    return IntStream.range(0, 8 + (i % 12)).map(n -> rand.nextInt(94) + 33)
                            .mapToObj(c -> Character.toString((char)c))
                            .collect(Collectors.joining());
  }
}
