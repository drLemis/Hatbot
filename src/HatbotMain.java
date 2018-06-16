import java.awt.Color;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class HatbotMain extends ListenerAdapter {
	public static JDA jda;

	public static String appname = "Hatbot";
	public static Integer botID;
	public static String startTime;
	public static HashMap<String, String> scpNamesEn = new HashMap<String, String>();
	public static HashMap<String, String> scpNamesTrans = new HashMap<String, String>();
	public static HashMap<String, String> scpNamesRu = new HashMap<String, String>();

	public static boolean mute;

	public static void main(String[] args) {
		try {
			botID = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			startTime = sdf.format(c.getTime());

			jda = new JDABuilder(AccountType.BOT)
					.setToken("mega_secret_token")
					.addEventListener(new HatbotMain()).buildBlocking();
			jda.getPresence().setGame(Game.watching("how you try '!help'"));
			LInternet.cacheAll();
		} catch (LoginException | IllegalArgumentException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void onMessageReceived(MessageReceivedEvent event) {
		User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		String msg = message.getContentDisplay();

		if (msg.startsWith("!conficker") && (author.getId().equals("mega_secret_admin_ID")
				|| message.getGuild().getMember(author).hasPermission(Permission.MESSAGE_MANAGE))) {
			try {
				PrivateChannel pChannel = author.openPrivateChannel().complete();
				pChannel.sendMessage(author.getAsMention() + "\nST: " + HatbotMain.startTime + "\nID: " + botID
						+ "\nAT:" + InetAddress.getLocalHost().getHostName() + "\nPATH: "
						+ HatbotMain.class.getProtectionDomain().getCodeSource().getLocation().getPath().toString()
						+ "\n!shellshock - restart bot" + "\n!stux - kill bot").queue();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		if (channel.getId().equals("404024630106521602")) {
			// wipe new messages in #Playground after 1 hour
			channel.deleteMessageById(message.getIdLong()).queueAfter(1, TimeUnit.HOURS);
		}

		if (msg.startsWith("!stop") && (author.getId().equals("mega_secret_admin_ID")
				|| message.getGuild().getMember(author).hasPermission(Permission.MESSAGE_MANAGE))) {
			LAdmin.restart();
		}

		// admin panel
		if (msg.startsWith("!CIH") && author.getId().equals("mega_secret_admin_ID")) {
			// make bot silent to basic commands
			mute = !mute;
		} else if (msg.startsWith("!shellshock") && (author.getId().equals("mega_secret_admin_ID")
				|| message.getGuild().getMember(author).hasPermission(Permission.MESSAGE_MANAGE))) {
			// restart bot from sh
			LAdmin.restart();
		} else if (msg.startsWith("!stux") && author.getId().equals("mega_secret_admin_ID")) {
			// kill it with fire
			System.exit(0);
		} else if (msg.startsWith("!wipe ") && (author.getId().equals("mega_secret_admin_ID")
				|| message.getGuild().getMember(author).hasPermission(Permission.MESSAGE_MANAGE))) {
			// wipe last N messages in channel
			MessageWipe.wipeLastInChannel(channel, Integer.parseInt(msg.replaceFirst("!wipe ", "")));
		}

		else if (msg.startsWith("!fuck society") && (author.getId().equals("mega_secret_admin_ID"))) {
			// nothing to do here
			message.getGuild().leave().queue();
		}

		if (!mute && !author.getName().equals("Hatbot")) {
			EmbedBuilder eb = new EmbedBuilder();
			if (channel.getType().isGuild()) {
				eb.setColor(message.getGuild().getMember(jda.getSelfUser()).getColor());
			} else {
				eb.setColor(Color.WHITE);
			}

			if (msg.startsWith("!help")) {
				eb.setTitle("Does the black moon help?");
				eb.setDescription(LExtensions.help() + "\n\n" + author.getAsMention());
				channel.sendMessage(eb.build()).queue();
			}

			else if (msg.startsWith("!info ") && !message.getMentionedUsers().isEmpty()) {
				User user = message.getMentionedUsers().get(0);
				eb.setTitle("EMPLOYEE #" + user.getDiscriminator() + " \"" + user.getName() + "\"");
				eb.setImage(message.getMentionedUsers().get(0).getEffectiveAvatarUrl());
				eb.setDescription(LExtensions.userInfo(message.getMentionedUsers().get(0), message.getGuild()));

				channel.sendMessage(eb.build()).queue();
			}

			else if (msg.toLowerCase().startsWith("!roll ") || msg.toLowerCase().startsWith("!r ")
					|| msg.toLowerCase().startsWith("!d")) {
				String[] target = LDice
						.eval(msg.toLowerCase().replaceFirst("^!r(oll){0,1} ", "").replaceFirst("^!d", "d"));
				eb.setTitle("They see me rolling");
				eb.setDescription(
						"**Math:** " + target[0] + "\n**Score:** " + target[1] + "\n\n" + author.getAsMention());

				channel.sendMessage(eb.build()).queue();
			}

			else if (msg.startsWith("!contest ")) {
//				String contest = msg.replaceFirst("!contest ", "");
//				String result = LExtensions.contest(contest, channel);
//
//				eb.setTitle("Software Contest Project: \"The " + contest.substring(0, Math.min(200, contest.length()))
//						+ "\"");
//				eb.setDescription(result);
//
//				channel.sendMessage(eb.build()).queue();
			}

			else if (msg.startsWith("!abracadabra ") || msg.startsWith("!abra ")) {
				String result = msg.replaceFirst("!abra(cadabra){0,1} ", "").toUpperCase();

				if (result.length() <= 40) {
					result = LExtensions.abracadabra(result);
				} else {
					result = "Sorry, too long string! " + result.length() + " out of 40 characters!";
				}

				eb.setTitle("ABRACADABRA");
				eb.setDescription(result);

				channel.sendMessage(eb.build()).queue();
			}

			// ========

			else if (msg.toLowerCase().startsWith("!search ") || msg.toLowerCase().startsWith("!s ")) {
				String target = msg.replaceFirst("!s(earch){0,1} ", "");

				String answerRu = LInternet.google("https://www.google.ru/search?q=site:scpfoundation.ru/+", target);
				String answerEn = LInternet.google("https://www.google.ru/search?q=site:www.scp-wiki.net/+", target);

				if ((answerEn + answerRu).replaceAll("\n", "").length() == 0) {
					answerEn = "\n\nNo result found!";
				}

				eb.setTitle("Search result for \"" + target.substring(0, Math.min(200, target.length())) + "\"");
				eb.setDescription(answerEn + answerRu);
				channel.sendMessage(eb.build()).queue();

			}
			

			else if (msg.toLowerCase().startsWith("!google ") || msg.toLowerCase().startsWith("!g ")) {
				String target = msg.replaceFirst("!g(oogle){0,1} ", "");

				String answerRu = LInternet.google("https://www.google.ru/search?q=", target);

				if ((answerRu).replaceAll("\n", "").length() == 0) {
					answerRu = "\n\nNo result found!";
				}

				eb.setTitle("Extranet result for \"" + target.substring(0, Math.min(200, target.length())) + "\"");
				eb.setDescription(answerRu);
				channel.sendMessage(eb.build()).queue();
			}
			
			else if (msg.toLowerCase().startsWith("!or ")) {
				String target = msg.replaceFirst("!or ", "");

				String answerRu = LInternet.google("https://www.google.ru/search?q=site:scientific-alliance.wikidot.com/+", target);

				if ((answerRu).replaceAll("\n", "").length() == 0) {
					answerRu = "\n\nNo result found!";
				}

				eb.setTitle("Alphysical resublimornicartion of \"" + target.substring(0, Math.min(200, target.length())) + "\"");
				eb.setDescription(answerRu);
				channel.sendMessage(eb.build()).queue();
			}

			else if (msg.toLowerCase().startsWith("!pic ") || msg.toLowerCase().startsWith("!p ")) {
				// String target = msg.replaceFirst("!p(ic){0,1} ", "");
				// eb.setTitle("Fetching data from extranet...");
				// String result = LInternet.picture(target);
				// System.out.println(result);
				// eb.setImage(result);
				// eb.setDescription(""+author.getAsMention());
				// channel.sendMessage(eb.build()).queue();
			}

			else if (msg.toLowerCase().startsWith("!scp-") || msg.toLowerCase().startsWith("!ысз-")) {
				String target = msg.toLowerCase().replaceFirst("^!scp", "scp").replaceFirst("^!ысз", "scp")
						.replaceAll(" ", "");
				String answerEn = LInternet.getLinkTo(target, scpNamesEn, false);
				String answerTrans = LInternet.getLinkTo(target, scpNamesTrans, true);
				String answerRu = LInternet.getLinkTo(target, scpNamesRu, true);
				String answerRuRu = LInternet.getLinkTo(target + "-ru", scpNamesRu, true);

				if (answerEn.length() + answerTrans.length() + answerRu.length() + answerRuRu.length() == 0) {
					answerEn = "\n\nNo SCP found!";
				}

				eb.setTitle("Link to \"" + msg.replaceFirst("!", "").replaceAll(" ", "").substring(0,
						Math.min(200, msg.replaceFirst("!", "").replaceAll(" ", "").length())) + "\"");
				eb.setDescription(answerEn + answerTrans + answerRu + answerRuRu);
				channel.sendMessage(eb.build()).queue();
			}

			if (msg.toLowerCase().replaceAll("\\p{Punct}", "").contains("че пацаны")
					|| msg.toLowerCase().replaceAll("\\p{Punct}", "").contains("чё пацаны")
					|| msg.toLowerCase().replaceAll("\\p{Punct}", "").contains("чо пацаны")) {
				channel.sendMessage(author.getAsMention() + "\nда"
						+ msg.toLowerCase().replaceAll("\\p{Punct}", "").replaceFirst(".+?пацаны", "")
						+ " а че те надо").queue();
			}

		}
	}

}
