/*
 * Copyright (C) 2004  Derek James and Philip Tucker
 *
 * This file is part of ANJI (Another NEAT Java Implementation).
 *
 * ANJI is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * created by Derek James on Aug 28, 2004
 */

package com.anji.imaging;

import java.awt.*;
import javax.imageio.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

/**
 * ImageBinarizer
 */
public class ImageBinarizer {

	/**
	 * @throws Exception
	 */
    public void binarizeFiles() throws Exception {
        // Read from a file
        File dir = new File("images/matches/");
        File dir2 = new File("images/mismatches/");

         String s[] = dir.list();
         for (int i=0; i<s.length; i++)
         {
              Image image = null;
              try {
                  StringBuffer sb1 = new StringBuffer(s[i].length());
                  sb1.append("images/matches/");
                  sb1.append(s[i]);
                  StringBuffer sb2= new StringBuffer(s[i].length());
                  sb2.append("images/matches/");
                  sb2.append(s[i]);
                  
                  File file = new File(sb1.toString());
                  image = ImageIO.read(file); 
                  BufferedImage bi = new BufferedImage( image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);                                  
                  
                  Graphics g = bi.createGraphics();
                  g.drawImage(image, 0, 0, null);
                  g.dispose();
          
                  int[] pixels = new int[ 50*50 ];
                  PixelGrabber pixelGrabber=new PixelGrabber(image, 0, 0, 50, 50, pixels, 0, 50 );
                  // GRAB
                  try {
                    pixelGrabber.grabPixels();
                  } catch (Exception e) {
                    System.out.println("PixelGrabber exception"); 
                  }
 
                  for (int m = 0; m < 2500; m++) {
                    //System.out.print(((float)(pixels[m]/(255*255) + 1)/-257) + " ");
                    if ( pixels[m] > -500000 ) {
                        pixels[m] = 0;
                    } else {
                        pixels[m] = 1;
                }
            
	            int pixIdx = 0;
	            for ( int y = 0; y < 50; y++ ) {
	                for ( int x = 0; x < 50; x++ ) {
	                    if ( pixels[pixIdx] == 1 ) {
	                        bi.setRGB( x, y, 0xff000000 );
	                    } else {
	                        bi.setRGB( x, y, 0xffffffff );
	                    }
	                    pixIdx++;
	                }
	            } 
                  File fileOut = new File(sb2.toString()); 
                  ImageIO.write(bi, "tif", fileOut);                        
                  }
              }
              catch (IOException e) {
              	throw e;
              }              
         }
         
         String s2[] = dir2.list();
         for (int i=0; i < s2.length; i++)
         {
              Image image = null;
              try {
                  StringBuffer sb1 = new StringBuffer(s2[i].length());
                  sb1.append("images/mismatches/");
                  sb1.append(s2[i]);
                  StringBuffer sb2= new StringBuffer(s2[i].length());
                  sb2.append("images/mismatches/");
                  sb2.append(s2[i]);
                  
                  File file = new File(sb1.toString());
                  image = ImageIO.read(file); 
                  BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);                                  

                  Graphics g = bi.createGraphics();
                  g.drawImage(image, 0, 0, null);
                  g.dispose();
          
                  int[] pixels = new int[ 50*50 ];
                  PixelGrabber pixelGrabber=new PixelGrabber(image, 0, 0, 50, 50, pixels, 0, 50 );
                  // GRAB
                  try {
                    pixelGrabber.grabPixels();
                  } catch (Exception e) {
                    System.out.println("PixelGrabber exception"); 
                  }
 
                  for (int m = 0; m < 2500; m++) {
                    //System.out.print(((float)(pixels[m]/(255*255) + 1)/-257) + " ");
                    if ( pixels[m] > -500000 ) {
                        pixels[m] = 0;
                    } else {
                        pixels[m] = 1;
                }
            
	            int pixIdx = 0;
	            for ( int y = 0; y < 50; y++ ) {
	                for ( int x = 0; x < 50; x++ ) {
	                    if ( pixels[pixIdx] == 1 ) {
	                        bi.setRGB( x, y, 0xff000000 );
	                    } else {
	                        bi.setRGB( x, y, 0xffffffff );
	                    }
	                    pixIdx++;
	                }
	            } 
                  File fileOut = new File(sb2.toString()); 
                  ImageIO.write(bi, "tif", fileOut);                        
                  }
              }
              catch (IOException e) {
              	throw e;
              }              
         }
    }
}

