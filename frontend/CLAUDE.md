# CLAUDE.md

See fail annab juhiseid Claude Code'ile (claude.ai/code) selle repositooriumi koodiga töötamisel.

## Käsud

```sh
npm install        # Installi sõltuvused
npm run dev        # Arendusserver aadressil http://localhost:8081
npm run build      # Tootmisbuild
npm run lint       # Käivita oxlint ja eslint (mõlemad --fix lipuga)
npm run format     # Prettieri formaatimine src/ kaustas
```

## Arhitektuur

See on Vue 3 + Vite frontend (Vali-IT grupiprojekt).

**Stack:** Vue 3 (Composition API), Vue Router 5, Pinia, Nuxt UI 4 (Tailwind CSS 4), Axios, Phosphor Icons

**UI teek (Nuxt UI):** Kasutusel on `@nuxt/ui` Vue + Vite režiimis. Seadistus: `ui()` plugin `vite.config.js`-is, `app.use(ui)` `src/main.js`-is (peab tulema pärast `app.use(router)`), `@import "tailwindcss"` ja `@import "@nuxt/ui"` failis `src/assets/main.css`, juurkomponent `<UApp>` failis `src/App.vue` ning `class="isolate"` `index.html`-i `#app` elemendil. Komponendid (`UButton`, `UForm`, `UInput` jne) ja composable'id (`useToast` jne) on auto-importitud — neid ei pea käsitsi importima. Failid `components.d.ts` ja `auto-imports.d.ts` genereeritakse automaatselt. Bootstrapi CSS-i enam ei laeta.

**Sisenemispunkt:** `index.html` laeb Vue rakenduse (`src/main.js` → `src/App.vue`).

**API proksi:** Vite suunab `/api` päringud kohalikule backendile (`http://localhost:8080`), vt `vite.config.js`.

**Globaalne axios:** Axios on registreeritud `app.config.globalProperties.$axios`-na — komponentides kasuta `this.$axios` (Options API) või inject via `getCurrentInstance` (Composition API).

**Olekuhaldus:** Pinia on registreeritud (`app.use(createPinia())`), aga store'e veel ei ole — need lisatakse kausta `src/stores/` vastavalt vajadusele.

**Marsruutimine:** Marsruudid on defineeritud `src/router/index.js`-is.

**Tee alias:** `@` viitab `src/` kaustale.

## Koodistiil

Prettieri seadistus: ilma semikooloniteta, ülakomad, 100-märgiline reavaheline laius. ESLint käivitab esmalt oxlinti, seejärel eslint-plugin-vue (olulised reeglid), Prettieri formaatimine on ESLintist välja jäetud.

## Keel

Selle faili (`CLAUDE.md`) sisu peab alati olema eestikeelne.

## Dokumentatsioon

Kogu dokumentatsioon asub `docs/` kausta alakaustades. Kõigi dokumentatsioonifailide sisu peab alati olema eestikeelne.
