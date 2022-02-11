package ru.javarush;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SourceFileCreatorTest {

    @Test
    public void getFileName() {
        assertEquals("Animal", SourceFileCreator.getFileName("public interface Animal {"));
        assertEquals("Cat", SourceFileCreator.getFileName("public class Cat implements Animal {"));
    }

    @Test
    public void getPackageName() {
        assertEquals("com.javarush.task.pro.task05.task0504", SourceFileCreator.getPackageName("package com.javarush.task.pro.task05.task0504;"));
//        assertEquals("com.javarush.task.task07.task0703", SourceFileCreator.getFileName("package com.javarush.task.task07.task0703;"));
    }
}