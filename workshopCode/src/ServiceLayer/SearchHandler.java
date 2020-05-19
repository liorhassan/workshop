package ServiceLayer;

import DomainLayer.TradingSystem.SystemFacade;
import DomainLayer.TradingSystem.SystemLogger;
import com.softcorporation.suggester.BasicSuggester;
import com.softcorporation.suggester.Suggestion;
import com.softcorporation.suggester.dictionary.BasicDictionary;
import com.softcorporation.suggester.util.BasicSuggesterConfiguration;
import com.softcorporation.suggester.util.SuggesterException;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchHandler {

    public String searchProduct(String name, String category, String[] keywords) {
        SystemLogger.getInstance().writeEvent(String.format("Search product command: name - %s, category - %s, keywords - %s", argToString(name), argToString(category), argArrayToString(keywords)));
        try {

            // create dictionary for spell checking
            List<String> localDictionary = SystemFacade.getInstance().getProductsNamesAndKeywords();
            if(name!=null)
                name = spellcheckAndCorrect(name,localDictionary);
            if(keywords!=null) {
                for (int i = 0; i < keywords.length; i++)
                    keywords[i] = spellcheckAndCorrect(keywords[i], localDictionary);
            }
            String[] args = {name, category, argArrayToString(keywords)};
            if (SystemFacade.getInstance().allEmptyString(args))
                throw new IllegalArgumentException("Must enter search parameter");
            return SystemFacade.getInstance().searchProducts(name, category, keywords);
        } catch (RuntimeException e) {
            SystemLogger.getInstance().writeError("Search product error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return e.getMessage();
        }
    }

    public String filterResults(Integer minPrice, Integer maxPrice, String category) {
        SystemLogger.getInstance().writeError(String.format("Filter results command: minPrice - %d, maxPrice - %d, category - %s", minPrice, maxPrice, argToString(category)));
        try {
            return SystemFacade.getInstance().filterResults(minPrice, maxPrice, category);
        } catch (Exception e) {
            SystemLogger.getInstance().writeError("Filter results error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return e.getMessage();
        }
    }

    private String argArrayToString(String[] arg) {
        return arg != null ? String.join(",", arg) : "";
    }

    private String argToString(String arg) {
        return arg != null ? arg : "";
    }

    public String spellcheckAndCorrect(String searchInput, List<String> localDictionary) {
        if (localDictionary.contains(searchInput))
            return searchInput;

        String language = "en";
        String dictFileName = "file://./dictionary/english.jar";
        try {
            BasicDictionary dictionary = new BasicDictionary(dictFileName);
            BasicSuggesterConfiguration configuration = new BasicSuggesterConfiguration("file://./dictionary/basicSuggester.config");

            BasicSuggester suggester = new BasicSuggester(configuration);
            suggester.attach(dictionary);

            ArrayList suggestions = suggester.getSuggestions(searchInput, 10, language);

            // go over suggestions and pick one that belongs to local dictionary
            for (int j = 0; j < suggestions.size(); j++) {
                Suggestion suggestion = (Suggestion) suggestions.get(j);
                if (localDictionary.contains(suggestion.getWord()))
                    return suggestion.getWord();
            }
        } catch (SuggesterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
