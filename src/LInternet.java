import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LInternet {

	public static String google(String site, String search) {
		String result = "\n";
		int i = 0;
		try {
			String charset = "UTF-8";
			String userAgent = "Hatbot (+http://scpfoundation.ru/)";

			Elements links = Jsoup.connect(site + URLEncoder.encode(search, charset)).userAgent(userAgent).get()
					.select(".g>.r>a");

			for (Element link : links) {
				if (i >= 10) {
					break;
				}
				// String title = link.text();
				String url = link.absUrl("href");
				url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

				if (!url.startsWith("http")) {
					continue; // Ads/news/etc.
				}

				if (!url.contains("/search:site/") && !url.contains("/system:") && !url.contains("/top-rated-pages/")) {
					i++;
					if (url.contains("/forum/")) {
						result = result + "\n[__[forum]__ " + clearTitle(getHeadTitle(url, site, link.text())) + "]("
								+ clearURL(url) + ")";
					} else if (url.contains("/draft:")) {
						result = result + "\n[__[draft]__ " + clearTitle(getHeadTitle(url, site, link.text())) + "]("
								+ clearURL(url) + ")";
					} else {
						result = result + "\n[" + clearTitle(getHeadTitle(url, site, link.text())) + "]("
								+ clearURL(url) + ")";
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static String getPage(String address) throws IOException, URISyntaxException {
		// Get URI and URL objects.
		URI uri = new URI(address);
		URL url = uri.toURL();

		// Get stream of the response.
		InputStream in = url.openStream();

		// Store results in StringBuilder.
		StringBuilder builder = new StringBuilder();
		byte[] data = new byte[256];

		// Read in the response into the buffer.
		// ... Read many bytes each iteration.
		int c;
		while ((c = in.read(data, 0, 256)) != -1) {
			if (!builder.toString().contains("</title>")) {
				builder.append(new String(data, 0, c));
			} else {
				break;
			}
		}

		String result = builder.toString();

		Pattern p = Pattern.compile(".*<title>(.*)</title>.*");
		Matcher m = p.matcher(result);

		if (m.find()) {
			result = m.group(1);
			System.out.println(result);
		} else {
			result = "";
		}
		
		in.close();

		// Return String.
		return result;
	}

	private static String clearTitle(String str) {
		str = str.replaceAll(" -( The)*? SCP Foundation$", "");
		str = str.replaceAll("^(The )*?SCP Foundation: ", "");
		str = str.replaceAll(" -( The)*? SCP Sandbox$", "");
		str = str.replaceAll(" - Андивионский Научный Альянс( - Wikidot)*?$", "");
		str = str.replaceAll("&quot;", "\"");

		return str;
	}

	private static String clearURL(String str) {
		str = str.replaceAll("printer--friendly//", "");

		return str;
	}

	private static String getHeadTitle(String pageURL, String site, String text) {
		String result = text;
		if (site.contains("scp") || site.contains("scientific-alliance")) {
			// InputStream response = null;
			// try {
			//
			// URLConnection connection = new URL(pageURL).openConnection();
			// connection.setRequestProperty("Range", "bytes=0-9999");
			//
			// response = new URL(pageURL).openStream();
			//
			// Scanner scanner = new Scanner(response);
			// String responseBody = scanner.useDelimiter("\\A").next();
			// scanner.close();
			// result = responseBody.substring(responseBody.indexOf("<title>") +
			// 7, responseBody.indexOf("</title>"));
			//
			// } catch (IOException ex) {
			// ex.printStackTrace();
			// } finally {
			// try {
			// response.close();
			// } catch (IOException ex) {
			// ex.printStackTrace();
			// }
			// }

			try {
				result = getPage(pageURL);
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String picture(String search) {
		try {
			String charset = "UTF-8";
			String userAgent = "Hatbot (+http://scpfoundation.ru/)";

			List<String> resultUrls = new ArrayList<String>();

			// String url = "https://www.google.com/search?site=imghp&q=" +
			// URLEncoder.encode(search, charset);

			// Document doc = Jsoup.connect(url).get();
			// Elements links = doc.select("a[href]");

			// System.out.println(doc.html());

			String googleUrl = "https://www.google.com/search?tbm=isch&q="
					+ URLEncoder.encode(search, charset).replace(",", "");
			Document doc1 = Jsoup.connect(googleUrl).userAgent(userAgent).timeout(10 * 1000).get();
			Element media = doc1.select("[data-src]").first();
			String finUrl = media.attr("abs:data-src");

			System.out.println(
					"<a href=\"http://images.google.com/search?tbm=isch&q=" + URLEncoder.encode(search, charset)
							+ "\"><img src=\"" + finUrl.replace("&quot", "") + "\" border=1/></a>");

			return resultUrls.get(new Random().nextInt(resultUrls.size()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getLinkTo(String scp, HashMap<String, String> scpNames, boolean russian) {
		String site;

		if (russian) {
			site = "http://www.scpfoundation.ru/";
		} else {
			site = "http://www.scp-wiki.net/";
		}

		if (scpNames.containsKey(scp)) {
			return "\n[" + scpNames.get(scp) + "](" + site + scp.toLowerCase() + ")";
		} else {
			return "";
		}
	}

	public static void cacheSCPList(String site, HashMap<String, String> scpNames) {
		Scanner input;
		try {
			input = new Scanner(new URL(site).openStream(), "UTF-8");
			String page = input.useDelimiter("\\A").next();
			input.close();

			Pattern pattern = Pattern.compile("=\"/(.+?)\">(SCP-[0-9]+.+?)(</li>|<br.*?>)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(page);

			while (matcher.find()) {
				String scpNum = matcher.group(1);
				String scpName = matcher.group(2).replaceAll("<.+?>", "").replaceAll("&quot;", "\"");

				if (!scpName.equals("[ACCESS DENIED]")) {
					scpNames.put(scpNum, scpName);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void cacheAll() {
		HatbotMain.scpNamesEn.clear();
		HatbotMain.scpNamesTrans.clear();
		HatbotMain.scpNamesRu.clear();
		LInternet.cacheSCPList("http://www.scp-wiki.net/scp-series", HatbotMain.scpNamesEn);
		LInternet.cacheSCPList("http://www.scp-wiki.net/scp-series-2", HatbotMain.scpNamesEn);
		LInternet.cacheSCPList("http://www.scp-wiki.net/scp-series-3", HatbotMain.scpNamesEn);
		LInternet.cacheSCPList("http://www.scp-wiki.net/scp-series-4", HatbotMain.scpNamesEn);

		LInternet.cacheSCPList("http://scpfoundation.ru/scp-list", HatbotMain.scpNamesTrans);
		LInternet.cacheSCPList("http://scpfoundation.ru/scp-list-2", HatbotMain.scpNamesTrans);
		LInternet.cacheSCPList("http://scpfoundation.ru/scp-list-3", HatbotMain.scpNamesTrans);
		LInternet.cacheSCPList("http://scpfoundation.ru/scp-list-4", HatbotMain.scpNamesTrans);

		LInternet.cacheSCPList("http://scpfoundation.ru/scp-list-ru", HatbotMain.scpNamesRu);
		System.out.println("validated");
	}

}
