package IBRest.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpFromUrl {
    // Returns the IP address of an URL
    // i.e. http://www.facebook.com -> 123.456.789.10
    public static String getIp( String hostname ) throws IOException {
        try {
            InetAddress ipaddress = InetAddress.getByName(hostname);
            System.out.println("IP address: " + ipaddress.getHostAddress());
            return ipaddress.getHostAddress();
        }
        catch ( UnknownHostException e ){
            System.out.println("Could not find IP address for: " + hostname);
            throw new IOException("Could not find IP address for: " + hostname);
        }
    }
}
