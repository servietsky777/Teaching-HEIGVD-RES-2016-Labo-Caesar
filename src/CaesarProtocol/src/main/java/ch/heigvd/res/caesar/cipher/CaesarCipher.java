package ch.heigvd.res.caesar.cipher;

import java.io.IOException;
import java.util.Random;

public class CaesarCipher {

  private int key;

  public CaesarCipher() {
    Random random = new Random();
    key = random.nextInt(25)+1;
  }

  public CaesarCipher(int key) throws IllegalArgumentException {
    this.setKey(key);
  }

  public String encryptMessage(String message) {
    String result = "";

    for(char c : message.toCharArray()) {
      result += encrypt(c);
    }

    return result;
  }

  public char encrypt(char c) {
    return (char)('a' + (((int)c + key) % 26));
  }

  public String decryptMessage(String message) {
    String result = "";

    for(char c : message.toCharArray()) {
      result += decrypt(c);
    }

    return result;
  }

  public char decrypt(char c) {
    return (char)('a' + (((int)c - key) % 26));
  }

  public int getKey() {
    return key;
  }

  public void setKey(int key) throws IllegalArgumentException {
    if (key < 1 && key > 25)
      throw new IllegalArgumentException("Must be beetween 1 and 25");

    this.key = key;
  }
}