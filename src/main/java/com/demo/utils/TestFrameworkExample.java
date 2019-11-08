package com.demo.utils;

public class TestFrameworkExample {

    private int add(int a, int b)
    {
        return a - b;
    }

    private int subtract(int a, int b)
    {
        return a - b;
    }

    //first approach of what a test framework does
    private void testAdd() throws Exception {

        if (add(5,5) != 10)
        {
            throw new Exception("Add method result doesn't match expected value");
        }

        // unreachable code if exception is thrown before
        if (subtract(5,5) != 0)
        {
            throw new Exception("Subtract method result doesn't match expected value");
        }
    }

    //second approach of what a test framework does
    private void testSubtract ()
    {
        if (add(5,5) != 10)
        {
            System.out.println("Add method result doesn't match expected value");
        }

        // unreachable code if exception is thrown before
        if (subtract(5,5) != 0)
        {
            System.out.println("Subtract method result doesn't match expected value");
        }
    }
}
