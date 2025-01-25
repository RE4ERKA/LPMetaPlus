package me.re4erka.lpmetaplus.emulation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark an emulated fake class.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Fake {

}
