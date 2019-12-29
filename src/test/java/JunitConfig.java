import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JunitConfig {

    @Value("${user.rootPath}")
    public String rootPath;

}
