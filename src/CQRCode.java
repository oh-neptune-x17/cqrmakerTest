import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import io.nayuki.qrcodegen.QrCode;

import java.awt.image.BufferedImage;
import java.util.GregorianCalendar;

/**
 * Created by Wojciech on 24/03/2019.
 * It can be changed at Edit | Settings | File and Code Templates.
 */
public class CQRCode {
    private int size;
    private boolean[][] redMask;
    private boolean[][] greenMask;
    private boolean[][] blueMask;
    private int[][] finalMask;
    private QrCode []qrCodes;

    public CQRCode(String message1Red, String message2Green, String message3Blue, QrCode.Ecc ECCLevel) {
        qrCodes = new QrCode[3];
        qrCodes[0] = QrCode.encodeText(message1Red,ECCLevel);
        qrCodes[1] = QrCode.encodeText(message2Green,ECCLevel);
        qrCodes[2] = QrCode.encodeText(message3Blue,ECCLevel);
        this.size = qrCodes[0].size;
       // System.out.println("Size " + qrCodes[0].size);
       // System.out.println("Version" + qrCodes[0].version);
        this.redMask = new boolean[size][size];
        this.greenMask = new boolean[size][size];
        this.blueMask = new boolean[size][size];
        this.finalMask = new int[size][size];
        fillMask(redMask,qrCodes[0]);
        fillMask(greenMask,qrCodes[1]);
        fillMask(blueMask,qrCodes[2]);
        mergeMasks();
//        drawFinderPattern(3,3);
//        drawFinderPattern(size - 4,3);
//        drawFinderPattern(3,size - 4);


    }

    public CQRCode(QrCode code1, QrCode code2, QrCode code3) {
    }

    private void fillMask(boolean[][] mask, QrCode qrCode) {
        try{
            for(int i = 0; i < size; i++) {
                for(int j = 0; j < size; j++) {
                    mask[j][i] = qrCode.getModule(i,j);
                }
            }

        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
    public void showMask(boolean[][] maskQR){
        int i ;
        for (boolean []masks: maskQR) {
            for(boolean mask : masks) {
                i = mask ? 1:0;
                System.out.printf("%4d",i);
            }
            System.out.println();
        }
    }

    public boolean[][] getRedMask () {
        return redMask;
    }

    public boolean[][] getGreenMask () {
        return greenMask;
    }

    public boolean[][] getBlueMask () {
        return blueMask;
    }

    public int[][] getFinalMask () {
        return finalMask;
    }

    /**
     *
     * @param i - number of mask number, between 0 and 3 - [0,3)
     * @return One mask of CQR - one black-white QRCode from io.nayuki.qrcodegen.QrCode (CQR is made from 3 different QRCodes)
     */
    public QrCode getQrCodes (int i) {
        assert i < 3;
        return qrCodes[i];
    }

    private void drawFinderPattern(int x, int y) {
        for (int dy = -4; dy <= 4; dy++) {
            for (int dx = -4; dx <= 4; dx++) {
                int dist = Math.max(Math.abs(dx), Math.abs(dy));  // Chebyshev/infinity norm
                int xx = x + dx, yy = y + dy;
                if (0 <= xx && xx < size && 0 <= yy && yy < size)
                    setFunctionModule(xx, yy, dist != 2 && dist != 4);
            }
        }
    }
    private void setFunctionModule(int x, int y, boolean isBlack) {
        finalMask[x][y] = isBlack ? 0 : 7;
        //isFunction[y][x] = true;
    }

    private void mergeMasks() {
        try{
            for(int i = 0; i < size; i ++) {
                for(int j = 0; j < size; j++) {
                    if(!redMask[i][j]) finalMask[i][j] += 1;
                    if(!greenMask[i][j]) finalMask[i][j] += 2;
                    if(!blueMask[i][j]) finalMask[i][j] += 4;
                }
            }
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public Planar<GrayU8> toImage(int scale, int border) {
        if (scale <= 0 || border < 0)
            throw new IllegalArgumentException("Value out of range");
        if (border > Integer.MAX_VALUE / 2 || size + border * 2L > Integer.MAX_VALUE / scale)
            throw new IllegalArgumentException("Scale or border too large");

        //BufferedImage result = new BufferedImage((size + border * 2) * scale, (size + border * 2) * scale,
                //BufferedImage.TYPE_INT_RGB);

//        boolean color = getModule(x / scale - border, y / scale - border);
//        result.setRGB(x, y, color ? 0x000000 : 0xFFFFFF);

        GrayU8 grayU8 = new GrayU8((size + border * 2)*scale,(size + border * 2)*scale);
        Planar<GrayU8> planar = new Planar<GrayU8>(GrayU8.class, grayU8.width,grayU8.height,3);
        for (int y = 0; y < grayU8.getHeight(); y++) {
            for (int x = 0; x < grayU8.getWidth(); x++) {
                if(!(getModule(x/scale - border,y / scale - border) > 7) &&  !(getModule(x / scale - border,
                        y / scale - border) < 0)) {
                    setColor(planar,x, y , getModule(x/scale - border, y / scale - border));
                }
            }
        }
        return planar;
    }

    public int getModule(int y, int x) {
//        return finalMask[x][y];
        return 0 <= x && x < size && 0 <= y && y < size ? finalMask[x][y] : 7;
    }
    public void setColor(Planar<GrayU8> planar, int x, int y, int pixValue){
        planar.getBand(0).set(x, y , 255 * ((pixValue & 1) != 0 ? 1 : 0));
        planar.getBand(1).set(x , y , 255 * ((pixValue & 2) != 0 ? 1 : 0));
        planar.getBand(2).set(x, y , 255 * ((pixValue & 4) != 0 ? 1 : 0));
    }



}

