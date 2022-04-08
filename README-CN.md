CastUnit Demo App
=====

简体中文 | [English](./README.md) 

<img src="./screenshots/Screenshot_capture_interface.jpg" alt="运行界面" title="运行界面截图" style="zoom: 33%;" />

## 关于能力开放

[能力开放(CastUnit)] 是 OPPO 视频接续能力开放接口，用户在三方应用中能实现手机和电视之间的在线视频接续能力。

这个项目提供了一种接入能力开放 (CastUnitSDK) 的解决方案，接入文档详见 [配置和初始化说明][CastUnitInstructions]。
在使用该 SDK 时，你可以参照本项目的调用逻辑。



支持的设备:

目前对于ColorOS12.1及以上系统的内销机型，CastUnit提供全平台功能支持，后续会逐步支持ColorOS12.0相关内销机型。



## 关于 CastUnit Demo App
目前，我们提供了一个示例程序来展示 CastUnit SDK 接口的调用方法。

## 状态
目前，1.0.0 版本的示例程序已经发布并且稳定，我们会定期持续发布版本来集成新功能或者解决一些稳定性问题。
非常欢迎您也可以参与到本项目中，Comments/Bugs/Questions/PR 都是受欢迎的。
如果您想要贡献您的代码，请仔细阅读 [CONTRIBUTING.md][contributing] 中的内容。

## 编译
本项目使用 gradle 构建起来非常的简单：

```shell
git clone git@github.com:oppo/CastUnit.git
cd CastUnit
./gradlew :app:assembleRelease
```

**注意：**: 确保您本地 *Android SDK* 和 *Android Support Repository* 已经安装, `$ANDROID_HOME` 的环境变量已经配置
或者配置 `sdk.dir=...` 即 SDK 的路径到项目根目录的 `local.properties` 文件中。

## 开发
根据 [编译](#编译) 中的步骤配置项目，并按照您的需求来修改对应的文件。推荐使用[Android Studio][android-studio] 便捷的导入整个项目。

使用 Android Studio 导入项目的步骤如下:

1. 打开Android Studio，并点击 *文件* 菜单或者 *欢迎页面*;
2. 点击 *打开...*。
3. 找到 CastUnit 根目录。
4. 选择 `setting.gradle` 文件，完成导入。

## 问题 & 获取帮助

如需要报告问题或者提出功能需求，请[在Github中打开一个新问题][open-new-issue]。
如有合作意向、疑问、建议或者其他任何问题，欢迎[给项目组发邮件][discussion]。

在打开一个新问题之前，请您务必仔细阅读 [问题报告清单][issue-reporting-guidelines]
如果问题未按照推荐的方式提报，可能会被当作无效问题关闭。

## Contribution
在您需要提交 Pull Request 到本项目之前，请您务必仔细阅读 [CONTRIBUTING.md][contributing]。
更多详细信息请参考 [Contributing docs page][contributing-page].

感谢所有为这个项目做出贡献的开发者!

## Thanks
* 感谢所有为这个项目做出贡献的开发者!

## 作者
@Wei Zhang at oppo, [@Wei Zhang](https://github.com/zhangweinj) at oppo.

## License
[Apache License 2.0][license]

Copyright (c) 2022 OPPO. All rights reserved.

[CastUnitLink]: https://open.oppomobile.com/newservice/capability?pagename=castunit
[CastUnitInstructions]: https://open.oppomobile.com/wiki/doc#id=10751
[issue-reporting-guidelines]: #
[open-new-issue]: https://github.com/oppo/CastUnit/issues
[android-studio]: https://developer.android.com/studio
[contributing-page]: https://github.com/oppo/CastUnit/blob/main/CONTRIBUTING.md
[discussion]: https://github.com/oppo/CastUnit/issues
[contributing]: https://github.com/oppo/CastUnit/blob/main/CONTRIBUTING.md
[license]: https://www.apache.org/licenses/LICENSE-2.0
