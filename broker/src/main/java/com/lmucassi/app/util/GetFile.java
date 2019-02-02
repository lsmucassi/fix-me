package com.lmucassi.app.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.*;

public class GetFile {

	public static int countLine(String filename){
		try{
            int count;
		    File file = new File(filename);
			FileReader fReader = new FileReader(file);
			LineNumberReader lReader = new LineNumberReader(fReader);
			lReader.skip(Long.MAX_VALUE);
			count = lReader.getLineNumber();
			lReader.close();
			return count;
		}
		catch (IOException err){
            errFile(1);
			err.getMessage();
		}
		return -1;
	}

	public static String[] getLine(String fName){
		try{
			File file = new File(fName);
			FileReader fReader = new FileReader(file);
			BufferedReader bReader = new BufferedReader(fReader);
			String line = null;
			String items[] = new String[countLine(fName) + 1];
			int index = 0;

			while ((line = bReader.readLine()) != null){
				items[index] = line;
				index++;
			}
			bReader.close();
			return items;
		}
		catch (IOException err){
		    errFile(2);
			err.getMessage();
		}
		return null;
	}


	public static void updateFile(String inst, double quant, int stock, String fName){
		try{
			String[] data = getLine(fName);
			String[] items = data[0].split(",");
			String delLine = null;
			String newLine = "";
			double newQuant = 0;
			File file = new File(fName);
			FileWriter Writer = new FileWriter(file);

			for (String line : items){
				if (line.contains(inst.split(" ")[0])){
                    delLine = line;
                    newQuant = Double.parseDouble(line.split(" ")[1]);
                }
			}

			if (stock == 1)
			    newQuant = newQuant + quant;
			else if (stock == 2)
			    newQuant = newQuant - quant;

			for (String line : items) {
				if (line.equals(delLine))
				    newLine = inst + " " + newQuant + "," + newLine;
				else
				    newLine = line + "," + newLine;
			}
			Writer.write(newLine);
			Writer.close();
		}
		catch (IOException err) {
		    errFile(0);
			System.out.println("\033[31;1mError: \033[0m" + err);
		}
	}

	public static void errFile(int num) {
        if (num == 0) {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("\033[31;1mError: Problem updating file [ File processing] \033[0m");
            System.out.println("-----------------------------------------------------------------------------------");
        } else if (num == 1) {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("\033[31;1mError: Problem processing file [ File processing] \033[0m");
            System.out.println("-----------------------------------------------------------------------------------");
        } else if (num == 2) {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("\033[31;1mError: Problem reading file [ File processing] \033[0m");
            System.out.println("-----------------------------------------------------------------------------------");
        }
    }
}