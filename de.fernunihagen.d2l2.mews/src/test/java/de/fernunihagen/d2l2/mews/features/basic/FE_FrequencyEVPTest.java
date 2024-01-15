package de.fernunihagen.d2l2.mews.features.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lift.api.Feature;

import de.fernunihagen.d2l2.features.util.FeatureTestUtil;
import de.fernunihagen.d2l2.mews.core.MewsTestBase;
import de.fernunihagen.d2l2.mews.core.MewsTestBase.ParserType;
import de.fernunihagen.d2l2.mews.structures.SE_FrequencyEVP;


public class FE_FrequencyEVPTest {
	
	final UimaContext context = null;
	
	@Test
	public void frequencyBandRatioTest()
		throws Exception
	{
		AnalysisEngine engine = MewsTestBase.getPreprocessingEngine("en",ParserType.noParser);
		JCas jcas = engine.newJCas();		
		jcas.setDocumentLanguage("en");
		jcas.setDocumentText("The test is ongoing");
		engine.process(jcas);
		SE_FrequencyEVP se = new SE_FrequencyEVP();
		se.initialize(null);
		se.process(jcas);
		
		
		FE_FrequencyEVP fe = new FE_FrequencyEVP();
		Set<Feature> features = fe.extract(jcas);
		Assertions.assertAll(
		        () -> assertEquals(6, features.size()),
		        () -> FeatureTestUtil.assertFeature("C2Ratio", 0.25, features.iterator().next(), 0.00001)
				);
	}
}
