# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Forge Recommended Versioning](https://docs.minecraftforge.net/en/1.12.x/conventions/versioning/).

## [1.12.2-1.0.7.1]
- build.gradle changes from CleanRooms template on github.
- Fixed Example Baubles Ring translation key for item name.
- Fix mod versioning, I forgot the API Identifier `0` within the jar, my bad.
- Added `IBauble#playEquipSound` for Server side eqipping sound identifiers which defaults to `Generic armor equip` sound.
- Added `IBauble#canRightClickEquip`, default true, almost all baubles should be right click equip-able now.
- The beginning of Curio GUI implementations controlled by boolean config option, defaults to false(disabled) `Alpha State`

## [1.12.2-1.0.6.0]
- XNiter(Me) Created fork of Baubles
- Maintaining group & class schema to allow drop in replacement
- Forge Gradle Update to 5
- Using CleanroomMC FG5 env build
- Added FancyGradle to build.gradle
- Update to Forge `1.12.2-14.23.5.2860`
- Versioning Schema is [Forge Recommended Versioning](https://docs.minecraftforge.net/en/1.12.x/conventions/versioning/)
- Update Registrations (what?)
- Update pl_pl.lang Thanks to(Pabilo8)
- Update tr_tr.lang Thanks to(RoCoKo)
- Update ru_ru.lang Thanks to(DrHesperus)
- Update uk_ua.lang Thanks to(burunduk)
- Added en_ud.lang Thanks to(The-Fireplace, X_Niter fixed spelling)
- Added ja_jp.lang Thanks to(2z6c)
- Added sv_se.lang, Thanks to(Regnander)
- Added BaubleEquipmentChangeEvent for other mods to work with
- Maintaining the [License](https://creativecommons.org/licenses/by-nc-sa/3.0/)
- Credit: [Original Baubles BY:Azanor13](https://www.curseforge.com/minecraft/mc-mods/baubles)

## 1.5.2
- API: added isBaubleEquipped helper method to BaublesAPI
- fixed player bauble syncing (Thanks pau101) closes #235

## 1.5.1
- added IBauble as a capability (see https://github.com/Azanor/Baubles/pull/208)

## 1.5.0
- updated for MC 1.12