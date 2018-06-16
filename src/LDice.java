import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class LDice {
	public static String resultstring;

	public synchronized static String[] eval(String text) {
		String result[] = new String[2];

		text = text.replaceAll("(<.*>)", "");
		text = text.replaceAll("-\\+", "-");
		text = text.replaceAll("\\+-", "-");
		text = text.replaceAll("\\/\\+", "/");
		text = text.replaceAll("\\*\\+", "*");
		text = text.replaceAll("^\\+", "");
		text = text.replaceAll("\\+\\+", "+");
		text = text.replaceAll("--", "+");
		text = text.replaceAll("\\*\\*", "*");
		text = text.replaceAll("//", "/");

		resultstring = text.replaceAll("\\*(?!\\*)", "x");

		text = text.replaceAll("(\\d+)\\^(\\d+)", "Math.pow($1, $2)");

		Pattern p = Pattern.compile("[a-zA-Z_d]");
		int i = 0;
		while (p.matcher(text).find() && i < 5) {
			text = parseDice(text);
			i++;
		}

		text = math(text);

		text = text.replaceAll("(\\.0)$", "");
		text = text.replaceAll("(\\.\\d{2})(\\d+)", "$1");
		text = text.replaceAll("(<.*>)", "");

		result[0] = resultstring;
		result[1] = text;
		return result;
	}

	public static String parseDice(String text) {

		Map<String, UnaryOperator<String>> bbMap = new HashMap<>();

		bbMap.put("(?i)[-]([0-9]*d[0-9]+)", s -> "-" + rollA(s));
		bbMap.put("(?i)[+]([0-9]*d[0-9]+)", s -> "+" + rollA(s));
		bbMap.put("(?i)[*]([0-9]*d[0-9]+)", s -> "*" + rollA(s));
		bbMap.put("(?i)[/]([0-9]*d[0-9]+)", s -> "/" + rollA(s));
		bbMap.put("(?i)[(]([0-9]*d[0-9]+)", s -> "(" + rollA(s));
		bbMap.put("(?i)^([0-9]*d[0-9]+)", s -> rollA(s));
		bbMap.put("(?i)(\\(.+\\)d[0-9]+)", s -> rollB(s));

		for (@SuppressWarnings("rawtypes")
		Map.Entry entry : bbMap.entrySet()) {
			StringBuffer buffer = new StringBuffer(text.length());

			Matcher matcher = Pattern.compile(entry.getKey().toString()).matcher(text);
			while (matcher.find()) {
				String match = (matcher.groupCount() > 0 ? matcher.group(1) : null);
				@SuppressWarnings("unchecked")
				String replacement = ((Function<String, String>) entry.getValue()).apply(match);
				matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
			}
			matcher.appendTail(buffer);

			text = buffer.toString();
		}
		return text;
	}

	public static String rollA(String str) {
		String substring = "**" + str + "** ";
		Integer result;

		String input = str;
		if (input.startsWith("d")) {
			input = 1 + input;
		}
		Matcher matcher = Pattern.compile("(\\d*)d").matcher(input);
		matcher.find();
		int h = Integer.valueOf(matcher.group(1));
		matcher = Pattern.compile("d(\\d+?)$").matcher(input);
		matcher.find();
		int x = 0;
		for (int i = 1; i < h + 1; i++) {
			result = (int) (Integer.parseInt(matcher.group(1)) * Math.random()) + 1;

			if (result == Integer.parseInt(matcher.group(1))) {
				substring += "[__**" + result + "**__]";
			} else if (result == 1) {
				substring += "[__**" + result + "**__]";
			} else {
				substring += "[" + result + "]";
			}
			x += result;
		}

		if (h == 1) {
			resultstring = resultstring.replaceFirst("(?<!\\*\\*)" + str, "(" + substring + ")");
		} else {
			resultstring = resultstring.replaceFirst("(?<!\\*\\*)" + str, "(" + substring + " **<" + x + ">**)");
		}

		return String.valueOf(x);
	}

	public static String rollB(String str) {

		String substring = "**" + str + "** ";
		Integer result = null;

		String strP = "1";

		Matcher m = Pattern.compile("\\((.+?)\\)d").matcher(str);
		while (m.find()) {
			strP = m.group(1);
		}

		strP = LDice.parseDice(strP);

		if (!strP.equals("")) {
			strP = math(strP);
		} else {
			strP = "1";
		}

		String input = "";
		m = Pattern.compile("(d.+)").matcher(str);
		while (m.find()) {
			input = m.group(1);
		}

		input = (int) (Math.floor(Double.parseDouble(strP))) + input;

		Matcher matcher = Pattern.compile("(\\d+)d").matcher(input);
		matcher.find();
		int h = Integer.valueOf(matcher.group(1));
		matcher = Pattern.compile("d(\\d+?)$").matcher(str);
		matcher.find();
		int x = 0;
		for (int i = 1; i < h + 1; i++) {
			result = (int) (Integer.parseInt(matcher.group(1)) * Math.random()) + 1;

			if (result == Integer.parseInt(matcher.group(1))) {
				substring += "[__**" + result + "**__]";
			} else if (result == 1) {
				substring += "[__**" + result + "**__]";
			} else {
				substring += "[" + result + "]";
			}
			x += result;
		}

		if (h == 1) {
			resultstring = resultstring.replaceFirst("(?<!\\*\\*)" + str, "(" + substring + ")");
		} else {
			resultstring = resultstring.replaceFirst("(?<!\\*\\*)" + str, "(" + substring + " **<" + x + ">**)");
		}

		return String.valueOf(x);
	}

	public static String math(String input) {
		if (input.length() == 0) {
			return input;
		}
		String result = input;
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("js");
			result = engine.eval(input).toString();
		} catch (ScriptException e) {

		}
		return result;
	}
}
