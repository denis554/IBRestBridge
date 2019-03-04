package IBRest;

import IBRest.Constants.IBRestURIConstants;
import IBRest.Model.TWS.EWrapperImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static IBRest.Constants.IBRestURIConstants.*;

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
            setConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set the configuration based on the text file
     */
    public static void setConfig() {
        WHITE_HOST_LIST.clear();
        File f = new File(IBRestURIConstants.CONFIG_FILE);
        if (f.isFile() && f.exists()) {
            JSONParser parser = new JSONParser();
            try {
                String text = new String(Files.readAllBytes(Paths.get(IBRestURIConstants.CONFIG_FILE)), StandardCharsets.UTF_8);
                JSONObject jObj = (JSONObject) parser.parse(text);
                if (jObj.containsKey(KEY_API_KEY)) {
                    API_KEY = (String) jObj.get(KEY_API_KEY);
                }
                if (jObj.containsKey(KEY_API_SECRET)) {
                    API_SECRET = (String) jObj.get(KEY_API_SECRET);
                }
                if (jObj.containsKey(KEY_IB_GW_IP)) {
                    String ip = (String) jObj.get(KEY_IB_GW_IP);
                    if (isValidIP(ip)) {
                        IB_GW_IP = ip;
                    } else {
                        System.out.println("invalidate IB GW IP : " + ip);
                    }
                }
                if (jObj.containsKey(KEY_IB_GW_PORT)) {
                    try {
                        int port = Integer.parseInt(jObj.get(KEY_IB_GW_PORT).toString());
                        if (port > 0) {
                            IB_GW_PORT = port;
                        } else {
                            System.out.println("invalidate Port Number : " + port);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (jObj.containsKey(KEY_WHITE_LIST)) {
                    JSONArray jAry = (JSONArray) jObj.get(KEY_WHITE_LIST);
                    int len = jAry.size();
                    if (len > 0) {
                        for (int i = 0; i < len; i++) {
                            String ip = (String) jAry.get(i);
                            if (isValidIP(ip)) {
                                WHITE_HOST_LIST.add(ip);
                            } else {
                                System.out.println("invalidate White IP : " + ip);
                            }
                        }
                    }
                }
                System.out.println("config file json = " + jObj.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("*********set system default config*********");
            for (String each: WHITE_DEFAULT_HOST_LIST) {
                WHITE_HOST_LIST.add(each);
            }
        }
        System.out.println("-----------system config start-----------");
        System.out.println("API-KEY : " + API_KEY);
        System.out.println("API_SECRET : " + API_SECRET);
        System.out.println("IB_GW_IP : " + IB_GW_IP);
        System.out.println("IB_GW_PORT : " + IB_GW_PORT);
        for (String each: WHITE_HOST_LIST) {
            System.out.println("White-Host : " + each);
        }
        System.out.println("-----------system config end-----------");
    }

    /**
     * check the validate IP
     * @param ipString
     * @return
     */
    public static boolean isValidIP(String ipString) {
        String IPADDRESS_PATTERN =
                "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ipString);
        if (matcher.matches()) {
            return true;
        } else{
            return false;
        }
    }
}
