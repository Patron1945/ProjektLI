package com.kucharz.patryk.li;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicResolver
{
	private static LogicResolver instance;

	LinkedList<String[]> logicLaws = new LinkedList<String[]>();
	LinkedList<String[]> shortLaws = new LinkedList<String[]>();
	public LinkedList<LinkedList<String>> wyniki = new LinkedList<LinkedList<String>>();

	protected LogicResolver()
	{
		// Wyrazenia regularne odpowiadajace za prawa logiki stosowane wtedy
		// kiedy w zdaniu logicznym wystepuja nawiasy
		// Zawieraja: znak poszukiwany, cale prawo logiki, docelowa forma
		logicLaws.add(new String[] { "=>", "~\\({1}[p-z=~>^()]+\\)?=>~?\\(?[p-z=~>^()]+\\){1}", "1&~2" });
		logicLaws.add(new String[] { "=>", "\\(?[p-z=~>^()]+\\)?=>~?\\(?[p-z=~>^()]+\\)?", "~1;2" });
		logicLaws.add(new String[] { "\\^", "~\\([p-z~^>=()]+\\^[p-z~^>=()]+\\)", "~1;~2" });
		logicLaws.add(new String[] { "\\^", "\\([p-z~^>=()]+\\^[p-z~^>=()]+\\)", "1&2" });
		logicLaws.add(new String[] { "v", "~\\([p-z~^>=()]+v{1}[p-z~^>=()]+\\)", "~1&~2" });
		logicLaws.add(new String[] { "v", "\\([p-z~^>=()]+v{1}[p-z~^>=()]+\\)", "1;2" });

		// Wyrazenia regularne odpowiadajace za prawa logiki stosowane wtedy
		// kiedy w zdaniu logicznym nie wystepuja nawiasy
		shortLaws.add(new String[] { "~?[p-z]{1}v{1}~?[p-z]{1}", "v", ";" });
		shortLaws.add(new String[] { "~?[p-z]{1}\\^{1}~?[p-z]{1}", "^", "&" });

	}

	public static LogicResolver getInstance()
	{
		if (instance == null)
			instance = new LogicResolver();

		return instance;
	}

	public int resolve(String logicSentence) throws Exception
	{
		if (!corrBracketChecker(logicSentence))
			throw new Exception("Problem z nawiasami");

		String result = logicSentence;
		String tmp = logicSentence;
		do
		{
			// System.out.println("POCZATEK PETLA: " + result);
			tmp = result;
			result = simplify(tmp);
			System.out.println("Drzewo: " + result);
		} while (!tmp.equals(result));

		// W tym momencie mamy grupy, których już nie przekształcimy
		// trzeba je teraz rozdzielić w miejscu wystapienia znaku ^
		LinkedList<String> subgroupsTab = buildLastLevelOfTree(result);

		wyniki.add(subgroupsTab);
		Boolean[] tmpResult = checkTautology(subgroupsTab);

		Boolean isTautology = true;
		Boolean isSpelnialny = false;
		for (int i = 0; i < tmpResult.length; i++)
		{
			// Wyznaczenie wartosciowosci dla ktorej zdanie nie jest prawdziwe
			if (tmpResult[i] == false)
			{
				isTautology = false;
			}
			else
			{
				isSpelnialny = true;
			}
		}

		if (isTautology)
		{
			return 0;
		}
		else if (isSpelnialny && !isTautology)
		{
			return 1;
		}
		else
		{
			return 2;
		}
	}

	private Boolean[] checkTautology(LinkedList<String> list)
	{
		Boolean[] tmpBool = new Boolean[list.size()];

		Boolean printed = false;

		for (int i = 0; i < list.size(); i++)
		{
			HashSet<String> tmpPositives = new HashSet<String>();
			HashSet<String> tmpNegatives = new HashSet<String>();

			String[] tmpTab = list.get(i).split(";");

			// Rozdzielamy zdania proste i negacje na dwie listy
			// porownujemy czy w danym zdaniu wystepuje zdanie i jego negacja
			// jesli tak to true, jesli nie to false
			for (int j = 0; j < tmpTab.length; j++)
			{
				if (tmpTab[j].matches("~[p-z]"))
				{
					String tmp = tmpTab[j].replaceAll("~", "");
					tmpNegatives.add(tmp);

				}
				else
				{
					tmpPositives.add(tmpTab[j]);
				}

			}

			tmpBool[i] = false;
			Iterator<String> iterator = tmpPositives.iterator();

			while (iterator.hasNext() && tmpBool[i] == false)
			{
				String tmp = iterator.next();
				tmpBool[i] = tmpNegatives.contains(tmp);
			}

			LinkedList<String> linkedList = new LinkedList<String>(tmpPositives);
			linkedList.addAll(tmpNegatives);

			if (tmpBool[i] == false && printed == false)
			{
				StringBuilder stringBuilder = new StringBuilder();
				for (int l = 0; l < linkedList.size(); l++)
				{
					if (l < tmpPositives.size())
					{
						stringBuilder.append(linkedList.get(l) + " = 0 ");
					}
					else
					{
						stringBuilder.append(linkedList.get(l) + " = 1 ");
					}
				}

				System.out.println("Wartosciowosc dla jakiej zdanie jest falszywe: " + stringBuilder.toString());

				printed = true;
			}
		}

		return tmpBool;
	}

	// Funkcja wypisujaca najnizsze liscie drzewa
	private LinkedList<String> buildLastLevelOfTree(String logicSentence)
	{
		String[] subgroupTab = logicSentence.split(";");
		LinkedList<String> subgroups = new LinkedList<String>();
		int numberOfAnds = 0;

		for (int i = 0; i < subgroupTab.length; i++)
		{
			String[] tmpTab = subgroupTab[i].split("&"); // ZMIANA \\^ NA &
			LinkedList<String> tmpList = new LinkedList<String>();

			for (int j = 0; j < tmpTab.length; j++)
			{
				String tmp = tmpTab[j];

				if (subgroups.size() > 0)
				{
					for (int k = 0; k < subgroups.size(); k++)
					{
						String add = tmp + ";" + subgroups.get(k);
						tmpList.add(add);
					}
				}
				else
					tmpList.add(tmp);

			}

			subgroups = tmpList;

		}

		System.out.println("Koniec: " + subgroups.toString());

		return subgroups;
	}

	private String simplify(String logicSentence)
	{
		int[] bracketRange = new int[logicSentence.length()];
		int bracketLevels = 0, tmpValue = 0;
		String result = "";

		String[] tmpTab = logicSentence.split(";");
		logicSentence = "";
		for (String str : tmpTab)
		{
			// Sprawdza czy mamy zbedne nawiasy (jesli tak to je usuwa)
			str = unnecessaryBracketSearcher(str);
			logicSentence += str + ";";
		}

		// Usuwamy ostatni znak & z powyzszej petli
		logicSentence = logicSentence.substring(0, logicSentence.length() - 1);

		tmpTab = logicSentence.split("&");
		logicSentence = "";
		for (String str : tmpTab)
		{
			// Sprawdza czy mamy zbedne nawiasy (jesli tak to je usuwa)
			str = unnecessaryBracketSearcher(str);
			logicSentence += str + "&";
		}

		// Usuwamy ostatni znak & z powyzszej petli
		logicSentence = logicSentence.substring(0, logicSentence.length() - 1);

		// Petla wyliczajaca zagniezdzenie i liczbe nawiasow
		for (int i = 0; i < logicSentence.length(); i++)
		{

			if (logicSentence.charAt(i) == '(')
			{
				tmpValue++;
			}
			else if (logicSentence.charAt(i) == ')')
			{
				tmpValue--;
			}

			if (tmpValue > bracketLevels)
				bracketLevels = tmpValue;

			bracketRange[i] = tmpValue;
		}

		// Nawiasy nie wystepuja w danym zdaniu
		if (bracketLevels == 0)
		{
			for (int j = 0; j < shortLaws.size(); j++)
			{
				String[] tmpLaw = shortLaws.get(j);
				// Szukamy =>,^, v
				int[] signStartEnd = new int[2];
				Pattern pattern = Pattern.compile(tmpLaw[0]);
				Matcher matcher = pattern.matcher(logicSentence);
				Boolean found = false;

				String longestGroup = "";
				int[] startEndGroup = new int[2];

				while (matcher.find())
				{
					// System.out.println("matcher.group(): " +
					// matcher.group());
					if (matcher.group().length() > longestGroup.length())
					{
						startEndGroup[0] = matcher.start();
						startEndGroup[1] = matcher.end();
						longestGroup = matcher.group();
						found = true;
					}
				}

				String[] tmpStringTab = new String[3];
				tmpStringTab[0] = logicSentence.substring(0, startEndGroup[0]);
				tmpStringTab[1] = logicSentence.substring(startEndGroup[0], startEndGroup[1]);
				tmpStringTab[2] = logicSentence.substring(startEndGroup[1], logicSentence.length());

				tmpStringTab[1] = tmpStringTab[1].replace(tmpLaw[1], tmpLaw[2]);

				if (found)
					return tmpStringTab[0] + tmpStringTab[1] + tmpStringTab[2];
			}

			return logicSentence;

		}
		// Nawiasy wystepuja
		else
		{
			for (int i = 0; i <= bracketLevels; i++)
			{
				for (int j = 0; j < logicLaws.size(); j++)
				{
					String[] tmpLaw = logicLaws.get(j);
					// Szukamy =>,^, v
					int[] signStartEnd = new int[2];
					Pattern pattern = Pattern.compile(tmpLaw[0]);
					Matcher matcher = pattern.matcher(logicSentence);
					Boolean found = false;
					int longest = 0;
					while (matcher.find())
					{

						if (bracketRange[matcher.start()] == i && longest < matcher.group().length())
						{
							longest = matcher.group().length();
							signStartEnd[0] = matcher.start();
							signStartEnd[1] = matcher.end();
							found = true;
						}

					}

					if (found)
					{
						// Szukamy wyrazenia regularnego
						pattern = Pattern.compile(tmpLaw[1]);
						matcher = pattern.matcher(logicSentence);

						String longestGroup = "";
						int[] startEndGroup = new int[2];

						while (matcher.find())
						{
							String tmp = matcher.group();
							if (matcher.group().length() > longestGroup.length()
									&& (matcher.start() <= signStartEnd[0] && matcher.end() >= signStartEnd[1]))
							{
								startEndGroup[0] = matcher.start();
								startEndGroup[1] = matcher.end();
								longestGroup = matcher.group();
							}
						}

						if (!longestGroup.equals(""))
						{
							String[] tmpStringTab = new String[3];
							tmpStringTab[0] = logicSentence.substring(0, startEndGroup[0]);
							tmpStringTab[1] = logicSentence.substring(startEndGroup[0], startEndGroup[1]);
							tmpStringTab[2] = logicSentence.substring(startEndGroup[1], logicSentence.length());

							int difference = tmpStringTab[0].length();

							result = transformSentenceWithLaw(tmpStringTab[1], signStartEnd[0] - difference, signStartEnd[1] - difference, tmpLaw[2],
									j);

							result = tmpStringTab[0] + result + tmpStringTab[2];
							return result;
						}
					}

				}
			}
		}
		return result;
	}

	private String transformSentenceWithLaw(String group, int start, int end, String finish, int law)
	{
		String[] result = new String[2];

		result[0] = group.substring(0, start);
		result[1] = group.substring(end, group.length());

		if ((law == 0 || law == 4 || law == 2))
		{
			result[0] = result[0].substring(2);
			result[1] = result[1].substring(0, result[1].length() - 1);
		}
		else if ((law == 5 || law == 3) && !result[0].contains(")"))
		{
			result[0] = result[0].substring(1);
			result[1] = result[1].substring(0, result[1].length() - 1);
		}

		// Podstawianie czesci zdania logicznego pod wzor zawarty w tablicy praw
		// logiki
		finish = finish.replaceFirst("1", result[0]);
		finish = finish.replaceFirst("2", result[1]);
		finish = finish.replaceAll("~~", "");

		return finish;
	}

	// Funkcja sprawdzajaca czy liczba nawiasow sie zgadza
	private Boolean corrBracketChecker(String logicSentence)
	{
		short bracketNumber = 0;
		for (int i = 0; i < logicSentence.length(); i++)
		{
			if (logicSentence.charAt(i) == '(')
			{
				bracketNumber++;
			}
			else if (logicSentence.charAt(i) == ')')
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
			return logicSentence;
		}
		else
		{
			for (int i = 0; i < logicSentence.length() - 1; i++)
			{

				if (logicSentence.charAt(i) == '(')
					tmp += 1;
				else if (logicSentence.charAt(i) == ')')
					tmp -= 1;

				if (tmp == 0)
				{
					return logicSentence;
				}
			}
		}

		return logicSentence = logicSentence.substring(1, logicSentence.length() - 1);

	}
	// Przykłady na których testowałem aplikację
	// ((p=>q)^(q=>r))=>~(p^~r) TAK
	// ((p=>q)^(q=>r))=>(p^~r) NIE
	// (pv~r)=>((p=>q)v(q^r)) NIE
	// ((p=>q)^(r=>q))=>~(p^~r) NIE
	// ((p=>q)^(q=>r))=>~(pv~r) NIE
	// ((p=>q)^(q^r))=>(pv~r) NIE
	// ((p=>q)v(q^r))=>(pv~r) NIE
	// ((p=>q)v(~q^r))=>(pv~r) NIE
	// ((p=>~q)v(q^r))=>(pv~r) NIE
	// ((~p=>q)v(q^p))=>(pv~q) TAK
	// ((~pvq)v(q^p))=>(pv~q) TAK
	// pv~p TAK
	// p^~p NIE
	// (p=>q)=>((pv~r)^~(q=>r)) NIE
	// (pv~r)=>((p=>q)v(q^r)) NIE
	// public static void main(String... args)
	// {
	// try
	// {
	// String[] przyklady = new String[] {
	// "((p=>q)^(q=>r))=>~(p^~r)",
	// "((p=>q)^(q=>r))=>(p^~r)",
	// "((p=>q)^(r=>q))=>~(p^~r)",
	// "((p=>q)^(q=>r))=>~(pv~r)",
	// "((p=>q)^(q^r))=>(pv~r)",
	// "((p=>q)v(q^r))=>(pv~r)",
	// "((p=>q)v(~q^r))=>(pv~r)",
	// "((p=>~q)v(q^r))=>(pv~r)",
	// "((~p=>q)v(q^p))=>(pv~q)",
	// "((~pvq)v(q^p))=>(pv~q)",
	// "(p=>q)=>((pv~r)^(~q=>r))",
	// "(p=>q)v((pv~r)^~(q=>~r))",
	// "(p=>q)v((pv~q)=>~(q=>p))",
	// };
	//
	// for(int i = 0; i < przyklady.length; i++)
	// {
	// System.out.println("START: " + przyklady[i]);
	// LogicResolver.getInstance().resolve(przyklady[i]);
	// }
	//
	// for(int i = 0; i < LogicResolver.getInstance().wyniki.size(); i++)
	// {
	// System.out.println("WYNIKI: " +
	// LogicResolver.getInstance().wyniki.get(i).toString());
	// }
	//
	// } catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	//
	// }

}
