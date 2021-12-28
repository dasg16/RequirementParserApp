package com.example.demo;

import java.io.IOException;

import javax.script.ScriptException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.das.datadriven.DataDrivenExcel;
import com.das.datadriven.DataDrivenTest;
import com.das.pojo.RequirementDetails;
import com.das.validation.RequirementParserPerformTasks;

@ComponentScan({ "com.das.datadriven", "com.das.validation", "com.das.common" })
public class RequirementParserTest {
	public static Object[] oneDObject;
	public static ApplicationContext applicationContext;

	@BeforeClass
	public void setLaunchActivities() throws IOException {
		applicationContext = new ClassPathXmlApplicationContext("spring.xml");

		var dataDrivenExcel = (DataDrivenExcel) applicationContext.getBean("DataDrivenExcel");
		String temp[] = dataDrivenExcel.fetchDataFromExcel();
		if (temp != null) {
			var dataDrivenTest = (DataDrivenTest) applicationContext.getBean("DataDrivenTest");
			oneDObject = DataDrivenTest.mapRowDetailsInPOJO(temp, DataDrivenExcel.getRows(), DataDrivenExcel.getCols());
		}

	}

	@Test
	public void performParallelTask() throws ScriptException {
		RequirementDetails requirementDetails = (RequirementDetails) oneDObject[0];
		var requirementParserPerformTasks = (RequirementParserPerformTasks) applicationContext
				.getBean("RequirementParserPerformTasks");
		requirementParserPerformTasks.run(requirementDetails);
	}

	@AfterClass
	public void tearDownActivities() throws IOException {
		var requirementParserPerformTasks = (RequirementParserPerformTasks) applicationContext
				.getBean("RequirementParserPerformTasks");
		requirementParserPerformTasks.printToExcell();
	}

}
