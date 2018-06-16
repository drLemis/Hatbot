import java.util.List;
import java.util.stream.Collectors;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class MessageWipe {

	public static void wipeAllInChannel(MessageChannel channel) {
		List<Message> messages = channel.getIterableHistory().cache(false).stream().limit(10000)
				.collect(Collectors.toList());

		for (int i = 0; i < messages.size(); i++) {
			Message message = messages.get(i);
			channel.deleteMessageById(message.getIdLong()).queue();
		}

	}

	public static void wipeLastInChannel(MessageChannel channel, int amount) {
		List<Message> messages = channel.getIterableHistory().cache(false).stream().limit(amount)
				.collect(Collectors.toList());

		for (int i = 0; i < messages.size(); i++) {
			Message message = messages.get(i);
			channel.deleteMessageById(message.getIdLong()).queue();
		}
	}
}
