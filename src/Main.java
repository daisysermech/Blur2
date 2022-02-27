public class Main {
    public static void main(String[] args) {
        String s = "ref";
        System.out.println(decode(s));
    }

    private static String decode(String node) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < node.length(); i++) {
            char c = node.charAt(i);
            char ans = (char) (((c - 'a') * 9 + 7) % 26 + 'a');
            sb.append(ans);
        }
        return sb.toString();
    }
}
