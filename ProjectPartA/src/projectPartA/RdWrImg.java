package projectPartA;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.image.DataBufferByte;

public class RdWrImg {

	public static short [][] grayImage;
	public static int width;
	public static int height;
	private static BufferedImage image;

	public static void main(String[] args) throws IOException {
		String templateName="Template.jpg";
		String fileNameInp="TenCardG.jpg";
		String fileNameOut="TenCardG4.jpg";
		readColourImage(fileNameInp);
		
		File source=new File(templateName);
		File template=new File(fileNameInp);
		BufferedImage im1 = ImageIO.read(source);
		BufferedImage im2 = ImageIO.read(template);
		
		int[][] result = templateMatching(im1, im2);
		
		short xCoord=(short)result[0][0];
		short yCoord=(short)result[0][0];
		short rectWidth=(short)im2.getWidth();
		short rectHeight=(short)im2.getHeight();

		writeColourImage(fileNameOut,xCoord,yCoord,rectWidth,rectHeight);

		System.out.println(">> Completed! Check the rectangle at the left top corner of the generated "+ fileNameOut+ "image under this project folderr");
	}   

	
	public static int[][] templateMatching(BufferedImage im1, BufferedImage im2) {
		
		int c1 = im1.getWidth();
		int r1 = im1.getHeight();
		int c2 = im2.getWidth();
		int r2 = im2.getHeight();

		int tempSize = r2*c2;
		double minimum = 1000000;

		BufferedImage Nimage;
		double absDiff = 0;
		double[][] absDiffMat = new double[r1-r2+1][c1-c2+1];
		
		for (int i=0; i<r1-r2+1; i++) {
			for (int j=0; j<c1-c2+1; j++) {
				Nimage = im1.getSubimage(j,i,c2,r2);
				

				if (absDiff < minimum) {
					minimum = absDiff;
				}
			}
		}
		
		int ratio = 10;
		double threshold = ratio*minimum;
		
		int[][] coordinates = new int[3][3]; //temporary to relieve errors
		return coordinates;
	}

	/**
	 *    
	 * @param fileName
	 */
	public static void readColourImage(String fileName) {

		try
		{
			// RGB pixel values
			byte[] pixels;

			File inp=new File(fileName);
			image = ImageIO.read(inp);
			width = image.getWidth();
			height = image.getHeight();          


			pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			System.out.println("Dimension of the image: WxH= " + width + " x "+height+" "+ "| num of pixels: "+ pixels.length);



			//rgb2gray in a 2D array grayImage                 
			int pr;// red
			int pg;//  green
			int pb;// blue     

			grayImage =new short [height][width];
			int coord;
			for (int i=0; i<height;i++)
				for(int j=0;j<width;j++)
				{        		     
					coord= 3*(i*width+j);
					pr= ((short) pixels[coord] & 0xff); // red
					pg= (((short) pixels[coord+1] & 0xff));//  green
					pb= (((short) pixels[coord+2] & 0xff));// blue                

					grayImage[i][j]=(short)Math.round(0.299 *pr + 0.587 * pg  + 0.114 * pb);         

				}  
		}
		catch (IOException e) {
			e.printStackTrace();
		} 

	}



	/**
	 * 
	 * @param fileName
	 * @param xCoord
	 * @param yCoord
	 * @param rectWidth
	 * @param rectHeight
	 */
	public static void writeColourImage(String fileName,short xCoord, short yCoord, short rectWidth, short rectHeight) {   
		try {                   

			Image scaledImage = image.getScaledInstance(-1,-1, 0);
			// rectangle coordinates and dimension to superimpose on the image
			ImageIO.write(
					add_Rectangle(scaledImage,xCoord,yCoord,rectWidth,rectHeight),"jpg",
					new File(fileName));

		} catch (IOException e) {
			e.printStackTrace();
		}       
	}



	/**
	 * 
	 * @param img
	 * @param xCoord
	 * @param yCoord
	 * @param rectWidth
	 * @param rectHeight
	 * @return
	 */
	public static BufferedImage add_Rectangle(Image img, short xCoord, short yCoord, short rectWidth, short rectHeight) {

		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bi = new BufferedImage(
				img.getWidth(null), img.getHeight(null),
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g2D = bi.createGraphics();
		g2D.drawImage(img, 0, 0, null);
		g2D.setColor(Color.RED);
		g2D.drawRect(xCoord, yCoord, rectWidth, rectHeight);              
		g2D.dispose();
		return bi;
	}

}