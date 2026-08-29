/**
 * LeetCode 42. 接雨水（Trapping Rain Water）
 * 核心思路：双指针法 —— 时间 O(n)，空间 O(1)
 */
public class TrapWater {

    /**
     * 每个位置能存的水 = min(左边最高柱子, 右边最高柱子) - 当前柱子高度
     * 用左右两个指针从两端向中间收拢：
     *   - 哪边的 max 更小，就处理哪边的指针（因为另一侧一定有更高的柱子兜底）
     *   - 水位由较小的一侧决定，所以这样做是安全的
     */
    public static int trap(int[] height) {
        if (height == null || height.length == 0) {
            return 0;
        }

        int left = 0, right = height.length - 1;
        int leftMax = height[left], rightMax = height[right];
        int water = 0;

        while (left < right) {
            if (leftMax < rightMax) {
                // 左侧较矮，水位由 leftMax 决定，左指针右移
                left++;
                leftMax = Math.max(leftMax, height[left]);
                water += leftMax - height[left];
            } else {
                // 右侧较矮（或相等），水位由 rightMax 决定，右指针左移
                right--;
                rightMax = Math.max(rightMax, height[right]);
                water += rightMax - height[right];
            }
        }

        return water;
    }

    public static void main(String[] args) {
        // 题目给的两个示例
        System.out.println(trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1})); // 期望 6
        System.out.println(trap(new int[]{4, 2, 0, 3, 2, 5}));              // 期望 9

        // 边界情况测试
        System.out.println(trap(new int[]{}));            // 空数组 -> 0
        System.out.println(trap(new int[]{5}));           // 单根柱子 -> 0
        System.out.println(trap(new int[]{3, 3, 3}));     // 平坦 -> 0
        System.out.println(trap(new int[]{1, 2, 3, 4}));  // 单调递增 -> 0
    }
}
