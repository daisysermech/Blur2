
//package blur;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import javax.imageio.ImageIO;
import parcs.*;

public class Algorithm implements AM 
{
    @Override
    public void run(AMInfo info)
    {
        try{
            
            System.out.println("Started daemon proccess.");
            Object o = info.parent.readObject();
            System.out.println(o);
            Image_SRZ img = (Image_SRZ)o;
            ImageIO.write(img.getImage(), "png", new File("proc.png"));
            int rad = info.parent.readInt();
            System.out.println("Readed radius - "+rad);
            System.out.println("image retrieved");
            var blurred = blurredImage(img.getImage(),rad);
            img = new Image_SRZ(blurred);
            info.parent.write(img);
            System.out.println("saved and thats all.");
        }catch(Exception e)
        {
            System.out.println("ERROR");
            e.printStackTrace();
        }
    }
    
    public static BufferedImage blurredImage(BufferedImage source, double radius)
    {
        if (radius == 0) {
        return source;
        }

        final int r = (int) Math.ceil(radius);
        final int rows = r * 2 + 1;
        final float[] kernelData = new float[rows * rows];

        final double sigma = radius / 3;
        final double sigma22 = 2 * sigma * sigma;
        final double sqrtPiSigma22 = Math.sqrt(Math.PI * sigma22);
        final double radius2 = radius * radius;

        double total = 0;
        int index = 0;
        double distance2;

        int x, y;
        for (y = -r; y <= r; y++) {
        for (x = -r; x <= r; x++) {
        distance2 = 1.0 * x * x + 1.0 * y * y;
        if (distance2 > radius2) {
        kernelData[index] = 0;
        } else {
        kernelData[index] = (float) (Math.exp(-distance2
        / sigma22) / sqrtPiSigma22);
        }
        total += kernelData[index];
        ++index;
        }
        }

        for (index = 0; index < kernelData.length; index++) {
        kernelData[index] /= total;
        }
        BufferedImage paddedSource = paddedImage(source, r);
        BufferedImage blurredPaddedImage = operatedImage(paddedSource,
        new ConvolveOp(new Kernel(rows, rows, kernelData),
        ConvolveOp.EDGE_ZERO_FILL, null));
        return blurredPaddedImage.getSubimage(r, r, source.getWidth(),
        source.getHeight());
    }

    public static BufferedImage paddedImage(BufferedImage source, int padding)
    {
        if (padding == 0) {
        return source;
        }

        BufferedImage newImage = newArgbBufferedImage(source.getWidth()
        + padding * 2, source.getHeight() + padding * 2);
        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, source.getWidth()
        + padding * 2, source.getHeight() + padding * 2);
        g.setComposite(AlphaComposite.Src);
        g.drawImage(source, padding, padding, null);
        return newImage;
        }

        public static BufferedImage operatedImage(BufferedImage source,
        BufferedImageOp op) {
        BufferedImage newImage = newArgbBufferedImage(source.getWidth(),
        source.getHeight());
        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(source, op, 0, 0);
        return newImage;
        }

        public static BufferedImage newArgbBufferedImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
        
    public static String imgToBase64String(BufferedImage img, String formatName)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try
        {
            ImageIO.write(img, formatName, os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        }
        catch (Exception e)
        {
            return null;
        }
    }
}