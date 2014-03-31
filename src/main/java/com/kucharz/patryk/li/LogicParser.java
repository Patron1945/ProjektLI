package com.kucharz.patryk.li;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicParser
{
	private static LogicParser instance;

	static String[][] tabLawOfLogic = {

	{ "~\\(?[p-z=~>^()]+\\)?=>~?\\(?[p-z=~>^()]+\\)?", "=>", "1&~2" },
			{ "\\(?[p-z=~>^()]+\\)?=>~?\\(?[p-z=~>^()]+\\)?", "=>", "~1;2" },
			{ "~\\([p-z~^>=()]+\\^[p-z~^>=()]+\\)", "\\^", "~1;~2" },
			{ "~\\([p-z~^>=()]+v{1}[p-z~^>=()]+\\)", "v", "~1&~2" } };

	static HashMap<String, String[]> mapLawOfLogic = new HashMap<String, String[]>();

	
	protected LogicParser()
	{
		mapLawOfLogic.put("=>", new String[] {
				"~\\(?[p-z=~>^()]+\\)?=>~?\\(?[p-z=~>^()]+\\)?", "=>", "1&~2" });
		mapLawOfLogic.put("=>", new String[] {
				"\\(?[p-z=~>^()]+\\)?=>~?\\(?[p-z=~>^()]+\\)?", "=>", "~1;2" });
		mapLawOfLogic.put("\\^", new String[] {
				"~\\([p-z~^>=()]+\\^[p-z~^>=()]+\\)", "\\^", "~1;~2" });
		mapLawOfLogic.put("v", new String[] {
				"~\\([p-z~^>=()]+v{1}[p-z~^>=()]+\\)", "v", "~1&~2" });
	}

	public static LogicParser getInstance()
	{
		if (instance == null)
			instance = new LogicParser();

		return instance;
	}

	public String parse(String logicSentence) throws Exception
	{
		// Usuwamy białe znaki, żeby nie przeszkadzały
		logicSentence = logicSentence.replaceAll("\\s+", "");

		if (!corrBracketChecker(logicSentence))
			throw new Exception("Problem z nawiasami");

		String tmp;
		String result = logicSentence;

		do
		{
			tmp = result;
			result = simplifyLogic(result);
			System.out.println("parse: result = " + result + ",tmp = " + tmp);
		} while (!result.equals(tmp));

		// System.out.println("parse: result = " + result);
		return result;
	}

	// Funkcja sprawdzajaca czy wprowadzone zdanie logiczne ma zamkniete
	// wszystkie nawiasy
	private Boolean corrBracketChecker(String logicSentence)
	{
		short bracketNumber = 0;
		for (int i = 0; i < logicSentence.length(); i++)
		{
			if (logicSentence.charAt(i) == '(')
			{
				bracketNumber++;
			} else if (logicSentence.charAt(i) == ')')
			{
				bracketNumber--;
			}

		}

		if (bracketNumber == 0)
			return true;
		else
			return false;

	}

	// Sprawdza czy mamy zbedne nawiasy (jesli tak to je usuwa)
	private String unnecessaryBracketSearcher(String logicSentence)
	{
		short tmp = 0;

		if (logicSentence.length() == 1)
		{
			// System.out.println("unnecessaryBracketSearcher: zwracam : " +
			// logicSentence);
			return logicSentence;
		} else
		{
			for (int i = 0; i < logicSentence.length() - 1; i++)
			{
				if (logicSentence.charAt(i) == '(')
					tmp += 1;
				else if (logicSentence.charAt(i) == ')')
					tmp -= 1;

				if (tmp == 0)
				{
					// System.out.println("unnecessaryBracketSearcher: zwracam : "
					// + logicSentence);
					return logicSentence;
				}

			}
		}

		// System.out.println("unnecessaryBracketSearcher: zwracam : " +
		// logicSentence.substring(1, logicSentence.length()-1));
		// Zwracamy zdanie bez nawiasów zewnętrznych
		return logicSentence = logicSentence.substring(1,
				logicSentence.length() - 1);

	}

	// 1. WERSJA
	// //Sprawdzamy czy dany string mozna przeksztalcic z wykorzystaniem praw
	// rachunku zdan
	// private String simplifyLogic(String logicSentence)
	// {
	// System.out.println("simplifyLogic: logicSentence = " + logicSentence);
	// String result = logicSentence;
	//
	// for(int i = 0; i < tabLawOfLogic.length; i++)
	// {
	// Pattern pat = Pattern.compile(tabLawOfLogic[i][0]);
	// Matcher mat = pat.matcher(result);
	// LinkedList<String> matches = new LinkedList<String>();
	// while(mat.find())
	// {
	// String tmp = mat.group();
	// matches.add(tmp);
	// }
	//
	// if(matches.size() > 0)
	// {
	// int max = findTheLongestString(matches);
	// result = transformStringWithLogicLaw(matches.get(max), tabLawOfLogic[i]);
	// result = logicSentence.replace(matches.get(max), result);
	// System.out.println("result: " + result);
	// return result;
	// }
	// }
	//
	// return result;
	// }

	// //Sprawdzamy czy dany string mozna przeksztalcic z wykorzystaniem praw
	// rachunku zdan
	private String simplifyLogic(String logicSentence)
	{
		System.out.println("simplifyLogic: logicSentence = " + logicSentence);
		String result = logicSentence;

		int[] sentence = new int[logicSentence.length()];
		int value = 0, highest = 0;
		
		//Przeliczamy string, aby wiedziec w ktorym miejscu sa nawiasy i co obejmuja
		for (int i = 0; i < logicSentence.length(); i++)
		{
			if (logicSentence.charAt(i) == '(')
			{
				value++;
			} 
			else if (logicSentence.charAt(i) == ')')
			{
				value--;
			}
			
			if(value > highest)
				highest = value;
			
			sentence[i] = value;
		}
		
		
	}

	// Poszukiwanie najdłuższego stringu
	private int findTheLongestString(LinkedList<String> list)
	{
		int maxLength = 0;
		int posString = -1;
		for (int i = 0; i < list.size(); i++)
		{
			String tmp = list.get(i);
			if (tmp.length() > maxLength)
			{
				maxLength = tmp.length();
				posString = i;
			}

		}

		return posString;
	}

	// Przekształcenie zdania logicznego z wykorzystaniem prawa logicznego
	private String transformStringWithLogicLaw(String group, String[] law)
	{
		System.out.println("transformStringWithLogicLaw: group = " + group
				+ ",law = " + law[0]);
		Pattern pattern = Pattern.compile(law[1]);
		Matcher matcher = pattern.matcher(group);
		law[1] = law[1].replace("\\", "");
		System.out.println("law[1]: " + law[1]);

		String p = "", q = "";
		int[] tab = new int[group.length()];
		int value = 0, lowest = -1;
		// Sprawdzamy co jest w nawiasach, a co poza nimi
		// w celu znalezienia wyrażeń poza nawiasami
		for (int i = 0; i < group.length(); i++)
		{
			if (group.charAt(i) == '(')
			{
				value++;
			} else if (group.charAt(i) == ')')
			{
				value--;
			} else if (group.substring(i, i + law[1].length()).equals(law[1]))
			{
				// System.out.println("LOWEST: " +
				// group.substring(i,i+law[1].length()));
				lowest = value;
			}

			tab[i] = value;
		}

		while (matcher.find())
		{
			System.out
					.println("transformStringWithLogicLaw: petlaWhile: matcher.group() = "
							+ matcher.group());
			System.out
					.println("transformStringWithLogicLaw: petlaWhile: tab[matcher.start()] = "
							+ tab[matcher.start()]);

			if (tab[matcher.start()] == lowest)
			{
				System.out
						.println("transformStringWithLogicLaw: przed group: OK");
				if (lowest == 0)
				{
					p = group.substring(0, matcher.start());
					q = group.substring(matcher.end(), group.length());
				} else if (lowest > 0)
				{
					p = group.substring(2, matcher.start());
					q = group.substring(matcher.end(), group.length() - 1);
				}

				String result = law[2];
				result = result.replaceFirst("1", p);
				result = result.replaceFirst("2", q);

				// String[] tmpTab = result.split(";");
				// System.out.println("transformStringWithLogicLaw: przed unnecessaryBracketSearcher: p = "
				// + tmpTab[0] + ",q = " + tmpTab[1]);
				// p = unnecessaryBracketSearcher(p);
				// System.out.println("transformStringWithLogicLaw: p = " + p);
				// q = unnecessaryBracketSearcher(q);
				// System.out.println("transformStringWithLogicLaw: q = " + q);
				// System.out.println("transformStringWithLogicLaw: petlaWhile: p;q = "
				// + p+";"+ q );
				//
				// result = p+";"+q;

				System.out.println("transformStringWithLogicLaw: zwracam: "
						+ result);

				if (result.matches("~?\\([p-z]{1}\\)[^v]~?\\([p-z]{1}\\)"))
				{
					result = result.replace("(", "");
					result = result.replace(")", "");
				}
				return result;
			}

		}

		return group;
	}

}
