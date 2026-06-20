# JLH6 Rebrand Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Transform StarlinkRouter into JLH6 — a dead-simple Android app with one button ("Abrir configurações do Android") and nothing else.

**Architecture:** Delete all routing/daemon/telnet code. Keep only MainActivity (single button → opens Android Settings). Package renamed `com.castilhoduarte.jlh6`. App class removed. Launcher icon = star only. Scripts kept and updated with new REPO/PKG refs.

**Tech Stack:** Android SDK 28/35, Java 17, AGP 8.7.3, Gradle 8.14.3. Zero third-party deps.

## Global Constraints

- `android.useAndroidX=false` — must stay in `gradle.properties`
- `minSdk = targetSdk = 28`, `compileSdk = 35` — do not change
- Zero third-party dependencies — Android SDK + Java only
- No Kotlin, no Compose, no AndroidX, no Shizuku
- `applicationId = com.castilhoduarte.jlh6`
- App name = "JLH6"
- New GitHub repo name = `jlh6` (was `starlinkrouter`)

---

## File Map

### DELETE (entirely)
- `app/src/main/java/com/castilhoduarte/starlinkrouter/` — whole old package dir
- `app/src/main/java/com/castilhoduarte/starlinkrouter/App.java`
- `app/src/main/java/com/castilhoduarte/starlinkrouter/BootReceiver.java`
- `app/src/main/java/com/castilhoduarte/starlinkrouter/BootService.java`
- `app/src/main/java/com/castilhoduarte/starlinkrouter/LogActivity.java`
- `app/src/main/java/com/castilhoduarte/starlinkrouter/StarlinkRouter.java`
- `app/src/main/java/com/castilhoduarte/starlinkrouter/TelnetRoot.java`
- `app/src/main/assets/starlinkrouter.sh`
- `app/src/main/res/drawable/bg_chip.xml`
- `app/src/main/res/drawable/bg_logs_button.xml`
- `app/src/main/res/drawable/ic_logs.xml`
- `app/src/main/res/drawable/ic_settings.xml`
- `app/src/main/res/drawable/ic_wifi.xml`
- `app/src/main/res/layout/activity_log.xml`
- `scripts/test/rule_lifecycle_test.sh`
- `scripts/test/TelnetRootTest.java`
- `scripts/test/README.md`
- `docs/DESIGN.md`
- `docs/ui-mockup.svg`

### CREATE
- `app/src/main/java/com/castilhoduarte/jlh6/MainActivity.java` — single button, opens Android Settings
- `app/src/main/res/drawable/ic_star.xml` — star vector for launcher foreground

### MODIFY
- `app/build.gradle.kts` — namespace + applicationId → `com.castilhoduarte.jlh6`
- `settings.gradle.kts` — rootProject.name → `"JLH6"`
- `app/src/main/AndroidManifest.xml` — remove BootService/BootReceiver/LogActivity; remove RECEIVE_BOOT_COMPLETED/FOREGROUND_SERVICE/WAKE_LOCK/INTERNET permissions; update theme ref
- `app/src/main/res/values/strings.xml` — app_name = "JLH6", one button string
- `app/src/main/res/values/themes.xml` — rename Theme.StarlinkRouter → Theme.JLH6
- `app/src/main/res/values/colors.xml` — remove unused chip/starlink/4g colors
- `app/src/main/res/layout/activity_main.xml` — single centered button, no route chip, no logs icon
- `app/src/main/res/drawable/ic_launcher_foreground.xml` — star only (remove wifi arcs)
- `app/src/main/res/drawable/ic_launcher_background.xml` — keep as-is (dark)
- `scripts/install-app.sh` — update PKG + REPO refs; stage (currently untracked)
- `scripts/install-apk.sh` — update REPO ref; stage (currently untracked)
- `.github/workflows/build.yml` — remove daemon syntax check + rule lifecycle tests + telnet parser tests
- `README.md` — rewrite for JLH6
- `CLAUDE.md` — rewrite for JLH6

---

### Task 1: New Java package — MainActivity

**Files:**
- Create: `app/src/main/java/com/castilhoduarte/jlh6/MainActivity.java`
- Delete: `app/src/main/java/com/castilhoduarte/starlinkrouter/` (all 6 Java files)

**Interfaces:**
- Produces: `com.castilhoduarte.jlh6.MainActivity` — Activity with `R.id.settings_button` click → opens Android Settings

- [ ] **Step 1: Create new package directory and MainActivity**

```bash
mkdir -p app/src/main/java/com/castilhoduarte/jlh6
```

Write `app/src/main/java/com/castilhoduarte/jlh6/MainActivity.java`:

```java
package com.castilhoduarte.jlh6;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

public final class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.settings_button).setOnClickListener(v -> openAndroidSettings());
    }

    private void openAndroidSettings() {
        Intent explicit = new Intent(Intent.ACTION_MAIN);
        explicit.setComponent(new ComponentName(
                "com.android.settings", "com.android.settings.Settings"));
        explicit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(explicit);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }
}
```

- [ ] **Step 2: Delete old Java files**

```bash
rm -rf app/src/main/java/com/castilhoduarte/starlinkrouter
```

- [ ] **Step 3: Delete unused assets and test scripts**

```bash
rm -f app/src/main/assets/starlinkrouter.sh
rm -f scripts/test/rule_lifecycle_test.sh
rm -f scripts/test/TelnetRootTest.java
rm -f scripts/test/README.md
rmdir scripts/test 2>/dev/null || true
rm -f docs/DESIGN.md docs/ui-mockup.svg
rmdir docs 2>/dev/null || true
```

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "refactor: new com.castilhoduarte.jlh6 package; delete all routing/telnet/boot code"
```

---

### Task 2: Gradle + Manifest + Resources config

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `settings.gradle.kts`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values/themes.xml`
- Modify: `app/src/main/res/values/colors.xml`

**Interfaces:**
- Consumes: `com.castilhoduarte.jlh6.MainActivity` from Task 1
- Produces: compilable Android project named JLH6 with applicationId `com.castilhoduarte.jlh6`

- [ ] **Step 1: Update `app/build.gradle.kts`**

Change `namespace` and `applicationId`:

```kotlin
android {
    namespace = "com.castilhoduarte.jlh6"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.castilhoduarte.jlh6"
        minSdk = 28
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 28
        versionCode = 1
        versionName = "1.0.0"
    }
    // rest unchanged
```

- [ ] **Step 2: Update `settings.gradle.kts`**

```kotlin
rootProject.name = "JLH6"
```

- [ ] **Step 3: Rewrite `app/src/main/AndroidManifest.xml`**

Remove all permissions except none (no network, no boot, no foreground service needed).
Remove BootService, BootReceiver, LogActivity entries.
Update theme ref.

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="false"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.JLH6">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:screenOrientation="landscape"
            android:configChanges="orientation|keyboardHidden|screenSize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```

- [ ] **Step 4: Rewrite `app/src/main/res/values/strings.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">JLH6</string>
    <string name="open_settings">Abrir configurações do Android</string>
</resources>
```

- [ ] **Step 5: Update `app/src/main/res/values/themes.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.JLH6" parent="@android:style/Theme.Material.NoActionBar">
        <item name="android:windowBackground">@drawable/bg_gradient</item>
        <item name="android:statusBarColor">@color/bg_top</item>
        <item name="android:navigationBarColor">@color/bg_bottom</item>
        <item name="android:colorAccent">@color/icon_bg</item>
    </style>
</resources>
```

- [ ] **Step 6: Trim `app/src/main/res/values/colors.xml`** — remove chip/router-specific colors

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="bg_top">#0F172A</color>
    <color name="bg_bottom">#020617</color>
    <color name="text_primary">#F8FAFC</color>
    <color name="off_gray">#334155</color>
    <color name="icon_bg">#0EA5E9</color>
    <color name="white">#FFFFFF</color>
</resources>
```

- [ ] **Step 7: Commit**

```bash
git add app/build.gradle.kts settings.gradle.kts app/src/main/AndroidManifest.xml \
        app/src/main/res/values/
git commit -m "refactor: rename project to JLH6; strip all router/boot config"
```

---

### Task 3: New layout + delete unused drawables

**Files:**
- Modify: `app/src/main/res/layout/activity_main.xml`
- Delete: `app/src/main/res/drawable/bg_chip.xml`, `bg_logs_button.xml`, `ic_logs.xml`, `ic_settings.xml`, `ic_wifi.xml`
- Delete: `app/src/main/res/layout/activity_log.xml`

**Interfaces:**
- Consumes: `R.id.settings_button` — referenced in `MainActivity.java`
- Consumes: `@drawable/bg_button`, `@drawable/bg_gradient` — both already exist, unchanged
- Produces: full-screen centered button layout

- [ ] **Step 1: Rewrite `app/src/main/res/layout/activity_main.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:background="@drawable/bg_gradient">

    <LinearLayout
        android:id="@+id/settings_button"
        android:layout_width="750dp"
        android:layout_height="190dp"
        android:orientation="vertical"
        android:gravity="center"
        android:background="@drawable/bg_button"
        android:clickable="true"
        android:focusable="true">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/open_settings"
            android:textColor="@color/white"
            android:textSize="52sp"
            android:textStyle="bold"
            android:gravity="center"
            android:letterSpacing="0.04" />

    </LinearLayout>

</LinearLayout>
```

- [ ] **Step 2: Delete unused drawables and layout**

```bash
rm -f app/src/main/res/drawable/bg_chip.xml
rm -f app/src/main/res/drawable/bg_logs_button.xml
rm -f app/src/main/res/drawable/ic_logs.xml
rm -f app/src/main/res/drawable/ic_settings.xml
rm -f app/src/main/res/drawable/ic_wifi.xml
rm -f app/src/main/res/layout/activity_log.xml
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/res/
git commit -m "ui: single centered button; delete all router/log UI artifacts"
```

---

### Task 4: Star launcher icon

**Files:**
- Modify: `app/src/main/res/drawable/ic_launcher_foreground.xml` — star only, no wifi arcs
- Keep: `ic_launcher_background.xml`, `mipmap-anydpi-v26/ic_launcher.xml`, `mipmap-anydpi-v26/ic_launcher_round.xml` — unchanged

**Interfaces:**
- Produces: adaptive launcher icon showing a single yellow/amber 5-pointed star on dark background

- [ ] **Step 1: Replace `app/src/main/res/drawable/ic_launcher_foreground.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">

    <path
        android:fillColor="#F59E0B"
        android:pathData="
          M54,24
          L60.18,42.27 L79.63,42.27 L64.72,53.73 L70.9,72 L54,60.54
          L37.1,72 L43.28,53.73 L28.37,42.27 L47.82,42.27 Z" />

</vector>
```

- [ ] **Step 2: Verify ic_launcher_background.xml still references valid colors**

The background uses `ic_launcher_background.xml`. Open it and confirm it doesn't reference any removed color. Run:

```bash
cat app/src/main/res/drawable/ic_launcher_background.xml
```

Expected: references `@color/bg_top` or a hex value — both fine. If it references a removed color (like `chip_starlink`), update to `#0F172A`.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/res/drawable/ic_launcher_foreground.xml
git commit -m "ui: launcher icon — star only, no wifi arcs"
```

---

### Task 5: Update install scripts

**Files:**
- Modify + stage: `scripts/install-app.sh` (untracked → add to git)
- Modify + stage: `scripts/install-apk.sh` (untracked → add to git)

Note: `scripts/install.sh` and `scripts/install-any.sh` are tracked-but-deleted. We're replacing them with install-app.sh (full Frida install for JLH6) and install-apk.sh (Frida install for any APK). These already exist on disk with the right structure — just need REPO/PKG refs updated.

**Interfaces:**
- Produces: `install-app.sh` installs `com.castilhoduarte.jlh6` from `github.com/jucastilhoduarte/jlh6`
- Produces: `install-apk.sh` fetches exploit bins from `github.com/jucastilhoduarte/jlh6`

- [ ] **Step 1: Update `scripts/install-app.sh`** — change PKG and REPO

In `scripts/install-app.sh`, find and replace:
- `PKG="com.castilhoduarte.starlinkrouter"` → `PKG="com.castilhoduarte.jlh6"`
- `REPO="https://github.com/jucastilhoduarte/starlinkrouter"` → `REPO="https://github.com/jucastilhoduarte/jlh6"`
- Any messages referencing "Starlink Router" → "JLH6"
- Remove comment about `uid <= 10999` / telnet access (no longer relevant)

The file header should become:
```sh
#!/bin/sh
# install-app.sh - instala (idempotente) o JLH6 a partir de um APK.
#
# Uso:
#   sh install-app.sh [url-do-apk]
#
# Sem argumento: baixa o .apk do ultimo release do REPO abaixo.
# Com argumento: usa a URL dada. Aceita .apk direto ou zip.
#
# GH_TOKEN (opcional, via env): vai como Bearer no download.
#
# Por que o exploit Frida e necessario: a multimidia bloqueia pm install
# de APKs externos. A injecao no system_server remove essa restricao.

set -u

PKG="com.castilhoduarte.jlh6"
REPO="https://github.com/jucastilhoduarte/jlh6"
```

And the final log lines:
```sh
log "INFO" "Instalado: $(pm path "$PKG" 2>/dev/null)"
echo "JLH6 instalado com sucesso."
```

- [ ] **Step 2: Update `scripts/install-apk.sh`** — change REPO ref

```sh
REPO="https://github.com/jucastilhoduarte/jlh6"
```

Update any inline comment referencing starlinkrouter repo.

- [ ] **Step 3: Stage both files and commit deletions**

```bash
git add scripts/install-app.sh scripts/install-apk.sh
# install-any.sh and install.sh are tracked-deleted — git already knows
git commit -m "scripts: rename install scripts; update REPO/PKG refs to jlh6"
```

---

### Task 6: Simplify CI workflow

**Files:**
- Modify: `.github/workflows/build.yml`

**Interfaces:**
- Produces: PR job = `assembleDebug` only; push-to-main job = same + `assembleRelease` + create GitHub release

- [ ] **Step 1: Rewrite `.github/workflows/build.yml`**

Remove: daemon syntax check, rule_lifecycle_test.sh, TelnetRootTest compilation+run.
Keep: JDK setup, gradlew permissions, compile steps, keystore decode, version bump, release upload.
Update release title from "Starlink Router" to "JLH6".

```yaml
name: Build

on:
  pull_request:
    branches: [main]
  push:
    branches: [main]

permissions:
  contents: write

jobs:
  pr-build:
    if: github.event_name == 'pull_request'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Configurar JDK
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Conceder permissão de execução ao gradlew
        run: chmod +x gradlew

      - name: Compilar (debug)
        run: ./gradlew assembleDebug --stacktrace

  release:
    if: github.event_name == 'push'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Configurar JDK
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Decodificar keystore
        run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > app/release.keystore

      - name: Calcular versão
        id: ver
        run: |
          VC="${GITHUB_RUN_NUMBER}"
          VN="1.0.${GITHUB_RUN_NUMBER}"
          echo "code=$VC" >> "$GITHUB_OUTPUT"
          echo "name=$VN" >> "$GITHUB_OUTPUT"

      - name: Gravar versão no build.gradle.kts
        run: |
          sed -i "s/versionName = \".*\"/versionName = \"${{ steps.ver.outputs.name }}\"/" app/build.gradle.kts
          sed -i "s/versionCode = [0-9]*/versionCode = ${{ steps.ver.outputs.code }}/" app/build.gradle.kts

      - name: Conceder permissão de execução ao gradlew
        run: chmod +x gradlew

      - name: Gerar release assinado
        env:
          SIGNING_STORE_PASSWORD: ${{ secrets.STORE_PASSWORD }}
          SIGNING_KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          SIGNING_KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: ./gradlew assembleRelease --stacktrace

      - name: Criar release e enviar APK
        env:
          GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: |
          TAG="v${{ steps.ver.outputs.name }}"
          gh release create "$TAG" \
            --title "JLH6 ${{ steps.ver.outputs.name }}" \
            --notes "Release automatizado"
          gh release upload "$TAG" \
            app/build/outputs/apk/release/app-release.apk
```

- [ ] **Step 2: Commit**

```bash
git add .github/workflows/build.yml
git commit -m "ci: strip daemon/telnet tests; update release title to JLH6"
```

---

### Task 7: Docs + CLAUDE.md rewrite

**Files:**
- Modify: `README.md`
- Modify: `CLAUDE.md`

- [ ] **Step 1: Rewrite `README.md`**

```markdown
# JLH6

Aplicativo Android para **minha head unit Haval/GWM**. Uma tela, um botão: abre as configurações do Android.

Não está em nenhuma loja. Instalado apenas no meu carro.

## Como instalar

```sh
# Instalar o JLH6
sh install-app.sh

# Instalar qualquer APK (bypass multimedia restrictions via Frida)
sh install-apk.sh <url-do-apk>
```

Os scripts fazem o bypass do bloqueio de `pm install` via injeção Frida no `system_server`.
Os binários do exploit estão em: `https://github.com/jucastilhoduarte/jlh6/releases/tag/exploit-bins`

## Build / release

- **Pull request → `assembleDebug`**
- **Merge para `main` → `assembleRelease` assinado** → publicado como release do GitHub com APK

Segredos de assinatura no Actions: `KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_PASSWORD`, `KEY_ALIAS`.
```

- [ ] **Step 2: Rewrite `CLAUDE.md`**

```markdown
# JLH6 — contexto para o Claude

## O que é isso

Aplicativo Android de propósito único para o sistema de entretenimento (head unit) pessoal de um carro Haval/GWM. Uma tela com um único botão que abre as configurações do Android. Nada mais.

## Regras absolutas — nunca quebre estas

- **Zero dependências de terceiros.** Apenas Android SDK. Apenas Java. Sem AndroidX, sem Compose, sem Kotlin, sem Shizuku, sem nada do Jetpack.
- `android.useAndroidX=false` em `gradle.properties` — deve permanecer assim.
- `minSdk = targetSdk = 28` — deliberado.
- `compileSdk = 35`, AGP 8.7.3, Gradle 8.14.3.
- PR → somente build debug. Merge em `main` → release assinada + `gh release create`.

## Arquivos principais

| Caminho | O que é |
|---------|---------|
| `app/src/main/java/com/castilhoduarte/jlh6/MainActivity.java` | Única tela. Botão central → abre `com.android.settings/.Settings`. |
| `scripts/install-app.sh` | Instala o JLH6 via exploit Frida (bypass de pm install da multimidia). |
| `scripts/install-apk.sh` | Instala qualquer APK via exploit Frida. |

## Exploit Frida (por que existe)

A head unit bloqueia `pm install` de APKs externos. Os scripts injetam no `system_server` via Frida para remover essa restrição durante a instalação.

Binários do exploit em: `https://github.com/jucastilhoduarte/jlh6/releases/tag/exploit-bins`

## CI (`.github/workflows/build.yml`)

- **PR**: `assembleDebug`
- **Push em main**: `assembleRelease` assinado + `gh release create`

Secrets: `KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_PASSWORD`, `KEY_ALIAS`.

## Design da UI

Tema escuro, landscape, 21:9. Sem ActionBar. Um botão retangular grande centralizado na tela.

## Pacote / assinatura

- `applicationId = com.castilhoduarte.jlh6`
- Assinado com chave pessoal do dono. Keystore em `~/Desktop/haval-actions-secrets`.
```

- [ ] **Step 3: Commit**

```bash
git add README.md CLAUDE.md
git commit -m "docs: rewrite README and CLAUDE.md for JLH6"
```

---

### Task 8: Build verify + GitHub rename + push

**Files:** none (verification + remote ops)

- [ ] **Step 1: Local build verify**

```bash
./gradlew assembleDebug --stacktrace
```

Expected: `BUILD SUCCESSFUL`. If it fails, check: (a) `R.layout.activity_main` has `settings_button` id, (b) no references to deleted classes remain in the manifest, (c) theme name matches `Theme.JLH6`.

- [ ] **Step 2: Rename GitHub repo**

```bash
gh repo rename jlh6 --yes
```

Expected output confirms repo renamed to `jucastilhoduarte/jlh6`.

- [ ] **Step 3: Update local remote URL**

```bash
git remote set-url origin https://github.com/jucastilhoduarte/jlh6.git
```

- [ ] **Step 4: Push to main**

```bash
git push origin main
```

- [ ] **Step 5: Verify on GitHub**

```bash
gh repo view jucastilhoduarte/jlh6 --web
```

Confirm repo name, README, and CI pipeline shows green on first run.
