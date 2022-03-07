
//package blur;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;

class Image_SRZ implements Serializable
{
    private BufferedImage image;

    public BufferedImage getImg()
    {
        return image;
    }
    public void setImg(BufferedImage bufferedImage)
    {
        image = bufferedImage;
    }
}