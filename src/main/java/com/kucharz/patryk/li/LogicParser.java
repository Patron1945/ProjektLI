package com.kucharz.patryk.li;

public class LogicParser
{
	private static LogicParser instance;
	
	static
	{
		String[][] tabLawOfLogic = new String[2][2];
	}
	
	protected LogicParser()
	{
		
	}
	
	public static LogicParser getInstance()
	{
		if(instance == null)
			instance = new LogicParser();
		
		return instance;
	}
	
	public Boolean parse(String logicSentence) throws Exception
	{
	    //Usuwamy białe znaki, żeby nie przeszkadzały
		logicSentence = logicSentence.replaceAll("\\s+","");
		
		if(!corrBracketChecker(logicSentence))
			throw new Exception("Problem z nawiasami");
		
		System.out.println(unecessaryBracketSearcher(logicSentence));
		return false;
	}
	
	
	//Funkcja sprawdzajaca czy wprowadzone zdanie logiczne ma zamkniete wszystkie nawiasy
	private Boolean corrBracketChecker(String logicSentence)
	{
		short bracketNumber = 0;
		for(int i = 0; i < logicSentence.length(); i++)
		{
			if(logicSentence.charAt(i) == '(')
			{
				bracketNumber++;
			}
			else if(logicSentence.charAt(i) == ')')
			{
				bracketNumber--;
			}

		}
		
		if(bracketNumber == 0)
			return true;
		else
			return false;

		
	}
	//Sprawdza czy mamy zbedne nawiasy (jesli tak to je usuwa)
	private String unecessaryBracketSearcher(String logicSentence)
	{
		short tmp = 0;
		for(int i = 0; i < logicSentence.length()-1; i++)
		{
			if(logicSentence.charAt(i) == '(')
				tmp += 1;
			else if(logicSentence.charAt(i) == ')')
				tmp -= 1;
			
			System.out.println("Tmp: "+ tmp + " char:" + logicSentence.charAt(i));
			
			if(tmp == 0)
				return logicSentence;
			
			
		}
		
		//Zwracamy zdanie bez nawiasów zewnętrznych
		return logicSentence = logicSentence.substring(1, logicSentence.length()-1);
		
	}
}
