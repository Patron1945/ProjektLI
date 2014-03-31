package com.kucharz.patryk.li;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicResolver
{
	private static LogicResolver instance;

	LinkedList<String[]> logicLaws = new LinkedList<String[]>();

	protected LogicResolver()
	{
		logicLaws.add(new String[] { "=>",
				"~\\(?[p-z=~>^()]+\\)?=>~?\\(?[p-z=~>^()]+\\)?", "1^~2" });
		logicLaws.add(new String[] { "=>",
				"\\(?[p-z=~>^()]+\\)?=>~?\\(?[p-z=~>^()]+\\)?", "~1;2" });
		logicLaws.add(new String[] { "\\^",
				"~\\([p-z~^>=()]+\\^[p-z~^>=()]+\\)", "~1;~2" });
		logicLaws.add(new String[] { "v",
				"~\\([p-z~^>=()]+v{1}[p-z~^>=()]+\\)", "~1&~2" });
	}

	public static LogicResolver getInstance()
	{
		if (instance == null)
			instance = new LogicResolver();

		return instance;
	}

	public Boolean resolve(String logicSentence) throws Exception
	{
		if (!corrBracketChecker(logicSentence))
			throw new Exception("Problem z nawiasami");

		String result = logicSentence;
		String tmp = logicSentence;
		do
		{
			System.out.println("POCZATEK PETLA: " + result);
			tmp = result;
			result = simplify(tmp);
			System.out.println("KONIEC PETLA ZWRACAMY: " + result);
		} while (!tmp.equals(result));

		//W tym momencie mamy grupy, których już nie przekształcimy
		//trzeba je teraz rozdzielić w miejscu wystapienia znaku ^
		LinkedList<String> subgroupsTab = buildLastLevelOfTree(result);
		
		Boolean[] tmpResult = checkTautology(subgroupsTab);
		
		Boolean resultBool = true; 
		for(int i = 0; i < tmpResult.length; i++)
		{
			if(tmpResult[i] == false)
				resultBool = false;
		}
		
		return resultBool;

	}
	
	private Boolean[] checkTautology(LinkedList<String> list)
	{
		Boolean[] tmpBool = new Boolean[list.size()];
		
		for(int i = 0; i < list.size(); i++)
		{
			LinkedList<String> tmpPositives = new LinkedList<String>();
			LinkedList<String> tmpNegatives = new LinkedList<String>();
			
			String[] tmpTab = list.get(i).split(";");
			
			for(int j = 0; j < tmpTab.length; j++)
			{
				if(tmpTab[j].matches("~[p-z]"))
				{
					String tmp = tmpTab[j].replaceAll("~", "");
					tmpNegatives.add(tmp);
				}
				else
				{
					tmpPositives.add(tmpTab[j]);
				}

			}
//			System.out.println("POSITIVES: " + tmpPositives.toString());
//			System.out.println("NEGATIVES: " + tmpNegatives.toString());
			tmpBool[i] = false;
			
			for(int j = 0; j < tmpPositives.size(); j++)
			{
				
				if(tmpNegatives.size() > 0)
				{
					for(int k = 0; k < tmpNegatives.size(); k++)
					{
						if(tmpNegatives.get(k).equals(tmpPositives.get(j)))
						{
							tmpBool[i] = true;
						}
					}
				}
			}
		}
		
		return tmpBool;
	}

	private LinkedList<String> buildLastLevelOfTree(String logicSentence)
	{
		String[] subgroupTab = logicSentence.split(";");
		LinkedList<String> subgroups = new LinkedList<String>();
		int numberOfAnds = 0;
//		Pattern pattern = Pattern.compile("\\^");
//		Matcher matcher;
		
		for(int i = 0; i < subgroupTab.length; i++)
		{
			if(subgroupTab[i].contains("^"))
				numberOfAnds++;
		}
		
//		String[] topLeaves = new String[(int) Math.pow(2, numberOfAnds)];
			
		for(int i = 0; i < subgroupTab.length; i++)
		{
			String[] tmpTab = subgroupTab[i].split("\\^");
//			System.out.println("Arrays: " + Arrays.toString(tmpTab));
			LinkedList<String> tmpList = new LinkedList<String>();
			
			for(int j = 0; j < tmpTab.length; j++)
			{
				String tmp = tmpTab[j];
//				System.out.println("tmp: " + tmp);
				
				if(subgroups.size()>0)
				{
					for(int k = 0; k < subgroups.size(); k++)
					{
						
						String add = tmp + ";" + subgroups.get(k);
//						System.out.println("TMP: " + tmp + " sub: " +  subgroups.get(k));
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
		for(String str: tmpTab)
		{
			System.out.println("STR: " + str);
			str = unnecessaryBracketSearcher(str);
			System.out.println("STR: " + str);
			logicSentence += str + ";";
		}
		
		logicSentence = logicSentence.substring(0, logicSentence.length()-1);

		for (int i = 0; i < logicSentence.length(); i++)
		{

			if (logicSentence.charAt(i) == '(')
			{
				tmpValue++;
			} else if (logicSentence.charAt(i) == ')')
			{
				tmpValue--;
			}

			if (tmpValue > bracketLevels)
				bracketLevels = tmpValue;

			bracketRange[i] = tmpValue;
		}
		
		if(bracketLevels == 0)
		{
			return logicSentence;
		}

		System.out.println("bracketLevels: " + bracketLevels);
		System.out.println("bracketRange: " + Arrays.toString(bracketRange));

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
				while (matcher.find())
				{
					if (bracketRange[matcher.start()] == i)
					{
						System.out.println("matcher.group(): "
								+ matcher.group() + " dla i: " + i);
						System.out.println("matcher.start(): "
								+ matcher.start() + " matcher.end(): "
								+ matcher.end());
						System.out.println("bracketRange[matcher.start()]: "
								+ bracketRange[matcher.start()]);
						signStartEnd[0] = matcher.start();
						signStartEnd[1] = matcher.end();
						found = true;
						break;
					}
				}

				if (found)
				{
					// Szukamy wyrazenia regularnego
					pattern = Pattern.compile(tmpLaw[1]);
					matcher = pattern.matcher(logicSentence);

					String longestGroup = "";
					int[] startEndGroup = new int[2];

					while (matcher.find()
							&& (matcher.start() <= signStartEnd[0] && matcher
									.end() >= signStartEnd[1]))
					{
						System.out.println("matcher.group(): "
								+ matcher.group());
						if (matcher.group().length() > longestGroup.length())
						{
							startEndGroup[0] = matcher.start();
							startEndGroup[1] = matcher.end();
							longestGroup = matcher.group();
						}
					}

					if (!longestGroup.equals(""))
					{
						String[] tmpStringTab = new String[3];
						tmpStringTab[0] = logicSentence.substring(0,
								startEndGroup[0]);
						tmpStringTab[1] = logicSentence.substring(
								startEndGroup[0], startEndGroup[1]);
						tmpStringTab[2] = logicSentence.substring(
								startEndGroup[1], logicSentence.length());

						int difference = tmpStringTab[0].length();
						System.out.println("transformSentenceWithLaw:wejscie: "
								+ tmpStringTab[1] + ", "
								+ (signStartEnd[0] - difference) + ", "
								+ (signStartEnd[1] - difference) + ", "
								+ tmpLaw[2]);
						result = transformSentenceWithLaw(tmpStringTab[1],
								signStartEnd[0] - difference, signStartEnd[1]
										- difference, tmpLaw[2], j);

						result = tmpStringTab[0] + result + tmpStringTab[2];
						// System.out.println("LAW: " + j);
						return result;
					}
				}

			}
		}

		return result;
	}

	private String transformSentenceWithLaw(String group, int start, int end,
			String finish, int law)
	{
		String[] result = new String[2];

		result[0] = group.substring(0, start);
		result[1] = group.substring(end, group.length());

		System.out.println("transformSentenceWithLaw: result[0]: " + result[0]
				+ ", result[1]: " + result[1]);

		if (law == 0 || law == 2 || law == 3)
		{
			result[0] = result[0].substring(2);
			result[1] = result[1].substring(0, result[1].length() - 1);
		}

		System.out.println("transformSentenceWithLaw: result[0]: " + result[0]
				+ ", result[1]: " + result[1]);

		finish = finish.replaceFirst("1", result[0]);
		finish = finish.replaceFirst("2", result[1]);

		finish = finish.replaceAll("~~", "");

		System.out.println("transformSentenceWithLaw: return: " + finish);
		return finish;
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
			 System.out.println("unnecessaryBracketSearcher: zwracam : " +
			 logicSentence);
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
					 System.out.println("unnecessaryBracketSearcher: zwracam : "
					 + logicSentence);
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

//	public static void main(String... args)
//	{
//		try
//		{
//			System.out.println("Wynik: "
//					+ LogicResolver.getInstance().resolve(
//							"((p=>q)^(q=>r))=>~(p^~r)"));
////			System.out.println("Wynik: "
////					+ LogicResolver.getInstance().resolve(
////							"((p=>q)^(q=>r))=>(p^~r)"));
////			System.out.println("Wynik: "
////					+ LogicResolver.getInstance().resolve(
////							"((p=>q)^(r=>q))=>~(p^~r)"));
//
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//	}

}
