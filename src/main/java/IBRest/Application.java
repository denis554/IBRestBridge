package IBRest;

import IBRest.Model.TWS.EWrapperImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static final EWrapperImpl IBCon = new EWrapperImpl();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
