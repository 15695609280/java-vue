# -*- coding: utf-8 -*-
"""
LeetCode 42. 接雨水（Trapping Rain Water）—— 提交格式
核心思路：双指针法 —— 时间 O(n)，空间 O(1)
"""


class Solution(object):
    def trap(self, height):
        """
        :type height: List[int]
        :rtype: int
        """
        if not height:
            return 0

        left, right = 0, len(height) - 1
        left_max, right_max = height[left], height[right]
        water = 0

        while left < right:
            if left_max < right_max:
                # 左侧较矮，水位由 left_max 决定，左指针右移
                left += 1
                left_max = max(left_max, height[left])
                water += left_max - height[left]
            else:
                # 右侧较矮（或相等），水位由 right_max 决定，右指针左移
                right -= 1
                right_max = max(right_max, height[right])
                water += right_max - height[right]

        return water


# 本地自测（提交到 LeetCode 时删除 main 部分即可）
if __name__ == "__main__":
    s = Solution()
    print(s.trap([0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]))  # 期望 6
    print(s.trap([4, 2, 0, 3, 2, 5]))                     # 期望 9
    print(s.trap([]))            # 0
    print(s.trap([5]))           # 0
    print(s.trap([3, 3, 3]))     # 0
    print(s.trap([1, 2, 3, 4]))  # 0
