public class App {
    public static String getGreeting() {
        return "Hello from CI/CD Pipeline with Jenkins + SonarQube + Docker!";
    }

    public static void main(String[] args) {
        System.out.println(App.getGreeting());
    }
}
