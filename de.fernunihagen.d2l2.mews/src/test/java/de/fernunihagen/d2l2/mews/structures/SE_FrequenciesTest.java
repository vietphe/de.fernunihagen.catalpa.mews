package de.fernunihagen.d2l2.mews.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lift.type.Frequency;

import de.fernunihagen.d2l2.features.util.FeatureTestUtil;
import de.fernunihagen.d2l2.mews.core.MewsTestBase;
import de.fernunihagen.d2l2.mews.core.MewsTestBase.ParserType;

public class SE_FrequenciesTest {
	
	@Test
	public void sE_FrequenciesTest() throws Exception {
		
		AnalysisEngine engine = MewsTestBase.getPreprocessingEngine("en",ParserType.noParser);
		JCas jcas = engine.newJCas();		
		jcas.setDocumentLanguage("en");
		jcas.setDocumentText("The test is ongoing");
		engine.process(jcas);
		
		SE_FrequencyEVP se = new SE_FrequencyEVP();
		se.initialize(null);
		se.process(jcas);
		
		for(Frequency s : JCasUtil.select(jcas, Frequency.class)) {
			System.out.println(s.toString());
		}
		Collection<Frequency> frequencies = JCasUtil.select(jcas, Frequency.class);
		Assertions.assertAll(
		        () -> assertEquals(4, frequencies.size()),
		        () -> assertEquals("A1", frequencies.iterator().next().getFrequencyBand())
				);
		
	}

}
