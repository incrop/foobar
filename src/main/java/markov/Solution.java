package markov;

import java.util.Arrays;

public class Solution {
    public static int[] solution(int[][] m) {
        AbsMarkovChain input = AbsMarkovChain.parse(m);
        Mat absorbProbs = Mat.identity(input.trans.height())
                .minus(input.trans)
                .invert()
                .multiply(input.absorb);
        int[] result = new int[absorbProbs.width() + 1];
        System.arraycopy(absorbProbs.nums[0], 0, result, 0, absorbProbs.width());
        result[absorbProbs.width()] = absorbProbs.dens[0];
        return result;
    }

    static class AbsMarkovChain {
        final Mat trans;
        final Mat absorb;

        private AbsMarkovChain(Mat trans, Mat absorb) {
            this.trans = trans;
            this.absorb = absorb;
        }

        static AbsMarkovChain parse(int[][] m) {
            int absorbIdx = -1;
            for (int i = 0; i < m.length; i++) {
                if (Arrays.stream(m[i]).allMatch(n -> n == 0)) {
                    if (absorbIdx == -1) {
                        absorbIdx = i;
                    }
                } else {
                    if (absorbIdx != -1) {
                        int[] tmpRow = m[i];
                        m[i] = m[absorbIdx];
                        m[absorbIdx] = tmpRow;
                        for (int k = 0; k < m.length; k++) {
                            int tmp = m[k][i];
                            m[k][i] = m[k][absorbIdx];
                            m[k][absorbIdx] = tmp;
                        }
                        absorbIdx++;
                    }
                }
            }
            int[][] transNums = new int[absorbIdx][absorbIdx];
            int[] transDens = new int[absorbIdx];
            int[][] absorbNums = new int[absorbIdx][m.length - absorbIdx];
            int[] absorbDens = new int[absorbIdx];
            for (int i = 0; i < absorbIdx; i++) {
                System.arraycopy(m[i], 0, transNums[i], 0, absorbIdx);
                System.arraycopy(m[i], absorbIdx, absorbNums[i], 0, m.length - absorbIdx);
                int den = Arrays.stream(m[i]).sum();
                transDens[i] = den;
                absorbDens[i] = den;
            }
            return new AbsMarkovChain(new Mat(transNums, transDens), new Mat(absorbNums, absorbDens));
        }
    }

    static class Mat {
        final int[][] nums;
        final int[] dens;

        Mat(int[][] nums, int[] dens) {
            this.nums = nums;
            this.dens = dens;
            simplifyAll();
        }

        static Mat identity(int size) {
            int[][] nums = new int[size][size];
            for (int i = 0; i < size; i++) {
                nums[i][i] = 1;
            }
            int[] dens = new int[size];
            Arrays.fill(dens, 1);
            return new Mat(nums, dens);
        }

        Mat minus(Mat that) {
            int m = height();
            int n = width();
            int[][] nums = new int[m][n];
            int[] dens = new int[m];
            for (int i = 0; i < m; i++) {
                dens[i] = this.dens[i] * that.dens[i];
                for (int j = 0; j < n; j++) {
                    nums[i][j] = this.nums[i][j] * that.dens[i] - that.nums[i][j] * this.dens[i];
                }
            }
            return new Mat(nums, dens);
        }

        // Gauss-Jordan method
        Mat invert() {
            int width2 = width() * 2;
            int[][] nums = new int[height()][width2];
            int[] dens = this.dens.clone();
            for (int i = 0; i < nums.length; i++) {
                System.arraycopy(this.nums[i], 0, nums[i], 0, width());
                nums[i][width() + i] = dens[i];
            }
            for (int i = 0; i < nums.length; i++) {
                if (nums[i][i] == 0) {
                    int k = i + 1;
                    while (nums[k][i] == 0) {
                        k++;
                    }
                    int[] tmpRow = nums[k];
                    nums[k] = nums[i];
                    nums[i] = tmpRow;
                    int tmp = dens[k];
                    dens[k] = dens[i];
                    dens[i] = tmp;
                }
                int num = nums[i][i];
                for (int j = 0; j < width2; j++) {
                    nums[i][j] *= dens[i];
                }
                dens[i] *= num;
                simplifyRow(nums, dens, i);
                for (int k = 0; k < nums.length; k++) {
                    if (i == k || nums[k][i] == 0) {
                        continue;
                    }
                    int n = nums[k][i];
                    for (int j = 0; j < width2; j++) {
                        nums[k][j] = nums[k][j] * dens[i] - nums[i][j] * n;
                    }
                    dens[k] *= dens[i];
                    simplifyRow(nums, dens, k);
                }
            }

            int[][] numsRes = new int[height()][width()];
            int[] densRes = dens.clone();
            for (int i = 0; i < nums.length; i++) {
                System.arraycopy(nums[i], width(), numsRes[i], 0, width());
            }
            return new Mat(numsRes, densRes);
        }

        Mat multiply(Mat that) {
            int[][] nums = new int[this.height()][that.width()];
            int[] dens = new int[this.height()];
            Arrays.fill(dens, 1);
            for (int i = 0; i < nums.length; i++) {
                for (int j = 0; j < nums[0].length; j++) {
                    int num = 0;
                    int den = 1;
                    for (int k = 0; k < this.width(); k++) {
                        int num1 = this.nums[i][k] * that.nums[k][j];
                        int den1 = this.dens[i] * that.dens[k];
                        num = num * den1 + num1 * den;
                        den = den * den1;
                        int gcd = absGcd(num, den);
                        num /= gcd;
                        den /= gcd;
                    }
                    for (int k = 0; k < nums[0].length; k++) {
                        nums[i][k] *= den;
                    }
                    nums[i][j] += num * dens[i];
                    dens[i] *= den;
                    simplifyRow(nums, dens, i);
                }
            }
            return new Mat(nums, dens);
        }

        int height() {
            return nums.length;
        }

        int width() {
            return nums[0].length;
        }

        private void simplifyAll() {
            for (int i = 0; i < height(); i++) {
                simplifyRow(nums, dens, i);
            }
        }

        private static void simplifyRow(int[][] nums, int[] dens, int i) {
            int gcd = dens[i];
            for (int j = 0; j < nums[i].length; j++) {
                gcd = absGcd(nums[i][j], gcd);
                if (gcd == 1) {
                    return;
                }
            }
            if (dens[i] < 0) {
                gcd = -gcd;
            }
            dens[i] /= gcd;
            for (int j = 0; j < nums[i].length; j++) {
                nums[i][j] /= gcd;
            }
        }
    }

    private static int absGcd(int a, int b) {
        while (a != 0) {
            int aTmp = a;
            a = b % a;
            b = aTmp;
        }
        return Math.abs(b);
    }
}