package com.example.demo;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.das.validation.RequirementParserPerformTasks;

public class UnitTestMCDC {
	public static ArrayList<ArrayList<Boolean>> outerBooleanArrayList;
	public static ArrayList<Boolean> arrayList;
	public static ArrayList<Boolean> decisionTable;

	@Test
	public void setOuterBooleanArrayList() {
		outerBooleanArrayList = new ArrayList<ArrayList<Boolean>>();
		getPermutation(4);

		RequirementParserPerformTasks.calculateMCDC(outerBooleanArrayList);
	}

	public static void getPermutation(int num) {
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

}
