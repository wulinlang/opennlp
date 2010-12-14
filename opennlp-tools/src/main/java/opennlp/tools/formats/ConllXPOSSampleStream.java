package opennlp.tools.formats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSSample;
import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.ParagraphStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * Parses the data from the CONLL 06 shared task into POS Samples.
 * <p>
 * More information about the data format can be found here:<br>
 * http://www.cnts.ua.ac.be/conll2006/
 * <p>
 * <b>Note:</b> Do not use this class, internal use only!
 */
public class ConllXPOSSampleStream extends FilterObjectStream<String, POSSample> {

  public ConllXPOSSampleStream(ObjectStream<String> lineStream) {
    super(new ParagraphStream(lineStream));
  }
  
  ConllXPOSSampleStream(Reader in) throws IOException {
    super(new ParagraphStream(new PlainTextByLineStream(in)));
  }

  public POSSample read() throws IOException {

    // The CONLL-X data has a word per line and each line is tab separated
    // in the following format:
    // ID, FORM, LEMMA, CPOSTAG, POSTAG, ... (max 10 fields)
     
    // One paragraph contains a whole sentence and, the token
    // and tag will be read from the FORM and POSTAG field.
    
   String paragraph = samples.read();
   
   POSSample sample = null;
   
   if (paragraph != null) {
     
     // paragraph get lines
     BufferedReader reader = new BufferedReader(new StringReader(paragraph));
     
     List<String> tokens = new ArrayList<String>(100);
     List<String> tags = new ArrayList<String>(100);
     
     String line;
     while ((line = reader.readLine())  != null) {
     
       final int minNumberOfFields = 5;
       
       String parts[] = line.split("\t");
       
       if (parts.length >= minNumberOfFields) {
         tokens.add(parts[1]);
         tags.add(parts[4]);
       }
       else {
         throw new InvalidFormatException("Every non-empty line must have at least " +
             minNumberOfFields + " fields!");
       }
     }
     
     // just skip empty samples and read next sample
     if (tokens.size() == 0)
       sample = read();
       
     sample = new POSSample(tokens.toArray(new String[tokens.size()]), tags.toArray(new String[tags.size()]));
   }
   
   return sample;
  }
}
