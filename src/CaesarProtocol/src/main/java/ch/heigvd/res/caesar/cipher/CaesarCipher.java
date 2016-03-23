package ch.heigvd.res.caesar.cipher;

import java.text.Normalizer;
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

    //Normalizer.Form.NFD used to avoid accented char to be recognized like letters
    for(char c : Normalizer.normalize(message, Normalizer.Form.NFD).toCharArray()) {
      result += encrypt(c);
    }

    return result;
  }

  private char encrypt(char c) {
    if(!Character.isLetter(c)) {
      return c;
    }
    else {
      char alpBeg = Character.isUpperCase(c) ? 'A' : 'a';
      
      return (char)(((int)c - alpBeg + key) % 26 + alpBeg);
    }
  }

  public String decryptMessage(String message) {
    String result = "";

    //Normalizer.Form.NFD used to avoid accented char to be recognized like letters
    for(char c : Normalizer.normalize(message, Normalizer.Form.NFD).toCharArray()) {
      result += decrypt(c);
    }

    return result;
  }

  private char decrypt(char c) {
    if(!Character.isLetter(c)) {
      return c;
    }
    else {
      char alpBeg = Character.isUpperCase(c) ? 'A' : 'a';
      return (char)(((int)c - alpBeg + 26 - key) % 26 + alpBeg);
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