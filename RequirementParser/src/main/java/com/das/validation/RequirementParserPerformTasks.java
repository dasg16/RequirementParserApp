package com.das.validation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

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

		// For Base line
		for (int i = 0; i < totalColumns; i++) {
			decisionTable.add(true);
		}
		outerBooleanArrayList.add(decisionTable);

		// For rest of the lines
		for (int i = 0; i < totalColumns; i++) {
			decisionTable = new ArrayList<Boolean>();
			for (int j = 0; j < totalColumns; j++) {
				if (i == j) {
					decisionTable.add(false);
				} else {
					decisionTable.add(true);
				}

			}
			outerBooleanArrayList.add(decisionTable);
		}

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

		return arrayList;

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
			String temp[] = value.split("and|or");
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
		equation = equation.replaceAll("or", "||");

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

		for (int k = 1; k < outerBooleanArrayList.size(); k++) {
			int i = 0;
			row = sheet.createRow(k);
			for (int j = 0; j < outerBooleanArrayList.get(k).size(); j++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellValue(outerBooleanArrayList.get(k).get(j));
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
