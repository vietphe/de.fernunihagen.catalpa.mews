package de.fernunihagen.d2l2.mews.features.basic;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.jcas.JCas;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lift.api.Feature;

import de.fernunihagen.d2l2.features.util.FeatureTestUtil;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class FE_CommaRatioTest {

	@Test
	public void nrOfCommasTest()
		throws Exception
	{
		
		AnalysisEngine engine = createEngine(NoOpAnnotator.class);

        JCas jcas = engine.newJCas();
        engine.process(jcas);
        jcas.setDocumentText("test ,");

        Token t1 = new Token(jcas, 0, 4);
        t1.addToIndexes();
        
        Token t2 = new Token(jcas, 5, 6);
        t2.addToIndexes();
        
		FE_CommaRatio fe = new FE_CommaRatio();
		Set<Feature> features = fe.extract(jcas);
		
		Assertions.assertAll(
		        () -> assertEquals(1, features.size()),
		        () -> FeatureTestUtil.assertFeature("CommaRatio", 0.5, features.iterator().next(), 0.00001)
				);
	}
	
}
