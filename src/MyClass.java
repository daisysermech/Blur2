import parcs.*;

import java.util.Arrays;

public class MyClass implements AM {
    public void run(AMInfo info) {
        String[] node = (String[])info.parent.readObject();
        System.out.println("data is " + Arrays.toString(node));
        System.out.println("Build started.");
        long s = System.nanoTime();
        String[] a = decode(node);
        System.out.println("ans is " + Arrays.toString(a));
        System.out.println("Build finished in " + (System.nanoTime() - s) / 1000000 + " ms");
        info.parent.write(a);
    }

    private String[] decode(String[] node) {
        String[] ans = new String[node.length];
        for (int i = 0; i < node.length; i++) {
            ans[i] = decode(node[i]);
        }
        return ans;
    }

    private String decode(String node) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < node.length(); i++) {
            char c = node.charAt(i);
            char ans = (char) (((c - 'a') * 9 + 7) % 26 + 'a');
            sb.append(ans);
        }
        return sb.toString();
    }

}
