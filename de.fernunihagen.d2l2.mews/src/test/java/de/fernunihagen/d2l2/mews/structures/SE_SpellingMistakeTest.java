package de.fernunihagen.d2l2.mews.structures;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.corenlp.CoreNlpSegmenter;
import org.dkpro.core.languagetool.LanguageToolChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lift.type.Structure;


public class SE_SpellingMistakeTest {
	
	@Test
	public void sE_FrequenciesTest() throws Exception {
		
		AnalysisEngineDescription segmenter = createEngineDescription(
				CoreNlpSegmenter.class,
				CoreNlpSegmenter.PARAM_LANGUAGE, "en"
		);
		AnalysisEngineDescription languageTool= createEngineDescription(
	             LanguageToolChecker.class,
	             LanguageToolChecker.PARAM_LANGUAGE,"en"              
	            );
		AnalysisEngineDescription description = createEngineDescription(segmenter,languageTool);
		AnalysisEngine engine = createEngine(description);
		JCas jcas = engine.newJCas();		
		jcas.setDocumentLanguage("en");
		jcas.setDocumentText("The test is acceptible");
		engine.process(jcas);
		
		SE_SpellingMistake se = new SE_SpellingMistake();
		se.process(jcas);
		
		for(Structure s : JCasUtil.select(jcas, Structure.class)) {
			if(s.getName().equals("spelling mistake"))
			System.out.println(s.toString());
		}
		Collection<Structure> structures = JCasUtil.select(jcas, Structure.class);
		Assertions.assertAll(
		        () -> assertEquals(1, structures.size()),
		        () -> assertEquals("spelling mistake", structures.iterator().next().getName())
				);
		
	}

}
