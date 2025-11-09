import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class AppTest {

    @Test
    public void testGreet() {
        String expected = "Hello, CI/CD with Jenkins, SonarQube, and Docker!";
        assertEquals(expected, App.greet());
    }
}
