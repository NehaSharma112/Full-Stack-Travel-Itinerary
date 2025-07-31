package com.travel.itinerary.travelItinerary.Services;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
@Service
public class TextProcessingService {
    private final Analyzer analyzer;//sets the rule and prvides tokenStream

    //constructor in which we will define rules what needs to be done
    public TextProcessingService(){
        analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String s) {

                StandardTokenizer tokenizer = new StandardTokenizer();//splits the sentence into words
                TokenStream filter = new LowerCaseFilter(tokenizer);//converts in lower case
                filter = new PorterStemFilter(filter);//converts words into its root word
                return new TokenStreamComponents(tokenizer,filter);//informs the analyzer that which tokenizer and filter has to be used
            }
        };
    }

    //returns analyzed text in terms of list
    public List<String> analyzeText(String text) throws IOException{
        List<String> tokens = new ArrayList<>();
//no catch block because exception is handled in method signature
        //analyzer.tokenStream provides token stream which are stemmed & in lowercase
        //token is complex object not a string there need to add property of CharTermAttribute
        try(TokenStream tokenStream = analyzer.tokenStream("", new StringReader(text))){
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);// adds CharTermAttribute prop to token stream
            tokenStream.reset();//starting the tokenStream

            while(tokenStream.incrementToken()){
                String token = charTermAttribute.toString();//gives each token as a string
                if(!token.trim().isEmpty()){
                    tokens.add(token);
                }
            }
            tokenStream.end();
        }
        return tokens;
    }

    public String processText(String text){
        if(text==null || text.trim().isEmpty())return "";
        try{
            List<String> processedText = analyzeText(text);
            return String.join(" ",processedText);//converts analyze list of string into sentence
        }catch(Exception e){
            return text.toLowerCase();
        }
    }



}
