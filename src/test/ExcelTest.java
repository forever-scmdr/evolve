package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;



public class ExcelTest {
	private String[] delimiters = {"Т/опт:", "Р/Усл:", "Пр-во:", "C/x:", "Т/роз:"};
	private HashSet<String> sections = new HashSet<String>();
	
	private void addLine(String line) {
		HashMap<Integer, String> typesStartIndeces = new HashMap<Integer, String>();
		for (String type : delimiters) {
			int index = line.indexOf(type);
			while (index >= 0) {
				typesStartIndeces.put(index, type);
				index = line.indexOf(type, index + type.length());
			}
		}
		ArrayList<Integer> typesIndecesOrdered = new ArrayList<Integer>(typesStartIndeces.keySet());
		Collections.sort(typesIndecesOrdered);
		typesIndecesOrdered.add(line.length());
		for (int i = 0; i < typesIndecesOrdered.size() - 1; i++) {
			int index = typesIndecesOrdered.get(i);
			int nextIndex = typesIndecesOrdered.get(i + 1);
			String type = typesStartIndeces.get(index);
			String section = line.substring(index + type.length(), nextIndex);
			section = section.trim() + " (" + type + ")";
			sections.add(section);
		}
	}
	
	public void readFile(String fileName) throws IOException {
		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "Cp1251"));
		String line;
		long time = System.currentTimeMillis();
		int counter = 0;
		// delimiters
		line = reader.readLine();
		delimiters = line.split(",");
		// обработка самого файла
		while ((line = reader.readLine()) != null) {
			addLine(line);
			counter++;
			if (System.currentTimeMillis() - time > 2000) {
				time = System.currentTimeMillis();
				System.out.println(counter);
			}
		}
		reader.close();
	}
	
	public ArrayList<String> sectionsSorted() {
		ArrayList<String> result = new ArrayList<String>(sections);
		Collections.sort(result);
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		ExcelTest test = new ExcelTest();
//		test.addLine("Т/опт: краны шаровые Т/роз: краны шаровые Т/опт: электронагреватели (ТЭНы) Т/опт: котлы универсальные, дизельные, газовые Т/роз: котлы универсальные, дизельные, газовые Р/Усл: установка, монтаж и обслуживание котельного оборудования");
		test.readFile("D:/book.csv");
		ArrayList<String> result = test.sectionsSorted();
		FileWriter fstream = new FileWriter("D:/sections.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		for (String string : result) {
			out.write(string);
			out.newLine();
		}
		out.close();
		fstream.close();
//		for (String string : result) {
//			System.out.println(string);
//		}
	}
}
