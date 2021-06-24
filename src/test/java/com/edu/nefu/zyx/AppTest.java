package com.edu.nefu.zyx;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import weka.classifiers.meta.LibD3C;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    public void doClassify() throws Exception{
        utils scan1 = new utils();
        System.out.print("Please input the parameter of LibD3C: ");
        String[] LibD3C_parm = scan1.getInput().split(" ");
        LibD3C.main(LibD3C_parm);
    }

    public static void main(String[] args) throws Exception{
        AppTest c = new AppTest();
        c.doClassify();
    }
}
