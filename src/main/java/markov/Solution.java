package markov;

import java.util.Arrays;

public class Solution {
    public static int[] solution(int[][] m) {
        // The problem can be modeled by Absorbing Markov chain
        // Stable forms are absorbing states, other forms are transient states.
        // We would need to create two matrices:
        // AbsMarkovChain.trans - transient to transient state probabilities (Q)
        // AbsMarkovChain.absorb - transient to absorbing state probabilities (R)
        //
        //
        // See https://en.wikipedia.org/wiki/Absorbing_Markov_chain

        AbsMarkovChain input = AbsMarkovChain.parse(m);

        // Corner case - all states are absorbing
        if (input.trans.height() == 0) {
            int[] result = new int[m.length + 1];
            result[0] = 1;
            result[m.length] = 1;
            return result;
        }

        // We would need to find absorbing probabilities for state 0.
        // This is covered by B = (I - Q)^-1 * R
        Mat absorbProbs = Mat.identity(input.trans.height())
                .minus(input.trans)
                .invert()
                .multiply(input.absorb);

        return convertResult(absorbProbs);
    }

    private static int[] convertResult(Mat absorbProbs) {
        int[] result = new int[absorbProbs.width() + 1];
        for (int i = 0; i < absorbProbs.width(); i++) {
            result[i] = (int) absorbProbs.nums[0][i];
        }
        result[absorbProbs.width()] = (int) absorbProbs.dens[0];
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
                    // Reorder rows (and columns) in case absorbing state is not at the end of the list.
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
                for (int j = 0; j < trans.width(); j++) {
                    trans.addValue(i, j, m[i][j], den);
                }
                trans.simplifyRow(i);
                for (int j = 0; j < absorb.width(); j++) {
                    absorb.addValue(i, j, m[i][j + absorbIdx], den);
                }
                absorb.simplifyRow(i);
            }
            return new AbsMarkovChain(trans, absorb);
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

        Mat(int height, int width) {
            this.nums = new long[height][width];
            this.dens = new long[height];
            Arrays.fill(dens, 1);
        }

        static Mat identity(int size) {
            Mat id = new Mat(size, size);
            for (int i = 0; i < size; i++) {
                id.addValue(i, i, 1, 1);
            }
            return id;
        }

        void addValue(int i, int j, long num, long den) {
            long oldDen = dens[i];
            if (oldDen == den) {
                nums[i][j] += num;
                return;
            }
            long lcm = den * oldDen / absGcd(den, oldDen);
            if (lcm > oldDen) {
                long mul = lcm / oldDen;
                for (int k = 0; k < width(); k++) {
                    nums[i][k] *= mul;
                }
            }
            nums[i][j] += (lcm / den) * num;
            dens[i] = lcm;
        }

        void simplifyRow(int i) {
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

        Mat minus(Mat that) {
            Mat res = copy();
            for (int i = 0; i < height(); i++) {
                for (int j = 0; j < width(); j++) {
                    res.addValue(i, j, -that.nums[i][j], that.dens[i]);
                }
            }
            return res;
        }

        // Gauss-Jordan method
        Mat invert() {
            // Copy of current matrix with identity matrix on the right half
            Mat mat = new Mat(height(), width() * 2);
            System.arraycopy(this.dens, 0, mat.dens, 0, width());
            for (int i = 0; i < mat.height(); i++) {
                System.arraycopy(this.nums[i], 0, mat.nums[i], 0, width());
                mat.nums[i][width() + i] = mat.dens[i];
            }
            
            for (int i = 0; i < mat.height(); i++) {
                // Switch row with zero value on diagonal with some other row
                if (mat.nums[i][i] == 0) {
                    int k = i + 1;
                    while (mat.nums[k][i] == 0) {
                        k++;
                    }
                    long[] tmpRow = mat.nums[k];
                    mat.nums[k] = mat.nums[i];
                    mat.nums[i] = tmpRow;
                    long tmp = mat.dens[k];
                    mat.dens[k] = mat.dens[i];
                    mat.dens[i] = tmp;
                }
                // Divide row to make 1 in diagonal
                if (mat.nums[i][i] != mat.dens[i]) {
                    long num = mat.nums[i][i];
                    long den = mat.dens[i];
                    for (int j = 0; j < mat.width(); j++) {
                        mat.nums[i][j] *= den;
                    }
                    mat.dens[i] *= num;
                    mat.simplifyRow(i);
                }
                // Remove this row mutiplied by some factor from all other rows, to have all 0 in the column
                for (int k = 0; k < mat.height(); k++) {
                    if (i == k || mat.nums[k][i] == 0) {
                        continue;
                    }
                    long num = mat.nums[k][i];
                    long den = mat.dens[i];
                    mat.dens[k] *= den;
                    for (int j = 0; j < mat.width(); j++) {
                        mat.nums[k][j] *= den;
                        mat.nums[k][j] -= mat.nums[i][j] * num;
                    }
                    mat.simplifyRow(k);
                }
            }
            // Return right half of the matrix
            Mat res = new Mat(height(), width());
            System.arraycopy(mat.dens, 0, res.dens, 0, width());
            for (int i = 0; i < height(); i++) {
                System.arraycopy(mat.nums[i], width(), res.nums[i], 0, width());
                res.simplifyRow(i);
            }
            return res;
        }

        Mat multiply(Mat that) {
            Mat res = new Mat(this.height(), that.width());
            for (int i = 0; i < res.height(); i++) {
                for (int j = 0; j < res.width(); j++) {
                    for (int k = 0; k < this.width(); k++) {
                        long num = this.nums[i][k] * that.nums[k][j];
                        long den = this.dens[i] * that.dens[k];
                        res.addValue(i, j, num, den);
                    }
                }
                res.simplifyRow(i);
            }
            return res;
        }

        int height() {
            return nums.length;
        }

        int width() {
            return nums[0].length;
        }

        private Mat copy() {
            Mat copy = new Mat(height(), width());
            System.arraycopy(dens, 0, copy.dens, 0, height());
            for (int i = 0; i < height(); i++) {
                System.arraycopy(nums[i], 0, copy.nums[i], 0, width());
            }
            return copy;
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
        a = Math.abs(a);
        b = Math.abs(b);
        while (a != 0) {
            long aTmp = a;
            a = b % a;
            b = aTmp;
        }
        return b;
    }
}