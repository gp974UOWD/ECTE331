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
	
	static int widthSource;
	static int heightSource;
	static int widthTemplate;
	static int heightTemplate;

	public static void main(String[] args) throws IOException {
		
		String templateName="Template.jpg";
		String sourceName="TenCardG.jpg";
		
		File source=new File(sourceName);
		File template=new File(templateName);
		
		BufferedImage sourceImage = ImageIO.read(source);
		BufferedImage templateImage = ImageIO.read(template);
		
		widthSource = sourceImage.getWidth();
		heightSource = sourceImage.getHeight();
		widthTemplate = templateImage.getWidth();
		heightTemplate = templateImage.getHeight();
		
		//short[][] graySource = readColourImage(sourceName);
		//short[][] grayTemplate = readColourImage(templateName);
		
		short[][] result = templateMatching(sourceImage, templateImage);
		
		// temporary
		short xCoord=result[0][2];
		short yCoord=result[0][1];
		
		short rectWidth=(short)templateImage.getWidth();
		short rectHeight=(short)templateImage.getHeight();
		
		String fileNameOut="Result.jpg";
		writeColourImage(fileNameOut,xCoord,yCoord,rectWidth,rectHeight);
	}   

	
	public static short[][] templateMatching(BufferedImage graySource, BufferedImage grayTemplate) throws IOException {
		
		int tempSize = heightTemplate*widthTemplate;
		double minimum = 1000000;

		BufferedImage Nimage;
		double absDiff;
		double[][] absDiffMat = new double[Math.abs(heightSource-heightTemplate)][Math.abs(widthSource-widthTemplate)];
		
		int ratio = 10;
		double threshold;
		
		short[][] coordinates = new short[1][2];
		
		int numOfMatches = 0;
		
		for (int i=0; i<heightSource-heightTemplate; i++) {
			for (int j=0; j<widthSource-widthTemplate; j++) {
				Nimage = graySource.getSubimage(i,j,widthTemplate,heightTemplate);
				//ImageIO.write(grayTemplate, "jpg", new File("Hi.jpg"));
				
				short[][] grayNimage = convertImage(Nimage);
				short[][] temp = convertImage(grayTemplate);
				
				absDiff = absDiff(grayNimage, temp, tempSize, Nimage.getHeight(), Nimage.getWidth());
				
				absDiffMat[i][j] = absDiff;
				
				if (absDiff < minimum) {
					minimum = absDiff;
				}
				
				threshold = ratio*minimum;
				
				if(absDiff <= threshold) {
					
				}
				
			}
		}
		
		return coordinates;
	}
	
	private static short[][] convertImage(BufferedImage Nimage) {
		
		byte[] pixels;
		pixels = ((DataBufferByte) Nimage.getRaster().getDataBuffer()).getData();
		
		int Nheight = Nimage.getHeight();
		int Nwidth = Nimage.getWidth();
		
		short[][] grayNimage = new short [Nheight][Nwidth];
		
		int coord;
		
		int pr;// red
		int pg;//  green
		int pb;// blue 
		
		for (int i=0; i<Nheight;i++)
			for(int j=0;j<Nwidth;j++)
			{        		     
				coord= 3*(i*Nwidth+j);
				pr= ((short) pixels[coord] & 0xff); // red
				pg= (((short) pixels[coord+1] & 0xff));//  green
				pb= (((short) pixels[coord+2] & 0xff));// blue                

				grayNimage[i][j]=(short)Math.round(0.299 *pr + 0.587 * pg  + 0.114 * pb);         

			}  
		
		return grayNimage;
	}
	
	private static double absDiff(short[][] grayNimage, short[][] grayTemplate, int tempSize, int heightNimage, int widthNimage) {

		double absDiff = 0;
		
		for (int i=0; i<heightNimage; i++) {
			for (int j=0; j<widthNimage; j++) {
				absDiff = (Math.abs(grayNimage[i][j] - grayTemplate[i][j]));
				//System.out.println(grayNimage[i][j] + "     " + grayTemplate[i][j] + "     " + absDiff);
			}
		}
		return absDiff;
		
	}

	public static short[][] readColourImage(String fileName) {

		try
		{
			// RGB pixel values
			byte[] pixels;

			File inp=new File(fileName);
			image = ImageIO.read(inp);
			width = image.getWidth();
			height = image.getHeight();          


			pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			//System.out.println("Dimension of the image: WxH= " + width + " x "+height+" "+ "| num of pixels: "+ pixels.length);
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
		
		return grayImage;

	}


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