package org.vanbart.example.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Compile time annotation which provides Dutch and English texts.
 * @author Ton van Bart
 * @since 23-4-13 22:07
 */
@Retention(RetentionPolicy.SOURCE)
public @interface Text {

    /**
     * Optional Dutch message text; optional, if omitted defaults to the English text.
     * @return
     */
    String nl() default "";

    /**
     * English message text.
     * @return
     */
    String en();

}
