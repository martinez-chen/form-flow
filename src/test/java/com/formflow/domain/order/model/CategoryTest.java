package com.formflow.domain.order.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CategoryTest {

    @Test
    void shouldCreateCategoryWithValidValue() {
        Category category = Category.of("IT_SUPPORT");
        
        assertThat(category.getValue()).isEqualTo("IT_SUPPORT");
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        assertThatThrownBy(() -> Category.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category value cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionForEmptyValue() {
        assertThatThrownBy(() -> Category.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category value cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionForBlankValue() {
        assertThatThrownBy(() -> Category.of("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category value cannot be null or empty");
    }

    @Test
    void shouldNormalizeCategoryValue() {
        Category category = Category.of("  it support  ");
        
        assertThat(category.getValue()).isEqualTo("IT_SUPPORT");
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        Category category1 = Category.of("IT_SUPPORT");
        Category category2 = Category.of("it_support");
        
        assertThat(category1).isEqualTo(category2);
        assertThat(category1.hashCode()).isEqualTo(category2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentValue() {
        Category category1 = Category.of("IT_SUPPORT");
        Category category2 = Category.of("HR_REQUEST");
        
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void shouldHandleSpacesInCategory() {
        Category category = Category.of("Human Resources");
        
        assertThat(category.getValue()).isEqualTo("HUMAN_RESOURCES");
    }

    @Test
    void shouldHandleMixedCaseWithSpaces() {
        Category category = Category.of("Bug Report");
        
        assertThat(category.getValue()).isEqualTo("BUG_REPORT");
    }
}