package IBRest;

import IBRest.Constants.IBRestURIConstants;
import IBRest.Model.TWS.EWrapperImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application {

    public static EWrapperImpl IBCon;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        setArgs(Arrays.asList(args));
        IBCon = new EWrapperImpl();
    }

    /**
     * set arguments
     * @param args
     */
    public static void setArgs(List<String> args) {
        try {
            for (String s: args) {
                System.out.println("arg = " + s);
                if (s.startsWith("ip:")) {
                    IBRestURIConstants.IB_GW_IP = s.substring("ip:".length());
                    System.out.println("IB GW IP = " + IBRestURIConstants.IB_GW_IP);
                }
                if (s.startsWith("port:")) {
                    IBRestURIConstants.IB_GW_PORT = Integer.parseInt(s.substring("port:".length()));
                    System.out.println("IB GW PORT = " + IBRestURIConstants.IB_GW_PORT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
