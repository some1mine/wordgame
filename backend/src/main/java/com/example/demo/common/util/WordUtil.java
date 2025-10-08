package com.example.demo.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;

@Slf4j
@Service
public class WordUtil {
    @Value("${korean-dic.request-url}") private String requestBaseUrl;

    @Value("${korean-dic.api-key}") private String apiKey;

    public WordUtil() {
        indexList.add('ㄱ');    indexList.add('ㄴ');    indexList.add('ㄷ');   indexList.add('ㄸ');
        indexList.add('ㄹ');    indexList.add('ㅁ');    indexList.add('ㅂ');   indexList.add('ㅃ');
        indexList.add('ㅅ');    indexList.add('ㅆ');    indexList.add('ㅇ');   indexList.add('ㅈ');
        indexList.add('ㅉ');    indexList.add('ㅊ');    indexList.add('ㅋ');   indexList.add('ㅌ');
        indexList.add('ㅍ');    indexList.add('ㅎ');

        firstList.add('가');    firstList.add('나');    firstList.add('다');   firstList.add('따');
        firstList.add('라');    firstList.add('마');    firstList.add('바');   firstList.add('빠');
        firstList.add('사');    firstList.add('싸');    firstList.add('아');   firstList.add('자');
        firstList.add('짜');    firstList.add('차');    firstList.add('카');   firstList.add('타');
        firstList.add('파');    firstList.add('하');    firstList.add('힣');
    }

    private final List<Character> indexList = new ArrayList<>();
    private final List<Character> firstList = new ArrayList<>();

    public boolean exists(String searchWord) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            RestTemplate template = new RestTemplate();
            String requestUrl = UriComponentsBuilder.fromHttpUrl(requestBaseUrl)
                    .queryParam("key", apiKey)
                    .queryParam("q", searchWord)
                    .build().toUriString();
            String body = template.getForEntity(requestUrl, String.class).getBody();

            NodeList nodelist = builder.parse(new InputSource(new StringReader(body))).getElementsByTagName("word");

            for (int i = 0; i < nodelist.getLength(); i++) {
                if (nodelist.item(i).getTextContent().equals(searchWord)) return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getInitials(String str) {
        char[] result = new char[str.length()];
        for (int i = 0; i < str.length(); i++) fillInitials(str, result, i);
        return String.join("", new String(result));
    }

    public void fillInitials(String str, char[] result, int i) {
        char c = str.charAt(i);
        for (int j = 0; j < firstList.size(); j++) {
            if (c == indexList.get(j) || (c >= firstList.get(j) && c < firstList.get(j + 1))) {
                result[i] = indexList.get(j); return;
            }
        }
    }

}
