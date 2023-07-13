package de.fernunihagen.d2l2.mews;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.featurepath.FeaturePathException;
import org.dkpro.core.api.featurepath.FeaturePathFactory;
import org.lift.api.Feature;
import org.lift.api.FeatureType;
import org.lift.api.LiftFeatureExtrationException;
import org.lift.type.FeatureAnnotationNumeric;

import de.fernunihagen.d2l2.mews.io.CasFeatureFileWriter;
import de.fernunihagen.d2l2.mews.io.FeatureCSVFileWriter;
import de.fernunihagen.d2l2.mews.io.FeatureSetBuilder;
import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.GrammarAnomaly;
import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SpellingAnomaly;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Compound;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.NC;

public class Analyzer extends JCasAnnotator_ImplBase {
	static StringBuilder sb;
	static String firstColumn;
	ArrayList<Map<String,String>> featureList;
//	ArrayList<String> arnomalyList;
	Map<String,String> arnomalyMap;
	Map<String,String> arnomalyMap2;
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		
		super.initialize(context);
		sb = new StringBuilder();
		featureList = new ArrayList<>();
//		arnomalyList = new ArrayList<>();
		arnomalyMap= new HashMap<>();
		arnomalyMap2= new HashMap<>();
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {		
		String id = "no Id";
		Collection<Token> tokens = JCasUtil.select(aJCas, Token.class);
//		int punct = 0;
//		for (Token t : tokens) {
//			if (t.getPos().getCoarseValue().equals("PUNCT")) {
//				punct++;
//			};
//		}
//		int numberOfWord = tokens.size()-punct;
		
		if (JCasUtil.exists(aJCas, DocumentMetaData.class)){
			DocumentMetaData meta = JCasUtil.selectSingle(aJCas, DocumentMetaData.class);
			id = meta.getDocumentId();
		}
		System.out.println(id);			
		Collection<GrammarAnomaly> gas = JCasUtil.select(aJCas, GrammarAnomaly.class);
		for (GrammarAnomaly g : gas) {
//			System.out.println(g.getDescription()+" - "+g.getCoveredText()+" - "+g.getBegin());
//			grammarAnomaly.put(g.getBegin()+"-"+g.getEnd(), g.getDescription());
//			gramm.add(g.getDescription());
			String desciption = g.getDescription();
			if(desciption.contains("Use a smart opening quote here.")||desciption.contains("Use a smart closing quote here.")) {
				continue;
			}
			desciption= desciption.replaceAll(",", "(comma)");
			desciption= desciption.replaceAll(";", "(semicolon)");
			String example = g.getCoveredText();
//			example= example.replaceAll(",", "(comma)");
//			example= example.replaceAll(";", "(semicolon)");
//			example= example.replaceAll("\"", "(quotesymbol)");
			String strBefore="";
			String strAfter = "";
			if(g.getBegin()<=80) {
				strBefore= aJCas.getDocumentText().substring(0,g.getBegin())+"*"+example+"*";
			}else {
				strBefore = "..." +aJCas.getDocumentText().substring(g.getBegin()-80,g.getBegin())+"*"+example+"*";
			}
			if(aJCas.getDocumentText().length()-g.getEnd()<=80) {
				strAfter = aJCas.getDocumentText().substring(g.getEnd(), aJCas.getDocumentText().length());
			}else {
				strAfter = aJCas.getDocumentText().substring(g.getEnd(), g.getEnd()+80)+"...";
			}
			
			String offset = "("+g.getBegin()+":"+g.getEnd()+")";
			String exampleSentence = strBefore+strAfter;
			exampleSentence= exampleSentence.replaceAll(",", "(comma)");
			exampleSentence= exampleSentence.replaceAll(";", "(semicolon)");
			exampleSentence= exampleSentence.replaceAll("\"", "(quotesymbol)");
			System.out.println(exampleSentence);
			arnomalyMap.put(desciption, exampleSentence);
			arnomalyMap2.put(desciption, exampleSentence+" "+offset+" "+id);
//			arnomalyList.add(desciption);
			
		}
		
//		try {
//			Set<Feature> fes = FeatureSetBuilder.buildFeatureSet(aJCas);
//			//add Annotation type
//			for (Feature f : fes) {
//				String name = f.getName();
//				FeatureType featureType = f.getType();
//				Object value = f.getValue();
////				System.out.println(name+": "+value.toString());
//				FeatureAnnotationNumeric fa = new FeatureAnnotationNumeric(aJCas, 0, 0);
//				fa.setName(name);
//				fa.setValue(Double.valueOf(value.toString()));
//				fa.addToIndexes();
//			}
//		
//		} catch (LiftFeatureExtrationException e) {
//			
//			e.printStackTrace();
//		}
////		writeCSVFile
//		try {
//			Set<Feature> fes = FeatureSetBuilder.buildFeatureSet(aJCas);
//			Map<String,String> featureMap = new HashMap<>();
//			featureMap.put("textId", id);
//			featureMap.put("length",String.valueOf(tokens.size()));
//			for (Feature feature : fes) {
////				System.out.println(feature.getName()+feature.getValue().toString());
//				featureMap.put(feature.getName(), feature.getValue().toString()); //TODO: check casting type
//			}
//			featureList.add(featureMap);
//		} catch (LiftFeatureExtrationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
	}
	@Override
	public void destroy() {
		
		try {
			exportGrammarAnormalyMap("D:\\HiWi\\LiFT\\output\\LoiSaiAD.csv", arnomalyMap2);
//			exportGrammarAnormalyMap("D:\\HiWi\\LiFT\\output\\ErrorTE2.csv", arnomalyMap2);
//			exportGrammarAnormaly("D:\\HiWi\\LiFT\\output\\AllErrorTE.csv", arnomalyList);
//			exportCSVFile("D:\\HiWi\\LiFT\\output\\SpellingADFinal.csv", featureList);
		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
	public static void exportCSVFile(String filePath, ArrayList<Map<String,String>> list) throws FileNotFoundException {
		StringBuilder builder = new StringBuilder();
		StringBuilder label = new StringBuilder();
		ArrayList<String> arrLabel = new ArrayList<>();
		
		Map<String,String> map1 = list.get(0);
		for (Map.Entry<String, String> entry : map1.entrySet()) {
			String key = entry.getKey();
//			System.out.println(key);
			label.append(key);
			label.append(",");
			arrLabel.add(key);
		}
		builder.append(label.toString());
		builder.append("\n");
		for (Map<String,String> map: list) {
			for (String s : arrLabel) {
				builder.append(map.get(s));
				builder.append(",");
			}
			builder.append("\n");
			
		}
		PrintWriter pw = new PrintWriter(new File(filePath));
		pw.write(builder.toString());
		pw.close();
		
	}
	public static void exportGrammarAnormaly(String filePath, ArrayList<String> list)throws FileNotFoundException {
		StringBuilder builder = new StringBuilder();
		for (String s : list) {
			builder.append(s);
			builder.append("\n");
		}
		PrintWriter pw = new PrintWriter(new File(filePath));
		pw.write(builder.toString());
		pw.close();
	}
	public static void exportGrammarAnormalyMap(String filePath, Map<String,String> map)throws FileNotFoundException {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
			builder.append(key);
			builder.append(";");
			builder.append(val);
			builder.append("\n");
			
			
		}
		PrintWriter pw = new PrintWriter(new File(filePath));
		pw.write(builder.toString());
		pw.close();
	}


}
