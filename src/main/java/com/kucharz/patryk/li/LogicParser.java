package com.kucharz.patryk.li;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicParser
{
	
	/*
	 * znaki specjalne: v - OR, & - AND
	 */
	private static LogicParser instance = null;

	String logicSentence;

	LinkedList<String> lawOfLogicListKeys = null;
	LinkedList<String> lawOfLogicListValues = null;;

	protected LogicParser()
	{
		lawOfLogicListKeys = new LinkedList<String>();
		lawOfLogicListKeys.add("[aou]la");
//		lawOfLogicListKeys.add("[]");

		lawOfLogicListValues = new LinkedList<String>();
		lawOfLogicListValues.add("ok!");
//		lawOfLogicListValues.add("nie ok!");
	}

	public static LogicParser getInstance()
	{
		if (instance == null)
		{
			instance = new LogicParser();
		}

		return instance;
	}

	public String parse(String sentence)
	{
		String result = simplify(sentence);
		
		return result;
	}

	public String simplify(String sentence)
	{
		
		String sent = sentence;
		for (int i = 0; i < lawOfLogicListKeys.size(); i++)
		{	
			
			
			
			sent = sentence.replaceAll(lawOfLogicListKeys.get(i), lawOfLogicListValues.get(i));
			
//			Pattern pattern = Pattern.compile(lawOfLogicListKeys.get(i));
//
//			Matcher matcher = pattern.matcher(sentence);
//
//			boolean found = false;
//			while (matcher.find())
//			{
//				System.out.println("I found the text" + matcher.group()
//						+ " starting at " + "index " + matcher.start()
//						+ " and ending at index " + matcher.end());
//				
//				found = true;
//			}
//			if (!found)
//			{
//				System.out.println("No match found.");
//			}
		}
		
		return sent;
	}
	
	public Boolean CheckTautology(String sentence)
	{
		Boolean tautology = false;
		
		
		return tautology;
	}

}
