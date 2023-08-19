import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.*;

public class ngram_analysis_2019510083 {

    public static String cleanPunc(String source)
    {
        String puncs = "[!#$%&'()*+,-./:;<=>?@^_`{|}~]";

        source = source.replaceAll("[!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]", "");

        for (int i = 0; i < 10; i++) {
            source = source.replace("  "," ");
        }

        return source;
    }

    public static String readFile(String filename) // read a txt file and returns context
    {
        String data = "";

        try
        {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine())
            {
                data += myReader.nextLine();
                if(myReader.hasNextLine())
                    data += " ";
            }
            myReader.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return data;
    }

    public static String[] bubbleSort(HashMap<String,Float> MM)
    {
        int n = MM.size();
        String[] sorted = MM.keySet().toArray(new String[0]);

        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (MM.get(sorted[j]) < MM.get(sorted[j + 1])) {
                        // swap arr[j+1] and arr[j]
                    String temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
        return sorted;
    }

    public static String[] getTokens(String text) // get words from a text
    {
        text = cleanPunc(text);
        text = text.trim();

        String tokens[] = text.split(" ");

        return tokens;
    }

    public static String[] getNgrams(int n, String[] tokens) // ngram combinations
    {
        int size = tokens.length;
        String[] combinations = new String[size-(n-1)];

        for (int i = 0; i < size-(n-1); i++) {
            combinations[i] = "";
            for (int j = 0; j < n; j++) {
                combinations[i] += tokens[i+j];
                if (j<n-1)
                    combinations[i] += " ";
            }
        }
        return combinations;
    }

    public static HashMap<String,Integer> getTokenCounts(String text, String[] tokens) // token counter
    {
        HashMap<String,Integer> counts = new HashMap<String, Integer>();

        for (String token : tokens) {
            if(counts.get(token) != null)
                continue;

            for (String token2 : tokens)
                if(token2.equals(token) && token.length()>0)
                {
                    if(counts.get(token) == null)
                        counts.put(token,1);
                    else
                        counts.replace(token,(counts.get(token)+1));
                }
        }

        return counts;
    }

    public static HashMap<String,Float> MarkovModel(HashMap<String,Integer> counts, String[] ngrams, float N)
    {
        HashMap<String,Float> MM_ngram = new HashMap<>();
        float temp = 1;

        for (String ngram : ngrams) {
            temp = 1;
            for (String token : ngram.split(" ")) {
                temp *= counts.get(token)/N;
            }
            MM_ngram.put(ngram,temp);
        }

        return MM_ngram;
    }

    public static void printMarkov(String header, HashMap<String,Float> MM, String[] tops)
    {
        int max = 50;
        if(tops.length < 50)
            max = tops.length;

        System.out.println("\n------------------------------ " + header);
        for (int i = 0; i < max; i++) {
            System.out.println((i+1) + ") " + tops[i] + " : " + MM.get(tops[i]) );
        }
        System.out.println("------------------------------ " + header);
    }

    // n gram analysis

    public static void main(String[] args) {
        double start, end, milisecond;
        milisecond = 1000000; // converts nanosecond to milisecond
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object

        System.out.print("\nPlease enter a txt file: ");
        String file = myObj.nextLine();  // Read user input

        String text = readFile(file);

        start = System.nanoTime();
        String[] tokens = getTokens(text); // split words
        int N = tokens.length; // token count
        end = System.nanoTime();
        System.out.println("\n-> Tokens split in " + (end-start)/milisecond + " milliseconds");

        start = System.nanoTime();
        HashMap<String, Integer> counts = getTokenCounts(text,tokens); // count the words frequency
        end = System.nanoTime();
        System.out.println("\n-> Tokens counted in " + (end-start)/milisecond + " milliseconds");

        start = System.nanoTime(); //Unigrams
        String[] unigrams = getNgrams(1,tokens); // gets n gram combinations
        HashMap<String, Float> MM1 = MarkovModel(counts,unigrams,N); // gets markov model n gram possibilities
        String[] sorted1 = bubbleSort(MM1);
        end = System.nanoTime();
        System.out.println("\n-> Unigrams counted in " + (end-start)/milisecond + " milliseconds");

        start = System.nanoTime(); //Bigrams
        String[] bigrams = getNgrams(2,tokens);
        HashMap<String, Float> MM2 = MarkovModel(counts,bigrams,N);
        String[] sorted2 = bubbleSort(MM2);
        end = System.nanoTime();
        System.out.println("\n-> Bigrams counted in " + (end-start)/milisecond + " milliseconds");

        start = System.nanoTime(); //trigrams
        String[] trigrams = getNgrams(3,tokens);
        HashMap<String, Float> MM3 = MarkovModel(counts,trigrams,N);
        String[] sorted3 = bubbleSort(MM3);
        end = System.nanoTime();
        System.out.println("\n-> Trigrams counted in " + (end-start)/milisecond + " milliseconds");

        printMarkov("Unigrams",MM1, sorted1); // print markov model
        printMarkov("Bigrams",MM2, sorted2);
        printMarkov("Trigrams",MM3, sorted3);
    }
}
