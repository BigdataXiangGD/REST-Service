package com.boraji.tutorial.springboot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CyclometicCalculation {

    public int calculateCC(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> ifs = Arrays.asList("if", "?");
        List<String> loops = Arrays.asList("while", "case", "for", "switch", "do");
        List<String> returns = Arrays.asList("return");
        List<String> conditions = Arrays.asList("&&", "||", "or", "and", "xor");
        int complexity = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] wordsInLine = line.split(" |;|\t");
            boolean ifFlag = false;
            for (String perWord : wordsInLine) {
                if (ifs.contains(perWord)) {
                    ifFlag = true;
                    complexity++;
                }
                if (loops.contains(perWord)) {
                    complexity++;
                }
                if (conditions.contains(perWord)) {
                    if (ifFlag) {
                        complexity += 2;
                    } else {
                        complexity++;
                    }
                }
                if (returns.contains(perWord)) {
                    complexity--;
                }
            }
        }
        return complexity;
    }

}