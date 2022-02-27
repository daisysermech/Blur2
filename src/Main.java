
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import parcs.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

public class Main implements AM {
    public static void main(String[] args){	
        task mainTask = new task();
        mainTask.addJarFile("Algorithm.jar");
        mainTask.addJarFile("Main.jar");
        (new Main()).run(new AMInfo(mainTask, (channel)null));
        mainTask.end();
    }
    
    @Override
    public void run(AMInfo info){
        String link;
        int radius;
        int threads = 4;
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(info.curtask.findFile("input.txt")));
            link = in.readLine();
            radius = Integer.parseInt(in.readLine());
        }
        catch (IOException e)
        {
            System.out.print("Error while reading input\n");
            return;
        }
        System.out.print("Read successful.");
        long tStart = System.nanoTime();
        BufferedImage res = solve(info, link, radius,threads);
        long tEnd = System.nanoTime();
        System.out.println("time = " + ((tEnd - tStart) / 1000000) + "ms");
        try{
        ImageIO.write(res, "PNG", new File("combined.png"));
        }catch (Exception e)
        {
            System.out.println("Error saving.");
        }
    }
    
    public static BufferedImage solve(AMInfo info, String imageUrl, int radius, int threads)
    {
        List<BufferedImage> reses = new ArrayList<>();
        List<point> points = new ArrayList<>();
        List<channel> channels = new ArrayList<>();
        
        //read img and split
        BufferedImage input = null;
        try
        {
            System.setProperty("http.agent", "Chrome");
            URL url = new URL(imageUrl);
            URLConnection conn = url.openConnection();
            InputStream in = conn.getInputStream();
            input = ImageIO.read(in);
            input.coerceData(true);

        BufferedImage imgs[] = new BufferedImage[threads];

        int subimage_Width = input.getWidth() / threads;
        int subimage_Height = input.getHeight();

        int offset=(int) (subimage_Width*0.05);
        int current_img = 0;

        for (int j = 0; j < threads; j++)
        {
        imgs[current_img] = new BufferedImage(subimage_Width+offset, subimage_Height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D img_creator = imgs[current_img].createGraphics();
        int src_first_x = subimage_Width * j;
        int src_first_y = subimage_Height;

        int dst_corner_x = subimage_Width * j + subimage_Width;
        int dst_corner_y = subimage_Height + subimage_Height;

        img_creator.drawImage(input, 0, 0, subimage_Width+offset, subimage_Height,
                src_first_x, src_first_y, dst_corner_x+offset, dst_corner_y, null);
        current_img++;
        }
        //paralle blur
        for (int i = 0; i < threads; i++){
            points.add(info.createPoint());
            channels.add(points.get(i).createChannel());
            points.get(i).execute("Algorithm");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imgs[i], "png", baos);
            byte[] bytes = baos.toByteArray();
            channels.get(i).write(bytes);
            channels.get(i).write(radius);
        }
        
        BufferedImage res;
        for(int i = 0; i < threads; i++){
            InputStream is = new ByteArrayInputStream((byte[])channels.get(i).readObject());
            BufferedImage img = ImageIO.read(is);
            reses.add(img);
        }
        
            	//unite img
        int w = input.getWidth();
        int h = input.getHeight();
        res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D  g = (Graphics2D)res.getGraphics();
        g.setComposite(AlphaComposite.Src);
        w /=reses.size();
        for(int i = 0; i < reses.size(); i++)
        {
            BufferedImage bi = (BufferedImage)reses.get(i);
            g.drawImage(bi, w*i, 0, null);
        }
        
        g.dispose();
        return res;
        
        }
        catch(Exception e)
        {
            System.out.println("Cannot read image.");
            return null;
        }
        
    }
}
