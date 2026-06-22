package com.aicode.core.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YNEnumTest {

    @Test
    @DisplayName("Y.val = true / N.val = false")
    void valuesAreBoolean() {
        assertTrue(YNEnum.Y.val);
        assertFalse(YNEnum.N.val);
    }

    @Test
    @DisplayName("getYN 大小写不敏感")
    void getYN_caseInsensitive() {
        assertEquals(YNEnum.Y, YNEnum.getYN("Y"));
        assertEquals(YNEnum.Y, YNEnum.getYN("y"));
        assertEquals(YNEnum.Y, YNEnum.getYN("y"));
        assertEquals(YNEnum.N, YNEnum.getYN("N"));
        assertEquals(YNEnum.N, YNEnum.getYN("n"));
    }

    @Test
    @DisplayName("getYN 非法值返回 null")
    void getYN_invalid_returnsNull() {
        assertNull(YNEnum.getYN("X"));
        assertNull(YNEnum.getYN(""));
        assertNull(YNEnum.getYN(null));
    }
}
