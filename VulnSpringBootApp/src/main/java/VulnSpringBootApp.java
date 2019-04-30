import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@RestController
@EnableAutoConfiguration
public class VulnSpringBootApp {

	@RequestMapping("/")
	String index() {
		User user = new User();
		user.name = "Ivan";
		String serizalizedUser = serializeToBase64String(user);

		String curlDeserializeUser = "curl -vv -d '" + 
																 "serializedUser=" + URLEncoder.encode(serizalizedUser) + "' http://localhost:8080/deserialize/user";

		return "curl requests: <br><br>" + 
					curlDeserializeUser + "<br><br>";
	}

	@RequestMapping(value = "/deserialize/user",
									method = RequestMethod.POST)
	String deserializeUser(@RequestParam String serializedUser) {
		User user = (User) deserializeFromBase64String(URLDecoder.decode(serializedUser));
		return "user.name: " + user.name + "\n";
	}

	public static void main(String[] args) {
		SpringApplication.run(VulnSpringBootApp.class, args);
	}

  private static String serializeToBase64String(Serializable obj) {
  	String result = "";
  	try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(obj);
	    oos.close();
	    result = Base64.getEncoder().encodeToString(baos.toByteArray());
  	} catch(IOException ex) {
  		ex.printStackTrace();
  	}
  	return result;
	}

	public static Object deserializeFromBase64String(String inputStr) {
		byte[] bytes = Base64.getDecoder().decode(inputStr);
		if (bytes == null) {
			return null;
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
			return ois.readObject();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}

