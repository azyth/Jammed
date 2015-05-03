package pwdClassifier;

/*
 * Created by Marcos on 3/24/15.
 *
 * Description/README: Determines password strength in a binary fashion
 * (strong or weak) by using the entropy method learned in lecture from
 * NIST guidelines.
 *
 * The program expects 2 files as commandline arguments
 * the input file containing a list of passwords (one per line), and the
 * output file to write to.
 *
 * The program also expects a dictionary input, called "sampleDictionary.txt"
 * to be in the same directory as the source/compiled file. It uses the
 * dictionary to look up input passwords, if they exist in the dictionary,
 * they are automatically determined to be weak.
 *
 * The minimum entropy of 25 was decided upon after online research about
 * the NIST guidelines, and browsing https://security.stackexchange.com/
 *
 */

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PasswordClassifier {

    private static final double minEntropy = 25; // Determined from NIST guidelines
    private static final String dictfile = "sampleDictionary.txt";
    private static final Charset charsetUTF8 = Charset.forName("UTF-8");

    public static void main(String[] args) {
        String inputFileName = args[0];
        String outputFileName = args[1];

        try {
            // Load sample dictionary
            List<String> dictLines = Files.readAllLines(Paths.get(dictfile), charsetUTF8);
            HashSet<String> dictionary = new HashSet<String>(dictLines);

            //// Load input file
            ArrayList<String> outPutData = new ArrayList<String>();
            List<String> lines = Files.readAllLines(Paths.get(inputFileName), charsetUTF8);
            for (String line : lines) {
                //System.out.println(line);
                if(pwdIsStrong(line, dictionary)) {
                    outPutData.add(line+", strong");
                } else { // weak
                    outPutData.add(line+", weak");
                }
            }

            // write data
            Path out = Paths.get(outputFileName);
            Files.write(out, outPutData, charsetUTF8);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean pwdIsStrong(String pwd, HashSet dictionary) {

        if(dictionary.contains(pwd)) {
            return false;
        }

        int pwdLen = pwd.length();
        // Calculate entropy according to NIST specs
        double entropy = 0;
        if(pwdLen > 1) {
            entropy = 4; // First char = 4 bits
        } else {
            return false; // pasword is trivially weak
        }

        int count = 1;
        boolean foundUppercase = false;
        boolean foundNonAlphabetic = false;
        for(int i = 0; i < pwdLen; i++) {
            // Handle password length
            if(count <= 7) {
                entropy = entropy + 2;
            } else if(count <= 20) {
                entropy = entropy + 1.5;
            } else {
                entropy = entropy + 1;
            }
            // check for uppercase
            if(!foundUppercase && Character.isUpperCase(pwd.codePointAt(i))) {
                entropy = entropy + 3;
                foundUppercase = true;
            }
            // check for non alphabetic characters
            if(!foundNonAlphabetic && (!Character.isAlphabetic(pwd.codePointAt(i)) || !Character.isLetterOrDigit(pwd.charAt(i)))) {
                entropy = entropy + 3;
                foundNonAlphabetic = true;
            }
            count = count + 1;
        }

        return entropy >= minEntropy;
    }


}
