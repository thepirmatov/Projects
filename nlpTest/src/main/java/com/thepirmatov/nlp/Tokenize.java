package com.thepirmatov.nlp;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.xpath.objects.XString;

import java.util.List;

public class Tokenize {

    public static void main(String[] args) {
       StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();

        String text = "Hello there! This is a simple text for NLP test.";

        CoreDocument coreDocument = new CoreDocument(text);

        stanfordCoreNLP.annotate(coreDocument);

        List<CoreLabel> coreLabelList = coreDocument.tokens();

        for(CoreLabel coreLabel :  coreLabelList){

            System.out.println(coreLabel.originalText());
        }
    }
}
