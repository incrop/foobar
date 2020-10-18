package markov;

import java.util.Arrays;

public class Solution {
    public static int[] solution(int[][] m) {
        AbsMarkovChain input = AbsMarkovChain.parse(m);
        Mat absorbProbs = Mat.identity(input.trans.height())
                .minus(input.trans)
                .invert()
                .multiply(input.absorb);
        return formatResult(absorbProbs);
    }

    private static int[] formatResult(Mat mat) {
        long commonDen = 1;
        for (int j = 0; j < mat.width(); j++) {
            long mul = commonDen * mat.dens[0][j];
            long gcd = absGcd(commonDen, mul);
            commonDen = mul / gcd;
        }
        int[] res = new int[mat.width() + 1];
        for (int j = 0; j < mat.width(); j++) {
            res[j] = (int) (mat.nums[0][j] * (commonDen / mat.dens[0][j]));
        }
        res[mat.width()] = (int)commonDen;
        return res;
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
            Mat trans = new Mat(absorbIdx, absorbIdx);
            Mat absorb = new Mat(absorbIdx, m.length - absorbIdx);
            for (int i = 0; i < absorbIdx; i++) {
                int den = Arrays.stream(m[i]).sum();
                for (int j = 0; j < absorbIdx; j++) {
                    trans.addVal(i, j, m[i][j], den);
                }
                for (int j = 0; j < m.length - absorbIdx; j++) {
                    absorb.addVal(i, j, m[i][j + absorbIdx], den);
                }
            }
            return new AbsMarkovChain(trans, absorb);
        }
    }

    static class Mat {
        final long[][] nums;
        final long[][] dens;

        Mat(int height, int width) {
            this.nums = new long[height][width];
            this.dens = new long[height][width];
            for (int i = 0; i < dens.length; i++) {
                Arrays.fill(dens[i], 1);
            }
        }

        void addVal(int i, int j, long num, long den) {
            nums[i][j] *= den;
            nums[i][j] += num * dens[i][j];
            dens[i][j] *= den;
            simplify(i, j);
        }

        void mulVal(int i, int j, long num, long den) {
            nums[i][j] *= num;
            dens[i][j] *= den;
            simplify(i, j);
        }

        private void simplify(int i, int j) {
            if (nums[i][j] == 0) {
                dens[i][j] = 1;
                return;
            }
            if (dens[i][j] < 0) {
                nums[i][j] = -nums[i][j];
                dens[i][j] = -dens[i][j];
            }
            long gcd = absGcd(nums[i][j], dens[i][j]);
            if (gcd == 1) {
                return;
            }
            nums[i][j] /= gcd;
            dens[i][j] /= gcd;
        }

        static Mat identity(int size) {
            Mat id = new Mat(size, size);
            for (int i = 0; i < size; i++) {
                id.addVal(i, i, 1, 1);
            }
            return id;
        }

        Mat minus(Mat that) {
            Mat res = clone();
            for (int i = 0; i < height(); i++) {
                for (int j = 0; j < width(); j++) {
                    res.addVal(i, j, -that.nums[i][j], that.dens[i][j]);
                }
            }
            return res;
        }

        // Gauss-Jordan method
        Mat invert() {
            Mat mat = new Mat(height(), width() * 2);
            for (int i = 0; i < mat.height(); i++) {
                System.arraycopy(this.nums[i], 0, mat.nums[i], 0, width());
                System.arraycopy(this.dens[i], 0, mat.dens[i], 0, width());
                mat.nums[i][width() + i] = 1;
            }
            for (int i = 0; i < mat.height(); i++) {
                if (mat.nums[i][i] == 0) {
                    int k = i + 1;
                    while (mat.nums[k][i] == 0) {
                        k++;
                    }
                    long[] tmpRow = mat.nums[k];
                    mat.nums[k] = mat.nums[i];
                    mat.nums[i] = tmpRow;
                    tmpRow = mat.dens[k];
                    mat.dens[k] = mat.dens[i];
                    mat.dens[i] = tmpRow;
                }
                {
                    long num = mat.nums[i][i];
                    long den = mat.dens[i][i];
                    for (int j = 0; j < mat.width(); j++) {
                        mat.mulVal(i, j, den, num);
                    }
                }
                for (int k = 0; k < mat.height(); k++) {
                    if (i == k || mat.nums[k][i] == 0) {
                        continue;
                    }
                    long num = mat.nums[k][i];
                    long den = mat.dens[k][i];
                    for (int j = 0; j < mat.width(); j++) {
                        mat.addVal(k, j, -num * mat.nums[i][j], den * mat.dens[i][j]);
                    }
                }
            }

            Mat res = new Mat(height(), width());
            for (int i = 0; i < height(); i++) {
                System.arraycopy(mat.nums[i], width(), res.nums[i], 0, width());
                System.arraycopy(mat.dens[i], width(), res.dens[i], 0, width());
            }
            return res;
        }

        Mat multiply(Mat that) {
            Mat res = new Mat(this.height(), that.width());
            for (int i = 0; i < this.height(); i++) {
                for (int j = 0; j < that.width(); j++) {
                    for (int k = 0; k < this.width(); k++) {
                        res.addVal(i, j, this.nums[i][k] * that.nums[k][j], this.dens[i][k] * that.dens[k][j]);
                    }
                }
            }
            return res;
        }

        int height() {
            return nums.length;
        }

        int width() {
            return nums[0].length;
        }

        @Override
        protected Mat clone()  {
            Mat res = new Mat(height(), width());
            for (int i = 0; i < height(); i++) {
                System.arraycopy(this.nums[i], 0, res.nums[i], 0, width());
                System.arraycopy(this.dens[i], 0, res.dens[i], 0, width());
            }
            return res;
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
                    sb.append(dens[i][j]);
                    sb.append(',');
                }
                sb.setLength(sb.length() - 1);
                sb.append(" ]\n");
            }
            return sb.toString();
        }
    }

    private static long absGcd(long a, long b) {
        while (a != 0) {
            long aTmp = a;
            a = b % a;
            b = aTmp;
        }
        return Math.abs(b);
    }
}