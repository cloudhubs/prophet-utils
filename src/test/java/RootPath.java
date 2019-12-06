import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RootPath {

    @Value("${user.rootPath}")
    private String rootPath;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
