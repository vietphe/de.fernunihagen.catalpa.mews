package de.fernunihagen.d2l2.mews;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.io.xmi.XmiWriter;
import org.lift.api.Configuration.Language;

import de.fernunihagen.d2l2.mews.io.AsapEssayReader;
import de.fernunihagen.d2l2.mews.io.AsapEssayReader.RatingBias;
import de.fernunihagen.d2l2.mews.io.TextReader;
import de.fernunihagen.d2l2.mews.structures.SE_Compound;
import de.fernunihagen.d2l2.mews.structures.SE_Connectives;
import de.fernunihagen.d2l2.mews.structures.SE_DLTIntegrationCost;
import de.fernunihagen.d2l2.mews.structures.SE_DiscourseReferent;
import de.fernunihagen.d2l2.mews.structures.SE_FiniteVerb;
import de.fernunihagen.d2l2.mews.structures.SE_FrequenciesDeReWo;
import de.fernunihagen.d2l2.mews.structures.SE_FrequenciesGoogleWF;
import de.fernunihagen.d2l2.mews.structures.SE_FrequencyEVP;
import de.fernunihagen.d2l2.mews.structures.SE_LanguageTool2;
import de.fernunihagen.d2l2.mews.structures.SE_SpellingMistake;



public class BaseExperiment {

	public static void main(String[] args) 
			throws Exception
	{
		String inputPath = "D:\\HiWi\\LiFT\\MEWS\\AD";
//		runTextExample("src/test/resources/txt/essay_en.txt", Language.English);
		runTextExample(inputPath, Language.English);
//		runTextExample("src/test/resources/txt/news_de.txt", Language.German);
	}
		
	private static void runTextExample(String inputPath, Language language) throws ResourceInitializationException, UIMAException, IOException {
		PreprocessingConfiguration config = new PreprocessingConfiguration(language);
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
				TextReader.class, TextReader.PARAM_INPUT_FILE,inputPath, TextReader.PARAM_LANGUAGE,"en" );
	
//		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(AsapEssayReader.class,
//			AsapEssayReader.PARAM_TARGET_LABEL, "score",
//				AsapEssayReader.PARAM_RATING_BIAS, RatingBias.low, AsapEssayReader.PARAM_DO_SPARSECLASSMERGING, false,
//				AsapEssayReader.PARAM_DO_NORMALIZATION, false, AsapEssayReader.PARAM_INPUT_FILE, "D:\\HiWi\\LiFT\\Corpus\\ASAP-aes\\training_set_rel3.tsv");

		AnalysisEngineDescription prepro = config.getUimaEngineDescription();
		AnalysisEngineDescription frequencyEVP = createEngineDescription(SE_FrequencyEVP.class);
		AnalysisEngineDescription connectivies = createEngineDescription(SE_Connectives.class, SE_Connectives.PARAM_LANGUAGE, "en",
				SE_Connectives.PARAM_USE_LEMMAS, false);
		AnalysisEngineDescription spellingMistake = createEngineDescription(SE_SpellingMistake.class);
		AnalysisEngineDescription discourseReferents = createEngineDescription(SE_DiscourseReferent.class, SE_DiscourseReferent.PARAM_LANGUAGE, "en");
		AnalysisEngineDescription finiteVerbs = createEngineDescription(SE_FiniteVerb.class, SE_FiniteVerb.PARAM_LANGUAGE, "en");
		AnalysisEngineDescription errors = createEngineDescription(SE_LanguageTool2.class);
		AnalysisEngineDescription analyzer = createEngineDescription(Analyzer.class);
		AnalysisEngineDescription levenshteinAnalyzer = createEngineDescription(LevenshteinDistanceAnalyzer.class);
		AnalysisEngineDescription xmiWriter = createEngineDescription(
				XmiWriter.class, 
				XmiWriter.PARAM_OVERWRITE, true,
				XmiWriter.PARAM_TARGET_LOCATION, "target/cas"
		);
		
		SimplePipeline.runPipeline(reader, 
				prepro,
//				errors,
//				frequencyEVP,
//				connectivies,
//				spellingMistake,
//				discourseReferents,
//				finiteVerbs,
//				analyzer,
				levenshteinAnalyzer
//				xmiWriter
		);
	}
}