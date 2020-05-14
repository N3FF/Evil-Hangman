import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;


/**
 * TDD: tests written against the Hangman Specification to validate each of the methods functions in
 * the prescribed way. Doesn't check: performance, javadoc, messages on exceptions, good design,
 * proper encapsulation of things not in the specification (fields, helper methods...), proper scope
 * (static/instance/local/loop local) of variables
 * 
 * Note, if the specification says "you should assume", I am making the assumption (which means, for
 * example, I won't test for a null or empty dictionary being passed to the constructor)
 * 
 * If you identify any tests you feel are incorrect/misinterpret/too strictly interpret, or any
 * missing tests, let me know - bonus points for correct identification
 * 
 * @author Amelia
 * @version 2017-05-23
 *
 */
public class HangmanManagerTest {

  // set up a few dictionaries for use in the tests
  // dictionary when we want or can use the simplest one
  private static final List<String> DICTIONARY_1 = Arrays.asList("a");
  // basic dictionary simple list with no words of length 2
  private static final List<String> DICTIONARY_BASIC =
      Arrays.asList("meet", "a", "cat", "i", "bat", "sit", "sip", "fate");
  private static final List<String> DICTIONARY_PATTERN = Arrays.asList("sesoopoeet");
  private static final int NUMBER_OS = 3;
  private static final int NUMBER_TS = 1;
  private static final int NUMBER_SS = 2;
  private static final Set<Character> PATTERN_LETTERS;
  static {
    PATTERN_LETTERS = new HashSet<Character>();
    PATTERN_LETTERS.add('e');
    PATTERN_LETTERS.add('o');
    PATTERN_LETTERS.add('p');
    PATTERN_LETTERS.add('s');
    PATTERN_LETTERS.add('t');
  }
  private static int PATTERN_LENGTH = 10;
  // just the words of length N from DICTIONARY_BASIC
  private static final int LENGTH_3 = 3;
  private static final List<String> DICTIONARY_BASIC_LENGTH_3 =
      Arrays.asList("cat", "bat", "sit", "sip");
  private static final int LENGTH_4 = 4;
  private static final List<String> DICTIONARY_BASIC_LENGTH_4 = Arrays.asList("meet", "fate");

  /**
   * 0 length throws an exception
   */
  @Test(expected = IllegalArgumentException.class)
  public void testHangmanManager_0Length() {
    // Arrange, Act
    new HangmanManager(DICTIONARY_1, 0, 4);
    // Assert -- exception, so it won't reach this:
    fail("0 length should throw an exception");
  }

  /**
   * negative max should throw an exception
   */
  @Test(expected = IllegalArgumentException.class)
  public void testHangmanManager_negativeMax() {
    // Arrange, Act
    new HangmanManager(DICTIONARY_1, 1, -1);
    // Assert -- exception, so it won't reach this:
    fail("negative max should throw an exception");
  }

  /**
   * It's fine for no words of the requested length to be in the dictionary
   */
  @Test
  public void testHangmanManager_noWordsOfLength() {
    // Arrange, Act
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 2, 1);
    // Assert
    assertEquals("potential word list should be empty since no words of requested length", 0,
        hm.words().size());
  }

  /**
   * If there are no words of the length matching, pattern is supposed to throw an error
   */
  @Test(expected = IllegalStateException.class)
  public void testPattern_noWordsOfLength() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 2, 1);
    // Act
    hm.pattern(); // don't need to see the result
    // Assert
    fail("pattern supposed to throw an exception if no words in the list already");
  }

  /**
   * If there are no words of the length matching, record is supposed to throw an error
   */
  @Test(expected = IllegalStateException.class)
  public void testRecord_noWordsOfLength() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 2, 1);
    // Act
    hm.record('a'); // don't need to see the result
    // Assert
    fail("record supposed to throw an exception if no words in the list already");
  }

  /**
   * If there are no words of the length matching, words should still return an empty list of words.
   * 
   * already tested by testHangmanManager_noWordsOfLength
   * 
   * @Test public void testWords_noWordsOfLength() { }
   */

  /**
   * If there are no words of the length matching, guessesLeft should still return the # of guesses
   * passed in.
   */
  @Test
  public void testGuessesLeft_noWordsOfLength() {
    // Arrange
    final int GUESSES_LEFT = 1; // make sure constructor and assert match
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 2, GUESSES_LEFT);
    // Act
    int guessesLeft = hm.guessesLeft();
    // Assert
    assertEquals("guessesLeft should be unphased by a 0-size set", GUESSES_LEFT, guessesLeft);
  }

  /**
   * If there are no words of the length matching, guesses should still return an empty set (no
   * guesses yet)
   */
  @Test
  public void testGuesses_noWordsOfLength() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 2, 1);
    // Act
    Set<Character> guesses = hm.guesses();
    // Assert
    assertEquals("guesses should be unphased by a 0-size set, and should be empty",
        Collections.emptySet(), guesses);
  }

  /**
   * the constructor filters out words of incorrect length and leaves in words of correct length;
   * all words within list, not at edges
   */
  @Test
  public void testWords_filtersInner() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 3, 1);
    // Act
    Set<String> words = hm.words();

    // Assert
    // set comparison won't work on equals, so use "removeall" to see if
    // they match
    Set<String> wordsLeft = new HashSet<>(words);
    wordsLeft.removeAll(DICTIONARY_BASIC_LENGTH_3);
    Set<String> extraWords = new HashSet<>(DICTIONARY_BASIC_LENGTH_3);
    extraWords.removeAll(words);
    assertEquals("needs to be just the words of length 3", 0, wordsLeft.size());
    assertEquals("needs to be all of the words of length 3", 0, extraWords.size());
  }

  /**
   * the constructor filters out words of incorrect length and leaves in words of correct length,
   * words at edges of list
   */
  @Test
  public void testWords_filtersEdges() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 4, 1);
    // Act
    Set<String> words = hm.words();
    // Assert
    // set comparison, can't use equals so do the math...
    Set<String> wordsLeft = new HashSet<>(words);
    wordsLeft.removeAll(DICTIONARY_BASIC_LENGTH_4);
    Set<String> extraWords = new HashSet<>(DICTIONARY_BASIC_LENGTH_4);
    extraWords.removeAll(words);
    assertEquals("needs to be just the words of length 3", 0, wordsLeft.size());
    assertEquals("needs to be all of the words of length 3", 0, extraWords.size());
  }

  /**
   * the number of guesses requested is correctly recorded
   */
  @Test
  public void testGuessesLeft_RegisteredCorrectly() {
    // Arrange
    final int GUESSES_REQUESTED = 2;
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 3, GUESSES_REQUESTED);
    // Act
    int guessesLeft = hm.guessesLeft();
    // Assert
    assertEquals("guesses left needs to show value requested", GUESSES_REQUESTED, guessesLeft);
  }

  /**
   * good guess (letter in word) doesn't change # guesses left
   */
  @Test
  public void testGuessesLeft_UnchangedOnGoodGuess() {
    // Arrange
    final int GUESSES_REQUESTED = 2;
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 4, GUESSES_REQUESTED);
    // Act
    hm.record('t'); // good guess, in all the words
    int guessesLeft = hm.guessesLeft();
    // Assert
    assertEquals("guesses left needs to be unchanged on a good guess", GUESSES_REQUESTED,
        guessesLeft);
  }

  /**
   * guess not in any word left in the list
   */
  @Test
  public void testGuessesLeft_DecreasedOnBadGuessNotInAnyWord() {
    // Arrange
    final int GUESSES_REQUESTED = 2;
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 4, GUESSES_REQUESTED);
    // Act
    hm.record('y'); // bad guess, not in any words
    int guessesLeft = hm.guessesLeft();
    // Assert
    assertEquals("guesses left needs to be decreased on a bad guess", GUESSES_REQUESTED - 1,
        guessesLeft);
  }

  /**
   * guess not in enough words that it will register as a bad guess
   */
  @Test
  public void testGuessesLeft_DecreasedOnBadGuessNotInEnoughWords() {
    // Arrange
    final int GUESSES_REQUESTED = 2;
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 3, GUESSES_REQUESTED);
    // Act
    hm.record('p'); // bad guess, in 1 word but 3 don't have it so that
                    // group wins
    int guessesLeft = hm.guessesLeft();
    // Assert
    assertEquals("guesses left needs to be decreased on a bad guess", GUESSES_REQUESTED - 1,
        guessesLeft);
  }

  /**
   * Note that you can guess you final time (when number of guesses left is 0) but a request after
   * that would fail. So we can do 2 guesses and have numGuesses at 0, we just can't make a third
   * guess. So we don't, because we are testing that we can reach 0 okay.
   */
  @Test
  public void testGuessesLeft_Reaches0() {
    // Arrange
    final int GUESSES_REQUESTED = 2;
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 3, GUESSES_REQUESTED);
    // Act
    hm.record('p'); // bad guess, in 1 word but 3 don't have it so that
                    // group wins
    hm.record('e'); // bad guess, in none of the words
    int guessesLeft = hm.guessesLeft();
    // Assert
    assertEquals("guesses left needs to derease all the way to 0", 0, guessesLeft);
  }

  /**
   * Can't guess after out of guesses. -- this is a record test, not a numGuesses test.
   */
  @Test(expected = IllegalStateException.class)
  public void testGuessesLeft_WontGoPast0() {
    // Arrange
    final int GUESSES_REQUESTED = 2;
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 3, GUESSES_REQUESTED);
    // Act
    hm.record('p'); // bad guess, in 1 word but 3 don't have it so that
                    // group wins
    hm.record('e'); // bad guess, in none of the words
    hm.record('z'); // bad guess, but OUT OF GUESSES
    // Assert
    fail("ran out of guesses, should have thrown IllegalStateException");
  }

  /**
   * guessesLeft unchanged on good guesses repeatedly
   */
  @Test
  public void testGuessesLeft_LotsOfGoodGuesses() {
    // Arrange
    final int GUESSES_REQUESTED = 4;
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, 4, GUESSES_REQUESTED);
    // Act
    hm.record('e'); // good guess, in all the words
    hm.record('t'); // good guess, in all the words
    int numGuesses = hm.guessesLeft();
    // Assert
    assertEquals("guessesLeft should not change when guesses are good", GUESSES_REQUESTED,
        numGuesses);
  }

  /**
   * good guess (letter in word) doesn't change # guesses left, even after a bad guess does.
   */
  @Test
  public void testGuessesLeft_MixOfGuessesUpdatesCorrectly() {
    // Arrange
    final int GUESSES_REQUESTED = 2;
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC_LENGTH_3, 3, GUESSES_REQUESTED);
    // Act
    hm.record('p'); // bad guess, avoids some words
    int guessesLeft1 = hm.guessesLeft();
    hm.record('t'); // good guess, in all the words
    int guessesLeft2 = hm.guessesLeft();
    // Assert
    assertEquals("guesses left needs to be changed on a bad guess", GUESSES_REQUESTED - 1,
        guessesLeft1);
    assertEquals("guesses left needs to be unchanged on a good guess", GUESSES_REQUESTED - 1,
        guessesLeft2);
  }

  // guesses change as guesses are made; don't change when it's a repeated
  // guess (but get an exception so...)

  /**
   * already checked no guesses yet above. so first guess, recorded? Note:assume lower case letter,
   * so don't test non-letters or uppercase.
   */
  @Test
  public void testGuesses_firstGuessBadGuess() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_PATTERN, PATTERN_LENGTH, PATTERN_LENGTH);
    // Act
    hm.record('a'); // won't find anything, don't care
    Set<Character> guesses = hm.guesses();
    // Assert
    assertEquals("recorded the first guess", 1, guesses.size());
    assertEquals("recorded the right letter", new Character('a'), guesses.iterator().next());
  }

  /**
   * first guess, but find it this time
   */
  @Test
  public void testGuesses_firstGuessGoodGuess() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_PATTERN, PATTERN_LENGTH, PATTERN_LENGTH);
    // Act
    hm.record('s'); // found it
    Set<Character> guesses = hm.guesses();
    // Assert
    assertEquals("recorded the first guess", 1, guesses.size());
    assertEquals("recorded the right letter", new Character('s'), guesses.iterator().next());
  }

  /**
   * mix of guesses, all recorded
   */
  @Test
  public void testGuesses_mixOfGuess() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_PATTERN, PATTERN_LENGTH, PATTERN_LENGTH);
    Set<Character> results = new HashSet<>();
    results.add('s');
    results.add('y');
    results.add('t');
    // Act
    hm.record('s'); // found it
    hm.record('y'); // not found
    hm.record('t'); // found it
    Set<Character> guesses = hm.guesses();
    // Assert
    assertEquals("recorded the guesses", 3, guesses.size());
    assertTrue("only the letters found", guesses.containsAll(results));
  }

  /**
   * all the guesses registered, even the last one
   */
  @Test
  public void testGuesses_finalGuess() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_PATTERN, PATTERN_LENGTH, PATTERN_LENGTH);
    // Act
    hm.record('s');
    hm.record('t');
    hm.record('e');
    hm.record('o');
    hm.record('p');
    Set<Character> guesses = hm.guesses();
    // Assert
    assertTrue("only the letters found", guesses.containsAll(PATTERN_LETTERS));
    assertTrue("all the letters found", PATTERN_LETTERS.containsAll(guesses));
  }

  /**
   * repeat guesses don't change the list; can't check it, this is a record test.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGuesses_repeatGuess() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_PATTERN, PATTERN_LENGTH, PATTERN_LENGTH);
    // Act
    hm.record('s');
    hm.guesses();
    hm.record('s'); // can't get beyond this
    // Assert
    fail("can't get a repeat guess to run");
  }

  @Test
  public void testPattern_noGuesses() {
    // Arrange
    final String EMPTY_PATTERN_4 = "----";
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, LENGTH_4, LENGTH_4);
    // Act
    String pattern = hm.pattern();
    // Assert
    assertEquals("pattern at start should be ----", EMPTY_PATTERN_4, pattern);
  }

  @Test
  public void testPattern_firstGoodGuess() {
    // Arrange
    final String T_PATTERN_3 = "--t";
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, LENGTH_3, LENGTH_3);
    hm.record('t');
    // Act
    String pattern = hm.pattern();
    // Assert
    assertEquals("pattern on first good guess should be --t", T_PATTERN_3, pattern);
  }

  /**
   * "cat", "bat", "sit", "sip" -- y finds all, p finds 1 so goes the other way
   */
  @Test
  public void testPattern_onlyBadGuesses() {
    // Arrange
    final String EMPTY_PATTERN_3 = "---";
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, LENGTH_3, LENGTH_3);
    hm.record('y'); // not in any string
    hm.record('p'); // not in some strings
    // Act
    String pattern = hm.pattern();
    // Assert
    assertEquals("pattern after bad guesses should be ---", EMPTY_PATTERN_3, pattern);
  }

  @Test
  public void testPattern_twoGoodGuesses() {
    // Arrange
    final String TWO_PATTERN_3 = "-at";
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, LENGTH_3, LENGTH_3);
    hm.record('t'); // in all strings
    hm.record('i'); // not in some strings
    hm.record('a'); // in all strings
    // Act
    String pattern = hm.pattern();
    // Assert
    assertEquals("pattern after two good guesses should be -at", TWO_PATTERN_3, pattern);
  }

  /**
   * Using the length-3 words (cat, bat, sit, sip) we have to navigate our way through the game ...
   * t chooses 3, i chooses 2 (cat/bat), b chooses 1 (which one? check and tune test to it)
   */
  @Test
  public void testPattern_gotTheWord() {
    // Arrange
    String full_pattern_3 = "cat"; // might be bat, see logic below
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, LENGTH_3, LENGTH_3);
    hm.record('t'); // in all strings
    hm.record('i'); // not in some strings
    hm.record('a'); // in all strings
    int didB = hm.record('b'); // not in some strings
    if (didB == 1) {
      full_pattern_3 = "bat";
    }
    hm.record('c'); // in only string left if bat not chosen; works even if b chosen
    // Act
    String pattern = hm.pattern();
    // Assert
    assertEquals("pattern after three good guesses should be " + full_pattern_3, full_pattern_3,
        pattern);
  }

  /**
   * now testing record, we want to see the first test finding the letter (largest set has 1 t)
   */
  @Test
  public void testRecord_firstGuessGood() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, LENGTH_3, LENGTH_3);
    // Act
    int foundT = hm.record('t');
    // Assert
    assertEquals("found the t", 1, foundT);
  }

  /**
   * looking for a letter that's not in any string...
   */
  @Test
  public void testRecord_firstGuessBadInNoString() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, LENGTH_3, LENGTH_3);
    // Act
    int foundZ = hm.record('z');
    // Assert
    assertEquals("expect no z found", 0, foundZ);
  }

  /**
   * looking for a letter that's in the smaller set (p)
   */
  @Test
  public void testRecord_firstGuessBadSmallerSet() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, LENGTH_3, LENGTH_3);
    // Act
    int foundP = hm.record('p');
    // Assert
    assertEquals("expect no p found", 0, foundP);
  }

  /* public void testRecord_matchOneLetter() {} done in firstGuessGood above */

  /**
   * use a string with several o's, p's, and s's in it.
   */
  @Test
  public void testRecord_matchSeveralLetters() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_PATTERN, PATTERN_LENGTH, PATTERN_LENGTH);
    // Act
    int findO = hm.record('o'); // won't find anything, don't care
    int findS = hm.record('s'); // won't find anything, don't care
    int findT = hm.record('t'); // won't find anything, don't care
    // Assert
    assertEquals("found all the o's", NUMBER_OS, findO);
    assertEquals("found all the s's", NUMBER_SS, findS);
    assertEquals("found all the t's", NUMBER_TS, findT);
  }

  // testRecord_repeatGuess -- already handled
  // testRecord_picksBiggerSet() {} -- did in firstGuessGood

  @Test
  public void testRecord_picksArbitrarySetWhenSameNumber() {
    // Arrange
    String fullPattern3 = "c-t"; // might be bat, see logic below
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, LENGTH_3, LENGTH_3);
    hm.record('t'); // in all strings
    hm.record('i'); // not in some strings, left with cat and bat
    int didB = hm.record('b'); // not in some strings
    if (didB == 1) {
      fullPattern3 = "b-t";
    }
    int didC = hm.record('c'); // in only string left if bat not chosen; works even if b chosen
    // Act
    String pattern = hm.pattern();
    // Assert
    // picked one or the other, either is fine.
    // ... if didB, pattern is b-t
    // ... if didC, pattern is c-t
    // had to choose one or the other
    assertEquals("had to choose b or c, not both", 1, didB + didC);
    if (didB == 1) {
      assertEquals("didB, pattern must be b-t", fullPattern3, pattern);
    } else {
      assertNotEquals("no didB, pattern must not be b-t", "b-t", pattern);
    }
    if (didC == 1) {
      assertEquals("didC, pattern must be c-t", fullPattern3, pattern);
    } else {
      assertNotEquals("no didC, pattern must not be c-t", "c-t", pattern);
    }
  }

  /**
   * No match so only partition is ---, and all of the words are still in it. Simplify -- just test
   * size of words list, since we already tested content earlier.
   */
  @Test
  public void testRecord_picksAllWhenNoMatch() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, LENGTH_3, LENGTH_3);
    // Act
    int beforeRecord = hm.words().size(); // 4 words
    int findZ = hm.record('z');
    int afterRecord = hm.words().size(); // still 4 words
    // Assert
    // findZ is 0, before and after record are the same
    assertEquals("no Z found", 0, findZ);
    assertEquals("size unchanged before and after find Z", beforeRecord, afterRecord);
    assertEquals("size is expected to be 4", 4, afterRecord);
  }

  /**
   * meet and fate both have an e, but their patterns differ. (earlier tests focused on finding or
   * not finding the letter, this is about finding it with two different patterns) No way to predict
   * which one is chosen (since same size sets, it relies on the implementation) but one of them
   * must be ...
   */
  @Test
  public void testRecord_sameLetterDifferentPartitions() {
    // Arrange
    HangmanManager hm = new HangmanManager(DICTIONARY_BASIC, LENGTH_4, LENGTH_4);
    String finalPatternE = "---e"; // assume fate found ("it's fate...")
    // Act
    int findE = hm.record('e');
    String pattern = hm.pattern();
    if (findE == 2) { // no fate today
      finalPatternE = "-ee-";
    } else if (findE != 1) {
      fail("had to find 1 or 2 e's");
    }
    // Assert
    assertEquals("found one of the partitions", finalPatternE, pattern);
  }

}
