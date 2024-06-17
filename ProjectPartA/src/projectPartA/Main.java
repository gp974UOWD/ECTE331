package projectPartA;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
	
	static int numOfThreads = 1; // Change according to requirements. numOfThreads = 1 when checking for single thread implementation

	public static void main(String[] args) throws IOException, InterruptedException{

		Main obj = new Main(); // Creating an object of the Main class to properly measure the execution time of class methods
		
		// Reading both images
		BufferedImage sourceImage = ImageIO.read(Data.source);
		BufferedImage templateImage = ImageIO.read(Data.template); 
		
		Data.widthSource = sourceImage.getWidth();
		Data.heightSource = sourceImage.getHeight();
		Data.widthTemplate = templateImage.getWidth();
		Data.heightTemplate = templateImage.getHeight();

		long start = System.nanoTime(); // Start measuring before executing the method
		
		if (numOfThreads == 1) {
			System.out.println("Working with " + numOfThreads + " thread");
		} else System.out.println("Working with " + numOfThreads + " threads");

		obj.ThreadedTemplateMatch(sourceImage, templateImage, Data.widthSource, Data.heightSource, Data.widthTemplate, Data.heightTemplate);
		
		long end = System.nanoTime(); // Check the time after executing the method
		
		double execution = (end - start) * 1e-9; // Converts from nanoseconds to seconds.
		double executionMinutes = execution / 60; // Converts from seconds to minutes
		
		System.out.println("Done! Execution time: " + executionMinutes + " minutes");
	}   



	private void ThreadedTemplateMatch(BufferedImage sourceImage, BufferedImage templateImage, int widthSource, int heightSource, int widthTemplate, int heightTemplate) throws IOException, InterruptedException {
		
		TemplateMatchThread[] Thread = new TemplateMatchThread[numOfThreads]; // Creates an array of threads to change with numOfThreads

		int sectionWidth = Data.widthSource/numOfThreads; // Adjusts the amount of sections depending on the number of threads
		int remainderWidth = Data.widthSource % numOfThreads; // In case source width / numOfThreads isnt perfectly divisible

		int sectionStart = 0; // Changes with each thread
		int sectionEnd = sectionWidth + remainderWidth; // The thread stops scanning at a certain point; the very first section includes the remainder so it doenst miss out on any pixels

		short[][] mergedCoordinates = new short[0][2]; // Initializes mergedCoordinates to be used later

		// For loop to create a new thread for every numOfThreads
		for (int i = 0; i < numOfThreads; i++) {
			Thread[i] = new TemplateMatchThread(sourceImage, templateImage, sectionStart, sectionEnd); // New thread object created, determined by an array
			Thread[i].start(); // Runs thread

			sectionStart = sectionEnd; // Everytime a thread is created, the next section to be used by the next thread is directly to the right of the current section
			sectionEnd += sectionWidth; // The next section stops at x times the section width.
		}

		for (int i=0; i<numOfThreads; i++) {
			Thread[i].join(); // The main thread waits until all threads have finished. Saves memory and allows the working threads to be synchronized.
			short[][] coordinates = Thread[i].getCoordinates(); // Grabs the coordinates from each thread when template matched.
			mergedCoordinates = mergeArrays(mergedCoordinates, coordinates); // Merge into mergedCoordinates
		}
		
		writeColourImage("Result.jpg", mergedCoordinates, (short)Data.widthTemplate, (short)Data.heightTemplate, sourceImage); // Outputs result image with rectangles in correct spots
	}


	private static short[][] mergeArrays(short[][]... arrays) {
		int length = 0;
		for (short[][] arr : arrays) {
			length += arr.length;
		}

		short[][] result = new short[length][];

		int index = 0;
		for (short[][] arr : arrays) {
			System.arraycopy(arr, 0, result, index, arr.length);
			index += arr.length;
		}

		return result;
	}

	// Method to output the BufferedImage file into the filestream
	public static void writeColourImage(String fileName, short[][] coordinates, short rectWidth, short rectHeight, BufferedImage image) {   
		try {                   
			Image scaledImage = image.getScaledInstance(-1,-1, 0);
			ImageIO.write(addRectangle(scaledImage, coordinates, rectWidth, rectHeight),"jpg", new File(fileName));

		} catch (IOException e) {
			e.printStackTrace();
		}       
	}

	// Method to write a BufferedImage with a rectangle at every matched coordinate
	public static BufferedImage addRectangle(Image img, short[][] coordinates, short rectWidth, short rectHeight) {

		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

		Graphics2D g2D = bi.createGraphics();
		g2D.drawImage(img, 0, 0, null);
		g2D.setColor(Color.RED);

		// Everytime a match is found, a rectangle is drawn at that spot.
		for(short[] coordinate : coordinates) {
			int xcoord = coordinate[0];
			int ycoord = coordinate[1];
			g2D.drawRect(xcoord, ycoord, rectWidth, rectHeight);
		}     

		g2D.dispose();
		return bi;
	}
}