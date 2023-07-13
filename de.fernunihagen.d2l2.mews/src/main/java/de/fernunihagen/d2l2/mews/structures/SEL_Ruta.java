package de.fernunihagen.d2l2.mews.structures;

import java.io.IOException;
import java.net.URISyntaxException;

import org.antlr.runtime.RecognitionException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.lift.api.StructureExtractor;

import de.fernunihagen.d2l2.mews.structure.ruta.RutaUtil;


public abstract class SEL_Ruta 
	extends StructureExtractor
{

	protected AnalysisEngine getRutaEngine(String script) 
			throws AnalysisEngineProcessException
	{
		try {
			return UIMAFramework.produceAnalysisEngine(
					RutaUtil.initRutaFE(script).getKey()
			);

		} catch (InvalidXMLException | ResourceInitializationException | IOException | RecognitionException
				| URISyntaxException e) {
			throw new AnalysisEngineProcessException();
		}
	}
}
