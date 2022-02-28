
//package blur;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import parcs.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.stream.Collectors;

public class Main implements AM {
    
    public int threads = 4;
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
        System.out.println("Read file: successful.");
        long tStart = System.nanoTime();
        BufferedImage res = solve(info, link, radius,threads);
        long tEnd = System.nanoTime();
        System.out.println("time = " + ((tEnd - tStart) / 1000000) + "ms");
        try{
        ImageIO.write(res, "PNG", new File("combined.png"));
        }catch (Exception e)
        {
            System.out.println("Error saving combined image.");
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
            System.out.println("Input image read successfully.");
            
            int w = input.getWidth()/threads;
            int h = input.getHeight()/threads;
            w*=threads;
            h*=threads;
            BufferedImage outputImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            outputImage.getGraphics().drawImage(input.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null);
            input=outputImage;
            
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
        System.out.println("Image cutted: success.");
        //parallel blur
        for (int i = 0; i < threads; i++){
            points.add(info.createPoint());
            channels.add(points.get(i).createChannel());
            points.get(i).execute("Algorithm");
            int[] data = ((DataBufferInt) imgs[i].getRaster().getDataBuffer()).getData();
            channels.get(i).write(imgs[i].getWidth());
            channels.get(i).write(imgs[i].getHeight());
            channels.get(i).write(data.length);
            for(int j=0;j<data.length;j++)
                channels.get(i).write(data[j]);
            channels.get(i).write(radius);
            
            System.out.println(i+" channel sent: "+Arrays.stream(data)
        .mapToObj(String::valueOf)
        .collect(Collectors.joining(" - ")));
        }
        
            System.out.println("Images blurred.");
        BufferedImage res;
        for(int i = 0; i < threads; i++){
            System.out.println(i+" point get image progress");
            int l = channels.get(i).readInt();
            int[] arr = new int[l];
            for (int j=0;j<l;j++)
                arr[j]=channels.get(i).readInt();
            BufferedImage image = new BufferedImage(imgs[i].getWidth(), imgs[i].getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            var r =image.getRaster();
            r.setPixels(0, 0,imgs[i].getWidth(), imgs[i].getHeight(),arr);
            image.setData(r);
            System.out.println(i+" point get image success");
            reses.add(image);
        }
        
            System.out.println("Blurred images recieved success.");
            	//unite img
        w = input.getWidth();
        h = input.getHeight();
        res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D  g = (Graphics2D)res.getGraphics();
        g.setComposite(AlphaComposite.Src);
        w /=reses.size();
        for(int i = 0; i < reses.size(); i++)
        {
            BufferedImage bi = (BufferedImage)reses.get(i);
            g.drawImage(bi, w*i, 0, null);
        }
        
            System.out.println("Images glued success.");
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
