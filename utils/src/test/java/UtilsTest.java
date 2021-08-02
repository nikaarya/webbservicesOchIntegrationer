import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {
    @Test
    public void simpleGETReturnsUrl(){
        String url = Utils.parseUrl("""
            GET / HTTP/1.1\r\n \
            Host: www.example.com\r\n \
            \r\n \
            """);
        assertThat(url).isEqualTo("/");
    }

    @Test
    public void filePathGETReturnsUrl() {
        String url = Utils.parseUrl("""
                GET /index.html HTTP/1.1\r\n \
                Host: www.example.com\r\n \
                \r\n \
                """);
        assertThat(url).isEqualTo("/index.html");
    }

    @Test
    public void filePathHeadReturnsHEADAndUrl() {
        HTTPType url = Utils.parseHttpRequestType("""
                HEAD /index.html HTTP/1.1\r\n \
                Host: www.example.com\r\n \
                \r\n \
                """);
        assertThat(url).isEqualTo(HTTPType.HEAD);
    }
}
