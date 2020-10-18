package markov;

import java.util.Arrays;

public class Solution {
    public static int[] solution(int[][] m) {
        AbsMarkovChain input = AbsMarkovChain.parse(m);
        if (input.trans.height() == 0) {
            int[] result = new int[m.length + 1];
            Arrays.fill(result, 1);
            result[m.length] = Arrays.stream(result).sum() - 1;
            return result;
        }
        Mat absorbProbs = Mat.identity(input.trans.height())
                .minus(input.trans)
                .invert()
                .multiply(input.absorb);
        int[] result = new int[absorbProbs.width() + 1];
        for (int i = 0; i < absorbProbs.width(); i++) {
            result[i] = (int) absorbProbs.nums[0][i];
        }
        result[absorbProbs.width()] = (int)absorbProbs.dens[0];
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
                if (isAbsorbing(i, m[i])) {
                    if (absorbIdx == -1) {
                        absorbIdx = i;
                    }
                } else {
                    if (absorbIdx != -1) {
                        for (int j = i; j > absorbIdx; j--) {
                            int[] tmpRow = m[j];
                            m[j] = m[j - 1];
                            m[j - 1] = tmpRow;
                            for (int k = 0; k < m.length; k++) {
                                int tmp = m[k][j];
                                m[k][j] = m[k][j - 1];
                                m[k][j - 1] = tmp;
                            }
                        }
                        absorbIdx++;
                    }
                }
            }
            long[][] transNums = new long[absorbIdx][absorbIdx];
            long[] transDens = new long[absorbIdx];
            long[][] absorbNums = new long[absorbIdx][m.length - absorbIdx];
            long[] absorbDens = new long[absorbIdx];
            for (int i = 0; i < absorbIdx; i++) {
                for (int j = 0; j < absorbIdx; j++) {
                    transNums[i][j] = m[i][j];
                }
                for (int j = 0; j < m.length - absorbIdx; j++) {
                    absorbNums[i][j] = m[i][j + absorbIdx];
                }
                int den = Arrays.stream(m[i]).sum();
                transDens[i] = den;
                absorbDens[i] = den;
            }
            return new AbsMarkovChain(new Mat(transNums, transDens), new Mat(absorbNums, absorbDens));
        }

        static boolean isAbsorbing(int i, int[] row) {
            for (int j = 0; j < row.length; j++) {
                if (j != i && row[j] != 0) {
                    return false;
                }
            }
            return true;
        }
    }

    static class Mat {
        final long[][] nums;
        final long[] dens;

        Mat(long[][] nums, long[] dens) {
            this.nums = nums;
            this.dens = dens;
            simplifyAll();
        }

        static Mat identity(int size) {
            long[][] nums = new long[size][size];
            for (int i = 0; i < size; i++) {
                nums[i][i] = 1;
            }
            long[] dens = new long[size];
            Arrays.fill(dens, 1);
            return new Mat(nums, dens);
        }

        Mat minus(Mat that) {
            int m = height();
            int n = width();
            long[][] nums = new long[m][n];
            long[] dens = new long[m];
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
            long[][] nums = new long[height()][width2];
            long[] dens = this.dens.clone();
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
                    long[] tmpRow = nums[k];
                    nums[k] = nums[i];
                    nums[i] = tmpRow;
                    long tmp = dens[k];
                    dens[k] = dens[i];
                    dens[i] = tmp;
                }
                long num = nums[i][i];
                for (int j = 0; j < width2; j++) {
                    nums[i][j] *= dens[i];
                }
                dens[i] *= num;
                simplifyRow(nums, dens, i);
                for (int k = 0; k < nums.length; k++) {
                    if (i == k || nums[k][i] == 0) {
                        continue;
                    }
                    long n = nums[k][i];
                    for (int j = 0; j < width2; j++) {
                        nums[k][j] = nums[k][j] * dens[i] - nums[i][j] * n;
                    }
                    dens[k] *= dens[i];
                    simplifyRow(nums, dens, k);
                }
            }

            long[][] numsRes = new long[height()][width()];
            long[] densRes = dens.clone();
            for (int i = 0; i < nums.length; i++) {
                System.arraycopy(nums[i], width(), numsRes[i], 0, width());
            }
            return new Mat(numsRes, densRes);
        }

        Mat multiply(Mat that) {
            long[][] nums = new long[this.height()][that.width()];
            long[] dens = new long[this.height()];
            Arrays.fill(dens, 1);
            for (int i = 0; i < nums.length; i++) {
                for (int j = 0; j < nums[0].length; j++) {
                    long num = 0;
                    long den = 1;
                    for (int k = 0; k < this.width(); k++) {
                        long num1 = this.nums[i][k] * that.nums[k][j];
                        long den1 = this.dens[i] * that.dens[k];
                        num = num * den1 + num1 * den;
                        den = den * den1;
                        long gcd = absGcd(num, den);
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

        private static void simplifyRow(long[][] nums, long[] dens, int i) {
            long gcd = dens[i];
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

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < height(); i++) {
                sb.append('[');
                for (int j = 0; j < width(); j++) {
                    sb.append(' ');
                    sb.append(nums[i][j]);
                    sb.append('/');
                    sb.append(dens[i]);
                    sb.append(',');
                }
                sb.setLength(sb.length() - 1);
                sb.append(" ]\n");
            }
            return sb.toString();
        }
    }

    static long absGcd(long a, long b) {
        while (a != 0) {
            long aTmp = a;
            a = b % a;
            b = aTmp;
        }
        return Math.abs(b);
    }
}