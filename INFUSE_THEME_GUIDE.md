# 🎨 Guida alla Gestione del Tema Infuse per NOVA Video Player

Questo documento contiene la guida completa per la manutenzione del **Tema Infuse** su NOVA Video Player, inclusa la procedura passo-passo per allineare il progetto alle **future release ufficiali di NOVA** senza perdere alcuna personalizzazione.

---

## 📌 Configurazione dei Repository

| Remote | Repository URL | Descrizione |
| :--- | :--- | :--- |
| **`origin`** | `https://github.com/pulcix/aos-AVP.git` | Il tuo fork personale su GitHub (dove salvi il tuo lavoro) |
| **`upstream`** | `https://github.com/nova-video-player/aos-AVP.git` | Repository ufficiale di NOVA Video Player |

- **Convenzione Nomi Rami**: `feature/infuse-v<VERSIONE>` (es. `feature/infuse-v6.6.4`)

---

## 🚀 Guida Passo-Passo per Aggiornare a una Nuova Release di NOVA

Quando NOVA rilascia una nuova versione ufficiale su GitHub (ad esempio `v6.6.5` o `v6.7.0`), segui questa procedura in **4 passaggi**:

### Passaggio 1: Salva il lavoro corrente su GitHub (`origin`)
Assicurati che tutte le tue modifiche correnti siano salvate e inviate sul tuo GitHub:
```bash
git push -u origin feature/infuse-v6.6.4
```

### Passaggio 2: Scarica le ultime release da NOVA (`upstream`)
Scarica i tag e le modifiche più recenti dal repository ufficiale:
```bash
git fetch upstream --tags
```

### Passaggio 3: Crea un nuovo ramo basato sulla nuova release
Crea un nuovo ramo partendo dal tag della nuova release (ad esempio `v6.6.5`):
```bash
git checkout -b feature/infuse-v6.6.5 v6.6.5
```

### Passaggio 4: Unisci le personalizzazioni del Tema Infuse
Unisci il tuo precedente ramo Infuse nel nuovo ramo:
```bash
git merge feature/infuse-v6.6.4
```

> 💡 **Nota Importante**: Grazie all'architettura a classi helper del tema Infuse, l'unione avverrà automaticamente **senza conflitti**. Una volta completato il merge, invia il nuovo ramo su GitHub:
> ```bash
> git push -u origin feature/infuse-v6.6.5
> ```

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

---

## 📌 Come Mantenere questa Guida Aperta all'Avvio di Android Studio

Android Studio salva lo stato dell'editor al momento della chiusura. Per fare in modo che questo file appaia **automaticamente come pro-memoria ogni volta che apri il progetto**:

1. Lascia questa scheda **`INFUSE_THEME_GUIDE.md`** aperta nell'editor di Android Studio.
2. Fai clic destro sulla scheda del file `INFUSE_THEME_GUIDE.md` in alto e seleziona **Pin Tab** (Aggiungi spilla).
3. In questo modo la scheda rimarrà sempre fissata e si riaprirà automaticamente ad ogni avvio del progetto in Android Studio!
