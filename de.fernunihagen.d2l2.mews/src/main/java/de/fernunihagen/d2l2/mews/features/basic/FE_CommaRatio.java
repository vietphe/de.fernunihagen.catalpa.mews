package de.fernunihagen.d2l2.mews.features.basic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import org.apache.uima.fit.descriptor.LanguageCapability;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.lift.api.Feature;
import org.lift.api.FeatureType;
import org.lift.api.LiftFeatureExtrationException;

import de.fernunihagen.d2l2.mews.featureSettings.FeatureExtractor_ImplBase;


/**
 * Counts the number of commas
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"})
@LanguageCapability({ "de","en" })
public class FE_CommaRatio 
	extends FeatureExtractor_ImplBase
{
	
	
	@Override
	public Set<Feature> extract(JCas jcas) 
			throws LiftFeatureExtrationException
	{		
		int nrOfParagraphs = 0;
		String filePath = "path/to/your/file.txt"; // replace with the actual path of your file

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int numParagraphs = 0;
            boolean inParagraph = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    if (inParagraph) {
                        numParagraphs++;
                        inParagraph = false;
                    }
                } else {
                    inParagraph = true;
                }
            }

            // Check if the last line was part of a paragraph
            if (inParagraph) {
                numParagraphs++;
            }

            System.out.println("Total number of paragraphs: " + numParagraphs);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return new Feature("NrOfParagraphs",nrOfParagraphs, FeatureType.NUMERIC).asSet();
		
	}

	@Override
	public String getPublicName() {
		return "CommaRatio";
	}
}

