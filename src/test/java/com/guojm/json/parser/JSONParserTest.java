package com.guojm.json.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guojm.json.JSONArray;
import com.guojm.json.JSONObject;
import com.guojm.json.tokenizer.StringSource;
import com.guojm.json.tokenizer.Tokenizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class JSONParserTest {

    @Test
    public void simpleJsonTest(){
        String json = """
                {
                  "name": "chat_toys",
                  "version": "0.1.0",
                  "dependencies": {
                    "@emotion/react": "^11.10.6",
                    "@emotion/styled": "^11.10.6",
                    "@mui/icons-material": "^5.11.11",
                    "@mui/material": "^5.11.15",
                    "@reduxjs/toolkit": "^1.9.3",
                    "@types/react-redux": "^7.1.25",
                    "lodash": "^4.17.21",
                    "react": "^18.2.0",
                    "react-dom": "^18.2.0",
                    "react-markdown": "^8.0.6",
                    "react-redux": "^8.0.5",
                    "react-router": "^6.10.0",
                    "react-router-dom": "^6.10.0",
                    "react-scripts": "5.0.1",
                    "typescript": "^4.4.4",
                    "web-vitals": "^2.1.0"
                  },
                  "scripts": {
                    "start": "react-scripts start",
                    "build": "react-scripts build",
                    "test": "react-scripts test",
                    "eject": "react-scripts eject"
                  },
                  "eslintConfig": {
                    "extends": [
                      "react-app",
                      "react-app/jest"
                    ]
                  },
                  "browserslist": {
                    "production": [
                      ">0.2%",
                      "not dead",
                      "not op_mini all"
                    ],
                    "development": [
                      "last 1 chrome version",
                      "last 1 firefox version",
                      "last 1 safari version"
                    ]
                  },
                  "devDependencies": {
                    "@types/jest": "^27.0.1",
                    "@types/lodash": "^4.14.191",
                    "@types/node": "^16.7.13",
                    "@types/react": "^18.0.0",
                    "@types/react-dom": "^18.0.0",
                    "@typescript-eslint/eslint-plugin": "^5.57.1",
                    "@typescript-eslint/parser": "^5.0.1",
                    "eslint": "^8.7.0",
                    "eslint-plugin-react": "^7.32.2",
                    "prettier": "^2.5.1"
                  }
                }
                """;

        var tokenList = new Tokenizer(new StringSource(json)).scan();
        var object = new JSONParser(tokenList).parse();
        Assertions.assertInstanceOf(JSONObject.class,object);
        System.out.println(object);
    }

    static record Detail(
            int age,
            boolean sex,
            double salary
    ){}

    static record Data(
         String name,
         Detail detail,
         Map<String,Object> extra
    ){};

    @Test
    public void jsonObjectTest() throws JsonProcessingException {
        var data = new Data(
                "guojm",
                new Detail(10,false,1.1e5),
                null
        );
        var json = new ObjectMapper().writeValueAsString(data);
        var tokenList = new Tokenizer(new StringSource(json)).scan();
        var object = new JSONParser(tokenList).parse();
        Assertions.assertInstanceOf(JSONObject.class,object);
        var jsonObject = (JSONObject)object;
        Assertions.assertNull(jsonObject.get("extra"));
        Assertions.assertEquals("guojm",jsonObject.getString("name"));

        Assertions.assertInstanceOf(JSONObject.class,jsonObject.get("detail"));
        var detailObject = jsonObject.getJSONObject("detail");
        Assertions.assertEquals(10,detailObject.getInt("age"));
        Assertions.assertFalse(detailObject.getBoolean("sex"));
        Assertions.assertEquals(1.1e5,detailObject.getDouble("salary"));
    }

    @Test
    public void arrayTest(){
        var json = """
                [{"a":0},{"a":1},{"a":2},{"a":3}]
                """;
        var tokenList = new Tokenizer(new StringSource(json)).scan();
        var object = new JSONParser(tokenList).parse();
        Assertions.assertInstanceOf(JSONArray.class,object);
        if (object instanceof JSONArray array){
            Assertions.assertEquals(4,array.size());
            for (int i = 0; i < array.size(); i++) {
                Assertions.assertInstanceOf(JSONObject.class,array.get(i));
                var jsonObject = (JSONObject)array.get(i);
                Assertions.assertEquals(i,jsonObject.getInt("a"));
            }
        }
    }
}
