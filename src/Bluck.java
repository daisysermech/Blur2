import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.File;

import parcs.AMInfo;
import parcs.channel;
import parcs.point;
import parcs.task;

public class Bluck {


    public static void main(String[] args) throws Exception {
        task curtask = new task();
        curtask.addJarFile("MyClass.jar");
        List<String> data = readData(curtask.findFile("input"));
        int n = Integer.parseInt(data.get(0));

        AMInfo info = new AMInfo(curtask, null);
        List<channel> channels = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            point p = info.createPoint();
            channel c = p.createChannel();
            channels.add(c);
            p.execute("MyClass");
        }
        long startTime = System.nanoTime();

        for (int i = 0; i < n; i++) {
            int count = data.size() / n; // 4
            int first = count * i + 1; // 5
            int last = i == n - 1 ? data.size() : count * (i + 1) + 1; // 9
            String[] d = new String[last - first];
            for (int j = first; j < last; j++) {
                d[j - first] = data.get(j);
            }
            //  System.out.println("data sent is " + Arrays.toString(d));
            channels.get(i).write(d);
        }

        System.out.println(((System.nanoTime() - startTime) / 1000000) + " ms took");

        List<String> answer = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String[] ans = (String[]) channels.get(i).readObject();
          //  System.out.println("Ans from " + i + " channel is " + Arrays.toString(ans));
            answer.addAll(Arrays.asList(ans));
        }
      //  answer.forEach(System.out::println);
        System.out.println("End of task");
        System.out.println("Calculating duration: " + ((System.nanoTime() - startTime) / 1000000) + "ms");
        curtask.end();
    }

    public static List<String> readData(String filename) throws Exception {
        Scanner sc = new Scanner(new File(filename));
        List<String> data = new ArrayList<>();
        while (sc.hasNext()) {
            data.add(sc.next());
        }
        return data;
    }
}
