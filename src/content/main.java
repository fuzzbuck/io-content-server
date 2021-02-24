package content;

import arc.util.Log;

public class main {
    public static ContentHandler handler;
    public static void main(String[] args) {
        Log.info("loading content");
        handler = new ContentHandler();
        Log.info("content loaded\nhosting server on port 6969");
        Server.init(6969);
    }

    private static String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "0123456789"
            + "abcdefghijklmnopqrstuvxyz";

    public static String randomString(int n){
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
