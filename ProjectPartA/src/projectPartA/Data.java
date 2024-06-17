package projectPartA;

import java.io.File;

// Small class to store the commonly used objects throughout all classes
public class Data{
	static String templateName = "Template.jpg"; // Change accordingly
	static String sourceName = "TenCardG.jpg"; // Change accordingly
	
	static File source = new File(sourceName);
	static File template = new File(templateName);

	static int widthSource;
	static int heightSource;
	static int widthTemplate;
	static int heightTemplate;
}