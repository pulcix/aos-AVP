# 🎨 Guida alla Gestione del Tema Infuse per NOVA Video Player

Questo documento contiene la guida completa per la manutenzione del **Tema Infuse** su NOVA Video Player (`org.courville.nova.infuse`), inclusa la procedura collaudata per allineare il progetto alle **future release ufficiali di NOVA** (inclusi i sottomoduli `MediaLib` e `FileCoreLibrary`), far convivere l'app con la versione ufficiale sul TV e migrare i database di backup.

---

## 📌 Configurazione dei Repository e Identità App

| Remote / Parametro | Valore | Descrizione |
| :--- | :--- | :--- |
| **`origin`** | `https://github.com/pulcix/aos-AVP.git` | Il tuo fork personale su GitHub (dove salvi il tuo lavoro) |
| **`upstream`** | `https://github.com/nova-video-player/aos-AVP.git` | Repository ufficiale di NOVA Video Player |
| **`applicationId`** | `org.courville.nova.infuse` | Package isolato per installazione parallela a NOVA ufficiale |
| **`sharedUserId`** | `archos.openmediacenter.infuse` | Isola l'UID Linux evitando `INSTALL_FAILED_SHARED_USER_INCOMPATIBLE` |
| **Rami Infuse** | `feature/infuse-v<VERSIONE>` | Es. `feature/infuse-v6.6.5` |

> ⚠️ **Sicurezza Git**: Non aggiungere mai il file `FirmaKeystore.jks` ai commit (`git add`). Verifica che sia ignorato prima di ogni push.

---

## 🚀 Guida Passo-Passo per Aggiornare a una Nuova Release di NOVA

Quando NOVA rilascia una nuova versione ufficiale su GitHub, segui questa procedura completa in **6 passaggi**:

### Passaggio 1: Verifica l'ultima release ufficiale su GitHub
Interroga le API di GitHub direttamente dal terminale per conoscere il tag più recente:
```bash
curl -s https://api.github.com/repos/nova-video-player/aos-AVP/releases/latest | grep '"tag_name":'
```

### Passaggio 2: Salva il lavoro corrente sul tuo GitHub (`origin`)
Assicurati che tutte le modifiche sul ramo attuale siano committate e inviate (sostituisci `<VECCHIA_VER>` con la versione corrente, es. `6.6.5`):
```bash
git push -u origin feature/infuse-v<VECCHIA_VER>
```

### Passaggio 3: Scarica i tag ufficiali da `upstream` (con `--force`)
Usa `--force` per evitare l'errore `would clobber existing tag` sui tag storici:
```bash
git fetch upstream --tags --force
```

### Passaggio 4: Crea il nuovo ramo e unisci il Tema Infuse
Crea il nuovo ramo partendo dal nuovo tag (es. `v6.7.0`) e integra le personalizzazioni del ramo precedente:
```bash
git checkout -b feature/infuse-v<NUOVA_VER> v<NUOVA_VER>
git merge feature/infuse-v<VECCHIA_VER>
```

### Passaggio 5: Allinea i sottomoduli (`MediaLib`, `FileCoreLibrary` e `native/*`)
NOVA gestisce `MediaLib` e `FileCoreLibrary` su branch di release dedicati (es. `upstream/v6.6` o `upstream/nova`) che **non** si aggiornano automaticamente con `git merge`. Allineali esplicitamente per evitare disallineamenti di schema SQLite (`Incompatible database version`):
```bash
# 1. Allinea i sottomoduli nativi registrati nel tag
git submodule sync --recursive
git submodule update --init --recursive

# 2. Aggiorna MediaLib e FileCoreLibrary al branch della nuova release (es. upstream/v6.7 o upstream/nova)
git -C MediaLib fetch https://github.com/nova-video-player/aos-MediaLib.git "+refs/heads/*:refs/remotes/upstream/*"
git -C MediaLib checkout -f upstream/<BRANCH_RELEASE>

git -C FileCoreLibrary fetch https://github.com/nova-video-player/aos-FileCoreLibrary.git "+refs/heads/*:refs/remotes/upstream/*"
git -C FileCoreLibrary checkout -f upstream/<BRANCH_RELEASE>
```
*Verifica versione database:* Controlla che `DATABASE_VERSION` in `MediaLib/src/com/archos/mediaprovider/video/VideoOpenHelper.java` corrisponda alla versione dell'app ufficiale:
```bash
grep "DATABASE_VERSION =" MediaLib/src/com/archos/mediaprovider/video/VideoOpenHelper.java
```

### Passaggio 6: Sincronizza Gradle e salva su GitHub
1. In Android Studio clicca su **File > Sync Project with Gradle Files**.
2. Invia il nuovo ramo sul tuo fork:
   ```bash
   git push -u origin feature/infuse-v<NUOVA_VER>
   ```

---

## 💾 Migrazione Backup (>200 MB) da NOVA Ufficiale a NOVA Infuse

La classe `MediaLibraryBackupService.java` è stata estesa per supportare l'importazione diretta dei backup (`backup.zip`) esportati dalla versione ufficiale `org.courville.nova`:
- **Decompressione in streaming** (`BUFFER_SIZE = 8192`) per gestire archivi oltre 200 MB su TV Android 32-bit senza `OutOfMemoryError`.
- **Mapping automatico `shared_prefs`**: converte `org.courville.nova_preferences.xml` in `org.courville.nova.infuse_preferences.xml`.
- **Aggiornamento percorsi SQLite**: sostituisce automaticamente `/org.courville.nova/` con `/org.courville.nova.infuse/` nelle tabelle dei database importati (`media.db`, `credentials_db`, `shortcuts_db`, `shortcuts2_db`).

### Procedura di importazione via ADB (consigliata per file grandi)
1. Esporta la libreria da **NOVA Ufficiale** (*Preferenze > Export media library + credentials + shortcuts*).
2. Copia `backup.zip` nella cartella di **NOVA Infuse** direttamente via ADB (evita troncamenti dei file manager TV):
   ```bash
   adb shell cp /sdcard/Android/data/org.courville.nova/files/backup.zip /sdcard/Android/data/org.courville.nova.infuse/files/backup.zip
   ```
3. Apri **NOVA Infuse** sul TV e avvia **Preferenze > Import media library + credentials + shortcuts**.

---

## 🛠️ Architettura e Classi Helper del Tema Infuse

Tutto il codice del tema Infuse è isolato e protetto dal controllo `ThemeManager.getInstance(context).isInfuseTheme()`. Le principali classi helper sono:

1. **`InfuseGridHelper.java`**:
   - Gestisce la griglia a 6 colonne con spaziatura uniforme (`104dp` top, `48dp` lati).
   - Abilita lo **scorrimento continuo a capo D-Pad** (Auto-Wrap stile tvOS) tra la fine di una riga e l'inizio della successiva.
   - Configura le ottimizzazioni di performance RecyclerView per scorrimento fluido a 60fps.

2. **`InfuseHeadersHelper.java`**:
   - Gestisce il pannello laterale sinistro delle categorie/generi per tutte le schermate (*Movies, TV Shows, Anime, Videos*).
   - Imposta la pillola arancione di selezione ed evita qualsiasi ritaglio d'immagine (`setClipToPadding(true)` + `setClipToOutline(true)`).

3. **`InfuseCardStyler.java`**:
   - Gestisce l'estetica delle locandine e l'animazione di zoom hardware-accelerata a 60fps (`FastOutSlowInInterpolator` + `.withLayer()`).
   - Mantiene l'altezza al pixel identica per tutte le card tramite `View.INVISIBLE`, eliminando i buchi nelle griglie.

4. **`InfuseDialogHelper.java`**:
   - Tematizza tutti gli `AlertDialog` con stile scuro translucido, angoli arrotondati `14dp` e pulsanti arancioni.

5. **`MyTitleView.java`**:
   - Gestisce la Top Bar globale con tasto `Search` e icone d'azione (`Sort`, `Refresh`, `Settings`) bloccate in alto.

6. **`MediaLibraryBackupService.java`**:
   - Gestisce export/import streaming e rimappatura percorsi cross-package (`org.courville.nova` $\rightarrow$ `org.courville.nova.infuse`).

---

## 📌 Come Mantenere questa Guida Aperta all'Avvio di Android Studio

1. Lascia questa scheda **`INFUSE_THEME_GUIDE.md`** aperta nell'editor di Android Studio.
2. Fai clic destro sulla scheda del file `INFUSE_THEME_GUIDE.md` in alto e seleziona **Pin Tab** (Aggiungi spilla).
3. La scheda rimarrà sempre fissata e si riaprirà automaticamente ad ogni avvio del progetto.