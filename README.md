## 图像处理
* 通过FrameBase类或者RunApplication启动
* 该项目为本人实验田，因此包内含有大量无关/过时代码

目前支持：
* 高斯模糊（Quick/Normal）
* 纹理模糊
* 边缘提取
* 光栅处理
* 风格化
* 饱和度/亮度调整

不建议使用：
* 高斯模糊（Normal）：处理速度太慢
* 边缘提取（Mar）：非高对比度场景效果太差
* 自定义光栅宽度：算法有问题
* 风格化（Erosion）：暂时放这，不属于风格化操作
* 饱和度/亮度（CDR）：仅用于光栅处理

后续方向：
* 多图显示
* 批量处理
