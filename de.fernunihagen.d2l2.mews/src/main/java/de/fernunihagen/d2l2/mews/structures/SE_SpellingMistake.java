package de.fernunihagen.d2l2.mews.structures;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.lift.api.StructureExtractor;
import org.lift.type.Structure;

import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.GrammarAnomaly;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class SE_SpellingMistake extends StructureExtractor {
	protected Set<String> singleWordRS;
	protected String singleWordPath;
	public static final String spellingMistake1 = "^Possible spelling mistake. Did you mean.*the (plural|past tense|comparative) form of the (noun|verb|adjective) '.*'?";
	public static final Pattern spellingMistakeP1 = Pattern.compile(spellingMistake1);
	public static final String spellingMistake2 = "^Possible spelling mistake found";
	public static final Pattern spellingMistakeP2 = Pattern.compile(spellingMistake2);
	public static final String spellingMistake3 = "^Possible typo. Did you mean.*";
	public static final Pattern spellingMistakeP3 = Pattern.compile(spellingMistake3);
	public static final String spellingMistake4 = "^Did you mean <suggestion>.*</suggestion>.*";
	public static final Pattern spellingMistakeP4 = Pattern.compile(spellingMistake4);
	
	public static boolean corpusIsMEWS = true;
	// manual for MEWS
	public static final String manual1 = "^Did you mean the preposition <suggestion>through</suggestion>.*";
	public static final Pattern manualP1 = Pattern.compile(manual1);
	public static final String manual2 = "^Did you mean <suggestion>am I</suggestion> or <suggestion>I am</suggestion>?";
	public static final Pattern manualP2 = Pattern.compile(manual2);
	public static final String manual3 = "^Did you mean <suggestion>doesn't</suggestion> (= verb)?";
	public static final Pattern manualP3 = Pattern.compile(manual3);
	public static final String manual4 = "^Did you mean the adjective <suggestion>great</suggestion>?";
	public static final Pattern manualP4 = Pattern.compile(manual4);
	
	//Levenshtein distance = 1, but is grammatical error, not sm
	public static final String noSm1 = "^Did you mean <suggestion>affect</suggestion> (have an effect upon)?";
	public static final Pattern noSmP1 = Pattern.compile(noSm1);	
	public static final String noSm2 = "^Did you mean <suggestion>allow</suggestion>? Alternatively, check whether the singular of 'mothers' should be used.";
	public static final Pattern noSmP2 = Pattern.compile(noSm2);
	public static final String noSm3 = "^Did you mean <suggestion>come</suggestion>? Alternatively, check whether the singular of 'parents' should be used.";
	public static final Pattern noSmP3 = Pattern.compile(noSm3);
	public static final String noSm4 = "^Did you mean <suggestion>defend</suggestion>?";
	public static final Pattern noSmP4 = Pattern.compile(noSm4);
	public static final String noSm5 = "^Did you mean <suggestion>happen</suggestion>?";
	public static final Pattern noSmP5 = Pattern.compile(noSm5);
	public static final String noSm6 = "^Did you mean <suggestion>many times</suggestion> or <suggestion>much time</suggestion>?";
	public static final Pattern noSmP6 = Pattern.compile(noSm6);
	public static final String noSm7 = "^Did you mean <suggestion>now</suggestion> (=at this moment) instead of 'no' (negation)?";
	public static final Pattern noSmP7 = Pattern.compile(noSm7);
	public static final String noSm8 = "^Did you mean <suggestion>seem</suggestion>? Alternatively, check whether the singular of 'children' should be used.";
	public static final Pattern noSmP8 = Pattern.compile(noSm8);
	public static final String noSm9 = "^Did you mean <suggestion>think</suggestion>?";
	public static final Pattern noSmP9 = Pattern.compile(noSm9);
	public static final String noSm10 = "^Did you mean <suggestion>want</suggestion>? Alternatively, check whether the singular of 'children' should be used.";
	public static final Pattern noSmP10 = Pattern.compile(noSm10);
	public static final String noSm11 = "^Did you mean <suggestion>whom</suggestion>?";
	public static final Pattern noSmP11 = Pattern.compile(noSm11);
	public static final String noSm12 = "^Did you mean <suggestion>work</suggestion>? Alternatively, check whether the singular of 'people' should be used.";
	public static final Pattern noSmP12 = Pattern.compile(noSm12);
	public static final String noSm13 = "^Did you mean <suggestion>think</suggestion>?";
	public static final Pattern noSmP13 = Pattern.compile(noSm13);
	
	
	public static final String noSm14 = "^Possible spelling mistake. Did you mean <suggestion>children</suggestion>, the plural form of the noun 'child'?";
	public static final Pattern noSmP14 = Pattern.compile(noSm14);
	public static final String noSm15 = "^Possible spelling mistake. Did you mean <suggestion>bought</suggestion>, the past tense form of the verb 'buy'?";
	public static final Pattern noSmP15 = Pattern.compile(noSm15);
	public static final String noSm16 = "^Possible spelling mistake. Did you mean <suggestion>caught</suggestion>, the past tense form of the verb 'catch'?";
	public static final Pattern noSmP16 = Pattern.compile(noSm16);
	public static final String noSm17 = "^Possible spelling mistake. Did you mean <suggestion>companies</suggestion>, the plural form of the noun 'company'?";
	public static final Pattern noSmP17 = Pattern.compile(noSm17);
	public static final String noSm18 = "^Did you mean <suggestion>every one</suggestion>?";
	public static final Pattern noSmP18 = Pattern.compile(noSm18);	
	public static final String noSm19 = "^Did you mean <suggestion>economic</suggestion> (=connected with economy)?";
	public static final Pattern noSmP19 = Pattern.compile(noSm19);
	public static final String noSm20 = "^Did you mean <suggestion>they are</suggestion> or <suggestion>there are</suggestion>?";
	public static final Pattern noSmP20 = Pattern.compile(noSm20);
	public static final String noSm21 = "^Did you mean <suggestion>at the moment</suggestion> (=currently)?";
	public static final Pattern noSmP21 = Pattern.compile(noSm21);
	public static final String noSm22 = "^Did you mean <suggestion>know</suggestion>?";
	public static final Pattern noSmP22 = Pattern.compile(noSm22);
	
	
	@Override
	public String getPublicStructureName() {
		return "Spelling mistake";
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String id = "no id";
		if (JCasUtil.exists(aJCas, DocumentMetaData.class)){
			DocumentMetaData meta = JCasUtil.selectSingle(aJCas, DocumentMetaData.class);
			id = meta.getDocumentId();
		}
		System.out.println(id);
		//load manual error lists 
		try {
			// if list file is not set, try to load default for language			
			Path path1 = Paths.get("resources/fehler_lists/fehler.txt");	
			singleWordPath = path1.toString();
			singleWordRS = readList(singleWordPath);	
						
		} catch (IOException e) {
			e.printStackTrace();
		}
		Set<Integer> offsets = new HashSet<>();
		Collection<GrammarAnomaly> gas = JCasUtil.select(aJCas, GrammarAnomaly.class);
		Collection<Token> tokens = JCasUtil.select(aJCas, Token.class);
		loop:
		for (GrammarAnomaly g : gas) {
//			System.out.println(g);
//			System.out.println("----------------------------------------");
			int begin = g.getBegin();
			String description = g.getDescription();
			String coveredText = g.getCoveredText();
			// löst die doppelte Annotation
			System.out.println(coveredText+": "+description);
			if(offsets.contains(begin)) {
				continue loop;
			}
			offsets.add(begin);
			
			Matcher spellingMistakeM1 = spellingMistakeP1.matcher(description);
			Matcher spellingMistakeM2 = spellingMistakeP2.matcher(description);
			Matcher spellingMistakeM3 = spellingMistakeP3.matcher(description);
			
			Matcher spellingMistakeM4 = spellingMistakeP4.matcher(description);	
			
			Matcher manualM1 = manualP1.matcher(description);
			Matcher manualM2 = manualP2.matcher(description);
			Matcher manualM3 = manualP3.matcher(description);
			Matcher manualM4 = manualP4.matcher(description);
			
			Matcher noSmM1 = noSmP1.matcher(description);
			Matcher noSmM2 = noSmP2.matcher(description);
			Matcher noSmM3 = noSmP3.matcher(description);
			Matcher noSmM4 = noSmP4.matcher(description);
			Matcher noSmM5 = noSmP5.matcher(description);
			Matcher noSmM6 = noSmP6.matcher(description);
			Matcher noSmM7 = noSmP7.matcher(description);
			Matcher noSmM8 = noSmP8.matcher(description);
			Matcher noSmM9 = noSmP9.matcher(description);
			Matcher noSmM10 = noSmP10.matcher(description);
			Matcher noSmM11 = noSmP11.matcher(description);
			Matcher noSmM12 = noSmP12.matcher(description);
			Matcher noSmM13 = noSmP13.matcher(description);
			Matcher noSmM14 = noSmP14.matcher(description);
			Matcher noSmM15 = noSmP15.matcher(description);
			Matcher noSmM16 = noSmP16.matcher(description);
			Matcher noSmM17 = noSmP17.matcher(description);
			Matcher noSmM18 = noSmP18.matcher(description);
			Matcher noSmM19 = noSmP19.matcher(description);
			Matcher noSmM20 = noSmP20.matcher(description);
			Matcher noSmM21 = noSmP21.matcher(description);
			Matcher noSmM22 = noSmP22.matcher(description);
			if (noSmM1.find()||noSmM2.find()||noSmM3.find()|| noSmM4.find()||noSmM5.find()||noSmM6.find()||noSmM7.find()
					|| noSmM8.find()||noSmM9.find()||noSmM10.find()||noSmM11.find()
					|| noSmM12.find()||noSmM13.find()||noSmM14.find()
					|| noSmM15.find()||noSmM16.find()||noSmM17.find()||noSmM18.find() ||noSmM19.find()||noSmM20.find()||noSmM21.find()
					|| noSmM22.find()
					|| coveredText.equals("childrens have")|| coveredText.equals("childrens")|| coveredText.equals("contras")|| coveredText.equals("mums")
					|| coveredText.equals("McDonald\'s")|| coveredText.equals("McDonalds")|| coveredText.equals("Kinderschokolade")|| coveredText.equals("Kidoh")
					|| coveredText.equals("unhuman")|| coveredText.equals("wahrnehmen")|| coveredText.equals("advertisings")|| coveredText.equals("Advertisings")
					|| coveredText.equals("childrengarden")|| coveredText.equals("Spongebob")|| coveredText.equals("Pipi")
					|| coveredText.equals("niveau")|| coveredText.equals("promisings")|| coveredText.equals("industriated")) {
			}else {
				if (spellingMistakeM1.find()||spellingMistakeM2.find()||spellingMistakeM3.find()
						|| manualM1.find()||manualM2.find()||manualM3.find()||manualM4.find()	)
				{
//					System.out.println(coveredText+": "+description);
						if(coveredText.equals("ther are")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-3);
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}else if(coveredText.equals("to young to")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-9);
							s.setName("spelling mistake");
							s.addToIndexes();
						}else if(coveredText.equals("lego")) {
							
						}else if(coveredText.equals("KIKA")) {
							
						}else if(coveredText.equals("Playmobil")) {
							
						}else if(coveredText.equals("Hipp")) {
							
						}else if(coveredText.equals("unpossible")) {
							
						}else if(coveredText.equals("unpolite")) {
							
						}else if(coveredText.equals("advertisings can")) {
							
						}else if(coveredText.equals("Advertisings on")) {
							
						}else if(coveredText.equals("advertisings on")) {
							
						}else if(coveredText.equals("wha they")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-5);
							s.setName("spelling mistake");
							s.addToIndexes();
						}else if(coveredText.equals("The are")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-4);
							s.setName("spelling mistake");
							s.addToIndexes();
						}else if(coveredText.equals("I watcched")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin+2);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
						}else if(coveredText.equals("there own")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-4);
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}
						else if(coveredText.equals("the I")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin-2);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}else if(coveredText.equals("better then")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin+7);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
						}else if(coveredText.equals("posess it")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-3);
							s.setName("spelling mistake");
							s.addToIndexes();
						}else if(coveredText.equals("older then")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin+6);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
						}else if(coveredText.equals("some where")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin+5);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
						}else if(coveredText.equals("childrens wishes")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-7);
							s.setName("spelling mistake");
							s.addToIndexes();
						}else {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
						}
//						System.out.println(coveredText+": "+description);
						
						
				}
					// with Levenshtein distance
				if (spellingMistakeM4.find()) {
					int beginOffsetOfSuggestionWord = description.indexOf("<suggestion>") + 12; // because <suggestion> has length 12
					int endOffsetOfSuggestionWord = description.indexOf("</suggestion>");
					String suggestionWord = description.substring(beginOffsetOfSuggestionWord, endOffsetOfSuggestionWord);
					int levenshteinDistance = LevenshteinDistance.getDefaultInstance().apply(suggestionWord, coveredText);
					System.out.println(levenshteinDistance);
					if(levenshteinDistance <= 2) {
						
						if(coveredText.equals("ther are")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-3);
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}else if(coveredText.equals("to young to")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-9);
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}else if(coveredText.equals("lego")) {
							
						}else if(coveredText.equals("KIKA")) {
							
						}else if(coveredText.equals("Playmobil")) {
							
						}else if(coveredText.equals("Hipp")) {
							
						}else if(coveredText.equals("unpossible")) {
							
						}else if(coveredText.equals("unpolite")) {
							
						}else if(coveredText.equals("wha they")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-5);
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}else if(coveredText.equals("advertisings have")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-5);
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}else if(coveredText.equals("Advertisings can")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-4);
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}else if(coveredText.equals("The are")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-4);
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}else if(coveredText.equals("I watcched")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin+2);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}else if(coveredText.equals("there own")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-4);
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}
						else if(coveredText.equals("the I")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin-2);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}else if(coveredText.equals("better then")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin+7);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
							System.out.println(coveredText+ ": "+description );
						}else if(coveredText.equals("posess it")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd()-3);
							s.setName("spelling mistake");
							s.addToIndexes();
						}else if(coveredText.equals("older then")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin+6);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
						}else if(coveredText.equals("some where")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin+5);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
						}else if(coveredText.equals("can belive")) {
							Structure s = new Structure(aJCas);
							s.setBegin(begin+4);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
						}else {
							Structure s = new Structure(aJCas);
							s.setBegin(begin);
							s.setEnd(g.getEnd());
							s.setName("spelling mistake");
							s.addToIndexes();
						}	
//						System.out.println(coveredText+": "+description);
					}
					
				}
			}					
		}
		
		ArrayList<Integer> offsetOfPunctuation = new ArrayList<>();		
		for (Token t: tokens) {
			if(t.getCoveredText().equals(".")||t.getCoveredText().equals(":")||t.getCoveredText().equals("?")||t.getCoveredText().equals("!")) {
				offsetOfPunctuation.add(t.getBegin());
			}
		}
		
		ArrayList<Token> tokenList1 = new ArrayList<>();
		for(Token t : tokens) {
			tokenList1.add(t);
		}
		ArrayList<Token> tokenList2 = new ArrayList<>();
		for(Token t : tokens) {
			tokenList2.add(t);
		}
		List<Integer> listOfFehlerOffset = new ArrayList<>();
		
		for (int i = 0; i < tokenList1.size(); i++) {
			
		}
		for(Token t : tokenList1) {
			try {
				if(t.getCoveredText().equals("aus")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("us")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("witch")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("this")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}			
				if(t.getCoveredText().equals("hole")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("day")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("One")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Advantage")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("that")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Children")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("in")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("volved")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("On")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("argument")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("electronic")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("devises")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("film")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("than")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("parents")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("loose")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("for")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("christmas")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("Some")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Advertisement")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}				
				if(t.getCoveredText().equals("age")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("form")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("if")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("adds")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("everyday")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("live")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("Witch")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("results")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("many")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("tings")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("positiv")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("site")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("Young")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Children")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				
				if(t.getCoveredText().equals("cereals")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("to")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("their")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("to")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("very")&&tokenList1.get(tokenList1.indexOf(t)-1).getCoveredText().equals("an")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)-1).getBegin());
				}
				
				if(t.getCoveredText().equals("to")&&tokenList1.get(tokenList1.indexOf(t)-1).getCoveredText().equals("around")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}		
				
				if(t.getCoveredText().equals("now")&&tokenList1.get(tokenList1.indexOf(t)-1).getCoveredText().equals("see")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("b")&&tokenList1.get(tokenList1.indexOf(t)-1).getCoveredText().equals("not")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("An")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Aspect")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("time")&&tokenList1.get(tokenList1.indexOf(t)-1).getCoveredText().equals("hole")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)-1).getBegin());
				}
				if(t.getCoveredText().equals("electronical")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("devises")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				
				if(t.getCoveredText().equals("there")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("parents")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("todays")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("society")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("there")&&tokenList1.get(tokenList1.indexOf(t)-1).getCoveredText().equals("for")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("cause")&&tokenList1.get(tokenList1.indexOf(t)-1).getCoveredText().equals("Of")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)-1).getBegin());
				}
				if(t.getCoveredText().equals("parents")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("challange")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				
				if(t.getCoveredText().equals("there")&&tokenList1.get(tokenList1.indexOf(t)-1).getCoveredText().equals("show")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("an")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("adults")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("teens")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("attention")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("adults")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("opinions")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("todays")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("population")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("or")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("How")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("todays")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("children")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals(":")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Children")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("todays")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("life")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				
				if(t.getCoveredText().equals("wont")&&tokenList1.get(tokenList1.indexOf(t)-1).getCoveredText().equals("children")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("the")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Television")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("a")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("scream")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("opinion")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Television")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("or")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Television")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}	
				//ADNew 12/10
				if(t.getCoveredText().equals("be")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("aloud")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("a")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("childs")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}	
				if(t.getCoveredText().equals("todays")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("adults")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}	
				if(t.getCoveredText().equals("reducted")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("prizes")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}	
				if(t.getCoveredText().equals("the")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("be")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("can")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("crab")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}	
				if(t.getCoveredText().equals("new")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Castle")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("Lego")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Company")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("Lego")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Castle")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("need")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("TV")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("'s")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+2).getBegin());
				}
				if(t.getCoveredText().equals("todays")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("households")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("With")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Television")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("In")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Television")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("to")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("boring")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("to")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Penguin")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("this")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("add")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("the")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("once")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("some")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("extend")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("real")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("live")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("kids")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("eyes")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("todays")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("tv")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("todays")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("kids")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("parent")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("'s")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("job")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("different")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("sites")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("to")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("naive")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("Every")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("view")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("parents")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("heads")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("alarm")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("in")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("\"")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("try")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("to")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("salty")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("to")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("fatty")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("too")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("so")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("these")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("adds")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("These")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("adds")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("TV")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("adds")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("the")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("a")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("we")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("thing")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("the")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("see")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("there")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("toys")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("there")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("parants")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("there")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("advertisment")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("there")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("development")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("there")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("movies")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("is")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("influences")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("small")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("one")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("'s")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+2).getBegin());
				}
				if(t.getCoveredText().equals("they")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("sop")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("social")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("live")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("the")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("thrash")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("television")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("adds")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("no")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("so")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("when")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("the")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("play")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("where")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("the")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("are")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("in")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("live")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("childrens")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("eyes")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("tell")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("heir")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("I")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("already")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("sad")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+2).getBegin());
				}
				if(t.getCoveredText().equals("An")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("Arguments")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("childs")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("attention")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("parents")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("responsability")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("run")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("it")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("'s")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+2).getBegin());
				}
				if(t.getCoveredText().equals("the")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("instantly")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("want")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+2).getBegin());
				}
				if(t.getCoveredText().equals("way")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("to")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("early")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("what")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("so")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("ever")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("childrens")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("channels")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("childrens")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("show")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("childrens")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("communication")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("witch")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("has")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("whit")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("other")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("parent")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("'s")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("money")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("parent")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("'s")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("fault")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("parent")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("'s")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("disability")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("it")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("'s")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("best")&&tokenList1.get(tokenList1.indexOf(t)+3).getCoveredText().equals("friend")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("his")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("o")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("about")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("live")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("you")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("red")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("sees")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("and")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("advertising")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("toy")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("than")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("a")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("parents")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("decision")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("these")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("advertisings")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("it")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+2).getBegin());
				}
				if(t.getCoveredText().equals("I")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("thing")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("consume")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("ones")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("to")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("by")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("a")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("buy")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("it")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("ore")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+2).getBegin());
				}
				if(t.getCoveredText().equals("they")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("sat")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("them")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("are")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("boy")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("and")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("So")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("they")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("loose")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+2).getBegin());
				}
				if(t.getCoveredText().equals("are")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("to")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("less")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("in")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("his")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("live")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+2).getBegin());
				}
				if(t.getCoveredText().toLowerCase().equals("they")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("seat")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("lay")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("to")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("often")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().toLowerCase().equals("a")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("other")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().equals("parenting")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("get")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("'s")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+2).getBegin());
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().equals("are")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("to")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("easily")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().toLowerCase().equals("important")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("rule")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().toLowerCase().equals("parents")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("job")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().toLowerCase().equals("parents")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("task")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().toLowerCase().equals("peoples")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("attention")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)).getBegin());
				}
				if(t.getCoveredText().toLowerCase().equals("to")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals("bang")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("advertising")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+1).getBegin());
				}
				if(t.getCoveredText().toLowerCase().equals("movies")&&tokenList1.get(tokenList1.indexOf(t)+1).getCoveredText().equals(",")&&tokenList1.get(tokenList1.indexOf(t)+2).getCoveredText().equals("then")) {
					listOfFehlerOffset.add(tokenList1.get(tokenList1.indexOf(t)+2).getBegin());
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
				
			
		}
		for (Token t: tokens) {
			// after punctuation must upper case
			if(offsetOfPunctuation.contains(t.getBegin()-2)) {
				
				if(Character.isLowerCase(t.getCoveredText().charAt(0))){
					Structure s = new Structure(aJCas);
					s.setBegin(t.getBegin());
					s.setEnd(t.getEnd());
					s.setName("spelling mistake");
					s.addToIndexes();
				}	
			}
			if(singleWordRS.contains(t.getCoveredText())) {
				Structure s = new Structure(aJCas);
				s.setBegin(t.getBegin());
				s.setEnd(t.getEnd());
				s.setName("spelling mistake");
				s.addToIndexes();
			}
			if(listOfFehlerOffset.contains(t.getBegin())) {
				Structure s = new Structure(aJCas);
				s.setBegin(t.getBegin());
				s.setEnd(t.getEnd());
				s.setName("spelling mistake");
				s.addToIndexes();
			}
		}
		
		
		
	}
	static Set<String> readList(String listFilePath) throws IOException {
		
		Set<String> listSet = new HashSet<String>();
		for (String line : FileUtils.readLines(new File(listFilePath), "UTF-8")) {
			listSet.add(line);
		}
		return listSet;
	}
}
