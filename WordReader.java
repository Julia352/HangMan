
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class WordReader {

	private ArrayList<String[]> words = new ArrayList<String[]>();
	private ArrayList<String[]> wordsCopy = new ArrayList<String[]>();

	public WordReader(String wordsFileName, String questionsFileName) {

		try (Scanner wordsInput = new Scanner(Paths.get(wordsFileName))) {
			try (Scanner questionsInput = new Scanner(Paths.get(questionsFileName))) {

				while (wordsInput.hasNext()) {
					words.add(new String[2]);
					words.get(words.size() - 1)[0] = wordsInput.next();
					words.get(words.size() - 1)[1] = questionsInput.next();
				}
				
			} catch (Exception e) {
				System.out.println("Couldn't find/read file: " + questionsFileName);
				System.out.println("Error message: " + e.getMessage());
			}

		} catch (Exception e) {
			System.out.println("Couldn't find/read file: " + wordsFileName);
			System.out.println("Error message: " + e.getMessage());
		}
		
		wordsCopy.addAll(words);

	}

	public String[] getRandomWord() {
		int i = (int) (Math.random() * wordsCopy.size());
		String[] result = wordsCopy.get(i);
		wordsCopy.remove(i);
		return result;
	}

}
