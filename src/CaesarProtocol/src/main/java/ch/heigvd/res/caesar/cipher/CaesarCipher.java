package ch.heigvd.res.caesar.cipher;

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
    if(!Character.isAlphabetic(c)) {
      return c;
    }
    else {
      char alpBeg = Character.isUpperCase(c) ? 'A' : 'a';
      
      return (char)(((int)c - alpBeg + key) % 26 + alpBeg);
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
    else {
      char alpBeg = Character.isUpperCase(c) ? 'A' : 'a';
      return (char)(((int)c - alpBeg + 26 + key) % 26 + alpBeg);
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