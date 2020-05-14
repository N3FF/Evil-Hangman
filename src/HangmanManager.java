import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Creates an evil hangman that will destroy your hopes and dreams.
 * 
 * @author Justin
 *
 */
public class HangmanManager {

  private Map<String, Set<String>> wordPools = new HashMap<>();
  private Set<Character> guessedChars = new HashSet<>();
  private int guessesLeft;
  private String pattern;

  /**
   * <b><i>HangmanManager</i></b>
   * <p>
   * <tt>public HangmanManager(List&lt;String&gt; dictionary, int length, int max)</tt>
   * </p>
   * <p>
   * Uses the <b>dictionary</b> of words using the <b>length</b> to create a new dictionary. Sets
   * <b>max</b> number guesses allowed.
   * </p>
   * 
   * @param dictionary - Complete dictionary
   * @param length - Length of the word to be guessed
   * @param max - Number of guesses allowed
   * 
   * @throws IllegalArgumentException No words are found of the <b>length</b> specified or the
   *         <b>max</b> number of guesses is less than 0.
   */
  public HangmanManager(List<String> dictionary, int length, int max) {
    if (length < 1 || max < 0) {
      throw new IllegalArgumentException(
          "You may not have a negative number of guesses or a word length that"
              + " is too short. You entered length: " + length + " max: " + max);
    }

    // Fills Set with all words from the dictionary of size "length"
    Set<String> wordSet = new HashSet<>();
    for (String word : dictionary) {
      if (word.length() == length) {
        wordSet.add(word);
      }
    }

    // Sets number of guesses left
    guessesLeft = max;

    // Builds the dash pattern
    StringBuilder patternBuilder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      patternBuilder.append('-');
    }

    pattern = patternBuilder.toString();

    // Put the pattern as the key with the set built above as the value
    wordPools.put(pattern, wordSet);
  }

  /**
   * @return <tt>Set&lt;String&gt;</tt> - A new set of words.
   */
  public Set<String> words() {
    return wordPools.get(pattern);
  }

  /**
   * @return <tt>int</tt> - The number of guesses remaining.
   */
  public int guessesLeft() {
    return guessesLeft;
  }

  /**
   * @return <tt>String</tt> - Current letter pattern created from guesses.
   * @throws IllegalStateException If the word list is empty.
   */
  public String pattern() {
    if (wordPools.get(pattern).isEmpty()) {
      throw new IllegalStateException("No words found");
    }
    return pattern;
  }

  /**
   * @return <tt>Set&lt;Character&gt;</tt> - All of the letters guessed.
   */
  public Set<Character> guesses() {
    return guessedChars;
  }

  /**
   * @param ch - The letter that has been guessed.
   * @return <tt>int</tt> - How many times the letter appears in the word.
   * @throws IllegalStateException If there is less than 1 guess or the word list is empty.
   * @throws IllegalArgumentException If the letter has already been guessed.
   */
  public int record(char ch) {
    if (guessesLeft < 1 || wordPools.get(pattern).isEmpty()) {
      throw new IllegalStateException("You have too few guesses available");
    }
    if (!guessedChars.add(ch)) {
      throw new IllegalArgumentException("This letter has already been guessed");
    }

    buildMap(ch);

    getLargestPool();

    // Replace the map with a new map that contains the current pattern and word pool.
    wordPools = new HashMap<String, Set<String>>() {
      private static final long serialVersionUID = 1L;
      {
        put(pattern, wordPools.get(pattern));
      }
    };

    return countCharMatches(ch);
  }

  // Builds a map for all possible solutions using the character passed in
  private void buildMap(char ch) {
    Set<String> words = new HashSet<>(wordPools.get(pattern));
    StringBuilder patternBuilder = new StringBuilder();
    for (String word : words) {
      patternBuilder.setLength(0);
      patternBuilder.append(pattern);
      // Create pattern string
      for (int i = 0, patLength = pattern.length(); i < patLength; i++) {
        if (word.charAt(i) == ch) {
          patternBuilder.setCharAt(i, ch);
        }
      }
      // Check if pattern key already exists. If it does, remove the word from its current pool and
      // put it in the correct pool. If the pattern doesn't exist in the keys, then create a new key
      // with the word value
      if (wordPools.containsKey(patternBuilder.toString())) {
        if (!pattern.equals(patternBuilder.toString())) {
          wordPools.get(pattern).remove(word);
          wordPools.get(patternBuilder.toString()).add(word);
        }
      } else {
        wordPools.put(patternBuilder.toString(), new HashSet<String>(Arrays.asList(word)));
        wordPools.get(pattern).remove(word);
      }
    }
  }

  // Iterates through the pool to find the largest Set
  private void getLargestPool() {
    // Starts with the size of the current pool so if there is another pool of equal size it won't
    // change. This means it will stick with the pool that causes you to lose a guess over a pool
    // that is the same size that contains a correct letter pick.
    int largestSet = wordPools.get(pattern).size();

    Iterator<Entry<String, Set<String>>> iter = wordPools.entrySet().iterator();
    while (iter.hasNext()) {
      Entry<String, Set<String>> pool = iter.next();
      // If it's the largest pool, set it as the new largest and use its pattern
      if (pool.getValue().size() > largestSet) {
        largestSet = pool.getValue().size();
        pattern = pool.getKey();
      }
    }
  }

  private int countCharMatches(char ch) {
    int letterCount = 0;
    // -1 if letter not found, otherwise returns letter index for first occurrence
    int charIndex = pattern.indexOf(ch);
    // Each time it finds a character in an index add 1 to the letter count
    if (charIndex > -1) {
      for (int i = charIndex; i < pattern.length(); i++) {
        if (pattern.charAt(i) == ch) {
          letterCount++;
        }
      }
    } else {
      // If no chars found remove a guess
      guessesLeft--;
    }
    return letterCount;
  }
}
