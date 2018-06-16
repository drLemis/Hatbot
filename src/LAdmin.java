import java.io.IOException;

public class LAdmin {
	public static void restart() {
		try {
			Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "/home/ubuntu/Hatbot/Hatbot.sh" });

			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
