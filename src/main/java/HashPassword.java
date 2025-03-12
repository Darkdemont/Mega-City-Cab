import org.mindrot.jbcrypt.BCrypt;

public class HashPassword {
    public static void main(String[] args) {
        String password = "admin1234"; // Change this to your desired password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12)); // Hash password
        System.out.println("Hashed Password: " + hashedPassword);
    }
}
