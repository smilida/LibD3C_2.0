package cn.edu.xmu.dm.d3c.metrics;

import java.util.List;

/**
 * FUNCTION_CalculateDis: Calculate the inconsistency between two classifiers.
 * FUNCTION_CalculateK: Calculate the κ measure between multiple classifiers
 */

public class ClassifierDiversity {
    public static double CalculateDis(List<Integer> first, List<Integer> second) {
        double Dis = 0.0D;
        int diffNum = 0;
        for (int i = 0; i < first.size() && second.size() > i; i++) {
            if (first.get(i) != second.get(i))
                diffNum++;
        }
        Dis = diffNum / first.size();
        return Dis;
    }

    public static double CalculateK(List[] classifyResult) {
        int L = classifyResult.length;
        int N = 0;
        for (int ii = 0; ii < classifyResult.length; ii++) {
            if (classifyResult[ii].size() != 0)
                N = classifyResult[ii].size();
        }
        int num = 0;
        double Dis = 0.0D;
        int i;
        for (i = 0; i < L - 1; i++) {
            for (int j = i + 1; j < L; j++)
                Dis += CalculateDis(classifyResult[i], classifyResult[j]);
        }
        Dis = Dis * 2.0D / (L * (L - 1));
        for (i = 0; i < classifyResult.length; i++) {
            for (int j = 0; j < classifyResult[i].size(); j++) {
                if (((Integer)classifyResult[i].get(j)).intValue() == 1)
                    num++;
            }
        }
        double p = num / (L * N);
        double k = 1.0D - Dis / 2.0D * p * (1.0D - p);
        return k;
    }
}
