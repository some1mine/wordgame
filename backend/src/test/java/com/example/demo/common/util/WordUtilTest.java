package com.example.demo.common.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WordUtilTest {
    @Autowired WordUtil wordUtil;

    @Test
    void exists() {
        System.out.println(wordUtil.exists("존재"));
    }
}