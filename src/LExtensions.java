import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class LExtensions {
	public static String help() {
		return "\n**===MAIN===**\n" + "**!scp-<number>** - get link to SCP.\nExample: `!scp-1184-ru`\n\n"
				+ "**!search <text>** - search for text on SCP-EN and SCP-RU.\nExample: `!search Курительная трубка`\nAlso: `!s Курительная трубка`\n\n"
				+ "**!or <text>** - search for text on ASS\nExample: `!or Алфизика`\n\n"
				+ "**!google <text>** - search for text on Google.\nExample: `!google Здоровенные аниме-титьки`\nAlso: `!g Порно с карликами`\n\n"
				+ "**===MISC===**\n"
				+ "**!roll <notation>** - dice roll.\nExample: `!roll d20+2d4-2`\nAlso: `!d20+2d4-2`, `!r d20+2d4-2`, `!roll d20+2d4-2`\n\n"
				+ "**!info <mention>** - info about user.\nExample: `!info @Username#1234`\n\n"
				+ "**!abracadabra <text>** - magic square.\nExample: `!abracadabra Кодзима - гений`\nAlso: `!abra многоходовочка`\n\n"
				+ "**!help** - this text";
	}
	
	public static String abracadabra(String contest) {
		StringBuffer output = new StringBuffer();
		
		for (int i = 0; i < contest.length(); i++) {
			output.append("\n`"+contest.substring(i, contest.length())+ " "+contest.substring(0, i)+"`");
		}

		return output.toString();
	}

	public static String contest(String contest, MessageChannel channel) {
		List<Message> messages = channel.getIterableHistory().cache(false).stream().limit(10000)
				.collect(Collectors.toList());
		Map<String, Integer> result = new HashMap<String, Integer>();
		

		for (int i = 0; i < messages.size(); i++) {
			Message message = messages.get(i);
			if (message.getContentDisplay().toLowerCase().contains(contest.toLowerCase())) {
				if (!result.containsKey(message.getAuthor().getAsMention())) {
					result.put(message.getAuthor().getAsMention(), 1);
				} else {
					result.put(message.getAuthor().getAsMention(), result.get(message.getAuthor().getAsMention()) + 1);
				}
			}
		}

		Map<String, Integer> sortedNewMap = result.entrySet().stream()
				.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		StringBuffer output = new StringBuffer();
		sortedNewMap.forEach((key, val) -> {
			output.append("\n" + val.toString() + " by " + key);
		});
		

		return output.toString();
	}

	public static String userInfo(User user, Guild guild) {
		StringBuffer result = new StringBuffer("NETWORK **"
				+ ChronoUnit.DAYS.between(user.getCreationTime().toLocalDate(), LocalDate.now()) + "** DAYS");

		result.append("\nEMPLOYEE **"
				+ ChronoUnit.DAYS.between(guild.getMember(user).getJoinDate().toLocalDate(), LocalDate.now())
				+ "** DAYS");

		result.append("\nSTATUS");
		
		if (guild.getMember(user).getRoles().isEmpty()) {
			result.append(" **N/A**");
		} else {
			for (Role role : guild.getMember(user).getRoles()) {
				result.append(" **" + role.getName() + "**,");
			}
			result.deleteCharAt(result.length() - 1);
		}

		return result.toString();
	}

}
