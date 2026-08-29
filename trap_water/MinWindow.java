/**
 * LeetCode 76. 最小覆盖子串（Minimum Window Substring）
 * 核心思路：滑动窗口（可变长度）+ 字符计数 —— 时间 O(m+n)，空间 O(字符集)
 */
class Solution {
    public String minWindow(String s, String t) {
        if (s == null || t == null || s.length() < t.length()) {
            return "";
        }

        // need[c]：字符 c 还需要多少个（覆盖 t 所需）
        int[] need = new int[128];
        for (char c : t.toCharArray()) {
            need[c]++;
        }

        int required = t.length(); // 还缺多少个字符（含重复计数）
        int left = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0; // 最优窗口的起始位置

        // 右指针不断扩张窗口
        for (int right = 0; right < s.length(); right++) {
            char rc = s.charAt(right);
            // 只有 t 里的字符才影响"是否还缺"的判断
            if (need[rc] > 0) {
                required--; // 缺口减一
            }
            need[rc]--; // t 之外的字符会变成负数，表示"多余"

            // 窗口已覆盖 t 的全部字符，开始收缩左边界找更短的窗口
            while (required == 0) {
                // 记录更短的答案
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                }

                char lc = s.charAt(left);
                need[lc]++; // 左边字符移出窗口
                if (need[lc] > 0) {
                    // 移出后缺口出现，窗口不再覆盖 t
                    required++;
                }
                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }
}

public class MinWindow {
    public static void main(String[] args) {
        Solution s = new Solution();

        // 题目示例
        System.out.println("[" + s.minWindow("ADOBECODEBANC", "ABC") + "]"); // 期望 [BANC]
        System.out.println("[" + s.minWindow("a", "a") + "]");               // 期望 [a]
        System.out.println("[" + s.minWindow("a", "aa") + "]");              // 期望 [] 空串

        // 边界/额外测试
        System.out.println("[" + s.minWindow("ab", "b") + "]");              // 期望 [b]
        System.out.println("[" + s.minWindow("bba", "ab") + "]");            // 期望 [ba]
        System.out.println("[" + s.minWindow("bbaac", "aba") + "]");         // 期望 [baa]
        System.out.println("[" + s.minWindow("abc", "d") + "]");             // 期望 [] 无解
        System.out.println("[" + s.minWindow("aaflslflsldkalskaaa", "aaa") + "]"); // 期望 [aaa]
        System.out.println("[" + s.minWindow("cabwefgewcwaefgcf", "cae") + "]");    // 期望 [cwae]
    }
}
