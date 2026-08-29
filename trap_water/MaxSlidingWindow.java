import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LeetCode 239. 滑动窗口最大值（Sliding Window Maximum）
 * 核心思路：单调递减队列（存下标）—— 时间 O(n)，空间 O(k)
 */
class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        // 窗口共 n-k+1 个，结果数组就是这个长度
        int[] result = new int[n - k + 1];
        // 双端队列存"下标"，对应的值从队头到队尾严格递减
        Deque<Integer> dq = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            // 1. 队头下标已滑出窗口左边界（下标 <= i-k），移除
            while (!dq.isEmpty() && dq.peekFirst() <= i - k) {
                dq.pollFirst();
            }

            // 2. 队尾值 <= 当前值，它们永远不可能再当最大值，移除
            //    （保证队列里的值从队头到队尾递减）
            while (!dq.isEmpty() && nums[dq.peekLast()] <= nums[i]) {
                dq.pollLast();
            }

            // 3. 当前下标入队
            dq.offerLast(i);

            // 4. 窗口已形成（i >= k-1），队头就是当前窗口最大值
            if (i >= k - 1) {
                result[i - k + 1] = nums[dq.peekFirst()];
            }
        }

        return result;
    }
}

public class MaxSlidingWindow {
    public static void main(String[] args) {
        Solution s = new Solution();

        // 题目示例 1：期望 [3,3,5,5,6,7]
        print(s.maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3));

        // 题目示例 2：期望 [1]
        print(s.maxSlidingWindow(new int[]{1}, 1));

        // 边界/额外测试
        print(s.maxSlidingWindow(new int[]{9, 8, 7, 6}, 2));      // 期望 [9,8,7] 递减序列
        print(s.maxSlidingWindow(new int[]{1, 2, 3, 4}, 2));      // 期望 [2,3,4] 递增序列
        print(s.maxSlidingWindow(new int[]{5, 5, 5, 5}, 3));      // 期望 [5,5] 全相等
        print(s.maxSlidingWindow(new int[]{2, -1, 2}, 3));        // 期望 [2] k=数组长度
        print(s.maxSlidingWindow(new int[]{-7, -8, 7, 5, 7, 1, 6, 0}, 4)); // 期望 [7,7,7,7,7]
    }

    private static void print(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        System.out.println(sb);
    }
}
