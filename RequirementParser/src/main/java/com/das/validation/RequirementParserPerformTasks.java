package com.das.validation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.script.ScriptException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scripting.support.StandardScriptEvaluator;
import org.springframework.scripting.support.StaticScriptSource;

import com.das.pojo.RequirementDetails;

/*
 * Author: Das. This Class converts Given, When and Then requirements into logical representation and 
 * calculate some of the critical possible scenarios and their outcomes.
 */

public class RequirementParserPerformTasks {

	public static ArrayList<RequirementDetails> details = new ArrayList<RequirementDetails>();
	public static ArrayList<String> arrayList = new ArrayList<String>();
	public static LinkedHashMap<String, String> outerMap = new LinkedHashMap<String, String>();
	public static ArrayList<ArrayList<Boolean>> outerBooleanArrayList = new ArrayList<ArrayList<Boolean>>();
	public static ArrayList<ArrayList<String>> outerStringArrayList = new ArrayList<ArrayList<String>>();
	public static Set<ArrayList<Boolean>> mcdcArrayList = new HashSet<ArrayList<Boolean>>();

	public static int totalColumns = 0;

	public static ArrayList<Boolean> decisionTable = new ArrayList<Boolean>();

	public ArrayList<String> run(RequirementDetails rangeValue) throws ScriptException {
		// TODO Auto-generated method stub
		// Given and When statements could have multiple conditions that we need to
		// fetch out
		RequirementParserPerformTasks.getDataIntoArrayList(rangeValue.getGiven(), "given");
		RequirementParserPerformTasks.getDataIntoArrayList(rangeValue.getWhen(), "when");
		RequirementParserPerformTasks.getDataIntoArrayList(rangeValue.getThen(), "then");

		String equation = RequirementParserPerformTasks.getLogicalString(rangeValue.getGiven(), rangeValue.getWhen());

		getPermutation(totalColumns, decisionTable, outerBooleanArrayList);

		for (int m = 0; m < outerBooleanArrayList.size(); m++) {
			String variableName = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			String equationCopy = equation;
			for (int j = 0; j < outerBooleanArrayList.get(m).size(); j++) {

				String replaceText = String.valueOf(variableName.charAt(0));

				String text = Boolean.toString(outerBooleanArrayList.get(m).get(j));

				equationCopy = equationCopy.replaceAll(replaceText, text);
				variableName = (String) variableName.subSequence(0 + 1, variableName.length());

			}
			boolean decisionResult = RequirementParserPerformTasks.executeEquation(equationCopy);
			outerBooleanArrayList.get(m).add(decisionResult);

		}
		calculateMCDC(outerBooleanArrayList);
		System.out.println(mcdcArrayList);
		return arrayList;

	}

	public static void getPermutation(int num, ArrayList<Boolean> decisionTable,
			ArrayList<ArrayList<Boolean>> outerBooleanArrayList) {
		int permuteLen = (int) Math.pow(2, num);
		boolean b[] = new boolean[num];
		for (int i = 0; i < b.length; i++) {
			b[i] = true;
		}

		for (int j = 0; j < permuteLen; j++) {
			decisionTable = new ArrayList<Boolean>();
			for (int i = 0; i < num; i++) {

				System.out.print("  " + b[i] + "  ");
				decisionTable.add(b[i]);
			}
			System.out.println(" ");

			for (int i = num - 1; i >= 0; i--) {
				if (b[i] == true) {
					b[i] = false;
					break;
				} else
					b[i] = true;
			}
			outerBooleanArrayList.add(decisionTable);
		}
	}

	public static void calculateMCDC(ArrayList<ArrayList<Boolean>> outerBooleanArrayList) {
		// TODO Auto-generated method stub
		for (int i = 1; i < outerBooleanArrayList.size(); i++) {
			System.out.println(outerBooleanArrayList);
			boolean hitOne = false;
			boolean hitTwo = false;
			int innerArrayListMaxSize = outerBooleanArrayList.get(i).size();
			for (int j = 0; j < outerBooleanArrayList.get(i).size(); j++) {
				boolean decisionElement = outerBooleanArrayList.get(i).get(innerArrayListMaxSize - 1);
				int decisionElementIndex = innerArrayListMaxSize - 1;
				if ((outerBooleanArrayList.get(i).get(j) == true) && (decisionElement == true)
						&& (j != innerArrayListMaxSize - 1)) {

					compareWithOtherLine(i, j, decisionElementIndex, outerBooleanArrayList);
				}

				if ((outerBooleanArrayList.get(i).get(j) == false) && (decisionElement == false)
						&& (j != innerArrayListMaxSize - 1)) {
					compareWithOtherLine(i, j, decisionElementIndex, outerBooleanArrayList);
				}
			}

		}
	}

	private static void compareWithOtherLine(int i, int j, int decisionElementIndex,
			ArrayList<ArrayList<Boolean>> outerBooleanArrayList) {
		// TODO Auto-generated method stub

		for (int p = 0; p < outerBooleanArrayList.size(); p++) {
			boolean decisionOne = false;
			boolean decisionTwo = true;
			if (p != i) {
				for (int m = 0; m < outerBooleanArrayList.get(p).size(); m++) {

					if (m == j) {
						if (outerBooleanArrayList.get(p).get(m) != outerBooleanArrayList.get(i).get(j)) {
							decisionOne = true;
						}
					} else if (m != decisionElementIndex) {

						if (outerBooleanArrayList.get(p).get(m) != outerBooleanArrayList.get(i).get(m)) {
							decisionTwo = false;
						}
					}
					if (m == decisionElementIndex) {
						if (outerBooleanArrayList.get(p).get(m) == outerBooleanArrayList.get(i).get(m)) {
							decisionTwo = false;
						}
					}
				}
				if (decisionOne && decisionTwo) {
					System.out.println("Found a pair of MCDC: " + p + " " + j);
					mcdcArrayList.add(outerBooleanArrayList.get(p));
					mcdcArrayList.add(outerBooleanArrayList.get(i));
					System.out.println(outerBooleanArrayList.get(p));
					System.out.println(outerBooleanArrayList.get(i));

				}

			}

		}
	}

	private static boolean executeEquation(String equation) {
		// TODO Auto-generated method stub
		StandardScriptEvaluator evaluator = new StandardScriptEvaluator();
		evaluator.setEngineName("groovy");
		return (Boolean) evaluator.evaluate(new StaticScriptSource(equation));
	}

	private static ArrayList<ArrayList<String>> getDataIntoArrayList(String value, String type) {
		// TODO Auto-generated method stub
		ArrayList<String> innerArrayList = new ArrayList<String>();

		if (!type.equalsIgnoreCase("then")) {
			value = value.replaceAll(",", "");
			String temp[] = value.split("and| or ");
			for (int i = 0; i < temp.length; i++) {
				innerArrayList.add(temp[i].trim());
				totalColumns++;
			}
		} else {
			innerArrayList.add(value);
		}
		outerStringArrayList.add(innerArrayList);
		return outerStringArrayList;
	}

	/*
	 * Author: Das This method converts Given and When statements into Logical
	 * Representation
	 */
	private static String getLogicalString(String given, String when) {
		// TODO Auto-generated method stub
		String equation = "";
		String variableName = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 0; i < outerStringArrayList.size(); i++) {
			for (int j = 0; j < outerStringArrayList.get(i).size(); j++) {
				if (i == 0) {
					String replaceText = String.valueOf(variableName.charAt(i));
					given = given.replaceAll(outerStringArrayList.get(i).get(j),
							String.valueOf(variableName.charAt(0)));
					variableName = (String) variableName.subSequence(i + 1, variableName.length());

				}

				if (i == 1) {
					String replaceText = String.valueOf(variableName.charAt(i));
					when = when.replaceAll(outerStringArrayList.get(i).get(j), String.valueOf(variableName.charAt(0)));
					variableName = (String) variableName.subSequence(0 + 1, variableName.length());

				}

			}
			if (i == 0) {
				equation = (equation + " " + given).trim();
				equation = "(" + equation + ")";
				equation = equation.replaceAll(",", "");
			}
			if (i == 1) {
				equation = equation + " && (" + when + ")";
			}
		}

		equation = "(" + equation + ")";
		equation = equation.replaceAll("and", "&&");
		equation = equation.replaceAll(" or ", "||");

		return equation;
	}

	public void printToExcell() throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		String filePath = System.getProperty("user.dir") + "\\ExcellDocs\\ResultPage.xlsx";
		FileOutputStream fos = new FileOutputStream(filePath);
		XSSFSheet sheet = workbook.createSheet("Details");
		XSSFRow row = sheet.createRow(0);
		int m = 0;
		for (int i = 0; i < outerStringArrayList.size(); i++) {
			for (int j = 0; j < outerStringArrayList.get(i).size(); j++) {

				XSSFCell cell = row.createCell(m);
				String cellValue = outerStringArrayList.get(i).get(j).toString();
				cell.setCellValue(cellValue);
				m++;
			}
		}

		Iterator<ArrayList<Boolean>> it = mcdcArrayList.iterator();
		int d = 0;
		while (it.hasNext()) {

			int i = 0;
			row = sheet.createRow(d + 1);
			d++;
			ArrayList<Boolean> tempBooleanList = it.next();
			for (int j = 0; j < tempBooleanList.size(); j++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellValue(tempBooleanList.get(j));
				i++;
				if (j == RequirementParserPerformTasks.outerMap.size() - 1) {
					break;
				}
			}
		}

		workbook.write(fos);
		System.out.println("Data added to the excel");
		fos.close();

	}
}
