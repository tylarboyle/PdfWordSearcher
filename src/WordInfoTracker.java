
public class WordInfoTracker {
    public String targetWordLowerCase;
    public String targetWordUpperCase;
    public String targetWordAllUpperCase;
    public int numberOfOccurances;
    public boolean absoluteSearch; // if true no delimiter check

    WordInfoTracker(String word) {
        absoluteSearch = word.contains("*");

        word = ScanForIllegals(word);
        targetWordLowerCase = word.toLowerCase();
        targetWordAllUpperCase = MakeAllUpperCase(targetWordLowerCase);
        targetWordUpperCase = targetWordLowerCase.substring(0, 1).toUpperCase() + targetWordLowerCase.substring(1);

    }

    public static String ScanForIllegals(String word) {
        word = word.replace("*", "");
        word = word.replace("\n", "");
        word = word.replace("\r", "");
        return word;
    }

    private String MakeAllUpperCase(String word) {

        String newWord = "";
        newWord = Character.toString(Character.toUpperCase(word.charAt(0)));
        for (int i = 1; i < word.length(); i++) {

            if (word.charAt(i - 1) == ' ' || word.charAt(i - 1) == '-') {
                newWord = newWord + Character.toString(Character.toUpperCase(word.charAt(i)));

            } else {
                newWord = newWord + Character.toString(word.charAt(i));
            }

        }

        return newWord;
    }

}
