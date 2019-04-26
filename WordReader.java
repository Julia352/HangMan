
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WordReader {
    private static final String fileName = "/res/words.txt";

    private ArrayList<String> words = new ArrayList<String>();
    private int i = -1;

    public WordReader(String fileName) {
    	 
        try (Scanner input = new Scanner(Paths.get(fileName))) {
               

            
            while (input.hasNext())
            	
                words.add(    input.next()  );
        }
        catch (Exception e) {
            System.out.println("Couldn't find/read file: " + fileName);
            System.out.println("Error message: " + e.getMessage());
        }
    }

   public String getRandomWord() {
    	i++;
    	return words.get(i);
    	
    }
        
}
