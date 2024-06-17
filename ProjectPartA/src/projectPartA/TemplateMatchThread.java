package projectPartA;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class TemplateMatchThread extends Thread{
	private BufferedImage sourceImage;
	private BufferedImage templateImage;
	private int sectionStart;
	private int sectionEnd;
	
	private short[][]coordinates; 
	
	// Constructor class; inherits methods and objects from invokers and superclass
	public TemplateMatchThread(BufferedImage sourceImage, BufferedImage templateImage, int sectionStart, int sectionEnd){
		super();

		this.sourceImage = sourceImage;
		this.templateImage = templateImage;
		this.sectionStart = sectionStart;
		this.sectionEnd = sectionEnd;
	}
	
	// Thread.start override
	public void run() {
		try {
			this.coordinates = templateMatching(sourceImage, templateImage, Data.widthSource, Data.heightSource, Data.widthTemplate, Data.heightTemplate, sectionStart, sectionEnd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// For use in the main class
	public short[][] getCoordinates(){
		return coordinates;
	}
	
	
	private static short[][] templateMatching(BufferedImage source, BufferedImage template, int widthSource, int heightSource, int widthTemplate, int heightTemplate, int sectionStart, int sectionEnd) throws IOException {
		
		double tempSize = heightTemplate*widthTemplate; // Total number of pixels in the template, to normalize the total absolute difference 

		double[][] absDiffMat = new double[Math.abs(sectionEnd-widthTemplate)][Math.abs(heightSource-heightTemplate)];

		short[][] coordinates = new short[1][2];

		ArrayList<short[]> coordinatesList = new ArrayList<short[]>();

		for (int i=sectionStart; i<Math.abs(sectionEnd-widthTemplate); i++) {
			for (int j=0; j<Math.abs(heightSource-heightTemplate); j++) {
				double absDiff = 0;
				BufferedImage Nimage = source.getSubimage(i,j,widthTemplate,heightTemplate);

				short[][] grayNimage = convertImage(Nimage);
				short[][] grayTemp = convertImage(template);
				
				// Compares the total sum of the difference between pixels, when comparing a slice of the source image vs the template.
				for (int a = 0; a < heightTemplate; a++) {
					for (int b = 0; b < widthTemplate; b++) {
						absDiff += Math.abs(grayNimage[a][b] - grayTemp[a][b])/tempSize;
					}
				}
				
				// Stores the absolute difference in a matrix at that particular position of the slice; to compare to a threshold value
				absDiffMat[i][j] = absDiff;

//				Didn't use threshold to figure out where the matches are; if Nimage happens to be a perfect
//				match to the template, then the absolute difference between images should be an 0, or an extremely
//				low number, so i chose 5.
				if (absDiffMat[i][j] <= 5) {
					// Adds the specific i and j values of if there is a perfect match. Done by using an ArrayList
					short[] coordinate = new short[] {(short) i, (short) j};
					coordinatesList.add(coordinate);
				}
			}
		}
		
		coordinates = coordinatesList.toArray(new short[0][]);
		return coordinates;
	}
	
	// Converts a BufferedImage to a 2d matrix of short values (0-255) by converting each pixel colors to grayscale
	private static short[][] convertImage(BufferedImage Nimage) {
	    int width = Nimage.getWidth();
	    int height = Nimage.getHeight();
	    
	    short[][] grayImage = new short[height][width];
	    
	    int rgb;
	    int pr; // red
	    int pg; // green
	    int pb; // blue 
	    
	    for (int i = 0; i < height; i++) {
	        for (int j = 0; j < width; j++) {
	        	
	            rgb = Nimage.getRGB(j,i); // Gets the rgb value at the specific coordinate
	            pr = (rgb >> 16) & 0xFF;
	            pg = (rgb >> 8) & 0xFF;
	            pb = rgb & 0xFF;

	            grayImage[i][j]=(short)Math.round(0.299 *pr + 0.587 * pg  + 0.114 * pb);
	        }
	    }

	    return grayImage;
	}
}
