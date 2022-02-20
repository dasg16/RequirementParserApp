package com.das.datadriven;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.das.pojo.RequirementDetails;

@Component
public class DataDrivenTest {

	public static Object[] mapRowDetailsInPOJO(String temp[], int rowCount, int columnCount) throws IOException {

		Object tempRows[] = new Object[rowCount];

		int secondLoopValue = columnCount;

		int k = 0;
		int rowCountValue = 0;
		RequirementDetails requirementDetails = new RequirementDetails();
		for (int j = secondLoopValue + 1; j < temp.length; j++) {
			System.out.println(temp[j]);
			if ((temp[j].isEmpty())) {
				secondLoopValue = j + 1;
				k = 0;
				rowCountValue++;
				continue;
			}

			if (k == 0)
				requirementDetails.setTestCaseID(temp[j]);
			if (k == 1)
				requirementDetails.setGiven(temp[j]);
			if (k == 2)
				requirementDetails.setWhen(temp[j]);
			if (k == 3)
				requirementDetails.setThen(temp[j]);
			if (k == 4) {
				requirementDetails.setCoverageType(temp[j]);
				tempRows[0] = requirementDetails;
				requirementDetails = new RequirementDetails();
				System.out.println(requirementDetails.toString());
			}
			k++;
		}

		return tempRows;

	}

}
