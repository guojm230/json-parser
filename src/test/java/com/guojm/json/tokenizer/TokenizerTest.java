package com.guojm.json.tokenizer;

import com.guojm.json.exception.InvalidTokenException;
import com.guojm.json.token.TokenKind;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenizerTest {

    @Test
    public void stringTest(){
        var str = """
                "'ab'\\t\\n\\"\\u0030\\u0031\\u0032"
                """.trim();
        var expected = "'ab'\t\n\"012";
        var tokenList = new Tokenizer(new StringSource(str)).scan();
        Assertions.assertEquals(1,tokenList.size());
        Assertions.assertEquals(TokenKind.String,tokenList.get(0).kind());
        Assertions.assertEquals(expected,tokenList.get(0).originalValue());
    }

    @Test
    public void invalidEscapeTest(){
        var invalidCodepointStr = """
                "\\u01"
                """.trim();
        Assertions.assertThrows(InvalidTokenException.class,()->{
            new Tokenizer(new StringSource(invalidCodepointStr)).scan();
        });

        var invalidEscapeStr = """
                "abc\\c"
                """.trim();
        Assertions.assertThrows(InvalidTokenException.class,()->{
            new Tokenizer(new StringSource(invalidEscapeStr)).scan();
        });
    }

    @Test
    public void readExponentNumberTest(){
        var numberStr = """
                1.1e+5
                """.trim();
        var tokenList = new Tokenizer(new StringSource(numberStr)).scan();
        Assertions.assertEquals(1,tokenList.size());
        Assertions.assertEquals(TokenKind.FloatNumber,tokenList.get(0).kind());

        var numberStr2 = """
                1.1e-5
                """.trim();
        var tokenList2 = new Tokenizer(new StringSource(numberStr2)).scan();
        Assertions.assertEquals(1,tokenList2.size());
        Assertions.assertEquals(TokenKind.FloatNumber,tokenList2.get(0).kind());

        var numberStr3 = """
                0.1e-5
                """.trim();
        var tokenList3 = new Tokenizer(new StringSource(numberStr3)).scan();
        Assertions.assertEquals(1,tokenList3.size());
        Assertions.assertEquals(TokenKind.FloatNumber,tokenList3.get(0).kind());

        var numberStr4 = """
                0
                """.trim();
        var tokenList4 = new Tokenizer(new StringSource(numberStr4)).scan();
        Assertions.assertEquals(1,tokenList4.size());
        Assertions.assertEquals(TokenKind.IntNumber,tokenList4.get(0).kind());

        var numberStr5 = """
                12345
                """.trim();
        var tokenList5 = new Tokenizer(new StringSource(numberStr5)).scan();
        Assertions.assertEquals(1,tokenList5.size());
        Assertions.assertEquals(TokenKind.IntNumber,tokenList5.get(0).kind());
    }

    @Test
    public void invalidNumberTest(){
        Assertions.assertThrows(InvalidTokenException.class,()->{
            var invalidNumberStr = """
                1.
                """.trim();
            new Tokenizer(new StringSource(invalidNumberStr)).scan();
        });

        Assertions.assertThrows(InvalidTokenException.class,()->{
            var invalidNumberStr = """
                .123
                """.trim();
            new Tokenizer(new StringSource(invalidNumberStr)).scan();
        });

        Assertions.assertThrows(InvalidTokenException.class,()->{
            var invalidNumberStr = """
                01
                """.trim();
            new Tokenizer(new StringSource(invalidNumberStr)).scan();
        });
    }
}
