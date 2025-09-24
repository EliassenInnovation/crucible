package com.eliassen.crucible.frameworkbrowser.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eliassen.crucible.common.helpers.JsonHelper;

import com.eliassen.crucible.frameworkbrowser.shared.Parser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParserHelper
{
    public static Parser[] getParsers()
    {
        return getParsers("parsers.json");
    }
    public static Parser[] getParsers(String parserFileName)
    {
        Parser[] parsers;

        ObjectMapper mapper = new ObjectMapper();
        JSONObject parserJson = JsonHelper.getJSONFileContent(parserFileName);
        JSONArray parsersArray = parserJson.getJSONArray("parsers");
        List<Parser> parserList = new ArrayList<>();

        for(int x = 0; x < parsersArray.length(); x++)
        {
            try
            {
                Parser parser = mapper.readValue(parsersArray.getJSONObject(x).toString(), Parser.class);
                if(parser.isEnabled())
                {
                    parserList.add(parser);
                }
            }
            catch (JSONException | JsonProcessingException e)
            {
                throw new RuntimeException("Could not parse a parser!");
            }
        }

        parsers = parserList.toArray(new Parser[parserList.size()]);
        Arrays.sort(parsers);

        return parsers;
    }
}
