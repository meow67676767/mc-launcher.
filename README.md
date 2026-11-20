<h1 align="center">PocketCraft Java</h1>

PocketCraft Java — лаунчер Minecraft: Java Edition для Android.

- Офлайн-вход по умолчанию, Microsoft — по желанию.
- Загрузчики модов: Forge, NeoForge, Fabric, Quilt, OptiFine.
- Поиск модов и модпаков (Modrinth, CurseForge).
- Встроенное обновление из GitHub Releases.
- Собственный интерфейс и иконка.

Скачать: [Releases](https://github.com/meow67676767/mc-launcher./releases)

## Основа проекта и лицензия

PocketCraft Java — это **форк** (производная работа) проекта
[Amethyst Android](https://github.com/AngelAuraMC/Amethyst-Android) от AngelAuraMC,
который основан на [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher)
и [Boardwalk](https://github.com/zhuowei/Boardwalk).
Большая часть кода (запуск Java, рендереры, управление) написана авторами этих проектов.
Полная история их изменений есть в репозитории Amethyst Android.

Код распространяется по лицензии **GNU LGPL-3.0** ([LICENSE](LICENSE)), как и у Amethyst.
Все авторские права оригинальных авторов сохранены. Подробности — в [NOTICE.md](NOTICE.md).

PocketCraft Java не связан с Mojang Studios, Microsoft или AngelAuraMC.
Minecraft — товарный знак Mojang Studios.

## Сборка

```bash
./gradlew :app_pojavlauncher:assembleDebug
```
Нужны JDK 8 и JDK 21, Android SDK и NDK 27. JRE для игры берётся так же, как в CI (`.github/workflows/android.yml`).

## Сторонние компоненты


* [Boardwalk](https://github.com/zhuowei/Boardwalk) (JVM Launcher): Unknown License/[Apache License 2.0](https://github.com/zhuowei/Boardwalk/blob/master/LICENSE) or GNU GPLv2.
* [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher): [GLGPL](https://github.com/PojavLauncherTeam/PojavLauncher/blob/v3_openjdk/LICENSE)
* Android Support Libraries: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
* [GL4ES](https://github.com/AngelAuraMC/gl4es): [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).
* [MobileGlues](https://github.com/MobileGL-Dev/MobileGlues): [LGPL-2.1 License](https://github.com/MobileGL-Dev/MobileGlues/blob/dev-es/LICENSE).
* [Krypton Wrapper](https://github.com/BZLZHH/NG-GL4ES): [MIT License](https://github.com/BZLZHH/NG-GL4ES/blob/main/LICENSE)
* [ANGLE](https://chromium.googlesource.com/angle/angle): [All Rights Reserved](app_pojavlauncher/src/main/assets/licenses/ANGLE_LICENSE).
* [OpenJDK](https://github.com/AngelAuraMC/openjdk-multiarch-jdk8u): [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).
* [LWJGL3](https://github.com/AngelAuraMC/lwjgl3): [BSD-3 License](https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md).
* [LWJGLX](https://github.com/AngelAuraMC/lwjglx) (LWJGL2 API compatibility layer for LWJGL3): unknown license.
* [Mesa 3D Graphics Library](https://gitlab.freedesktop.org/mesa/mesa): [MIT License](https://docs.mesa3d.org/license.html).
* [bhook](https://github.com/bytedance/bhook) (Used for exit code trapping): [MIT license](https://github.com/bytedance/bhook/blob/main/LICENSE).
* [libepoxy](https://github.com/anholt/libepoxy): [MIT License](https://github.com/anholt/libepoxy/blob/master/COPYING).
* [virglrenderer](https://github.com/AngelAuraMC/virglrenderer): [MIT License](https://gitlab.freedesktop.org/virgl/virglrenderer/-/blob/master/COPYING).
* [OpenAL-Soft](https://github.com/kcat/openal-soft): [GNU GPLv2](app_pojavlauncher/src/main/assets/licenses/OPENAL-SOFT_GPL2)
  * [oboe](https://github.com/google/oboe): [Apache License 2.0](app_pojavlauncher/src/main/assets/licenses/OBOE_APACHE2).
  * [pfffft](https://bitbucket.org/jpommier/pffft/src/master/): [ARR](app_pojavlauncher/src/main/assets/licenses/PFFFT_LICENSE)
* [SDL3](https://github.com/libsdl-org/SDL): [zlib License](https://github.com/libsdl-org/SDL/blob/main/LICENSE.txt)
* [sdl2-compat](https://github.com/libsdl-org/sdl2-compat): [zlib License](https://github.com/libsdl-org/sdl2-compat/blob/main/LICENSE.txt)
* Thanks to [MCHeads](https://mc-heads.net) for providing Minecraft avatars.
* Шрифты Manrope и Unbounded: SIL Open Font License, см. [THIRD_PARTY_FONTS.md](THIRD_PARTY_FONTS.md).
