import boofcv.core.image.ConvertImage;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import io.nayuki.qrcodegen.QrCode;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import boofcv.struct.image.*;

/**
 * Created by Wojciech on 23/03/2019.
 * It can be changed at Edit | Settings | File and Code Templates.
 */
public class Test {
    String [] strings;
   public static ListDisplayPanel gui = new ListDisplayPanel();



    public static void main (String[] args) throws IOException {
        String data = readFileAsString("Text.txt");
       // System.out.println(data);
        int length = data.length()/11;

        String[] divideString = divideString(data,11);
//        try{
//            for(int i = 0; i < divideString.length; i++) {
//                generateQR(divideString[i],i);
//            }
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//
////        mergeQr(1);
        for(int i = 0; i < length; i+=3) {
            CQRCode cqrCode = new CQRCode(divideString[i],
                    divideString[i+1],
                    divideString[i+2],
                    QrCode.Ecc.HIGH);
            //    cqrCode.showMask(cqrCode.getRedMask());
            //    System.out.println();
            //    cqrCode.showMask(cqrCode.getGreenMask());
            //    System.out.println();
            //    cqrCode.showMask(cqrCode.getBlueMask());
            //    System.out.println();


            //   int [][] tab = cqrCode.getFinalMask();
            //   for(int[] t : tab) {
            //       for(int d : t) {
            //           System.out.printf("%4d",d);
            //       }
            //       System.out.println();
            //   }

            Planar<GrayU8> planar = cqrCode.toImage(10, 4);

          //  gui.addImage(planar, "ttt");
            UtilImageIO.saveImage(planar, "ColorQRs/CQR"+Integer.toBinaryString(i)+".png");
            //planar = convertMasks(planar);
            UtilImageIO.saveImage(planar.getBand(0), "RedMasks/CQRRED" + Integer.toBinaryString(i) +".png");
            UtilImageIO.saveImage(planar.getBand(1), "GreenMasks/CQRGREEN" + Integer.toBinaryString(i) +".png");
            UtilImageIO.saveImage(planar.getBand(2), "BlueMasks/CQRBLUE" + Integer.toBinaryString(i) +".png");
           // gui.addImage(planar.getBand(0), "RED");
           // gui.addImage(planar.getBand(1), "GREEN");
           // gui.addImage(planar.getBand(2), "BLUE");

          //  GrayU8 red = planar.getBand(0);
          //  gui.addImage(red, "GrayU8RED");


          //  ShowImages.showWindow(gui, "title", true);


            QrCode qrCode = cqrCode.getQrCodes(2);
            BufferedImage img = qrCode.toImage(10, 4);           // Convert to bitmap image

            File imgFile = new File("TestQRsMasks/TESTQRMASK"+Integer.toBinaryString(i)+".png");   // File path for output
            ImageIO.write(img, "png", imgFile);
        //    System.out.println(cqrCode.getModule(0, 9));
        }
//        System.out.println("TEST!!!!!");
//        System.out.println((4 & 4) != 0 ? 1 : 0);
//        System.out.println((4 & 2) != 0 ? 1 : 0);
//        System.out.println((4 & 1) != 0 ? 1 : 0);
    }
    public static Planar<GrayU8> convertMasks(Planar<GrayU8> planar) {
        for(int y = 0; y < planar.height; y++) {
            for (int x = 0; x < planar.width; x++) {
                planar.getBand(0).set(x,y,255-(planar.getBand(0).get(x,y)));
                planar.getBand(1).set(x,y,255-(planar.getBand(1).get(x,y)));
                planar.getBand(2).set(x,y,255-(planar.getBand(2).get(x,y)));

            }
        }
        return planar;
    }


    public static String readFileAsString(String filename) throws IOException {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(filename)));

        return  data;
    }

    public static String[] divideString(String string,int qrVersionLenth) {
        if(string.isEmpty()) {
            throw  new NullPointerException();
        }
        int length = string.length();
        int parts = length/qrVersionLenth;
        int qrVersionLengthCorrect = qrVersionLenth-1;
        String[] strings = new String[parts];

        for(int i = 0; i < parts; i++) {
            strings[i] = string.substring(i*qrVersionLenth,(i+1)*qrVersionLengthCorrect + i + 1);
        }

//        for(String s: strings) {
//            System.out.println(s);
//        }
        return strings;
    }

//    private static void mergeQRs(String filePath, int i) throws IOException {
//        File []imgFile = new File[3];
//        BufferedImage []bufferedImage = null;
//        GrayU8 []grayU8s = new GrayU8[3];
//        Planar<GrayU8>
//        int width = 290;
//        int height = 290;
//
//        for(int ii = 0; ii < 2; ii++) {
//            imgFile[i] = new File("ImagesRaw/" + Integer.toBinaryString(i*ii));
//            bufferedImage[i] = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
//            bufferedImage[i] = ImageIO.read(imgFile[i]);
//            grayU8s[i] = char
//
//        }
//
//        Planar<GrayU8> grayU8Planar = new Planar<GrayU8>(GrayU8.class,width,height,3);
//    }

    private static void mergeQr(int i) throws IOException {
        int width = 290;
        int height = 290;
//        File imgFile = new File("ImagesRaw/" + Integer.toBinaryString(i)+".png");
//        BufferedImage bufferedImage = null;
//        bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
//        bufferedImage = ImageIO.read(imgFile);

        GrayU8 grayU8a = UtilImageIO.loadImage("ImagesRaw/" + Integer.toBinaryString(i)+".png",GrayU8.class);

        Planar<GrayU8> grayU8Planar = new Planar<GrayU8>(GrayU8.class,width,height,3);
        grayU8Planar.setBand(0,grayU8a);
        gui.addImage(grayU8Planar,"ttt" );
        ShowImages.showWindow(gui,"title", true);
    }

//    private void drawFinderPattern(int x, int y, int width, int heigth) {
//        for (int dy = -4; dy <= 4; dy++) {
//            for (int dx = -4; dx <= 4; dx++) {
//                int dist = Math.max(Math.abs(dx), Math.abs(dy));  // Chebyshev/infinity norm
//                int xx = x + dx, yy = y + dy;
//                if (0 <= xx && xx < size && 0 <= yy && yy < size)
//                    setFunctionModule(xx, yy, dist != 2 && dist != 4);
//            }
//        }
//    }
//public BufferedImage toImage(int scale, int border) {
//    if (scale <= 0 || border < 0)
//        throw new IllegalArgumentException("Value out of range");
//    if (border > Integer.MAX_VALUE / 2 || size + border * 2L > Integer.MAX_VALUE / scale)
//        throw new IllegalArgumentException("Scale or border too large");
//
//    BufferedImage result = new BufferedImage((size + border * 2) * scale, (size + border * 2) * scale, BufferedImage.TYPE_INT_RGB);
//    for (int y = 0; y < result.getHeight(); y++) {
//        for (int x = 0; x < result.getWidth(); x++) {
//            boolean color = getModule(x / scale - border, y / scale - border);
//            result.setRGB(x, y, color ? 0x000000 : 0xFFFFFF);
//        }
//    }
//    return result;
//}



    private static void generateQR(String text, int i) throws IOException {
        QrCode.Ecc errCorLvl = QrCode.Ecc.HIGH;  // Error correction level

        QrCode qr = QrCode.encodeText(text, errCorLvl);  // Make	 the QR Code symbol

        BufferedImage img = qr.toImage(10, 4);           // Convert to bitmap image

        File imgFile = new File("ImagesRaw/"+Integer.toBinaryString(i) + ".png");   // File path for output
        ImageIO.write(img, "png", imgFile);              // Write image to file
        String svg = qr.toSvgString(4);                  // Convert to SVG XML code
    }









}
