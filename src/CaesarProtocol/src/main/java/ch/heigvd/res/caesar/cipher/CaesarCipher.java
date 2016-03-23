package ch.heigvd.res.caesar.cipher;

import java.io.IOException;
import java.util.Random;
import java.lang.Math;

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
    if(!Character.isAlphabetic(c)) {
      return c;
    }
    if(Character.isUpperCase(c)) {
      return (char)(((int)c - 'A' + key) % 26 + 'A');
    }
    else {
      return (char)(((int)c - 'a' + key) % 26 + 'a');
    }
  }

  public String decryptMessage(String message) {
    String result = "";

    for(char c : message.toCharArray()) {
      result += decrypt(c);
    }

    return result;
  }

  public char decrypt(char c) {
    if(!Character.isAlphabetic(c)) {
      return c;
    }
    if(Character.isUpperCase(c)) {
      return (char)(('Z' - (int)c + key) % 26 + 'A');
    }
    else {
      return (char)(('z' - (int)c + key) % 26 + 'a');
    }
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