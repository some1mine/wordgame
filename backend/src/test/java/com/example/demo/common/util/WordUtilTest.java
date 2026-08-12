package com.example.demo.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WordUtilTest {

    private final WordUtil wordUtil = new WordUtil();

    @Test
    void convertsHangulSyllablesToInitialConsonants() {
        assertThat(wordUtil.getInitials("가방")).isEqualTo("ㄱㅂ");
        assertThat(wordUtil.getInitials("초성게임")).isEqualTo("ㅊㅅㄱㅇ");
    }

    @Test
    void keepsInitialConsonantsAsTheyAre() {
        assertThat(wordUtil.getInitials("ㄱㅂ")).isEqualTo("ㄱㅂ");
    }

    @Test
    void handlesEmptyInput() {
        assertThat(wordUtil.getInitials("")).isEmpty();
    }
}
