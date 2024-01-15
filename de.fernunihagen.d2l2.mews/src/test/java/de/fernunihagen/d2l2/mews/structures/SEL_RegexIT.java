package de.fernunihagen.d2l2.mews.structures;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lift.api.Feature;

import de.fernunihagen.d2l2.features.util.FeatureTestUtil;
import de.fernunihagen.d2l2.mews.featureSettings.FEL_GenericStructureCounter;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class SEL_RegexIT {

	@Test
	public void SELRegex_test()
		throws Exception
	{
		
		AnalysisEngine engine = createEngine(
			SEL_Regex.class,
			SEL_Regex.PARAM_REGEXP, ","
		);
		
		
        JCas jcas = engine.newJCas();
        engine.process(jcas);
        jcas.setDocumentText("test ,");

        Token t1 = new Token(jcas, 0, 4);
        t1.addToIndexes();
        
        Token t2 = new Token(jcas, 5, 6);
        t2.addToIndexes();
        
		engine.process(jcas);
		
		FEL_GenericStructureCounter extractor = new FEL_GenericStructureCounter("RegExp_,");
		Set<Feature> features = new HashSet<Feature>(extractor.extract(jcas));
		
		Assertions.assertAll(
        		() -> assertEquals(1, features.size()),
        		() -> FeatureTestUtil.assertFeature("FN_" + extractor.getInternalName(), 0.5, features.iterator().next(), 0.0001)
        );
	}
	
}
