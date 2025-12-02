import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AppTest {

    @Test
    public void testGreeting() {
        String expected = "Hello from CI/CD Pipeline with Jenkins + SonarQube + Docker!";
        assertEquals(expected, App.getGreeting());
    }
}
